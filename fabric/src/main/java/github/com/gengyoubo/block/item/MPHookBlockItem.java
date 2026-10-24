package github.com.gengyoubo.block.item;

import net.minecraft.world.item.Item;
import github.com.gengyoubo.block.MPHookBlock;
import github.com.gengyoubo.common.block.item.MPGHookBlockItemBase;

import static github.com.gengyoubo.core.MPBlockCore.HookBlock;

public class MPHookBlockItem extends MPGHookBlockItemBase {
    public MPHookBlockItem(Item.Properties properties) {
        super(HookBlock.get(), properties, MPHookBlock.class);
    }
}
