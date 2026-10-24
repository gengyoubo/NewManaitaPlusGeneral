package github.com.gengyoubo.MPG.blockEntity;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import github.com.gengyoubo.MPG.block.entity.MPBrewingStandBlockEntity;
import github.com.gengyoubo.MPG.core.MPGBlockCore;

public class RenderBrewingManaitaBlockEntity extends AbstractRenderManaitaBlockEntity<MPBrewingStandBlockEntity> {
    public RenderBrewingManaitaBlockEntity(BlockEntityRendererProvider.Context context) {
        super(context, MPGBlockCore.BrewingBlockItem.get());
    }
}

