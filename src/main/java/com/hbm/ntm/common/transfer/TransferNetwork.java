package com.hbm.ntm.common.transfer;

import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;

@SuppressWarnings("null")
public final class TransferNetwork {
    private final TransferNetworkKind kind;
    private final Set<BlockPos> nodes = new LinkedHashSet<>();
    private long lastTransferTick = Long.MIN_VALUE;
    private long transferThisTick;

    public TransferNetwork(final TransferNetworkKind kind) {
        this.kind = kind;
    }

    public TransferNetworkKind kind() {
        return this.kind;
    }

    public Set<BlockPos> nodes() {
        return Set.copyOf(this.nodes);
    }

    public void add(final BlockPos pos) {
        this.nodes.add(pos.immutable());
    }

    public boolean contains(final BlockPos pos) {
        return this.nodes.contains(pos);
    }

    public int size() {
        return this.nodes.size();
    }

    public void recordTransfer(final long gameTime, final long amount) {
        if (amount <= 0) {
            return;
        }
        if (this.lastTransferTick != gameTime) {
            this.lastTransferTick = gameTime;
            this.transferThisTick = 0;
        }
        this.transferThisTick += amount;
    }

    public long getTransferThisTick(final long gameTime) {
        return this.lastTransferTick == gameTime ? this.transferThisTick : 0L;
    }
}
