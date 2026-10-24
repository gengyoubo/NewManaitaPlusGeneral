package github.com.gengyoubo.block.item;

import net.minecraft.world.item.Item;
import github.com.gengyoubo.block.MPBrewingStandBlock;

import static github.com.gengyoubo.core.MPBlockCore.BrewingBlock;

public class MPBrewingBlockItem extends MPTypedBlockItem {
    public MPBrewingBlockItem(Item.Properties properties) {
        super(BrewingBlock.get(), properties, "block.brewing.", MPBrewingStandBlock.class);
    }
}


