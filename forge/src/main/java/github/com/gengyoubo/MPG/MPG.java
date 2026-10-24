package github.com.gengyoubo.MPG;

import com.mojang.logging.LogUtils;
import github.com.gengyoubo.common.MPGCommon;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import github.com.gengyoubo.MPG.core.*;
import github.com.gengyoubo.MPG.network.Networking;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import github.com.gengyoubo.MPG.compat.CuriosCompat;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

@Mod(MPG.MODID)
public class MPG {
    public static final String MODID = MPGCommon.MOD_ID;
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<net.minecraft.world.level.block.Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);
    public static final DeferredRegister<Attribute> ATTRIBUTE_TYPE = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER_DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final CreativeModeTab MANAITA_PLUS_TAB = new CreativeModeTab("ManaitaPlusTab") {
        @Override
        public ItemStack makeIcon() {
            return MPGBlockCore.CraftingBlockItem.get().getDefaultInstance();
        }

        @Override
        public void fillItemList(NonNullList<ItemStack> output) {
            acceptMPGType(MPGBlockCore.CraftingBlockItem.get(), output, 8);
            acceptMPGType(MPGBlockCore.FurnaceBlockItem.get(), output, 8);
            acceptMPGType(MPGBlockCore.BrewingBlockItem.get(), output, 8);
            acceptMPGType(MPGBlockCore.HookBlockItem.get(), output, 7);

            acceptMPGType(MPGItemCore.ManaitaCraftingPortable.get(), output, 8);
            acceptMPGType(MPGItemCore.ManaitaFurnacePortable.get(), output, 8);
            acceptMPGType(MPGItemCore.ManaitaBrewingPortable.get(), output, 8);
            if (MPGItemCore.isCuriosLoaded()) {
                acceptMPGType(MPGItemCore.ManaitaCraftingRing.get(), output, 8);
                acceptMPGType(MPGItemCore.ManaitaFurnaceRing.get(), output, 8);
                acceptMPGType(MPGItemCore.ManaitaBrewingRing.get(), output, 8);
        }

            output.add(new ItemStack(MPGItemCore.ManaitaSwordGod.get()));
            output.add(new ItemStack(MPGItemCore.ManaitaSword.get()));
            output.add(new ItemStack(MPGItemCore.ManaitaBow.get()));
            output.add(new ItemStack(MPGItemCore.ManaitaShovel.get()));
            output.add(new ItemStack(MPGItemCore.ManaitaPickaxe.get()));
            output.add(new ItemStack(MPGItemCore.ManaitaAxe.get()));
            output.add(new ItemStack(MPGItemCore.ManaitaPaxel.get()));
            output.add(new ItemStack(MPGItemCore.ManaitaHoe.get()));
            output.add(new ItemStack(MPGItemCore.ManaitaShears.get()));
            output.add(new ItemStack(MPGItemCore.ManaitaHelmet.get()));
            output.add(new ItemStack(MPGItemCore.ManaitaChestplate.get()));
            output.add(new ItemStack(MPGItemCore.ManaitaLeggings.get()));
            output.add(new ItemStack(MPGItemCore.ManaitaBoots.get()));
            output.add(new ItemStack(MPGItemCore.ManaitaHook.get()));
            output.add(new ItemStack(MPGItemCore.ManaitaSource.get()));
        }
    };

    @SuppressWarnings("removal")
    public MPG() {
        MPGCommon.init();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        Networking.registerMessage();

        // Force-load all registry holder classes before RegisterEvent starts firing.
        MPGBlockCore.init();
        MPGItemCore.init();
        MPGEntityCore.init();
        MPGBlockEntityCore.init();
        MPGMenuCore.init();
        MPGRecipeSerializerCore.init();
        MPGAttributeCore.init();
        MPGConditionCore.init();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        ATTRIBUTE_TYPE.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
        MENU_TYPES.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        RECIPE_SERIALIZER_DEFERRED_REGISTER.register(modEventBus);
        if (MPGItemCore.isCuriosLoaded()) {
            modEventBus.addListener((InterModEnqueueEvent event) -> CuriosCompat.registerSlots());
        }

        net.minecraftforge.fml.ModLoadingContext.get().registerConfig(Type.COMMON, MPGConfig.SPEC);
    }

    private static void acceptMPGType(Item item, NonNullList<ItemStack> output, int maxType) {
        for (int type = 0; type <= maxType; type++) {
            ItemStack stack = new ItemStack(item);
            MPGItemStackData.putInt(stack, MPGNBTData.ItemType, type);
            output.add(stack);
        }
    }

}
