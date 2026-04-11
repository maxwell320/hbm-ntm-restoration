package api.hbm.tile;

import com.hbm.util.Tuple.Quartet;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface ILoadedTile {

    boolean isLoaded();

    final class TileAccessCache {
        public static final Map<Quartet<Integer, Integer, Integer, ResourceLocation>, TileAccessCache> cache = new HashMap<>();
        public static final int NULL_CACHE = 20;
        public static final int NONNULL_CACHE = 60;

        public final BlockEntity tile;
        public final long expiresOn;

        public TileAccessCache(final BlockEntity tile, final long expiresOn) {
            this.tile = tile;
            this.expiresOn = expiresOn;
        }

        public boolean hasExpired(final long worldTime) {
            if (this.tile != null && this.tile.isRemoved()) {
                return true;
            }
            if (worldTime >= this.expiresOn) {
                return true;
            }
            return this.tile instanceof ILoadedTile loadedTile && !loadedTile.isLoaded();
        }

        public static final Quartet<Integer, Integer, Integer, ResourceLocation> publicCumRag = new Quartet<>(0, 0, 0, ResourceLocation.withDefaultNamespace("overworld"));

        public static BlockEntity getTileOrCache(final Level level, final int x, final int y, final int z) {
            publicCumRag.mangle(x, y, z, level.dimension().location());
            TileAccessCache cacheEntry = cache.get(publicCumRag);
            if (cacheEntry == null || cacheEntry.hasExpired(level.getGameTime())) {
                final BlockEntity tile = level.getBlockEntity(new BlockPos(x, y, z));
                cacheEntry = new TileAccessCache(tile, level.getGameTime() + (tile == null ? NULL_CACHE : NONNULL_CACHE));
                cache.put(publicCumRag.clone(), cacheEntry);
                return tile;
            }
            return cacheEntry.tile;
        }

        public static BlockEntity getTileOrCache(final Level level, final BlockPos pos) {
            return getTileOrCache(level, pos.getX(), pos.getY(), pos.getZ());
        }
    }
}
