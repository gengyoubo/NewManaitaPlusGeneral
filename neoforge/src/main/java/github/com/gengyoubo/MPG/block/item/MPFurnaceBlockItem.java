package github.com.gengyoubo.MPG.block.item;

import net.minecraft.world.item.Item;
import github.com.gengyoubo.MPG.block.MPFurnaceBlock;

import static github.com.gengyoubo.MPG.core.MPGBlockCore.FurnaceBlock;

public class MPFurnaceBlockItem extends MPTypedBlockItem {
    public MPFurnaceBlockItem(Item.Properties properties) {
        super(FurnaceBlock.get(), properties, "block.furnace.", MPFurnaceBlock.class);
    }
}
