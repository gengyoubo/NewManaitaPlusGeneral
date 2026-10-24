package github.com.gengyoubo.common.client.gui;

import github.com.gengyoubo.common.config.MPGConfigValues;
import github.com.gengyoubo.common.menu.MPGCraftingMenuBase;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.CraftingRecipeBookComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

/** Shared crafting screen; the recipe book plumbing comes from {@link AbstractRecipeBookScreen}. */
public class MPGCraftingScreenBase<M extends MPGCraftingMenuBase> extends AbstractRecipeBookScreen<M> {
    private static final Identifier CRAFTING_TABLE_LOCATION =
            Identifier.withDefaultNamespace("textures/gui/container/crafting_table.png");
    private final String doublingText;

    public MPGCraftingScreenBase(M menu, Inventory inventory, Component title) {
        super(menu, new CraftingRecipeBookComponent(menu), inventory, title);
        doublingText = MPGConfigValues.crafting_doubling_value + "x";
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = 29;
    }

    @Override
    protected @NotNull ScreenPosition getRecipeBookButtonPosition() {
        return new ScreenPosition(leftPos + 5, height / 2 - 49);
    }

    @Override
    protected void extractLabels(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        graphics.text(font, doublingText, 126, 22, MPGScreenColors.LABEL, false);
    }

    @Override
    public void extractBackground(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, CRAFTING_TABLE_LOCATION, leftPos, (height - imageHeight) / 2,
                0.0F, 0.0F, imageWidth, imageHeight, 256, 256);
    }
}
