package github.com.gengyoubo.mixin;

import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Vanilla 1.19.2 builds "textures/models/armor/" + material name, which breaks for
 * namespaced material names such as "manaita_plus_general:manaita_armor".
 */
@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin {
    @Inject(method = "getArmorLocation", at = @At("HEAD"), cancellable = true)
    private void manaitaPlusGeneral$namespacedArmorLocation(ArmorItem item, boolean legs, @Nullable String overlay,
                                                           CallbackInfoReturnable<ResourceLocation> cir) {
        String name = item.getMaterial().getName();
        int separator = name.indexOf(':');
        if (separator < 0) {
            return;
        }
        cir.setReturnValue(new ResourceLocation(name.substring(0, separator), "textures/models/armor/"
                + name.substring(separator + 1) + "_layer_" + (legs ? 2 : 1)
                + (overlay == null ? "" : "_" + overlay) + ".png"));
    }
}
