package github.com.gengyoubo.common.item.tier;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

/**
 * Manaita tool and sword properties. 26.1 removed {@code Tier}; tools are configured through item components.
 * Manaita gear never takes damage and mines every matching block instantly.
 */
public final class MPGToolMaterials {
    private static final float INSTANT_SPEED = Float.POSITIVE_INFINITY;

    private MPGToolMaterials() {
    }

    public static Item.Properties tool(Item.Properties properties, TagKey<Block> mineableTag) {
        HolderGetter<Block> blocks = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
        return unbreakable(properties).component(DataComponents.TOOL, new Tool(List.of(
                Tool.Rule.deniesDrops(blocks.getOrThrow(BlockTags.INCORRECT_FOR_NETHERITE_TOOL)),
                Tool.Rule.minesAndDrops(blocks.getOrThrow(mineableTag), INSTANT_SPEED)), 1.0F, 0, true));
    }

    public static Item.Properties sword(Item.Properties properties) {
        HolderGetter<Block> blocks = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
        return unbreakable(properties)
                .component(DataComponents.TOOL, new Tool(List.of(
                        Tool.Rule.minesAndDrops(HolderSet.direct(Blocks.COBWEB.builtInRegistryHolder()), 15.0F),
                        Tool.Rule.overrideSpeed(blocks.getOrThrow(BlockTags.SWORD_INSTANTLY_MINES), Float.MAX_VALUE),
                        Tool.Rule.overrideSpeed(blocks.getOrThrow(BlockTags.SWORD_EFFICIENT), 1.5F)), 1.0F, 0, false))
                .component(DataComponents.WEAPON, new Weapon(0));
    }

    /** Fire-resistant, single-stack and never damaged; the vanilla "Unbreakable" tooltip line is hidden. */
    public static Item.Properties unbreakable(Item.Properties properties) {
        return properties.fireResistant()
                .stacksTo(1)
                .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                .component(DataComponents.TOOLTIP_DISPLAY,
                        TooltipDisplay.DEFAULT.withHidden(DataComponents.UNBREAKABLE, true));
    }
}
