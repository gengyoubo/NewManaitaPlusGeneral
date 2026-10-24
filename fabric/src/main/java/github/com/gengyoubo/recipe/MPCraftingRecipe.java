package github.com.gengyoubo.recipe;

import com.mojang.serialization.MapCodec;
import github.com.gengyoubo.MPGConfig;
import github.com.gengyoubo.core.MPItemCore;
import github.com.gengyoubo.common.recipe.MPGSourceCopyRecipeBase;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class MPCraftingRecipe extends MPGSourceCopyRecipeBase {
    public static final MPCraftingRecipe INSTANCE = new MPCraftingRecipe();
    public static final RecipeSerializer<MPCraftingRecipe> SERIALIZER =
            new RecipeSerializer<>(MapCodec.unit(INSTANCE), StreamCodec.unit(INSTANCE));

    private MPCraftingRecipe() {
        super(MPItemCore.ManaitaSource, () -> MPGConfig.source_doubling_value, () -> SERIALIZER);
    }
}
