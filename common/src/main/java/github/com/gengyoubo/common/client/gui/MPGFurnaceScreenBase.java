package github.com.gengyoubo.common.client.gui;

import net.minecraft.network.chat.TextComponent;
import github.com.gengyoubo.common.menu.MPGFurnaceMenuBase;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.SmeltingRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MPGFurnaceScreenBase<M extends MPGFurnaceMenuBase> extends AbstractFurnaceScreen<M> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("textures/gui/container/furnace.png");
    private static final ResourceLocation LIT_PROGRESS_SPRITE =
            new ResourceLocation("container/furnace/lit_progress");
    private static final ResourceLocation BURN_PROGRESS_SPRITE =
            new ResourceLocation("container/furnace/burn_progress");

    private final String doublingText;

    public MPGFurnaceScreenBase(M menu, Inventory inventory, Component title, int doublingValue) {
        super(menu, new SmeltingRecipeBookComponent(), inventory, title, TEXTURE);
        this.doublingText = doublingValue + "x";
    }

    @Override
    protected void renderLabels(PoseStack guiGraphics, int mouseX, int mouseY) {
        font.draw(guiGraphics, title, titleLabelX, titleLabelY, 4210752);
        font.draw(guiGraphics, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 4210752);
        font.draw(guiGraphics, getDoublingComponent(), 118, 22, 4210752);
    }

    protected Component getDoublingComponent() {
        return new TextComponent(doublingText);
    }
}
