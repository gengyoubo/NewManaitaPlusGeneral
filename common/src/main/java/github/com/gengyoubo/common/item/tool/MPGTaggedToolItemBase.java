package github.com.gengyoubo.common.item.tool;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class MPGTaggedToolItemBase extends MPGToolItemBase {
    private final TagKey<Block> mineableTag;

    protected MPGTaggedToolItemBase(Tier tier, TagKey<Block> mineableTag) {
        super(tier, mineableTag);
        this.mineableTag = mineableTag;
    }

    protected MPGTaggedToolItemBase(Tier tier, TagKey<Block> mineableTag, MPGToolProfile profile) {
        super(tier, mineableTag, profile);
        this.mineableTag = mineableTag;
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState state) {
        return state.is(mineableTag);
    }

    @Override
    public boolean accept(BlockState state) {
        return !state.is(mineableTag);
    }
}
