package api.hbm.ntl;

import net.minecraft.world.item.ItemStack;

public interface IStorageComponent {

    EnumStorageType getType();

    StorageManifest getManifest();

    int getManifestVersion();

    ItemStack storeStack(ItemStack stack, boolean simulate);
}
