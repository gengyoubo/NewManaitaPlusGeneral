package github.com.gengyoubo.common.client.gui;

import github.com.gengyoubo.common.block.entity.MPGBrewingStandBlockEntityBase;
import github.com.gengyoubo.common.config.MPGConfigValues;
import github.com.gengyoubo.common.menu.MPGBrewingStandMenuBase;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

/** Shared brewing stand screen for all loaders. */
public class MPGBrewingStandScreenBase<M extends MPGBrewingStandMenuBase> extends AbstractContainerScreen<M> {
    private static final Identifier FUEL_LENGTH_SPRITE =
            Identifier.withDefaultNamespace("container/brewing_stand/fuel_length");
    private static final Identifier BREW_PROGRESS_SPRITE =
            Identifier.withDefaultNamespace("container/brewing_stand/brew_progress");
    private static final Identifier BUBBLES_SPRITE = Identifier.withDefaultNamespace("container/brewing_stand/bubbles");
    private static final Identifier BREWING_STAND_LOCATION =
            Identifier.withDefaultNamespace("textures/gui/container/brewing_stand.png");
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
    protected void extractLabels(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        graphics.text(font, doublingText, titleLabelX + font.width(title), titleLabelY, MPGScreenColors.LABEL, false);
    }

    @Override
    public void extractBackground(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int left = (width - imageWidth) / 2;
        int top = (height - imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, BREWING_STAND_LOCATION, left, top, 0.0F, 0.0F,
                imageWidth, imageHeight, 256, 256);

        int fuelWidth = Mth.clamp((18 * menu.getFuel() + 19) / 20, 0, 18);
        if (fuelWidth > 0) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, FUEL_LENGTH_SPRITE, 18, 4, 0, 0,
                    left + 60, top + 44, fuelWidth, 4);
        }

        int brewingTicks = menu.getBrewingTicks();
        if (brewingTicks <= 0) {
            return;
        }
        int progress = (int) (28.0F * (1.0F - (float) brewingTicks / MPGBrewingStandBlockEntityBase.BREW_TIME));
        if (progress > 0) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BREW_PROGRESS_SPRITE, 9, 28, 0, 0,
                    left + 97, top + 16, 9, progress);
        }
        int bubbleHeight = BUBBLE_LENGTHS[Math.max(0, brewingTicks - 1) % BUBBLE_LENGTHS.length];
        if (bubbleHeight > 0) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BUBBLES_SPRITE, 12, 29, 0, 29 - bubbleHeight,
                    left + 63, top + 14 + 29 - bubbleHeight, 12, bubbleHeight);
        }
    }
}
