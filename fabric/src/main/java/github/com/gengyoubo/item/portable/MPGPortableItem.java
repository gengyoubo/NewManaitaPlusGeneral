package github.com.gengyoubo.item.portable;

import net.minecraft.world.item.Item;
import github.com.gengyoubo.common.item.MPGPortableItemBase;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class MPGPortableItem extends MPGPortableItemBase {
    protected MPGPortableItem(Item.Properties properties, String translationPrefix) {
        super(properties, translationPrefix);
    }

    protected final void openPortableScreen(ServerPlayer serverPlayer, ItemStack itemInHand, Level level,
                                            String titleKey, PortableMenuFactory menuFactory) {
        serverPlayer.openMenu(new ExtendedMenuProvider<BlockPos>() {
            @Override
            public BlockPos getScreenOpeningData(ServerPlayer player) {
                return BlockPos.ZERO;
            }

            @Override
            public @NotNull Component getDisplayName() {
                return Component.translatable(titleKey);
            }

            @Override
            public @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory,
                                                              @NotNull Player player) {
                return menuFactory.create(containerId, inventory, player, itemInHand, level);
            }
        });
    }
}
