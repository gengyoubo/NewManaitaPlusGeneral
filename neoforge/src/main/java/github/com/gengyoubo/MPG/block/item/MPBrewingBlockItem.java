package github.com.gengyoubo.MPG.block.item;

import net.minecraft.world.item.Item;
import github.com.gengyoubo.MPG.block.MPBrewingStandBlock;

import static github.com.gengyoubo.MPG.core.MPGBlockCore.BrewingBlock;

public class MPBrewingBlockItem extends MPTypedBlockItem {
    public MPBrewingBlockItem(Item.Properties properties) {
        super(BrewingBlock.get(), properties, "block.brewing.", MPBrewingStandBlock.class);
    }
}
