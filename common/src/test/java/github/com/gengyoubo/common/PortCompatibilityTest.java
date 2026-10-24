package github.com.gengyoubo.common;

import com.google.gson.JsonParser;
import github.com.gengyoubo.common.recipe.MPNBTCraftingRecipe;
import github.com.gengyoubo.common.util.MPGInventoryData;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import io.netty.buffer.Unpooled;
import net.minecraft.SharedConstants;
import net.minecraft.core.NonNullList;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PortCompatibilityTest {
    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void multipliedInventorySurvivesSaveAndReload() {
        NonNullList<ItemStack> original = NonNullList.withSize(3, ItemStack.EMPTY);
        original.set(2, new ItemStack(Items.DIAMOND, 4096));
        MPGItemStackData.putInt(original.get(2), MPGNBTData.ItemType, 7);
        CompoundTag saved = new CompoundTag();
        MPGInventoryData.saveAllItems(saved, original);
        NonNullList<ItemStack> restored = NonNullList.withSize(3, ItemStack.EMPTY);
        MPGInventoryData.loadAllItems(saved, restored);
        assertTrue(restored.get(0).isEmpty());
        assertEquals(4096, restored.get(2).getCount());
        assertEquals(7, MPGItemStackData.getInt(restored.get(2), MPGNBTData.ItemType));
    }

    @Test
    void vanillaInventoryDataRemainsReadable() {
        NonNullList<ItemStack> original = NonNullList.withSize(3, ItemStack.EMPTY);
        original.set(1, new ItemStack(Items.COAL, 64));
        CompoundTag saved = new CompoundTag();
        net.minecraft.world.ContainerHelper.saveAllItems(saved, original);
        NonNullList<ItemStack> restored = NonNullList.withSize(3, ItemStack.EMPTY);
        MPGInventoryData.loadAllItems(saved, restored);
        assertEquals(64, restored.get(1).getCount());
        assertTrue(restored.get(1).is(Items.COAL));
    }

    @Test
    void legacyRecipeTypeSurvivesNetworkRoundTrip() {
        var serializer = new MPNBTCraftingRecipe.Serializer();
        var id = new ResourceLocation("manaita_plus_general", "port_test");
        var recipe = serializer.fromJson(id, JsonParser.parseString("""
                {"pattern":["X"],"key":{"X":{"item":"minecraft:diamond","type":7}},
                 "result":{"item":"minecraft:diamond","count":2,"nbt":{"ManaitaType":7}}}
                """).getAsJsonObject());
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            serializer.toNetwork(buffer, recipe);
            var decoded = serializer.fromNetwork(id, buffer);
            assertEquals(id, decoded.getId());
            ItemStack result = decoded.getResultItem();
            assertEquals(2, result.getCount());
            assertEquals(7, MPGItemStackData.getInt(result, MPGNBTData.ItemType));
            assertEquals(7, MPGItemStackData.getInt(decoded.getIngredients().get(0).getItems()[0], MPGNBTData.ItemType));
        } finally {
            buffer.release();
        }
    }
}
