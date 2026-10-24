package github.com.gengyoubo.item.tool.base;

import net.minecraft.world.item.Item;
import github.com.gengyoubo.common.item.tool.MPGTaggedToolItemBase;
import github.com.gengyoubo.common.item.tool.MPGToolProfile;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public abstract class MPTaggedToolItem extends MPGTaggedToolItemBase {
    protected MPTaggedToolItem(Item.Properties properties, TagKey<Block> mineableTag) {
        super(properties, mineableTag);
    }

    protected MPTaggedToolItem(Item.Properties properties, TagKey<Block> mineableTag, MPGToolProfile profile) {
        super(properties, mineableTag, profile);
    }
}
