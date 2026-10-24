package github.com.gengyoubo.MPG.recipe;

import com.mojang.serialization.MapCodec;
import github.com.gengyoubo.MPG.MPGConfig;
import github.com.gengyoubo.MPG.core.MPGItemCore;
import github.com.gengyoubo.common.recipe.MPGSourceCopyRecipeBase;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class MPGCraftingRecipe extends MPGSourceCopyRecipeBase {
    public static final MPGCraftingRecipe INSTANCE = new MPGCraftingRecipe();
    public static final RecipeSerializer<MPGCraftingRecipe> SERIALIZER =
            new RecipeSerializer<>(MapCodec.unit(INSTANCE), StreamCodec.unit(INSTANCE));

    private MPGCraftingRecipe() {
        super(MPGItemCore.ManaitaSource, () -> MPGConfig.source_doubling_value, () -> SERIALIZER);
    }
}
