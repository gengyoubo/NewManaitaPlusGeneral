package github.com.gengyoubo.common;

import github.com.gengyoubo.common.util.MPGInventoryData;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import net.minecraft.SharedConstants;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PortCompatibilityTest {
    private static RegistryAccess registries;

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        registries = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        // The game normally binds these during resource loading, after bootstrap in 26.1.
        Items.DIAMOND.builtInRegistryHolder().bindComponents(DataComponents.COMMON_ITEM_COMPONENTS);
        Items.COAL.builtInRegistryHolder().bindComponents(DataComponents.COMMON_ITEM_COMPONENTS);
    }

    @Test
    void multipliedInventorySurvivesSaveAndReload() {
        var items = NonNullList.withSize(3, ItemStack.EMPTY);
        items.set(2, new ItemStack(Items.DIAMOND, 4096));
        MPGItemStackData.putInt(items.get(2), MPGNBTData.ItemType, 7);
        var output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, registries);
        MPGInventoryData.saveAllItems(output, items);
        var restored = NonNullList.withSize(3, ItemStack.EMPTY);
        MPGInventoryData.loadAllItems(TagValueInput.create(ProblemReporter.DISCARDING, registries,
                output.buildResult()), restored);
        assertTrue(restored.get(0).isEmpty());
        assertEquals(4096, restored.get(2).getCount());
        assertTrue(restored.get(2).is(Items.DIAMOND));
        assertEquals(7, MPGItemStackData.getInt(restored.get(2), MPGNBTData.ItemType));
    }

    @Test
    void vanillaInventoryDataRemainsReadable() {
        var items = NonNullList.withSize(3, ItemStack.EMPTY);
        items.set(1, new ItemStack(Items.COAL, 64));
        var output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, registries);
        ContainerHelper.saveAllItems(output, items);
        var restored = NonNullList.withSize(3, ItemStack.EMPTY);
        MPGInventoryData.loadAllItems(TagValueInput.create(ProblemReporter.DISCARDING, registries,
                output.buildResult()), restored);
        assertEquals(64, restored.get(1).getCount());
        assertTrue(restored.get(1).is(Items.COAL));
    }

    @Test
    void tierChangesUpdateTheNewItemModelComponentWithoutLosingModes() {
        ItemStack stack = new ItemStack(Items.DIAMOND);
        MPGItemStackData.putBoolean(stack, "port_test_mode", true);
        MPGItemStackData.putInt(stack, MPGNBTData.ItemType, 8);
        assertEquals(8.0F, stack.get(DataComponents.CUSTOM_MODEL_DATA).floats().getFirst());
        MPGItemStackData.putInt(stack, MPGNBTData.ItemType, 0);
        assertFalse(stack.has(DataComponents.CUSTOM_MODEL_DATA));
        assertTrue(MPGItemStackData.getBoolean(stack, "port_test_mode"));
    }

    @Test
    void recipesDecodeBeforeItemComponentsAreBoundAndKeepTheirTierOverTheNetwork() {
        var ops = registries.createSerializationContext(com.mojang.serialization.JsonOps.INSTANCE);
        var json = com.google.gson.JsonParser.parseString("""
                {"pattern":["X"],"key":{"X":{"item":"minecraft:netherite_ingot","type":7}},
                 "result":{"item":"minecraft:netherite_ingot","count":2,"nbt":{"ManaitaType":7}}}
                """);
        assertFalse(Items.NETHERITE_INGOT.builtInRegistryHolder().areComponentsBound());
        var recipe = github.com.gengyoubo.common.recipe.MPNBTCraftingRecipe.MAP_CODEC.codec()
                .parse(ops, json).getOrThrow();
        assertEquals(1, recipe.display().size());
        var buffer = new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(), registries);
        try {
            var codec = github.com.gengyoubo.common.recipe.MPNBTCraftingRecipe.STREAM_CODEC;
            codec.encode(buffer, recipe);
            var decoded = codec.decode(buffer);
            Items.NETHERITE_INGOT.builtInRegistryHolder().bindComponents(DataComponents.COMMON_ITEM_COMPONENTS);
            var result = decoded.assemble(net.minecraft.world.item.crafting.CraftingInput.EMPTY);
            assertEquals(2, result.getCount());
            assertEquals(7, MPGItemStackData.getInt(result, MPGNBTData.ItemType));
            assertEquals(7.0F, result.get(DataComponents.CUSTOM_MODEL_DATA).floats().getFirst());
        } finally {
            buffer.release();
        }
    }
}
