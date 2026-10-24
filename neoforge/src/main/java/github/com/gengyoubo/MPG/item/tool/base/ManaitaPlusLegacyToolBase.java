package github.com.gengyoubo.MPG.item.tool.base;

import net.minecraft.world.item.Item;
import github.com.gengyoubo.MPG.item.tier.MPGToolTier;
import github.com.gengyoubo.common.item.tool.MPGToolItemBase;
import github.com.gengyoubo.common.item.tool.MPGToolProfile;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ManaitaPlusLegacyToolBase extends MPGToolItemBase {
    public ManaitaPlusLegacyToolBase(Item.Properties properties, TagKey<Block> mineableTag) {
        super(properties, mineableTag);
    }

    public ManaitaPlusLegacyToolBase(Item.Properties properties, TagKey<Block> mineableTag, MPGToolProfile profile) {
        super(properties, mineableTag, profile);
    }

    @Override
    protected boolean messageUsesOverlay() {
        return false;
    }
}
