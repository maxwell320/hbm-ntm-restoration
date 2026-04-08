package com.hbm.ntm.common.block.entity;

import com.hbm.ntm.common.block.BarrelBlock;
import com.hbm.ntm.common.block.BarrelType;
import com.hbm.ntm.common.item.BarrelBlockItem;
import com.hbm.ntm.common.item.IItemFluidIdentifier;
import com.hbm.ntm.common.menu.BarrelMenu;
import com.hbm.ntm.common.pollution.PollutionSavedData;
import com.hbm.ntm.common.pollution.PollutionType;
import com.hbm.ntm.common.registration.HbmBlockEntityTypes;
import com.hbm.ntm.common.registration.HbmBlocks;
import com.hbm.ntm.common.saveddata.TomImpactSavedData;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public class BarrelBlockEntity extends BlockEntity implements MenuProvider {
    private static final int SLOT_TYPE_IN = 0;
    private static final int SLOT_TYPE_OUT = 1;
    private static final int SLOT_LOAD_IN = 2;
    private static final int SLOT_LOAD_OUT = 3;
    private static final int SLOT_UNLOAD_IN = 4;
    private static final int SLOT_UNLOAD_OUT = 5;
    private final com.hbm.ntm.common.fluid.HbmFluidTank tank;
    private final ItemStackHandler items = new ItemStackHandler(6) {
        @Override
        protected void onContentsChanged(final int slot) {
            BarrelBlockEntity.this.setChanged();
        }
    };
    private final Map<Direction, LazyOptional<IFluidHandler>> sidedFluidCapabilities = new EnumMap<>(Direction.class);
    private final Map<Direction, LazyOptional<IItemHandler>> sidedItemCapabilities = new EnumMap<>(Direction.class);
    private LazyOptional<IFluidHandler> tankCapability = LazyOptional.empty();
    private LazyOptional<IItemHandler> itemCapability = LazyOptional.empty();
    private @Nullable ResourceLocation configuredFluidId;
    private int mode;

    public BarrelBlockEntity(final BlockPos pos, final BlockState state) {
        super(HbmBlockEntityTypes.BARREL.get(), pos, state);
        this.tank = new com.hbm.ntm.common.fluid.HbmFluidTank(this.capacity(), this::isFluidValidForBarrel, this::onContentsChanged);
    }

    public static void serverTick(final Level level, final BlockPos pos, final BlockState state, final BarrelBlockEntity barrel) {
        barrel.tickServer();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.createCapabilities();
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.tankCapability.invalidate();
        this.itemCapability.invalidate();
        this.sidedFluidCapabilities.values().forEach(LazyOptional::invalidate);
        this.sidedItemCapabilities.values().forEach(LazyOptional::invalidate);
        this.sidedFluidCapabilities.clear();
        this.sidedItemCapabilities.clear();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        this.createCapabilities();
    }

    @Override
    protected void saveAdditional(final CompoundTag tag) {
        super.saveAdditional(tag);
        final CompoundTag tankTag = new CompoundTag();
        this.tank.writeToNBT(tankTag);
        tag.put("tank", tankTag);
        tag.put("items", this.items.serializeNBT());
        tag.putInt("mode", this.mode);
        if (this.configuredFluidId != null) {
            tag.putString("configured_fluid", this.configuredFluidId.toString());
        }
    }

    @Override
    public void load(final CompoundTag tag) {
        super.load(tag);
        if (tag.contains("tank", CompoundTag.TAG_COMPOUND)) {
            this.tank.readFromNBT(tag.getCompound("tank"));
        }
        if (tag.contains("items", CompoundTag.TAG_COMPOUND)) {
            this.items.deserializeNBT(tag.getCompound("items"));
        }
        this.mode = tag.getInt("mode") & 3;
        this.configuredFluidId = tag.contains("configured_fluid") ? ResourceLocation.tryParse(tag.getString("configured_fluid")) : null;
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(final CompoundTag tag) {
        this.load(tag);
    }

    @Override
    public void onDataPacket(final Connection connection, final ClientboundBlockEntityDataPacket packet) {
        final CompoundTag tag = packet.getTag();
        if (tag != null) {
            this.load(tag);
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(final @NotNull Capability<T> capability, final @Nullable Direction side) {
        if (capability == ForgeCapabilities.FLUID_HANDLER) {
            return side == null ? this.tankCapability.cast() : this.sidedFluidCapabilities.getOrDefault(side, LazyOptional.empty()).cast();
        }
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return side == null ? this.itemCapability.cast() : this.sidedItemCapabilities.getOrDefault(side, LazyOptional.empty()).cast();
        }
        return super.getCapability(capability, side);
    }

    @Override
    public Component getDisplayName() {
        return this.getBlockState().getBlock().getName();
    }

    @Override
    public AbstractContainerMenu createMenu(final int containerId, final Inventory inventory, final Player player) {
        return new BarrelMenu(containerId, inventory, this, ContainerLevelAccess.create(Objects.requireNonNull(this.level), this.worldPosition), this.worldPosition);
    }

    public ItemStackHandler getItemHandler() {
        return this.items;
    }

    public int getMode() {
        return this.mode;
    }

    public void cycleMode() {
        this.mode = (this.mode + 1) & 3;
        this.invalidateCaps();
        this.reviveCaps();
        this.setChanged();
        this.syncToClient();
    }

    public int getFluidAmount() {
        return this.tank.getFluidAmount();
    }

    public int capacity() {
        return this.barrelType().capacity();
    }

    public FluidStack getFluid() {
        return this.tank.getFluid().copy();
    }

    public @Nullable ResourceLocation getConfiguredFluidId() {
        return this.configuredFluidId;
    }

    public String getConfiguredFluidDisplayName() {
        final FluidStack stack = this.tank.getFluid();
        if (!stack.isEmpty()) {
            return stack.getDisplayName().getString();
        }
        if (this.configuredFluidId != null && net.minecraftforge.registries.ForgeRegistries.FLUIDS.containsKey(this.configuredFluidId)) {
            return new FluidStack(net.minecraftforge.registries.ForgeRegistries.FLUIDS.getValue(this.configuredFluidId), 1).getDisplayName().getString();
        }
        return "Empty";
    }

    public int getComparatorOutput() {
        if (this.tank.getFluidAmount() <= 0 || this.tank.getCapacity() <= 0) {
            return 0;
        }
        return Math.min(15, (int) ((double) this.tank.getFluidAmount() / (double) this.tank.getCapacity() * 15.0D) + 1);
    }

    public void loadFromItem(final ItemStack stack) {
        this.configuredFluidId = BarrelBlockItem.getConfiguredFluidId(stack);
        this.tank.setFluidStack(BarrelBlockItem.getStoredFluid(stack, this.capacity()));
        this.setChanged();
        this.syncToClient();
    }

    public void dropContents() {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }
        for (int i = 0; i < this.items.getSlots(); i++) {
            net.minecraft.world.Containers.dropItemStack(this.level, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), this.items.getStackInSlot(i));
        }
    }

    public boolean stillUsableByPlayer(final Player player) {
        return !this.isRemoved() && player.distanceToSqr(this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 0.5D, this.worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    public void setConfiguredFluidId(@Nullable final ResourceLocation configuredFluidId, final boolean resetTank) {
        this.configuredFluidId = configuredFluidId;
        if (resetTank) {
            this.tank.setFluidStack(FluidStack.EMPTY);
        }
        this.setChanged();
        this.syncToClient();
    }

    private BarrelType barrelType() {
        return this.getBlockState().getBlock() instanceof final BarrelBlock barrelBlock ? barrelBlock.barrelType() : BarrelType.STEEL;
    }

    private void tickServer() {
        this.processFluidTypeItems();
        this.processContainerSlots();
        this.checkFluidInteraction();
    }

    private void processFluidTypeItems() {
        final ItemStack stack = this.items.getStackInSlot(SLOT_TYPE_IN);
        if (!(stack.getItem() instanceof final IItemFluidIdentifier identifier)) {
            return;
        }
        if (!this.items.getStackInSlot(SLOT_TYPE_OUT).isEmpty()) {
            return;
        }
        final ResourceLocation fluidId = identifier.getFluidId(stack);
        if (fluidId == null || fluidId.equals(this.configuredFluidId)) {
            return;
        }
        this.configuredFluidId = fluidId;
        this.tank.setFluidStack(FluidStack.EMPTY);
        this.items.setStackInSlot(SLOT_TYPE_OUT, stack.copy());
        this.items.setStackInSlot(SLOT_TYPE_IN, ItemStack.EMPTY);
        this.setChanged();
        this.syncToClient();
    }

    private void processContainerSlots() {
        this.tryEmptyContainer(SLOT_LOAD_IN, SLOT_LOAD_OUT);
        this.tryFillContainer(SLOT_UNLOAD_IN, SLOT_UNLOAD_OUT);
    }

    private void tryEmptyContainer(final int inputSlot, final int outputSlot) {
        final ItemStack input = this.items.getStackInSlot(inputSlot);
        if (input.isEmpty()) {
            return;
        }
        final FluidActionResult result = FluidUtil.tryEmptyContainer(input.copyWithCount(1), this.tank, Integer.MAX_VALUE, null, true);
        if (!result.isSuccess() || !this.canMoveToOutput(outputSlot, result.getResult())) {
            return;
        }
        this.items.extractItem(inputSlot, 1, false);
        this.insertOutput(outputSlot, result.getResult());
        if (this.configuredFluidId == null && !this.tank.getFluid().isEmpty()) {
            this.configuredFluidId = net.minecraftforge.registries.ForgeRegistries.FLUIDS.getKey(this.tank.getFluid().getFluid());
        }
        this.setChanged();
        this.syncToClient();
    }

    private void tryFillContainer(final int inputSlot, final int outputSlot) {
        final ItemStack input = this.items.getStackInSlot(inputSlot);
        if (input.isEmpty()) {
            return;
        }
        final FluidActionResult result = FluidUtil.tryFillContainer(input.copyWithCount(1), this.tank, Integer.MAX_VALUE, null, true);
        if (!result.isSuccess() || !this.canMoveToOutput(outputSlot, result.getResult())) {
            return;
        }
        this.items.extractItem(inputSlot, 1, false);
        this.insertOutput(outputSlot, result.getResult());
        this.setChanged();
        this.syncToClient();
    }

    private boolean canMoveToOutput(final int outputSlot, final ItemStack result) {
        final ItemStack existing = this.items.getStackInSlot(outputSlot);
        return existing.isEmpty() || ItemStack.isSameItemSameTags(existing, result) && existing.getCount() + result.getCount() <= existing.getMaxStackSize();
    }

    private void insertOutput(final int outputSlot, final ItemStack result) {
        final ItemStack existing = this.items.getStackInSlot(outputSlot);
        if (existing.isEmpty()) {
            this.items.setStackInSlot(outputSlot, result.copy());
            return;
        }
        existing.grow(result.getCount());
        this.items.setStackInSlot(outputSlot, existing);
    }

    private boolean isFluidValidForBarrel(final FluidStack stack) {
        if (stack.isEmpty()) {
            return true;
        }
        final ResourceLocation fluidId = net.minecraftforge.registries.ForgeRegistries.FLUIDS.getKey(stack.getFluid());
        if (fluidId == null) {
            return false;
        }
        return this.configuredFluidId == null || this.configuredFluidId.equals(fluidId);
    }

    private void onContentsChanged() {
        this.setChanged();
        if (this.level != null) {
            this.syncToClient();
            this.level.updateNeighbourForOutputSignal(this.worldPosition, this.getBlockState().getBlock());
        }
    }

    private void syncToClient() {
        if (this.level != null) {
            final BlockState state = this.getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        }
    }

    private void checkFluidInteraction() {
        if (this.level == null || this.tank.getFluid().isEmpty()) {
            return;
        }
        final BarrelType barrelType = this.barrelType();
        final FluidStack stack = this.tank.getFluid();
        final FluidType fluidType = stack.getFluid().getFluidType();
        final int corrosiveRating = fluidType instanceof final com.hbm.ntm.common.fluid.HbmFluidType hbmFluidType ? hbmFluidType.getCorrosiveRating() : 0;
        final boolean antimatter = fluidType instanceof final com.hbm.ntm.common.fluid.HbmFluidType hbmFluidType && hbmFluidType.isAntimatter();
        final boolean hot = fluidType instanceof final com.hbm.ntm.common.fluid.HbmFluidType hbmFluidType ? hbmFluidType.isHot() : fluidType.getTemperature() > 350;

        if (!barrelType.isAntimatter() && antimatter) {
            this.destroyWithExplosion();
            return;
        }
        if (barrelType.isPlastic() && (corrosiveRating > 0 || hot || antimatter)) {
            this.level.removeBlock(this.worldPosition, false);
            this.level.playSound(null, this.worldPosition, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
            return;
        }
        if ((barrelType.isIron() && corrosiveRating > 0) || (barrelType.isSteel() && corrosiveRating > 50)) {
            this.corrodeIntoBarrel();
            return;
        }
        if (barrelType.isCorroded()) {
            if (this.level.random.nextInt(3) == 0) {
                this.tank.setFluidAmount(this.tank.getFluidAmount() - 1);
                this.applyLeakPollution(stack.getFluid());
            }
            if (this.level.random.nextInt(3 * 60 * 20) == 0) {
                this.level.removeBlock(this.worldPosition, false);
            }
        }
        if (stack.getFluid().isSame(Fluids.WATER) && TomImpactSavedData.get(this.level).fire() > 1.0E-5F && this.level.getBrightness(LightLayer.SKY, this.worldPosition) > 7) {
            this.destroyWithExplosion();
        }
    }

    private void applyLeakPollution(final Fluid fluid) {
        final ResourceLocation fluidId = net.minecraftforge.registries.ForgeRegistries.FLUIDS.getKey(fluid);
        if (fluidId == null) {
            return;
        }
        final String path = fluidId.getPath();
        if (path.equals("nitric_acid") || path.equals("watz") || path.equals("phosgene") || path.equals("mustardgas")) {
            PollutionSavedData.incrementPollution(this.level, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), PollutionType.POISON, 0.0005F);
        }
    }

    private void corrodeIntoBarrel() {
        if (this.level == null) {
            return;
        }
        final FluidStack fluid = this.tank.getFluid().copy();
        final CompoundTag itemTag = this.items.serializeNBT();
        final ResourceLocation selectedFluid = this.configuredFluidId;
        final int retainedMode = this.mode;
        this.level.setBlock(this.worldPosition, HbmBlocks.BARREL_CORRODED.get().defaultBlockState(), 3);
        if (this.level.getBlockEntity(this.worldPosition) instanceof final BarrelBlockEntity barrel) {
            barrel.items.deserializeNBT(itemTag);
            barrel.mode = retainedMode;
            barrel.configuredFluidId = selectedFluid;
            fluid.setAmount(Math.min(fluid.getAmount(), barrel.capacity()));
            barrel.tank.setFluidStack(fluid);
            barrel.setChanged();
        }
        this.level.playSound(null, this.worldPosition, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    private void destroyWithExplosion() {
        if (this.level == null) {
            return;
        }
        final double x = this.worldPosition.getX() + 0.5D;
        final double y = this.worldPosition.getY() + 0.5D;
        final double z = this.worldPosition.getZ() + 0.5D;
        this.level.removeBlock(this.worldPosition, false);
        this.level.explode(null, x, y, z, 5.0F, Level.ExplosionInteraction.BLOCK);
    }

    private void createCapabilities() {
        this.tankCapability = LazyOptional.of(() -> this.tank);
        this.itemCapability = LazyOptional.of(() -> this.items);
        this.sidedFluidCapabilities.clear();
        this.sidedItemCapabilities.clear();
        for (final Direction direction : Direction.values()) {
            this.sidedFluidCapabilities.put(direction, LazyOptional.of(() -> new com.hbm.ntm.common.fluid.SidedFluidHandler(this.tank, this.mode == 0 || this.mode == 1, this.mode == 1 || this.mode == 2)));
            this.sidedItemCapabilities.put(direction, LazyOptional.of(() -> this.items));
        }
    }
}
