package github.com.gengyoubo.item.tool;

import net.minecraft.world.item.Item;
import net.minecraft.tags.BlockTags;
import github.com.gengyoubo.item.tool.base.MPTaggedToolItem;
import github.com.gengyoubo.common.item.tool.MPGToolProfile;

public class MPPickaxeItem extends MPTaggedToolItem {
    public MPPickaxeItem(Item.Properties properties) {
        super(properties, BlockTags.MINEABLE_WITH_PICKAXE, MPGToolProfile.PICKAXE);
    }
}
