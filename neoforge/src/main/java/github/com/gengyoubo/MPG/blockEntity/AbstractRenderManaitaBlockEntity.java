package github.com.gengyoubo.MPG.blockEntity;

import github.com.gengyoubo.MPG.core.MPGBlockCore;
import github.com.gengyoubo.common.client.renderer.MPGBlockEntityRendererBase;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractRenderManaitaBlockEntity<T extends BlockEntity>
        extends MPGBlockEntityRendererBase<T> {
    protected AbstractRenderManaitaBlockEntity(BlockEntityRendererProvider.Context context, ItemStack displayStack) {
        super(context, displayStack, MPGBlockCore.HookBlock.get().defaultBlockState());
    }
}
