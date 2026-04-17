package com.hbm.ntm.common.block.entity;

import api.hbm.block.ICrucibleAcceptor;
import com.hbm.inventory.material.Mats.MaterialStack;
import com.hbm.ntm.common.config.RotaryFurnaceMachineConfig;
import com.hbm.ntm.common.fluid.HbmFluidTank;
import com.hbm.ntm.common.item.IItemFluidIdentifier;
import com.hbm.ntm.common.material.HbmMaterialDefinition;
import com.hbm.ntm.common.material.HbmMaterials;
import com.hbm.ntm.common.material.LegacyMaterialColors;
import com.hbm.ntm.common.menu.RotaryFurnaceMenu;
import com.hbm.ntm.common.multiblock.MultiblockCoreBE;
import com.hbm.ntm.common.multiblock.MultiblockStructure;
import com.hbm.ntm.common.pollution.PollutionType;
import com.hbm.ntm.common.registration.HbmBlockEntityTypes;
import com.hbm.ntm.common.registration.HbmBlocks;
import com.hbm.ntm.common.registration.HbmFluids;
import com.hbm.ntm.common.rotary.HbmRotaryFurnaceRecipes;
import com.hbm.ntm.common.rotary.RotaryFurnaceStructure;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public class RotaryFurnaceBlockEntity extends MultiblockCoreBE {
    public static final int SLOT_INPUT_1 = 0;
    public static final int SLOT_INPUT_2 = 1;
    public static final int SLOT_INPUT_3 = 2;
    public static final int SLOT_FLUID_ID = 3;
    public static final int SLOT_FUEL = 4;
    public static final int SLOT_COUNT = 5;

    public static final int TANK_INPUT = 0;
    public static final int TANK_STEAM = 1;
    public static final int TANK_SPENT_STEAM = 2;
    public static final int TANK_SMOKE = 3;

    private static final int[] INPUT_SLOTS = new int[]{SLOT_INPUT_1, SLOT_INPUT_2, SLOT_INPUT_3};
    private static final int[] SLOT_ACCESS = new int[]{SLOT_INPUT_1, SLOT_INPUT_2, SLOT_INPUT_3, SLOT_FLUID_ID, SLOT_FUEL};
    private static final Map<String, HbmMaterialDefinition> MATERIALS_BY_ID = HbmMaterials.ordered().stream()
        .collect(Collectors.toMap(HbmMaterialDefinition::id, material -> material));

    private float progress;
    private float burnHeat = 1.5F;
    private int burnTime;
    private int maxBurnTime;
    private int steamUsed;

    private String outputMaterialId = "";
    private int outputAmount;

    public RotaryFurnaceBlockEntity(final BlockPos pos, final BlockState state) {
        super(HbmBlockEntityTypes.MACHINE_ROTARY_FURNACE.get(), pos, state, SLOT_COUNT);
    }

    @Override
    public MultiblockStructure getStructure() {
        return RotaryFurnaceStructure.INSTANCE;
    }

    @Override
    protected HbmFluidTank[] createFluidTanks() {
        return new HbmFluidTank[]{
            this.createFluidTank(Math.max(1, RotaryFurnaceMachineConfig.INSTANCE.inputTankCapacity()), this::isInputFluidAllowed),
            this.createFluidTank(Math.max(1, RotaryFurnaceMachineConfig.INSTANCE.steamTankCapacity()), this::isSteamFluid),
            this.createFluidTank(Math.max(1, RotaryFurnaceMachineConfig.INSTANCE.spentSteamTankCapacity()), this::isSpentSteamFluid),
            this.createFluidTank(Math.max(1, RotaryFurnaceMachineConfig.INSTANCE.smokeTankCapacity()), this::isSmokeFluid)
        };
    }

    @Override
    protected boolean canFillFromSide(final Direction side) {
        final Direction direction = this.getDirection();
        final Direction rot = direction.getClockWise();
        return side == direction.getOpposite() || side == rot;
    }

    @Override
    protected boolean canDrainFromSide(final Direction side) {
        final Direction direction = this.getDirection();
        final Direction rot = direction.getClockWise();
        return side == direction.getOpposite() || side == rot || side == Direction.UP;
    }

    public static void serverTick(final Level level, final BlockPos pos, final BlockState state, final RotaryFurnaceBlockEntity furnace) {
        boolean changed = false;

        changed |= furnace.pullPortFluids();
        changed |= furnace.pushPortFluids();
        changed |= furnace.tryTransferOutput();

        final Optional<HbmRotaryFurnaceRecipes.RotaryRecipe> recipe = furnace.findRecipe();

        if (recipe.isPresent() && furnace.burnTime <= 0) {
            changed |= furnace.consumeFuel();
        }

        if (furnace.burnTime > 0) {
            furnace.burnTime--;
            changed = true;

            if (recipe.isPresent() && furnace.canProcess(recipe.get())) {
                final float speed = Math.max(furnace.burnHeat, 1.0F);
                furnace.progress += speed / (float) Math.max(1, recipe.get().duration());
                changed |= furnace.consumeSteam(recipe.get(), speed);
                furnace.bufferPollutionIntoSmokeTank(TANK_SMOKE, PollutionType.SOOT, HbmFluids.SMOKE.getStillFluid(), MACHINE_SOOT_PER_SECOND / 10.0F);

                if (furnace.progress >= 1.0F) {
                    furnace.progress = 0.0F;
                    if (furnace.processRecipe(recipe.get())) {
                        changed = true;
                    }
                }
            }
        }

        if (recipe.isEmpty() || !furnace.canProcess(recipe.get())) {
            if (furnace.progress > 0.0F) {
                furnace.progress = 0.0F;
                changed = true;
            }
        }

        changed |= furnace.returnSpentSteam();

        if (changed) {
            furnace.markChangedAndSync();
        }
        furnace.tickMachineStateSync();
    }

    private Optional<HbmRotaryFurnaceRecipes.RotaryRecipe> findRecipe() {
        final HbmFluidTank tank = this.getFluidTank(TANK_INPUT);
        final FluidStack inputFluid = tank == null || tank.isEmpty() ? FluidStack.EMPTY : tank.getFluid().copy();
        final ItemStackHandler handler = this.getInternalItemHandler();
        return HbmRotaryFurnaceRecipes.findRecipe(
            inputFluid,
            handler.getStackInSlot(SLOT_INPUT_1),
            handler.getStackInSlot(SLOT_INPUT_2),
            handler.getStackInSlot(SLOT_INPUT_3));
    }

    private boolean canProcess(final HbmRotaryFurnaceRecipes.RotaryRecipe recipe) {
        if (recipe == null) {
            return false;
        }

        if (this.steamUsed > 100) {
            return false;
        }

        if (this.outputAmount > 0 && !Objects.equals(this.outputMaterialId, recipe.outputMaterial().id())) {
            return false;
        }

        if (this.outputAmount + recipe.outputAmount() > Math.max(HbmRotaryFurnaceRecipes.QUANTA_PER_INGOT, RotaryFurnaceMachineConfig.INSTANCE.maxOutputAmount())) {
            return false;
        }

        final HbmFluidTank steamTank = this.getFluidTank(TANK_STEAM);
        if (steamTank == null) {
            return false;
        }

        final int requiredSteam = (int) Math.ceil(recipe.steamPerTick() * Math.max(this.burnHeat, 1.0F));
        if (steamTank.getFluidAmount() < requiredSteam) {
            return false;
        }

        final HbmFluidTank spentSteamTank = this.getFluidTank(TANK_SPENT_STEAM);
        if (spentSteamTank == null) {
            return false;
        }
        final int requiredReturnCapacity = Math.max(0, requiredSteam / 100);
        if (spentSteamTank.getCapacity() - spentSteamTank.getFluidAmount() < requiredReturnCapacity) {
            return false;
        }

        final FluidStack fluidRequirement = recipe.fluidRequirementCopy();
        if (!fluidRequirement.isEmpty()) {
            final HbmFluidTank inputTank = this.getFluidTank(TANK_INPUT);
            if (inputTank == null || inputTank.isEmpty()) {
                return false;
            }
            final FluidStack stored = inputTank.getFluid();
            if (!stored.isFluidEqual(fluidRequirement) || stored.getAmount() < fluidRequirement.getAmount()) {
                return false;
            }
        }

        return recipe.matches(fluidRequirementInput(), List.of(
            this.getInternalItemHandler().getStackInSlot(SLOT_INPUT_1),
            this.getInternalItemHandler().getStackInSlot(SLOT_INPUT_2),
            this.getInternalItemHandler().getStackInSlot(SLOT_INPUT_3)));
    }

    private FluidStack fluidRequirementInput() {
        final HbmFluidTank tank = this.getFluidTank(TANK_INPUT);
        return tank == null || tank.isEmpty() ? FluidStack.EMPTY : tank.getFluid().copy();
    }

    private boolean processRecipe(final HbmRotaryFurnaceRecipes.RotaryRecipe recipe) {
        if (!this.consumeIngredients(recipe)) {
            return false;
        }

        final FluidStack fluidRequirement = recipe.fluidRequirementCopy();
        if (!fluidRequirement.isEmpty()) {
            final HbmFluidTank inputTank = this.getFluidTank(TANK_INPUT);
            if (inputTank == null || inputTank.drain(fluidRequirement.getAmount(), IFluidHandler.FluidAction.SIMULATE).getAmount() < fluidRequirement.getAmount()) {
                return false;
            }
            inputTank.drain(fluidRequirement.getAmount(), IFluidHandler.FluidAction.EXECUTE);
        }

        if (this.outputAmount <= 0) {
            this.outputMaterialId = recipe.outputMaterial().id();
        }
        this.outputAmount = Math.min(
            Math.max(HbmRotaryFurnaceRecipes.QUANTA_PER_INGOT, RotaryFurnaceMachineConfig.INSTANCE.maxOutputAmount()),
            this.outputAmount + recipe.outputAmount());
        return true;
    }

    private boolean consumeIngredients(final HbmRotaryFurnaceRecipes.RotaryRecipe recipe) {
        final ItemStackHandler handler = this.getInternalItemHandler();

        for (final var requirement : recipe.ingredients()) {
            boolean consumed = false;
            for (final int slot : INPUT_SLOTS) {
                final ItemStack stack = handler.getStackInSlot(slot);
                if (!requirement.matches(stack)) {
                    continue;
                }
                final ItemStack reduced = stack.copy();
                reduced.shrink(requirement.count());
                handler.setStackInSlot(slot, reduced.isEmpty() ? ItemStack.EMPTY : reduced);
                consumed = true;
                break;
            }
            if (!consumed) {
                return false;
            }
        }
        return true;
    }

    private boolean consumeFuel() {
        final ItemStack fuel = this.getInternalItemHandler().getStackInSlot(SLOT_FUEL);
        if (fuel.isEmpty()) {
            return false;
        }

        final int baseBurnTime = ForgeHooks.getBurnTime(fuel, RecipeType.SMELTING);
        if (baseBurnTime <= 0) {
            return false;
        }

        final float timeModifier = this.isCokeFuel(fuel) ? 1.25F : 1.5F;
        this.maxBurnTime = Math.max(1, Math.round(baseBurnTime * timeModifier * 0.5F));
        this.burnTime = this.maxBurnTime;
        this.burnHeat = 1.5F;

        final ItemStack reduced = fuel.copy();
        reduced.shrink(1);
        this.getInternalItemHandler().setStackInSlot(SLOT_FUEL, reduced.isEmpty() ? ItemStack.EMPTY : reduced);
        return true;
    }

    private boolean consumeSteam(final HbmRotaryFurnaceRecipes.RotaryRecipe recipe, final float speed) {
        final HbmFluidTank steamTank = this.getFluidTank(TANK_STEAM);
        if (steamTank == null || recipe.steamPerTick() <= 0) {
            return false;
        }

        final float speedFactor = (float) (13.0D * Math.log10(Math.max(1.0F, speed)) + 1.0D);
        final int steamCost = Math.max(0, (int) (recipe.steamPerTick() * speedFactor));
        if (steamCost <= 0) {
            return false;
        }

        final int drained = steamTank.drain(steamCost, IFluidHandler.FluidAction.EXECUTE).getAmount();
        if (drained <= 0) {
            return false;
        }

        this.steamUsed += drained;
        return true;
    }

    private boolean returnSpentSteam() {
        if (this.steamUsed < 100) {
            return false;
        }

        final HbmFluidTank spentSteam = this.getFluidTank(TANK_SPENT_STEAM);
        if (spentSteam == null) {
            return false;
        }

        final int available = this.steamUsed / 100;
        if (available <= 0) {
            return false;
        }

        final FluidStack produced = new FluidStack(HbmFluids.SPENTSTEAM.getStillFluid(), available);
        final int accepted = spentSteam.fill(produced, IFluidHandler.FluidAction.EXECUTE);
        if (accepted <= 0) {
            return false;
        }

        this.steamUsed -= accepted * 100;
        return true;
    }

    private boolean pullPortFluids() {
        if (this.level == null || this.level.isClientSide()) {
            return false;
        }

        boolean moved = false;
        final int transfer = RotaryFurnaceMachineConfig.INSTANCE.fluidTransferPerTick();
        final Direction direction = this.getDirection();
        final Direction rot = direction.getClockWise();

        moved |= this.pullFromNeighbor(TANK_STEAM, this.getSteamPortPosA(), direction.getOpposite(), transfer);
        moved |= this.pullFromNeighbor(TANK_STEAM, this.getSteamPortPosB(), direction.getOpposite(), transfer);
        moved |= this.pullFromNeighbor(TANK_INPUT, this.getInputFluidPortPosA(), rot, transfer);
        moved |= this.pullFromNeighbor(TANK_INPUT, this.getInputFluidPortPosB(), rot, transfer);

        return moved;
    }

    private boolean pushPortFluids() {
        if (this.level == null || this.level.isClientSide()) {
            return false;
        }

        boolean moved = false;
        final int transfer = RotaryFurnaceMachineConfig.INSTANCE.gasTransferPerTick();
        final Direction direction = this.getDirection();

        moved |= this.pushToNeighbor(TANK_SPENT_STEAM, this.getSteamPortPosA(), direction.getOpposite(), transfer);
        moved |= this.pushToNeighbor(TANK_SPENT_STEAM, this.getSteamPortPosB(), direction.getOpposite(), transfer);
        moved |= this.pushToNeighbor(TANK_SMOKE, this.getSmokeVentPos(), Direction.UP, transfer);

        return moved;
    }

    private boolean pullFromNeighbor(final int tankIndex, final BlockPos portPos, final Direction side, final int maxTransfer) {
        if (this.level == null || maxTransfer <= 0) {
            return false;
        }

        final HbmFluidTank tank = this.getFluidTank(tankIndex);
        if (tank == null) {
            return false;
        }

        final BlockEntity neighbor = this.level.getBlockEntity(portPos.relative(side));
        if (neighbor == null) {
            return false;
        }

        final IFluidHandler neighborHandler = neighbor.getCapability(ForgeCapabilities.FLUID_HANDLER, side.getOpposite()).orElse(null);
        if (neighborHandler == null) {
            return false;
        }

        final int space = tank.getCapacity() - tank.getFluidAmount();
        if (space <= 0) {
            return false;
        }

        FluidStack simulatedDrain;
        if (tank.isEmpty()) {
            simulatedDrain = neighborHandler.drain(Math.min(space, maxTransfer), IFluidHandler.FluidAction.SIMULATE);
        } else {
            final FluidStack template = tank.getFluid().copy();
            template.setAmount(Math.min(space, maxTransfer));
            simulatedDrain = neighborHandler.drain(template, IFluidHandler.FluidAction.SIMULATE);
        }

        if (simulatedDrain.isEmpty()) {
            return false;
        }

        final int accepted = tank.fill(simulatedDrain.copy(), IFluidHandler.FluidAction.SIMULATE);
        if (accepted <= 0) {
            return false;
        }

        final FluidStack drained = neighborHandler.drain(Math.min(simulatedDrain.getAmount(), accepted), IFluidHandler.FluidAction.EXECUTE);
        if (drained.isEmpty()) {
            return false;
        }

        return tank.fill(drained, IFluidHandler.FluidAction.EXECUTE) > 0;
    }

    private boolean pushToNeighbor(final int tankIndex, final BlockPos portPos, final Direction side, final int maxTransfer) {
        if (this.level == null || maxTransfer <= 0) {
            return false;
        }

        final HbmFluidTank tank = this.getFluidTank(tankIndex);
        if (tank == null || tank.isEmpty()) {
            return false;
        }

        final BlockEntity neighbor = this.level.getBlockEntity(portPos.relative(side));
        if (neighbor == null) {
            return false;
        }

        final IFluidHandler neighborHandler = neighbor.getCapability(ForgeCapabilities.FLUID_HANDLER, side.getOpposite()).orElse(null);
        if (neighborHandler == null) {
            return false;
        }

        final FluidStack stored = tank.getFluid();
        final int offered = Math.min(maxTransfer, stored.getAmount());
        if (offered <= 0) {
            return false;
        }

        final int accepted = neighborHandler.fill(new FluidStack(stored, offered), IFluidHandler.FluidAction.EXECUTE);
        if (accepted <= 0) {
            return false;
        }

        tank.drain(accepted, IFluidHandler.FluidAction.EXECUTE);
        return true;
    }

    private boolean tryTransferOutput() {
        if (this.level == null || this.level.isClientSide() || this.outputAmount <= 0) {
            return false;
        }

        final HbmMaterialDefinition outputMaterial = this.outputMaterial();
        if (outputMaterial == null) {
            this.outputAmount = 0;
            this.outputMaterialId = "";
            return true;
        }

        final MaterialStack offered = new MaterialStack(outputMaterial, this.outputAmount);
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    final BlockPos candidate = this.getOutputPos().offset(x, y, z);
                    final BlockState state = this.level.getBlockState(candidate);
                    if (!(state.getBlock() instanceof final ICrucibleAcceptor acceptor)) {
                        continue;
                    }

                    if (!acceptor.canAcceptPartialFlow(this.level, candidate, Direction.UP, offered.copy())) {
                        continue;
                    }

                    final MaterialStack remainder = acceptor.flow(this.level, candidate, Direction.UP, offered.copy());
                    final int remaining = remainder == null ? 0 : Math.max(0, remainder.amount);
                    if (remaining < this.outputAmount) {
                        this.outputAmount = remaining;
                        if (this.outputAmount <= 0) {
                            this.outputAmount = 0;
                            this.outputMaterialId = "";
                        }
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private BlockPos getSteamPortPosA() {
        final Direction direction = this.getDirection();
        final Direction rot = direction.getClockWise();
        return this.getCorePos().offset(-2 * direction.getStepX() - 2 * rot.getStepX(), 0, -2 * direction.getStepZ() - 2 * rot.getStepZ());
    }

    private BlockPos getSteamPortPosB() {
        final Direction direction = this.getDirection();
        final Direction rot = direction.getClockWise();
        return this.getCorePos().offset(-2 * direction.getStepX() - rot.getStepX(), 0, -2 * direction.getStepZ() - rot.getStepZ());
    }

    private BlockPos getInputFluidPortPosA() {
        final Direction direction = this.getDirection();
        final Direction rot = direction.getClockWise();
        return this.getCorePos().offset(direction.getStepX() + 3 * rot.getStepX(), 0, direction.getStepZ() + 3 * rot.getStepZ());
    }

    private BlockPos getInputFluidPortPosB() {
        final Direction direction = this.getDirection();
        final Direction rot = direction.getClockWise();
        return this.getCorePos().offset(-direction.getStepX() + 3 * rot.getStepX(), 0, -direction.getStepZ() + 3 * rot.getStepZ());
    }

    private BlockPos getOutputPos() {
        final Direction rot = this.getDirection().getClockWise();
        return this.getCorePos().offset(3 * rot.getStepX(), 1, 3 * rot.getStepZ());
    }

    private BlockPos getSmokeVentPos() {
        final Direction rot = this.getDirection().getClockWise();
        return this.getCorePos().offset(rot.getStepX(), 4, rot.getStepZ());
    }

    private boolean isCokeFuel(final ItemStack stack) {
        final ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return id != null && id.getPath().contains("coke");
    }

    private boolean isInputFluidAllowed(final FluidStack stack) {
        if (stack.isEmpty()) {
            return true;
        }

        if (this.isSteamFluid(stack) || this.isSpentSteamFluid(stack) || this.isSmokeFluid(stack)) {
            return false;
        }

        final ItemStack idStack = this.getInternalItemHandler().getStackInSlot(SLOT_FLUID_ID);
        if (!(idStack.getItem() instanceof final IItemFluidIdentifier identifier)) {
            return true;
        }

        final ResourceLocation fluidId = identifier.getFluidId(idStack);
        if (fluidId == null) {
            return true;
        }

        final ResourceLocation stackId = ForgeRegistries.FLUIDS.getKey(stack.getFluid());
        return fluidId.equals(stackId);
    }

    private boolean isSteamFluid(final FluidStack stack) {
        return !stack.isEmpty() && stack.getFluid().isSame(HbmFluids.STEAM.getStillFluid());
    }

    private boolean isSpentSteamFluid(final FluidStack stack) {
        return !stack.isEmpty() && stack.getFluid().isSame(HbmFluids.SPENTSTEAM.getStillFluid());
    }

    private boolean isSmokeFluid(final FluidStack stack) {
        return !stack.isEmpty() && stack.getFluid().isSame(HbmFluids.SMOKE.getStillFluid());
    }

    private @Nullable HbmMaterialDefinition outputMaterial() {
        if (this.outputMaterialId == null || this.outputMaterialId.isBlank()) {
            return null;
        }
        return MATERIALS_BY_ID.get(this.outputMaterialId);
    }

    @Override
    public boolean isItemValid(final int slot, final @NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        if (slot == SLOT_FLUID_ID) {
            return stack.getItem() instanceof IItemFluidIdentifier;
        }

        if (slot == SLOT_FUEL) {
            return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
        }

        if (slot == SLOT_INPUT_1 || slot == SLOT_INPUT_2 || slot == SLOT_INPUT_3) {
            return HbmRotaryFurnaceRecipes.all().stream()
                .flatMap(recipe -> recipe.ingredients().stream())
                .anyMatch(ingredient -> ingredient.ingredient().test(stack));
        }

        return false;
    }

    @Override
    public boolean canInsertIntoSlot(final int slot, final @NotNull ItemStack stack, final @Nullable Direction side) {
        return this.isItemValid(slot, stack);
    }

    @Override
    public boolean canExtractFromSlot(final int slot, final @Nullable Direction side) {
        return false;
    }

    @Override
    public int[] getAccessibleSlots(final @Nullable Direction side) {
        return SLOT_ACCESS;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(HbmBlocks.MACHINE_ROTARY_FURNACE.get().getDescriptionId());
    }

    @Override
    public AbstractContainerMenu createMenu(final int containerId, final @NotNull Inventory inventory, final @NotNull Player player) {
        return new RotaryFurnaceMenu(containerId, inventory, this);
    }

    public float getProgress() {
        return this.progress;
    }

    public int getBurnTime() {
        return this.burnTime;
    }

    public int getMaxBurnTime() {
        return this.maxBurnTime;
    }

    public float getBurnHeat() {
        return this.burnHeat;
    }

    public int getSteamUsed() {
        return this.steamUsed;
    }

    public int getOutputAmount() {
        return this.outputAmount;
    }

    public int getMaxOutputAmount() {
        return Math.max(HbmRotaryFurnaceRecipes.QUANTA_PER_INGOT, RotaryFurnaceMachineConfig.INSTANCE.maxOutputAmount());
    }

    public String getOutputMaterialName() {
        final HbmMaterialDefinition material = this.outputMaterial();
        return material == null ? "" : material.displayName();
    }

    public int getOutputColor() {
        final HbmMaterialDefinition material = this.outputMaterial();
        return material == null ? 0xFFFFFFFF : 0xFF000000 | LegacyMaterialColors.sharedPartTint(material);
    }

    public int getInputFluidAmount() {
        final HbmFluidTank tank = this.getFluidTank(TANK_INPUT);
        return tank == null ? 0 : tank.getFluidAmount();
    }

    public int getInputFluidCapacity() {
        final HbmFluidTank tank = this.getFluidTank(TANK_INPUT);
        return tank == null ? Math.max(1, RotaryFurnaceMachineConfig.INSTANCE.inputTankCapacity()) : tank.getCapacity();
    }

    public String getInputFluidName() {
        final HbmFluidTank tank = this.getFluidTank(TANK_INPUT);
        return tank == null || tank.isEmpty() ? "Empty" : tank.getFluid().getDisplayName().getString();
    }

    public int getSteamAmount() {
        final HbmFluidTank tank = this.getFluidTank(TANK_STEAM);
        return tank == null ? 0 : tank.getFluidAmount();
    }

    public int getSteamCapacity() {
        final HbmFluidTank tank = this.getFluidTank(TANK_STEAM);
        return tank == null ? Math.max(1, RotaryFurnaceMachineConfig.INSTANCE.steamTankCapacity()) : tank.getCapacity();
    }

    public int getSpentSteamAmount() {
        final HbmFluidTank tank = this.getFluidTank(TANK_SPENT_STEAM);
        return tank == null ? 0 : tank.getFluidAmount();
    }

    public int getSpentSteamCapacity() {
        final HbmFluidTank tank = this.getFluidTank(TANK_SPENT_STEAM);
        return tank == null ? Math.max(1, RotaryFurnaceMachineConfig.INSTANCE.spentSteamTankCapacity()) : tank.getCapacity();
    }

    public boolean hasOutputAcceptor() {
        if (this.level == null) {
            return false;
        }
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    final BlockPos candidate = this.getOutputPos().offset(x, y, z);
                    if (this.level.getBlockState(candidate).getBlock() instanceof ICrucibleAcceptor) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected void saveMachineData(final @NotNull CompoundTag tag) {
        super.saveMachineData(tag);
        tag.putFloat("progress", this.progress);
        tag.putFloat("burnHeat", this.burnHeat);
        tag.putInt("burnTime", this.burnTime);
        tag.putInt("maxBurnTime", this.maxBurnTime);
        tag.putInt("steamUsed", this.steamUsed);
        tag.putString("outputMaterial", this.outputMaterialId == null ? "" : this.outputMaterialId);
        tag.putInt("outputAmount", this.outputAmount);
    }

    @Override
    protected void loadMachineData(final @NotNull CompoundTag tag) {
        super.loadMachineData(tag);
        this.progress = Math.max(0.0F, tag.getFloat("progress"));
        this.burnHeat = Math.max(1.0F, tag.getFloat("burnHeat"));
        this.burnTime = Math.max(0, tag.getInt("burnTime"));
        this.maxBurnTime = Math.max(0, tag.getInt("maxBurnTime"));
        this.steamUsed = Math.max(0, tag.getInt("steamUsed"));
        this.outputMaterialId = tag.getString("outputMaterial");
        this.outputAmount = Math.max(0, tag.getInt("outputAmount"));
        if (this.outputAmount <= 0) {
            this.outputMaterialId = "";
        }
    }

    @Override
    protected void writeAdditionalMachineStateSync(final CompoundTag tag) {
        tag.putInt("progressScaled", Math.max(0, Math.min(10_000, Math.round(this.progress * 10_000.0F))));
        tag.putInt("burnTime", this.burnTime);
        tag.putInt("maxBurnTime", this.maxBurnTime);
        tag.putInt("burnHeatScaled", Math.round(this.burnHeat * 100.0F));
        tag.putInt("steamUsed", this.steamUsed);

        final HbmFluidTank steam = this.getFluidTank(TANK_STEAM);
        tag.putInt("steamAmount", steam == null ? 0 : steam.getFluidAmount());
        tag.putInt("steamCapacity", steam == null ? RotaryFurnaceMachineConfig.INSTANCE.steamTankCapacity() : steam.getCapacity());

        final HbmFluidTank spentSteam = this.getFluidTank(TANK_SPENT_STEAM);
        tag.putInt("spentSteamAmount", spentSteam == null ? 0 : spentSteam.getFluidAmount());
        tag.putInt("spentSteamCapacity", spentSteam == null ? RotaryFurnaceMachineConfig.INSTANCE.spentSteamTankCapacity() : spentSteam.getCapacity());

        final HbmFluidTank input = this.getFluidTank(TANK_INPUT);
        tag.putInt("inputFluidAmount", input == null ? 0 : input.getFluidAmount());
        tag.putInt("inputFluidCapacity", input == null ? RotaryFurnaceMachineConfig.INSTANCE.inputTankCapacity() : input.getCapacity());
        tag.putString("inputFluidName", input == null || input.isEmpty() ? "Empty" : input.getFluid().getDisplayName().getString());

        tag.putString("outputMaterialName", this.getOutputMaterialName());
        tag.putInt("outputAmount", this.outputAmount);
        tag.putInt("maxOutput", this.getMaxOutputAmount());
        tag.putInt("outputColor", this.getOutputColor());
        tag.putBoolean("hasOutputAcceptor", this.hasOutputAcceptor());
    }

    @Override
    protected void readMachineStateSync(final CompoundTag tag) {
        this.progress = Math.max(0.0F, tag.getInt("progressScaled") / 10_000.0F);
        this.burnTime = Math.max(0, tag.getInt("burnTime"));
        this.maxBurnTime = Math.max(0, tag.getInt("maxBurnTime"));
        this.burnHeat = Math.max(1.0F, tag.getInt("burnHeatScaled") / 100.0F);
        this.steamUsed = Math.max(0, tag.getInt("steamUsed"));

        this.outputAmount = Math.max(0, tag.getInt("outputAmount"));
        if (this.outputAmount <= 0) {
            this.outputMaterialId = "";
        }
    }
}
