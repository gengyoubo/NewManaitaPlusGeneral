package github.com.gengyoubo.core;

import github.com.gengyoubo.item.*;
import github.com.gengyoubo.item.armor.MPArmor;
import github.com.gengyoubo.item.portable.MPBrewingPortable;
import github.com.gengyoubo.item.portable.MPCraftingPortable;
import github.com.gengyoubo.item.portable.MPFurnacePortable;
import github.com.gengyoubo.item.ring.MPBrewingRing;
import github.com.gengyoubo.item.ring.MPCraftingRing;
import github.com.gengyoubo.item.ring.MPFurnaceRing;
import github.com.gengyoubo.item.tool.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import static github.com.gengyoubo.MPG.ITEMS;
import static github.com.gengyoubo.common.registry.MPGRegistryIds.*;

public class MPItemCore {
    private static final boolean TRINKETS_LOADED = FabricLoader.getInstance().isModLoaded("trinkets");

    public static final RegistryObject<Item> ManaitaSword = ITEMS.register(SWORD, MPSwordItem::new);
    public static final RegistryObject<Item> ManaitaSwordGod = ITEMS.register(GOD_SWORD, MPGodSwordItem::new);
    public static final RegistryObject<Item> ManaitaBow = ITEMS.register(BOW, MPBowItem::new);
    public static final RegistryObject<Item> ManaitaAxe = ITEMS.register(AXE, MPAxeItem::new);
    public static final RegistryObject<Item> ManaitaHoe = ITEMS.register(HOE, MPHoeItem::new);
    public static final RegistryObject<Item> ManaitaPaxel = ITEMS.register(PAXEL, MPPaxelItem::new);
    public static final RegistryObject<Item> ManaitaPickaxe = ITEMS.register(PICKAXE, MPPickaxeItem::new);
    public static final RegistryObject<Item> ManaitaShears = ITEMS.register(SHEARS, MPShearsItem::new);
    public static final RegistryObject<Item> ManaitaShovel = ITEMS.register(SHOVEL, MPShovelItem::new);
    public static final RegistryObject<Item> ManaitaHelmet = ITEMS.register(HELMET, MPArmor.Helmet::new);
    public static final RegistryObject<Item> ManaitaChestplate = ITEMS.register(CHESTPLATE, MPArmor.Chestplate::new);
    public static final RegistryObject<Item> ManaitaLeggings = ITEMS.register(LEGGINGS, MPArmor.Leggings::new);
    public static final RegistryObject<Item> ManaitaBoots = ITEMS.register(BOOTS, MPArmor.Boots::new);
    public static final RegistryObject<Item> ManaitaSource = ITEMS.register(SOURCE, MPSourceItem::new);
    public static final RegistryObject<Item> ManaitaHook = ITEMS.register(HOOK, MPHookItem::new);
    public static final RegistryObject<Item> ManaitaCraftingPortable = ITEMS.register(CRAFTING_PORTABLE, MPCraftingPortable::new);
    public static final RegistryObject<Item> ManaitaFurnacePortable = ITEMS.register(FURNACE_PORTABLE, MPFurnacePortable::new);
    public static final RegistryObject<Item> ManaitaBrewingPortable = ITEMS.register(BREWING_PORTABLE, MPBrewingPortable::new);
    // Rings are accessories; they only exist when Trinkets provides the ring slots.
    public static final RegistryObject<Item> ManaitaCraftingRing = TRINKETS_LOADED ? ITEMS.register(CRAFTING_RING, MPCraftingRing::new) : null;
    public static final RegistryObject<Item> ManaitaFurnaceRing = TRINKETS_LOADED ? ITEMS.register(FURNACE_RING, MPFurnaceRing::new) : null;
    public static final RegistryObject<Item> ManaitaBrewingRing = TRINKETS_LOADED ? ITEMS.register(BREWING_RING, MPBrewingRing::new) : null;

    public static boolean isTrinketsLoaded() {
        return TRINKETS_LOADED;
    }

    public static void init() {
    }
}
