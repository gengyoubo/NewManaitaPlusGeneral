package github.com.gengyoubo.common.recipe;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

/** Shared source-copy custom recipe. Loader classes only supply registered objects/config. */
public abstract class MPGSourceCopyRecipeBase extends CustomRecipe {
    private final Supplier<? extends Item> sourceItem;
    private final IntSupplier multiplier;
    private final Supplier<? extends RecipeSerializer<?>> serializer;

    protected MPGSourceCopyRecipeBase(net.minecraft.resources.ResourceLocation id, Supplier<? extends Item> sourceItem,
                                      IntSupplier multiplier, Supplier<? extends RecipeSerializer<?>> serializer) {
        super(id);
        this.sourceItem = sourceItem;
        this.multiplier = multiplier;
        this.serializer = serializer;
    }

    @Override
    public boolean matches(CraftingContainer input, @NotNull Level level) {
        int sourceCount = 0;
        int nonEmptyCount = 0;
        for (int slot = 0; slot < input.getContainerSize(); slot++) {
            ItemStack stack = input.getItem(slot);
            if (!stack.isEmpty()) {
                nonEmptyCount++;
                if (stack.is(sourceItem.get())) {
                    sourceCount++;
                }
            }
        }
        return nonEmptyCount == 2 && sourceCount >= 1;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingContainer input) {
        boolean consumedSource = false;
        for (int slot = 0; slot < input.getContainerSize(); slot++) {
            ItemStack stack = input.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (!consumedSource && stack.is(sourceItem.get())) {
                consumedSource = true;
                continue;
            }
            ItemStack output = stack.copy();
            output.setCount(Math.max(1, multiplier.getAsInt()));
            return output;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull ItemStack getResultItem() {
        return sourceItem.get().getDefaultInstance();
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return serializer.get();
    }
}
