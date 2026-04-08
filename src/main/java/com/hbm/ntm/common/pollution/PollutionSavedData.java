package com.hbm.ntm.common.pollution;

import com.hbm.ntm.HbmNtmMod;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

@Mod.EventBusSubscriber(modid = HbmNtmMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
@SuppressWarnings("null")
public final class PollutionSavedData extends SavedData {
    private static final String DATA_NAME = "hbmpollution";
    private static final int CELL_SHIFT = 6;
    private static final float EPSILON = 0.0001F;
    private static int tickCounter;
    private final Map<Long, PollutionCell> pollution = new HashMap<>();

    public static PollutionSavedData get(final ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(PollutionSavedData::load, PollutionSavedData::new, DATA_NAME);
    }

    public static void incrementPollution(final Level level, final int x, final int y, final int z, final PollutionType type, final float amount) {
        if (!(level instanceof final ServerLevel serverLevel) || amount == 0.0F) {
            return;
        }
        get(serverLevel).incrementCell(x >> CELL_SHIFT, z >> CELL_SHIFT, type, amount);
    }

    public static float getPollution(final Level level, final int x, final int y, final int z, final PollutionType type) {
        if (!(level instanceof final ServerLevel serverLevel)) {
            return 0.0F;
        }
        return get(serverLevel).getCellValue(x >> CELL_SHIFT, z >> CELL_SHIFT, type);
    }

    public static PollutionSavedData load(final CompoundTag tag) {
        final PollutionSavedData data = new PollutionSavedData();
        final ListTag cells = tag.getList("cells", CompoundTag.TAG_COMPOUND);
        for (int i = 0; i < cells.size(); i++) {
            final CompoundTag cellTag = cells.getCompound(i);
            final PollutionCell cell = new PollutionCell(
                cellTag.getFloat("soot"),
                cellTag.getFloat("heavy_metal"),
                cellTag.getFloat("poison"));
            if (!cell.isEmpty()) {
                data.pollution.put(key(cellTag.getInt("x"), cellTag.getInt("z")), cell);
            }
        }
        return data;
    }

    @Override
    public CompoundTag save(final CompoundTag tag) {
        final ListTag cells = new ListTag();
        for (final Map.Entry<Long, PollutionCell> entry : this.pollution.entrySet()) {
            if (entry.getValue().isEmpty()) {
                continue;
            }
            final CompoundTag cellTag = new CompoundTag();
            cellTag.putInt("x", x(entry.getKey()));
            cellTag.putInt("z", z(entry.getKey()));
            cellTag.putFloat("soot", entry.getValue().soot);
            cellTag.putFloat("heavy_metal", entry.getValue().heavyMetal);
            cellTag.putFloat("poison", entry.getValue().poison);
            cells.add(cellTag);
        }
        tag.put("cells", cells);
        return tag;
    }

    @SubscribeEvent
    public static void onServerTick(final TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        tickCounter++;
        if (tickCounter < 60) {
            return;
        }
        tickCounter = 0;
        if (ServerLifecycleHooks.getCurrentServer() == null) {
            return;
        }
        for (final ServerLevel level : ServerLifecycleHooks.getCurrentServer().getAllLevels()) {
            get(level).tick();
        }
    }

    private void incrementCell(final int x, final int z, final PollutionType type, final float amount) {
        final long key = key(x, z);
        final PollutionCell cell = this.pollution.computeIfAbsent(key, ignored -> new PollutionCell());
        switch (type) {
            case SOOT -> cell.soot = Math.max(0.0F, cell.soot + amount);
            case HEAVY_METAL -> cell.heavyMetal = Math.max(0.0F, cell.heavyMetal + amount);
            case POISON -> cell.poison = Math.max(0.0F, cell.poison + amount);
        }
        if (cell.isEmpty()) {
            this.pollution.remove(key);
        }
        this.setDirty();
    }

    private float getCellValue(final int x, final int z, final PollutionType type) {
        final PollutionCell cell = this.pollution.get(key(x, z));
        if (cell == null) {
            return 0.0F;
        }
        return switch (type) {
            case SOOT -> cell.soot;
            case HEAVY_METAL -> cell.heavyMetal;
            case POISON -> cell.poison;
        };
    }

    private void tick() {
        if (this.pollution.isEmpty()) {
            return;
        }
        final Map<Long, PollutionCell> next = new HashMap<>();
        for (final Map.Entry<Long, PollutionCell> entry : this.pollution.entrySet()) {
            final PollutionCell source = entry.getValue().copy();
            float spreadSoot = 0.0F;
            float spreadPoison = 0.0F;

            if (source.soot > 10.0F) {
                spreadSoot = source.soot * 0.05F;
                source.soot *= 0.8F;
            }
            source.soot *= 0.99F;
            source.heavyMetal *= 0.9995F;

            if (source.poison > 10.0F) {
                spreadPoison = source.poison * 0.025F;
                source.poison *= 0.9F;
            } else {
                source.poison *= 0.995F;
            }

            add(next, entry.getKey(), source.soot, source.heavyMetal, source.poison);
            add(next, offset(entry.getKey(), 1, 0), spreadSoot, 0.0F, spreadPoison);
            add(next, offset(entry.getKey(), -1, 0), spreadSoot, 0.0F, spreadPoison);
            add(next, offset(entry.getKey(), 0, 1), spreadSoot, 0.0F, spreadPoison);
            add(next, offset(entry.getKey(), 0, -1), spreadSoot, 0.0F, spreadPoison);
        }
        this.pollution.clear();
        this.pollution.putAll(next);
        final Iterator<Map.Entry<Long, PollutionCell>> iterator = this.pollution.entrySet().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getValue().isEmpty()) {
                iterator.remove();
            }
        }
        this.setDirty();
    }

    private static void add(final Map<Long, PollutionCell> target, final long key, final float soot, final float heavyMetal, final float poison) {
        if (Math.abs(soot) <= EPSILON && Math.abs(heavyMetal) <= EPSILON && Math.abs(poison) <= EPSILON) {
            return;
        }
        final PollutionCell cell = target.computeIfAbsent(key, ignored -> new PollutionCell());
        cell.soot = Math.max(0.0F, cell.soot + soot);
        cell.heavyMetal = Math.max(0.0F, cell.heavyMetal + heavyMetal);
        cell.poison = Math.max(0.0F, cell.poison + poison);
    }

    private static long key(final int x, final int z) {
        return ((long) x << 32) | (z & 0xFFFFFFFFL);
    }

    private static int x(final long key) {
        return (int) (key >> 32);
    }

    private static int z(final long key) {
        return (int) key;
    }

    private static long offset(final long key, final int offsetX, final int offsetZ) {
        return key(x(key) + offsetX, z(key) + offsetZ);
    }

    private static final class PollutionCell {
        private float soot;
        private float heavyMetal;
        private float poison;

        private PollutionCell() {
        }

        private PollutionCell(final float soot, final float heavyMetal, final float poison) {
            this.soot = soot;
            this.heavyMetal = heavyMetal;
            this.poison = poison;
        }

        private PollutionCell copy() {
            return new PollutionCell(this.soot, this.heavyMetal, this.poison);
        }

        private boolean isEmpty() {
            return this.soot <= EPSILON && this.heavyMetal <= EPSILON && this.poison <= EPSILON;
        }
    }
}
