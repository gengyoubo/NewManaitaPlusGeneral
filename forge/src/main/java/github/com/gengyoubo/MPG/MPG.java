package github.com.gengyoubo.MPG;

import net.minecraft.network.chat.TranslatableComponent;
import com.mojang.logging.LogUtils;
import github.com.gengyoubo.common.MPGCommon;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
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
import github.com.gengyoubo.MPG.baubles.common.capability.BaublesCapability;

@Mod(MPG.MODID)
public class MPG {
    public static final String MODID = MPGCommon.MOD_ID;
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<net.minecraft.world.level.block.Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);
    public static final DeferredRegister<Attribute> ATTRIBUTE_TYPE = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER_DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final CreativeModeTab MANAITA_PLUS_TAB = new CreativeModeTab("manaita_plus_general") {
        @Override public ItemStack makeIcon() { return MPGBlockCore.CraftingBlockItem.get().getDefaultInstance(); }
        @Override public Component getDisplayName() { return new TranslatableComponent("itemGroup.ManaitaPlusTab"); }
        @Override public void fillItemList(net.minecraft.core.NonNullList<ItemStack> output) {
                acceptMPGType(MPGBlockCore.CraftingBlockItem.get(), output, 8);
                acceptMPGType(MPGBlockCore.FurnaceBlockItem.get(), output, 8);
                acceptMPGType(MPGBlockCore.BrewingBlockItem.get(), output, 8);
                acceptMPGType(MPGBlockCore.HookBlockItem.get(), output, 7);

                acceptMPGType(MPGItemCore.ManaitaCraftingPortable.get(), output, 8);
                acceptMPGType(MPGItemCore.ManaitaFurnacePortable.get(), output, 8);
                acceptMPGType(MPGItemCore.ManaitaBrewingPortable.get(), output, 8);
                if (github.com.gengyoubo.MPG.integration.MPGAccessories.isEnabled()) {
                    acceptMPGType(MPGItemCore.ManaitaCraftingRing.get(), output, 8);
                    acceptMPGType(MPGItemCore.ManaitaFurnaceRing.get(), output, 8);
                    acceptMPGType(MPGItemCore.ManaitaBrewingRing.get(), output, 8);
                }

                output.add(MPGItemCore.ManaitaSwordGod.get().getDefaultInstance());
                output.add(MPGItemCore.ManaitaSword.get().getDefaultInstance());
                output.add(MPGItemCore.ManaitaBow.get().getDefaultInstance());
                output.add(MPGItemCore.ManaitaShovel.get().getDefaultInstance());
                output.add(MPGItemCore.ManaitaPickaxe.get().getDefaultInstance());
                output.add(MPGItemCore.ManaitaAxe.get().getDefaultInstance());
                output.add(MPGItemCore.ManaitaPaxel.get().getDefaultInstance());
                output.add(MPGItemCore.ManaitaHoe.get().getDefaultInstance());
                output.add(MPGItemCore.ManaitaShears.get().getDefaultInstance());
                output.add(MPGItemCore.ManaitaHelmet.get().getDefaultInstance());
                output.add(MPGItemCore.ManaitaChestplate.get().getDefaultInstance());
                output.add(MPGItemCore.ManaitaLeggings.get().getDefaultInstance());
                output.add(MPGItemCore.ManaitaBoots.get().getDefaultInstance());
                output.add(MPGItemCore.ManaitaHook.get().getDefaultInstance());
                output.add(MPGItemCore.ManaitaSource.get().getDefaultInstance());
        }
    };

    @SuppressWarnings("removal")
    public MPG() {
        MPGCommon.init();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        Networking.registerMessage();
        modEventBus.addListener((net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent event) ->
                event.enqueueWork(github.com.gengyoubo.MPG.integration.MPGAccessories::register));

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

        net.minecraftforge.fml.ModLoadingContext.get().registerConfig(Type.COMMON, MPGConfig.SPEC);
    }

    private static void acceptMPGType(Item item, java.util.List<ItemStack> output, int maxType) {
        for (int type = 0; type <= maxType; type++) {
            ItemStack stack = new ItemStack(item);
            MPGItemStackData.putInt(stack, MPGNBTData.ItemType, type);
            output.add(stack);
        }
    }

}
