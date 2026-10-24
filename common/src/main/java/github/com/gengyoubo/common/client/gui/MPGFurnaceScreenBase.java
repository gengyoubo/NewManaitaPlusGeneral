package github.com.gengyoubo.common.client.gui;

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

    private final String doublingText;

    public MPGFurnaceScreenBase(M menu, Inventory inventory, Component title, int doublingValue) {
        super(menu, new SmeltingRecipeBookComponent(), inventory, title, TEXTURE);
        this.doublingText = doublingValue + "x";
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        font.draw(poseStack, title, titleLabelX, titleLabelY, 4210752);
        font.draw(poseStack, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 4210752);
        font.draw(poseStack, getDoublingComponent(), 118, 22, 4210752);
    }

    protected Component getDoublingComponent() {
        return Component.literal(doublingText);
    }
}
