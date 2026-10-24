package github.com.gengyoubo.MPG.blockEntity;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import github.com.gengyoubo.MPG.block.entity.MPBrewingStandBlockEntity;
import github.com.gengyoubo.MPG.core.MPGBlockCore;

@OnlyIn(Dist.CLIENT)
public class RenderBrewingManaitaBlockEntity extends AbstractRenderManaitaBlockEntity<MPBrewingStandBlockEntity> {
    public RenderBrewingManaitaBlockEntity(BlockEntityRendererProvider.Context context) {
        super(context, new ItemStack(MPGBlockCore.BrewingBlockItem.get()));
    }
}

