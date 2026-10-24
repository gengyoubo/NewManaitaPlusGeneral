package github.com.gengyoubo.mixin;

import github.com.gengyoubo.common.item.armor.MPGArmorItemBase;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "die", at = @At("TAIL"))
    private void manaitaPlusGeneral$deathDrops(net.minecraft.world.damagesource.DamageSource source, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!entity.level.isClientSide) {
            github.com.gengyoubo.common.event.MPGEventLogic.handleFabricDeathDrops(entity, source,
                    github.com.gengyoubo.MPGConfig.item_drops_doubling_value);
        }
    }

    @Inject(method = "jumpFromGround", at = @At("TAIL"))
    private void manaitaPlusGeneral$bootsJump(CallbackInfo ci) {
        if ((Object) this instanceof Player player) MPGArmorItemBase.applyBootsJump(player);
    }
}
