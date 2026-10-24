package github.com.gengyoubo.common.integration.jei;

import github.com.gengyoubo.common.MPGCommon;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MPGSourceCopyRecipeCategory implements IRecipeCategory<MPGSourceCopyRecipe> {
    public static final RecipeType<MPGSourceCopyRecipe> TYPE =
            RecipeType.create(MPGCommon.MOD_ID, "source_copying", MPGSourceCopyRecipe.class);
    private static final int GRID_X = 1;
    private static final int GRID_Y = 1;
    private static final int SLOT_SIZE = 18;
    private static final int HIDDEN_SLOT = -1000;
    private static final ResourceLocation VANILLA_GUI = new ResourceLocation("jei", "textures/gui/gui_vanilla.png");

    private final IDrawable background;
    private final IDrawable icon;

    public MPGSourceCopyRecipeCategory(IGuiHelper guiHelper, ItemStack iconStack) {
        background = guiHelper.createDrawable(VANILLA_GUI, 0, 60, 116, 54);
        icon = guiHelper.createDrawableItemStack(iconStack);
    }

    @Override
    public @NotNull RecipeType<MPGSourceCopyRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.manaita_plus_general.source_copying");
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull MPGSourceCopyRecipe recipe,
                          @NotNull IFocusGroup focuses) {
        builder.setShapeless(58, 1);
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)
                .addItemStacks(recipe.sources()).addItemStacks(recipe.inputs());
        builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addItemStacks(recipe.outputs());
        builder.addSlot(RecipeIngredientRole.INPUT, GRID_X, GRID_Y)
                .addItemStacks(recipe.sources());
        builder.addSlot(RecipeIngredientRole.INPUT, HIDDEN_SLOT, HIDDEN_SLOT).addItemStacks(recipe.inputs());
        builder.addSlot(RecipeIngredientRole.OUTPUT, HIDDEN_SLOT, HIDDEN_SLOT).addItemStacks(recipe.outputs());
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, GRID_X + SLOT_SIZE, GRID_Y)
                .addItemStacks(recipe.inputs());
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 95, 19).addItemStacks(recipe.outputs());
    }

    @Override
    public ResourceLocation getRegistryName(MPGSourceCopyRecipe recipe) {
        return new ResourceLocation(MPGCommon.MOD_ID, "source_copying");
    }
}
