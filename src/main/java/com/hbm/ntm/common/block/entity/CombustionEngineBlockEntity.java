package com.hbm.ntm.common.block.entity;

import com.hbm.ntm.common.energy.EnergyConnectionMode;
import com.hbm.ntm.common.energy.EnergyNetworkDistributor;
import com.hbm.ntm.common.fluid.CombustibleFuelRegistry;
import com.hbm.ntm.common.fluid.CombustibleFuelRegistry.FuelData;
import com.hbm.ntm.common.fluid.HbmFluidTank;
import com.hbm.ntm.common.item.BatteryItem;
import com.hbm.ntm.common.item.IItemFluidIdentifier;
import com.hbm.ntm.common.item.PistonSetItem;
import com.hbm.ntm.common.menu.CombustionEngineMenu;
import com.hbm.ntm.common.pollution.PollutionType;
import com.hbm.ntm.common.registration.HbmBlockEntityTypes;
import com.hbm.ntm.common.registration.HbmBlocks;
import com.hbm.ntm.common.registration.HbmFluids;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public class CombustionEngineBlockEntity extends MachineBlockEntity {
    public static final int SLOT_FUEL_IN = 0;
    public static final int SLOT_FUEL_OUT = 1;
    public static final int SLOT_PISTON = 2;
    public static final int SLOT_BATTERY = 3;
    public static final int SLOT_ID = 4;
    public static final int SLOT_COUNT = 5;

    public static final int TANK_FUEL = 0;
    public static final int TANK_SMOKE = 1;
    public static final int TANK_SMOKE_LEADED = 2;
    public static final int TANK_SMOKE_POISON = 3;
    public static final int TANK_CAPACITY = 24_000;
    public static final int POWER_CAPACITY = 2_500_000;
    private static final int SMOKE_BUFFER_CAPACITY = 50;

    private static final int[] ACCESSIBLE_SLOTS = {SLOT_FUEL_IN, SLOT_FUEL_OUT, SLOT_PISTON, SLOT_BATTERY, SLOT_ID};

    private @Nullable ResourceLocation configuredFuelId;
    private boolean engineOn;
    private int setting;
    private int tenthBuffer;
    private boolean wasOn;
    private int lastGeneration;
    private @Nullable Fluid lastBurnedFuel;

    public CombustionEngineBlockEntity(final BlockPos pos, final BlockState state) {
        super(HbmBlockEntityTypes.MACHINE_COMBUSTION_ENGINE.get(), pos, state, SLOT_COUNT);
    }

    public static void serverTick(final Level level, final BlockPos pos, final BlockState state, final CombustionEngineBlockEntity machine) {
        machine.tickServer(level, pos, state);
    }

    @Override
    protected @Nullable com.hbm.ntm.common.energy.HbmEnergyStorage createEnergyStorage() {
        return this.createSimpleEnergyStorage(POWER_CAPACITY, 0, POWER_CAPACITY);
    }

    @Override
    protected HbmFluidTank[] createFluidTanks() {
        return new HbmFluidTank[] {
            this.createFluidTank(TANK_CAPACITY, this::isFuelFluid),
            this.createFluidTank(SMOKE_BUFFER_CAPACITY, this::isSmokeFluid),
            this.createFluidTank(SMOKE_BUFFER_CAPACITY, this::isLeadedSmokeFluid),
            this.createFluidTank(SMOKE_BUFFER_CAPACITY, this::isPoisonSmokeFluid)
        };
    }

    @Override
    protected EnergyConnectionMode getEnergyConnectionMode(final @Nullable Direction side) {
        return side == Direction.DOWN ? EnergyConnectionMode.NONE : EnergyConnectionMode.EXTRACT;
    }

    @Override
    public boolean isItemValid(final int slot, final @NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return switch (slot) {
            case SLOT_FUEL_IN -> this.canEmptyIntoFuelTank(stack);
            case SLOT_PISTON -> stack.getItem() instanceof PistonSetItem;
            case SLOT_BATTERY -> stack.getItem() instanceof BatteryItem;
            case SLOT_ID -> stack.getItem() instanceof IItemFluidIdentifier;
            default -> false;
        };
    }

    @Override
    public boolean canInsertIntoSlot(final int slot, final @NotNull ItemStack stack, final @Nullable Direction side) {
        return this.isItemValid(slot, stack);
    }

    @Override
    public boolean canExtractFromSlot(final int slot, final @Nullable Direction side) {
        if (slot == SLOT_FUEL_OUT) {
            return true;
        }
        if (slot == SLOT_BATTERY) {
            final ItemStack battery = this.getInternalItemHandler().getStackInSlot(SLOT_BATTERY);
            return this.isBatteryFull(battery);
        }
        return false;
    }

    @Override
    public int[] getAccessibleSlots(final @Nullable Direction side) {
        return ACCESSIBLE_SLOTS;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(HbmBlocks.MACHINE_COMBUSTION_ENGINE.get().getDescriptionId());
    }

    @Override
    public AbstractContainerMenu createMenu(final int containerId, final @NotNull Inventory inventory, final @NotNull Player player) {
        return new CombustionEngineMenu(containerId, inventory, this);
    }

    @Override
    protected Set<String> allowedControlKeys() {
        return Set.of("repair", "turnOn", "setting");
    }

    @Override
    protected void applyControlData(final CompoundTag data) {
        if (data.getBoolean("turnOn")) {
            this.engineOn = !this.engineOn;
        }
        if (data.contains("setting")) {
            this.setting = Mth.clamp(data.getInt("setting"), 0, 30);
        }
    }

    public boolean isEngineOn() {
        return this.engineOn;
    }

    public boolean wasOn() {
        return this.wasOn;
    }

    public int getSetting() {
        return this.setting;
    }

    public int getFuelAmount() {
        final HbmFluidTank tank = this.getFluidTank(TANK_FUEL);
        return tank == null ? 0 : tank.getFluidAmount();
    }

    public int getFuelCapacity() {
        final HbmFluidTank tank = this.getFluidTank(TANK_FUEL);
        return tank == null ? TANK_CAPACITY : tank.getCapacity();
    }

    public String getFuelName() {
        final HbmFluidTank tank = this.getFluidTank(TANK_FUEL);
        if (tank == null || tank.isEmpty()) {
            return "Empty";
        }
        return tank.getFluid().getDisplayName().getString();
    }

    public int getLastGeneration() {
        return this.lastGeneration;
    }

    public int getPistonTierIndex() {
        final PistonSetItem.Tier tier = this.getPistonTier();
        if (tier == null) {
            return -1;
        }
        return switch (tier) {
            case STEEL -> 0;
            case DURA -> 1;
            case DESH -> 2;
            case STARMETAL -> 3;
        };
    }

    @Override
    protected void saveMachineData(final @NotNull CompoundTag tag) {
        if (this.configuredFuelId != null) {
            tag.putString("configuredFuelId", this.configuredFuelId.toString());
        }
        tag.putBoolean("engineOn", this.engineOn);
        tag.putBoolean("wasOn", this.wasOn);
        tag.putInt("setting", this.setting);
        tag.putInt("tenthBuffer", this.tenthBuffer);
        tag.putInt("lastGeneration", this.lastGeneration);
    }

    @Override
    protected void loadMachineData(final @NotNull CompoundTag tag) {
        this.configuredFuelId = tag.contains("configuredFuelId") ? ResourceLocation.tryParse(tag.getString("configuredFuelId")) : null;
        this.engineOn = tag.getBoolean("engineOn");
        this.wasOn = tag.getBoolean("wasOn");
        this.setting = Mth.clamp(tag.getInt("setting"), 0, 30);
        this.tenthBuffer = Mth.clamp(tag.getInt("tenthBuffer"), 0, 9);
        this.lastGeneration = Math.max(0, tag.getInt("lastGeneration"));
    }

    @Override
    protected void writeAdditionalMachineStateSync(final CompoundTag tag) {
        tag.putBoolean("engineOn", this.engineOn);
        tag.putBoolean("wasOn", this.wasOn);
        tag.putInt("setting", this.setting);
        tag.putInt("fuel", this.getFuelAmount());
        tag.putInt("fuelCapacity", this.getFuelCapacity());
        tag.putString("fuelName", this.getFuelName());
        tag.putInt("energy", this.getStoredEnergy());
        tag.putInt("maxEnergy", this.getMaxStoredEnergy());
        tag.putInt("generation", this.lastGeneration);
        tag.putInt("pistonTier", this.getPistonTierIndex());
    }

    private void tickServer(final Level level, final BlockPos pos, final BlockState state) {
        this.handleFuelIdentifier();
        this.handleFuelContainerSlots();

        this.wasOn = false;
        this.lastGeneration = 0;

        if (this.engineOn && this.setting > 0) {
            this.lastGeneration = this.generatePower();
            this.wasOn = this.lastGeneration > 0;
        }
        if (this.wasOn && level.getGameTime() % 5L == 0L) {
            this.bufferFuelBurnPollution(this.lastBurnedFuel);
        }

        this.chargeBatterySlot();
        this.pushEnergy(level, pos);
        this.transferSmokeToNeighbors();
        this.updateLitState(level, pos, state, this.wasOn);
        this.tickMachineStateSync();
    }

    private void handleFuelIdentifier() {
        final ItemStack stack = this.getInternalItemHandler().getStackInSlot(SLOT_ID);
        if (!(stack.getItem() instanceof final IItemFluidIdentifier identifier)) {
            return;
        }
        final ResourceLocation fuelId = identifier.getFluidId(stack);
        if (fuelId == null || fuelId.equals(this.configuredFuelId)) {
            return;
        }

        this.configuredFuelId = fuelId;

        final HbmFluidTank tank = this.getFluidTank(TANK_FUEL);
        if (tank != null && !tank.isEmpty()) {
            final ResourceLocation currentId = ForgeRegistries.FLUIDS.getKey(tank.getFluid().getFluid());
            if (currentId != null && !currentId.equals(fuelId)) {
                tank.setFluidStack(FluidStack.EMPTY);
                this.tenthBuffer = 0;
            }
        }
    }

    private void handleFuelContainerSlots() {
        final ItemStack input = this.getInternalItemHandler().getStackInSlot(SLOT_FUEL_IN);
        if (input.isEmpty()) {
            return;
        }
        final HbmFluidTank tank = this.getFluidTank(TANK_FUEL);
        if (tank == null) {
            return;
        }

        final net.minecraftforge.fluids.FluidActionResult result = FluidUtil.tryEmptyContainer(input.copyWithCount(1), tank, Integer.MAX_VALUE, null, true);
        if (!result.isSuccess() || !this.canMoveToOutput(SLOT_FUEL_OUT, result.getResult())) {
            return;
        }

        this.getInternalItemHandler().extractItem(SLOT_FUEL_IN, 1, false);
        this.insertOutput(SLOT_FUEL_OUT, result.getResult());

        if (this.configuredFuelId == null && !tank.isEmpty()) {
            this.configuredFuelId = ForgeRegistries.FLUIDS.getKey(tank.getFluid().getFluid());
        }
    }

    private int generatePower() {
        final HbmFluidTank tank = this.getFluidTank(TANK_FUEL);
        final PistonSetItem.Tier pistonTier = this.getPistonTier();
        this.lastBurnedFuel = null;
        if (tank == null || tank.isEmpty() || pistonTier == null) {
            return 0;
        }
        final FluidStack fuel = tank.getFluid().copy();

        final FuelData fuelData = CombustibleFuelRegistry.dataFor(tank.getFluid().getFluid());
        if (fuelData == null) {
            return 0;
        }

        final double efficiency = pistonTier.efficiency(fuelData.grade());
        if (efficiency <= 0.0D) {
            return 0;
        }

        int fuelTenths = tank.getFluidAmount() * 10 + this.tenthBuffer;
        if (fuelTenths <= 0) {
            return 0;
        }

        final int speed = this.setting * 2;
        final int toBurn = Math.min(fuelTenths, speed);
        if (toBurn <= 0) {
            return 0;
        }

        final int generated = (int) (toBurn * (fuelData.combustionEnergy() / 10_000.0D) * efficiency);
        if (generated <= 0) {
            return 0;
        }

        final IEnergyStorage storage = this.getEnergyStorage(null);
        if (storage != null) {
            storage.receiveEnergy(generated, false);
        }

        fuelTenths -= toBurn;
        final int newWhole = fuelTenths / 10;
        this.tenthBuffer = fuelTenths % 10;
        final int toDrain = tank.getFluidAmount() - newWhole;
        if (toDrain > 0) {
            tank.drain(toDrain, FluidAction.EXECUTE);
        }

        this.lastBurnedFuel = fuel.getFluid();

        return generated;
    }

    private void bufferFuelBurnPollution(final @Nullable Fluid fuel) {
        if (fuel == null) {
            return;
        }
        final CombustibleFuelRegistry.BurnPollution pollution = CombustibleFuelRegistry.burnPollutionFor(fuel);
        if (pollution.isEmpty()) {
            return;
        }

        this.bufferPollutionIntoSmokeTank(TANK_SMOKE, PollutionType.SOOT, HbmFluids.SMOKE.getStillFluid(), pollution.soot());
        this.bufferPollutionIntoSmokeTank(TANK_SMOKE_LEADED, PollutionType.HEAVY_METAL, HbmFluids.SMOKE_LEADED.getStillFluid(), pollution.heavyMetal());
        this.bufferPollutionIntoSmokeTank(TANK_SMOKE_POISON, PollutionType.POISON, HbmFluids.SMOKE_POISON.getStillFluid(), pollution.poison());
    }

    private void transferSmokeToNeighbors() {
        this.exportTankToNeighbors(TANK_SMOKE);
        this.exportTankToNeighbors(TANK_SMOKE_LEADED);
        this.exportTankToNeighbors(TANK_SMOKE_POISON);
    }

    private boolean chargeBatterySlot() {
        final ItemStack batteryStack = this.getInternalItemHandler().getStackInSlot(SLOT_BATTERY);
        if (!(batteryStack.getItem() instanceof final BatteryItem batteryItem)) {
            return false;
        }

        final IEnergyStorage storage = this.getEnergyStorage(null);
        if (storage == null) {
            return false;
        }

        final int available = storage.extractEnergy(Integer.MAX_VALUE, true);
        if (available <= 0) {
            return false;
        }

        final int stored = batteryItem.getStoredEnergy(batteryStack);
        final int room = batteryItem.getCapacity() - stored;
        if (room <= 0) {
            return false;
        }

        final int transfer = Math.min(Math.min(available, room), batteryItem.getChargeRate());
        if (transfer <= 0) {
            return false;
        }

        storage.extractEnergy(transfer, false);
        batteryItem.withStoredEnergy(batteryStack, stored + transfer);
        this.getInternalItemHandler().setStackInSlot(SLOT_BATTERY, batteryStack);
        return true;
    }

    private int pushEnergy(final Level level, final BlockPos pos) {
        final IEnergyStorage storage = this.getEnergyStorage(null);
        if (storage == null) {
            return 0;
        }

        final int available = Math.min(storage.getEnergyStored(), storage.extractEnergy(Integer.MAX_VALUE, true));
        if (available <= 0) {
            return 0;
        }

        final int transferred = EnergyNetworkDistributor.distribute(level, pos, available, available, null);
        if (transferred > 0) {
            storage.extractEnergy(transferred, false);
        }
        return transferred;
    }

    private void updateLitState(final Level level, final BlockPos pos, final BlockState state, final boolean active) {
        if (!state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT) || state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT) == active) {
            return;
        }
        level.setBlock(pos, state.setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT, active), 3);
    }

    private boolean isFuelFluid(final FluidStack stack) {
        if (stack.isEmpty()) {
            return true;
        }
        final FuelData data = CombustibleFuelRegistry.dataFor(stack.getFluid());
        if (data == null) {
            return false;
        }
        final ResourceLocation fluidId = ForgeRegistries.FLUIDS.getKey(stack.getFluid());
        if (fluidId == null) {
            return false;
        }
        return this.configuredFuelId == null || this.configuredFuelId.equals(fluidId);
    }

    private PistonSetItem.@Nullable Tier getPistonTier() {
        final ItemStack stack = this.getInternalItemHandler().getStackInSlot(SLOT_PISTON);
        if (stack.getItem() instanceof final PistonSetItem pistonSetItem) {
            return pistonSetItem.getTier();
        }
        return null;
    }

    private boolean isSmokeFluid(final FluidStack stack) {
        return stack.isEmpty() || stack.getFluid() == HbmFluids.SMOKE.getStillFluid();
    }

    private boolean isLeadedSmokeFluid(final FluidStack stack) {
        return stack.isEmpty() || stack.getFluid() == HbmFluids.SMOKE_LEADED.getStillFluid();
    }

    private boolean isPoisonSmokeFluid(final FluidStack stack) {
        return stack.isEmpty() || stack.getFluid() == HbmFluids.SMOKE_POISON.getStillFluid();
    }

    private boolean canEmptyIntoFuelTank(final ItemStack stack) {
        final LazyOptional<IFluidHandlerItem> optional = FluidUtil.getFluidHandler(stack.copyWithCount(1));
        if (!optional.isPresent()) {
            return false;
        }
        final IFluidHandlerItem fluidHandler = optional.resolve().orElse(null);
        if (fluidHandler == null || fluidHandler.getTanks() <= 0) {
            return false;
        }
        final FluidStack contained = fluidHandler.drain(Integer.MAX_VALUE, FluidAction.SIMULATE);
        return !contained.isEmpty() && this.isFuelFluid(contained);
    }

    private boolean canMoveToOutput(final int outputSlot, final ItemStack result) {
        final ItemStack existing = this.getInternalItemHandler().getStackInSlot(outputSlot);
        return existing.isEmpty()
            || ItemStack.isSameItemSameTags(existing, result) && existing.getCount() + result.getCount() <= existing.getMaxStackSize();
    }

    private void insertOutput(final int outputSlot, final ItemStack result) {
        final ItemStack existing = this.getInternalItemHandler().getStackInSlot(outputSlot);
        if (existing.isEmpty()) {
            this.getInternalItemHandler().setStackInSlot(outputSlot, result.copy());
            return;
        }
        existing.grow(result.getCount());
        this.getInternalItemHandler().setStackInSlot(outputSlot, existing);
    }

    private boolean isBatteryFull(final ItemStack stack) {
        if (!(stack.getItem() instanceof final BatteryItem batteryItem)) {
            return false;
        }
        return batteryItem.getStoredEnergy(stack) >= batteryItem.getCapacity();
    }
}
