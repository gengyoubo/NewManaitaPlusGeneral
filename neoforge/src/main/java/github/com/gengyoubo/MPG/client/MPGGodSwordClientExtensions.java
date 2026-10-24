package github.com.gengyoubo.MPG.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

/** Loaded only by the client event subscriber. */
public final class MPGGodSwordClientExtensions {
    private MPGGodSwordClientExtensions() {}

    public static final IClientItemExtensions INSTANCE = new IClientItemExtensions() {
        @Nullable
        @Override
        public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
            if (entityLiving.getUsedItemHand() == hand && entityLiving.getUseItemRemainingTicks() > 0) {
                return HumanoidModel.ArmPose.BLOCK;
            }
            return null;
        }

        @Override
        public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {
            if (player.isUsingItem() && player.getUseItemRemainingTicks() > 0 && player.getUsedItemHand() == (arm == HumanoidArm.LEFT ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND)) {
                int side = arm == HumanoidArm.RIGHT ? 1 : -1;
                double f = Mth.sin(swingProcess * swingProcess * Mth.PI);
                double f1 = Mth.sin(Mth.sqrt(swingProcess) * Mth.PI);
                poseStack.translate(side * 0.56, -0.52 + equipProcess * -0.6, -0.72);
                poseStack.translate(side * -0.1414214, 0.08, 0.1414214);
                poseStack.mulPose(Axis.XP.rotationDegrees((float) (-102.25F - f1 * 80.0F)));
                poseStack.mulPose(Axis.YP.rotationDegrees((float) (side * 13.365F - f * 20.0F)));
                poseStack.mulPose(Axis.ZP.rotationDegrees((float) (side * 78.050003F - f1 * 20.0F)));
                return true;
            }
            return false;
        }
    };
}
