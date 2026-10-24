package github.com.gengyoubo.recipe;

import github.com.gengyoubo.MPGConfig;
import github.com.gengyoubo.common.recipe.MPGSourceCopyRecipeBase;
import github.com.gengyoubo.core.MPItemCore;
import github.com.gengyoubo.core.MPRecipeSerializerCore;

import net.minecraft.world.item.crafting.SimpleRecipeSerializer;

public class MPCraftingRecipe extends MPGSourceCopyRecipeBase {
    public MPCraftingRecipe(net.minecraft.resources.ResourceLocation id) {
        super(id, MPItemCore.ManaitaSource, () -> MPGConfig.source_doubling_value,
                () -> MPRecipeSerializerCore.CraftingRecipe.get());
    }

    public static class Serializer extends SimpleRecipeSerializer<MPCraftingRecipe> {
        public Serializer() {
            super(MPCraftingRecipe::new);
        }
    }
}
