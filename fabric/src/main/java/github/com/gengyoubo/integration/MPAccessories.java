package github.com.gengyoubo.integration;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import java.util.Optional;

/** Keeps Trinkets optional and avoids loading its API on installations without it. */
public final class MPAccessories {
    private MPAccessories() {}
    public static boolean isEnabled() { return FabricLoader.getInstance().isModLoaded("trinkets"); }
    public static void register() { if (isEnabled()) TrinketsIntegration.register(); }
    public static Optional<ItemStack> equippedRing(Player player) {
        return isEnabled() ? TrinketsIntegration.equippedRing(player) : Optional.empty();
    }
    public static boolean equip(Player player, ItemStack stack) {
        return isEnabled() && TrinketsIntegration.equip(player, stack);
    }
}
