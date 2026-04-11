package api.hbm.entity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class RadarEntry {
    public String unlocalizedName;
    public int blipLevel;
    public int posX;
    public int posY;
    public int posZ;
    public int dim;
    public int entityID;
    public boolean redstone;

    public RadarEntry() {
    }

    public RadarEntry(final String name, final int level, final int x, final int y, final int z, final int dim, final int entityID, final boolean redstone) {
        this.unlocalizedName = name;
        this.blipLevel = level;
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        this.dim = dim;
        this.entityID = entityID;
        this.redstone = redstone;
    }

    public RadarEntry(final IRadarDetectableNT detectable, final Entity entity, final boolean redstone) {
        this(detectable.getUnlocalizedName(), detectable.getBlipLevel(), entity.blockPosition().getX(), entity.blockPosition().getY(), entity.blockPosition().getZ(),
            entity.level().dimension() == net.minecraft.world.level.Level.OVERWORLD ? 0 : entity.level().dimension().location().hashCode(), entity.getId(), redstone);
    }

    public RadarEntry(final IRadarDetectable detectable, final Entity entity) {
        this(detectable.getTargetType().name, detectable.getTargetType().ordinal(), entity.blockPosition().getX(), entity.blockPosition().getY(), entity.blockPosition().getZ(),
            entity.level().dimension() == net.minecraft.world.level.Level.OVERWORLD ? 0 : entity.level().dimension().location().hashCode(), entity.getId(), entity.getDeltaMovement().y < 0.0D);
    }

    public RadarEntry(final Player player) {
        this(player.getName().getString(), IRadarDetectableNT.PLAYER, player.blockPosition().getX(), player.blockPosition().getY(), player.blockPosition().getZ(),
            player.level().dimension() == net.minecraft.world.level.Level.OVERWORLD ? 0 : player.level().dimension().location().hashCode(), player.getId(), true);
    }

    public void fromBytes(final FriendlyByteBuf buf) {
        this.unlocalizedName = buf.readUtf();
        this.blipLevel = buf.readShort();
        this.posX = buf.readInt();
        this.posY = buf.readInt();
        this.posZ = buf.readInt();
        this.dim = buf.readShort();
        this.entityID = buf.readInt();
        this.redstone = buf.readBoolean();
    }

    public void toBytes(final FriendlyByteBuf buf) {
        buf.writeUtf(this.unlocalizedName);
        buf.writeShort(this.blipLevel);
        buf.writeInt(this.posX);
        buf.writeInt(this.posY);
        buf.writeInt(this.posZ);
        buf.writeShort(this.dim);
        buf.writeInt(this.entityID);
        buf.writeBoolean(this.redstone);
    }
}
