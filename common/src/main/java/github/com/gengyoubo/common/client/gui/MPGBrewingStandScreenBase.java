package github.com.gengyoubo.common.client.gui;

import github.com.gengyoubo.common.block.entity.MPGBrewingStandBlockEntityBase;
import github.com.gengyoubo.common.config.MPGConfigValues;
import github.com.gengyoubo.common.menu.MPGBrewingStandMenuBase;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

/** Shared brewing stand screen for all loaders. */
public class MPGBrewingStandScreenBase<M extends MPGBrewingStandMenuBase> extends AbstractContainerScreen<M> {
    private static final ResourceLocation BREWING_STAND_LOCATION =
            new ResourceLocation("textures/gui/container/brewing_stand.png");
    private static final int[] BUBBLE_LENGTHS = {29, 24, 20, 16, 11, 6, 0};
    private final String doublingText;

    public MPGBrewingStandScreenBase(M menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        doublingText = MPGConfigValues.brewing_doubling_value + "x";
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;
    }

    @Override
    public void render(@NotNull PoseStack graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(@NotNull PoseStack graphics, int mouseX, int mouseY) {
        font.draw(graphics, title, titleLabelX, titleLabelY, 4210752);
        font.draw(graphics, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 4210752);
        font.draw(graphics, doublingText, titleLabelX + font.width(title), titleLabelY, 4210752);
    }

    @Override
    protected void renderBg(@NotNull PoseStack graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BREWING_STAND_LOCATION);
        int left = (width - imageWidth) / 2;
        int top = (height - imageHeight) / 2;
        blit(graphics, left, top, 0, 0, imageWidth, imageHeight);

        int fuelWidth = Mth.clamp((18 * menu.getFuel() + 19) / 20, 0, 18);
        if (fuelWidth > 0) {
            blit(graphics, left + 60, top + 44, 176, 29, fuelWidth, 4);
        }

        int brewingTicks = menu.getBrewingTicks();
        if (brewingTicks <= 0) {
            return;
        }

        int progress = (int) (28.0F * (1.0F - (float) brewingTicks
                / MPGBrewingStandBlockEntityBase.BREW_TIME));
        if (progress > 0) {
            blit(graphics, left + 97, top + 16, 176, 0, 9, progress);
        }

        int bubbleHeight = BUBBLE_LENGTHS[Math.max(0, brewingTicks - 1) % BUBBLE_LENGTHS.length];
        if (bubbleHeight > 0) {
            blit(graphics, left + 63, top + 43 - bubbleHeight,
                    185, 29 - bubbleHeight, 12, bubbleHeight);
        }
    }
}
