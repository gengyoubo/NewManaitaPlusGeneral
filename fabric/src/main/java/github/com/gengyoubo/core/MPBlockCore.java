package github.com.gengyoubo.core;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import github.com.gengyoubo.block.MPBrewingStandBlock;
import github.com.gengyoubo.block.MPFurnaceBlock;
import github.com.gengyoubo.block.MPCraftingBlock;
import github.com.gengyoubo.block.MPHookBlock;
import github.com.gengyoubo.block.item.MPBrewingBlockItem;
import github.com.gengyoubo.block.item.MPFurnaceBlockItem;
import github.com.gengyoubo.block.item.MPCraftingBlockItem;
import github.com.gengyoubo.block.item.MPHookBlockItem;

import static github.com.gengyoubo.MPG.BLOCKS;
import static github.com.gengyoubo.MPG.ITEMS;
import static github.com.gengyoubo.common.registry.MPGRegistryIds.*;

public class MPBlockCore {
    public static final RegistryObject<Block> CraftingBlock = MPRegistrations.block(CRAFTING_BLOCK, MPCraftingBlock::new);
    public static final RegistryObject<Item> CraftingBlockItem = MPRegistrations.item(CRAFTING_BLOCK, MPCraftingBlockItem::new);

    public static final RegistryObject<Block> FurnaceBlock = MPRegistrations.block(FURNACE_BLOCK, MPFurnaceBlock::new);
    public static final RegistryObject<Item> FurnaceBlockItem = MPRegistrations.item(FURNACE_BLOCK, MPFurnaceBlockItem::new);

    public static final RegistryObject<Block> BrewingBlock = MPRegistrations.block(BREWING_BLOCK, MPBrewingStandBlock::new);
    public static final RegistryObject<Item> BrewingBlockItem = MPRegistrations.item(BREWING_BLOCK, MPBrewingBlockItem::new);

    public static final RegistryObject<Block> HookBlock = MPRegistrations.block(HOOK_BLOCK, MPHookBlock::new);
    public static final RegistryObject<Item> HookBlockItem = MPRegistrations.item(HOOK_BLOCK, MPHookBlockItem::new);

    public static void init() {
    }

}

