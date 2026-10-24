package github.com.gengyoubo.MPG.baubles.common.lib;

import github.com.gengyoubo.MPG.baubles.api.BaubleType;
import github.com.gengyoubo.MPG.baubles.common.capability.BaublesCapability;
import github.com.gengyoubo.MPG.baubles.common.container.InventoryBaubles;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.function.Predicate;

public final class PlayerHandler {
    private PlayerHandler() {
    }

    public static InventoryBaubles getPlayerBaubles(Player player) {
        return BaublesCapability.get(player);
    }

    public static boolean equipItem(Player player, ItemStack stack) {
        return github.com.gengyoubo.MPG.integration.MPGAccessories.equip(player, stack);
    }

    public static Optional<ItemStack> getEquippedRing(Player player) {
        return github.com.gengyoubo.MPG.integration.MPGAccessories.equippedRing(player);
    }

    public static Optional<ItemStack> findFirstMatching(Player player, BaubleType type, Predicate<ItemStack> predicate) {
        return BaublesCapability.find(player).flatMap(inventory -> inventory.findFirstMatching(type, predicate));
    }
}
