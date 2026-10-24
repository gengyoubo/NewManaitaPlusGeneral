package github.com.gengyoubo.MPG.blockEntity;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import github.com.gengyoubo.MPG.block.entity.MPFurnaceBlockEntity;
import github.com.gengyoubo.MPG.core.MPGBlockCore;

public class RenderFurnaceManaitaBlockEntity extends AbstractRenderManaitaBlockEntity<MPFurnaceBlockEntity> {
    public RenderFurnaceManaitaBlockEntity(BlockEntityRendererProvider.Context context) {
        super(context, MPGBlockCore.FurnaceBlockItem.get());
    }
}

