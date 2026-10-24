package github.com.gengyoubo.mixin;

import github.com.gengyoubo.common.item.armor.MPGArmorItemBase;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin {
    @Inject(method = "getArmorLocation", at = @At("HEAD"), cancellable = true)
    private void manaitaPlusGeneral$armorTexture(ArmorItem armor, boolean innerLayer, String suffix,
                                                CallbackInfoReturnable<ResourceLocation> cir) {
        if (armor instanceof MPGArmorItemBase) {
            cir.setReturnValue(new ResourceLocation("manaita_plus_general",
                    "textures/models/armor/manaita_armor_layer_" + (innerLayer ? 2 : 1)
                            + (suffix == null ? "" : "_" + suffix) + ".png"));
        }
    }
}
