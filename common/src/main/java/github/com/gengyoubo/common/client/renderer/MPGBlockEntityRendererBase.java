package github.com.gengyoubo.common.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import github.com.gengyoubo.common.block.MPGBlockData;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Shared mounted-block renderer; loader modules only supply registered item and hook states. */
public abstract class MPGBlockEntityRendererBase<T extends BlockEntity>
        implements BlockEntityRenderer<T, MPGBlockEntityRendererBase.MountedRenderState> {
    private static final BlockDisplayContext HOOK_DISPLAY_CONTEXT = BlockDisplayContext.create();
    private final ItemModelResolver itemModelResolver;
    private final BlockModelResolver blockModelResolver;
    private final Item displayItem;
    private final BlockState hookBlockTemplate;

    protected MPGBlockEntityRendererBase(BlockEntityRendererProvider.Context context, Item displayItem,
                                         BlockState hookBlockTemplate) {
        this.itemModelResolver = context.itemModelResolver();
        this.blockModelResolver = context.blockModelResolver();
        this.displayItem = displayItem;
        this.hookBlockTemplate = hookBlockTemplate;
    }

    @Override
    public @NotNull MountedRenderState createRenderState() {
        return new MountedRenderState();
    }

    @Override
    public void extractRenderState(@NotNull T blockEntity, @NotNull MountedRenderState state, float partialTick,
                                   @NotNull Vec3 cameraPosition,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPosition, breakProgress);
        BlockState blockState = blockEntity.getBlockState();
        state.facing = blockState.getValue(MPGBlockData.FACING);
        state.wall = blockState.getValue(MPGBlockData.WALL);

        ItemStack displayStack = new ItemStack(displayItem);
        MPGItemStackData.putInt(displayStack, MPGNBTData.ItemType, blockState.getValue(MPGBlockData.TYPES));
        itemModelResolver.updateForTopItem(state.item, displayStack, ItemDisplayContext.FIXED,
                blockEntity.getLevel(), null, 0);

        int hookType = blockState.getValue(MPGBlockData.HOOK);
        state.hasHook = hookType != 8;
        if (state.hasHook) {
            BlockState hookState = hookBlockTemplate
                    .setValue(MPGBlockData.FACING, state.facing)
                    .setValue(MPGBlockData.TYPES, hookType);
            blockModelResolver.update(state.hook, hookState, HOOK_DISPLAY_CONTEXT);
        }
    }

    @Override
    public void submit(@NotNull MountedRenderState state, @NotNull PoseStack poseStack,
                       @NotNull SubmitNodeCollector collector, @NotNull CameraRenderState camera) {
        poseStack.pushPose();
        applyWallTransform(poseStack, state.wall, state.facing);
        state.item.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();

        if (state.hasHook) {
            state.hook.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        }
    }

    public static class MountedRenderState extends BlockEntityRenderState {
        public Direction facing = Direction.NORTH;
        public Direction wall = Direction.NORTH;
        public boolean hasHook;
        public final ItemStackRenderState item = new ItemStackRenderState();
        public final BlockModelRenderState hook = new BlockModelRenderState();
    }

    private static void applyWallTransform(PoseStack poseStack, Direction wall, Direction facing) {
        switch (wall) {
            case NORTH -> {
                poseStack.translate(0.5F, 0.5F, 0.0F);
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));
            }
            case SOUTH -> {
                poseStack.translate(0.5F, 0.5F, 1.0F);
                poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            }
            case WEST -> {
                poseStack.translate(0.0F, 0.5F, 0.5F);
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            }
            case EAST -> {
                poseStack.translate(1.0F, 0.5F, 0.5F);
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            }
            case UP -> {
                poseStack.translate(0.5F, 1.0F, 0.5F);
                applyVerticalFacing(poseStack, facing);
            }
            case DOWN -> {
                poseStack.translate(0.5F, 0.0F, 0.5F);
                applyVerticalFacing(poseStack, facing);
            }
        }
    }

    private static void applyVerticalFacing(PoseStack poseStack, Direction facing) {
        switch (facing) {
            case NORTH -> poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            case SOUTH -> poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            case WEST -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            }
            case EAST -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));
            }
            default -> {
            }
        }
    }
}
