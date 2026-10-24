package github.com.gengyoubo.MPG.item.tool.base;

import net.minecraft.world.item.Item;
import github.com.gengyoubo.MPG.item.tier.MPGToolTier;
import github.com.gengyoubo.common.item.tool.MPGTaggedToolItemBase;
import github.com.gengyoubo.common.item.tool.MPGToolProfile;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public abstract class ManaitaPlusLegacyTaggedToolItem extends MPGTaggedToolItemBase {
    protected ManaitaPlusLegacyTaggedToolItem(Item.Properties properties, TagKey<Block> mineableTag) {
        super(properties, mineableTag);
    }

    protected ManaitaPlusLegacyTaggedToolItem(Item.Properties properties, TagKey<Block> mineableTag, MPGToolProfile profile) {
        super(properties, mineableTag, profile);
    }

    @Override
    protected boolean messageUsesOverlay() {
        return false;
    }
}
