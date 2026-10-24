package github.com.gengyoubo.common.item;

import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;
import github.com.gengyoubo.common.util.MPText;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Shared source-item behaviour; platform subclasses only open their menu. */
public abstract class MPGSourceItemBase extends Item {
    protected MPGSourceItemBase(Item.Properties properties) {
        super(properties.fireResistant());
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return Component.literal(MPText.manaita_mode.formatting(translate("item.source.name")));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
            @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);
        tooltip.accept(Component.literal(MPText.manaita_infinity.formatting(translate("info.source.1"))));
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player,
                                                            @NotNull InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            openCraftingMenu(serverPlayer, level);
        }
        return InteractionResult.SUCCESS;
    }

    protected abstract void openCraftingMenu(ServerPlayer player, Level level);

    protected static String translate(String key) {
        return Component.translatable(key).getString();
    }
}
