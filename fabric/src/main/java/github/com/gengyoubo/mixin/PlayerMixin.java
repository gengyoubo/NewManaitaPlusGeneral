package github.com.gengyoubo.mixin;

import github.com.gengyoubo.common.event.MPGEventLogic;
import github.com.gengyoubo.common.item.armor.MPGArmorItemBase;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void manaitaPlusGeneral$syncArmorState(CallbackInfo ci) {
        MPGArmorItemBase.syncArmorState((Player) (Object) this);
    }

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void manaitaPlusGeneral$cancelHelmetAttack(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount,
                                                        CallbackInfoReturnable<Boolean> cir) {
        Player player = (Player) (Object) this;
        if (MPGArmorItemBase.shouldCancelDamage(player, source)) {
            MPGEventLogic.resetPlayerDamageState(player);
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
    private void manaitaPlusGeneral$cancelHelmetHurt(net.minecraft.server.level.ServerLevel level, DamageSource source, float amount, CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (MPGArmorItemBase.shouldCancelDamage(player, source)) {
            MPGEventLogic.resetPlayerDamageState(player);
            ci.cancel();
        }
    }

    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    private void manaitaPlusGeneral$cancelHelmetDeath(DamageSource source, CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (MPGArmorItemBase.shouldCancelDamage(player, source)) {
            MPGEventLogic.resetPlayerDamageState(player);
            ci.cancel();
        }
    }

    @Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
    private void manaitaPlusGeneral$cancelBootsLanding(double fallDistance, float damageMultiplier,
                                                       DamageSource source,
                                                       CallbackInfoReturnable<Boolean> cir) {
        Player player = (Player) (Object) this;
        if (MPGArmorItemBase.hasManaitaBoots(player)) {
            MPGEventLogic.resetPlayerFallState(player);
            cir.setReturnValue(false);
        }
    }
}
