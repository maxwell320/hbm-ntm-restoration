package api.hbm.entity;

import net.minecraft.world.damagesource.DamageSource;

public interface IResistanceProvider {

    float[] getCurrentDTDR(DamageSource damage, float amount, float pierceDT, float pierce);

    void onDamageDealt(DamageSource damage, float amount);
}
