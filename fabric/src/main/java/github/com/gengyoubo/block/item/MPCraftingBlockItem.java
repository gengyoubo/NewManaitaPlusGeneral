package github.com.gengyoubo.block.item;

import net.minecraft.world.item.Item;
import github.com.gengyoubo.block.MPCraftingBlock;
import github.com.gengyoubo.core.MPBlockCore;

public class MPCraftingBlockItem extends MPTypedBlockItem {
    public MPCraftingBlockItem(Item.Properties properties) {
        super(MPBlockCore.CraftingBlock.get(), properties, "block.crafting.", MPCraftingBlock.class);
    }
}


