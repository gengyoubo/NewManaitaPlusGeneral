package github.com.gengyoubo.common.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/** Replacement for the removed {@code Player#displayClientMessage(Component, boolean)}. */
public final class MPGMessages {
    private MPGMessages() {
    }

    public static void show(Player player, Component message, boolean overlay) {
        if (overlay) {
            player.sendOverlayMessage(message);
        } else {
            player.sendSystemMessage(message);
        }
    }
}
