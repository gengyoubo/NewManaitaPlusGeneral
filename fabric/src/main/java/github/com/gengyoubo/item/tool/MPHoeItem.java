package github.com.gengyoubo.item.tool;

import net.minecraft.world.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;
import github.com.gengyoubo.item.tool.base.MPTaggedToolItem;
import github.com.gengyoubo.item.tool.base.MPToolActionHelper;
import github.com.gengyoubo.common.item.tool.MPGToolProfile;

public class MPHoeItem extends MPTaggedToolItem {
    public MPHoeItem(Item.Properties properties) {
        super(properties, BlockTags.MINEABLE_WITH_HOE, MPGToolProfile.HOE);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        int range = getRange(context.getItemInHand()) >> 1;
        boolean changed = MPToolActionHelper.applyInRange(context, range, (pos, state) -> MPToolActionHelper.applyHoeTillAction(context, pos, state));
        return changed ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }
}
