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
    @Inject(method = "jumpFromGround", at = @At("TAIL"))
    private void manaitaPlusGeneral$bootsJump(CallbackInfo ci) {
        if ((Object) this instanceof Player player) MPGArmorItemBase.applyBootsJump(player);
    }
}
