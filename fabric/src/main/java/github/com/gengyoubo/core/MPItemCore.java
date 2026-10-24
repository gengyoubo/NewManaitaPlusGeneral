package github.com.gengyoubo.core;

import github.com.gengyoubo.item.*;
import github.com.gengyoubo.item.armor.MPArmor;
import github.com.gengyoubo.item.portable.MPBrewingPortable;
import github.com.gengyoubo.item.portable.MPCraftingPortable;
import github.com.gengyoubo.item.portable.MPFurnacePortable;
import github.com.gengyoubo.item.tool.*;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import static github.com.gengyoubo.MPG.ITEMS;
import static github.com.gengyoubo.common.registry.MPGRegistryIds.*;

public class MPItemCore {
    public static final RegistryObject<Item> ManaitaSword = MPRegistrations.item(SWORD, MPSwordItem::new);
    public static final RegistryObject<Item> ManaitaSwordGod = MPRegistrations.item(GOD_SWORD, MPGodSwordItem::new);
    public static final RegistryObject<Item> ManaitaBow = MPRegistrations.item(BOW, MPBowItem::new);
    public static final RegistryObject<Item> ManaitaAxe = MPRegistrations.item(AXE, MPAxeItem::new);
    public static final RegistryObject<Item> ManaitaHoe = MPRegistrations.item(HOE, MPHoeItem::new);
    public static final RegistryObject<Item> ManaitaPaxel = MPRegistrations.item(PAXEL, MPPaxelItem::new);
    public static final RegistryObject<Item> ManaitaPickaxe = MPRegistrations.item(PICKAXE, MPPickaxeItem::new);
    public static final RegistryObject<Item> ManaitaShears = MPRegistrations.item(SHEARS, MPShearsItem::new);
    public static final RegistryObject<Item> ManaitaShovel = MPRegistrations.item(SHOVEL, MPShovelItem::new);
    public static final RegistryObject<Item> ManaitaHelmet = MPRegistrations.item(HELMET, MPArmor.Helmet::new);
    public static final RegistryObject<Item> ManaitaChestplate = MPRegistrations.item(CHESTPLATE, MPArmor.Chestplate::new);
    public static final RegistryObject<Item> ManaitaLeggings = MPRegistrations.item(LEGGINGS, MPArmor.Leggings::new);
    public static final RegistryObject<Item> ManaitaBoots = MPRegistrations.item(BOOTS, MPArmor.Boots::new);
    public static final RegistryObject<Item> ManaitaSource = MPRegistrations.item(SOURCE, MPSourceItem::new);
    public static final RegistryObject<Item> ManaitaHook = MPRegistrations.item(HOOK, MPHookItem::new);
    public static final RegistryObject<Item> ManaitaCraftingPortable = MPRegistrations.item(CRAFTING_PORTABLE, MPCraftingPortable::new);
    public static final RegistryObject<Item> ManaitaFurnacePortable = MPRegistrations.item(FURNACE_PORTABLE, MPFurnacePortable::new);
    public static final RegistryObject<Item> ManaitaBrewingPortable = MPRegistrations.item(BREWING_PORTABLE, MPBrewingPortable::new);

    public static void init() {
    }
}
