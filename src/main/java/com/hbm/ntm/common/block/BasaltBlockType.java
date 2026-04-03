package com.hbm.ntm.common.block;

public enum BasaltBlockType {
    BASALT("basalt", "Basalt", true),
    BASALT_SMOOTH("basalt_smooth", "Smooth Basalt", false),
    BASALT_POLISHED("basalt_polished", "Polished Basalt", false),
    BASALT_BRICK("basalt_brick", "Basalt Bricks", false),
    BASALT_TILES("basalt_tiles", "Basalt Tiles", false);

    private final String blockId;
    private final String displayName;
    private final boolean pillar;

    BasaltBlockType(final String blockId, final String displayName, final boolean pillar) {
        this.blockId = blockId;
        this.displayName = displayName;
        this.pillar = pillar;
    }

    public String blockId() {
        return this.blockId;
    }

    public String displayName() {
        return this.displayName;
    }

    public boolean pillar() {
        return this.pillar;
    }
}
