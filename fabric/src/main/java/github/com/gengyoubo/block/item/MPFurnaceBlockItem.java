package github.com.gengyoubo.block.item;

import net.minecraft.world.item.Item;
import github.com.gengyoubo.block.MPFurnaceBlock;

import static github.com.gengyoubo.core.MPBlockCore.FurnaceBlock;

public class MPFurnaceBlockItem extends MPTypedBlockItem {
    public MPFurnaceBlockItem(Item.Properties properties) {
        super(FurnaceBlock.get(), properties, "block.furnace.", MPFurnaceBlock.class);
    }
}


