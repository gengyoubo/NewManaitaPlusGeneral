package github.com.gengyoubo.MPG.item.tool;

import net.minecraft.world.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import github.com.gengyoubo.MPG.item.tool.base.ManaitaPlusLegacyTaggedToolItem;
import github.com.gengyoubo.common.item.tool.MPGToolProfile;
import org.jetbrains.annotations.NotNull;

public class MPGPickaxeItem extends ManaitaPlusLegacyTaggedToolItem {
    public MPGPickaxeItem(Item.Properties properties) {
        super(properties, BlockTags.MINEABLE_WITH_PICKAXE, MPGToolProfile.PICKAXE);
    }

    @Override
    public boolean canPerformAction(@NotNull net.minecraft.world.item.ItemInstance stack, net.neoforged.neoforge.common.@NotNull ItemAbility toolAction) {
        return false;
    }

}
