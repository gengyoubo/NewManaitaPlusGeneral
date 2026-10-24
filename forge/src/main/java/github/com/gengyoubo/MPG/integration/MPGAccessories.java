package github.com.gengyoubo.MPG.integration;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import java.util.Optional;

/** Optional Curios entry points; the bridge loads only when Curios is present. */
public final class MPGAccessories {
    private MPGAccessories() {}
    public static boolean isEnabled() { return ModList.get().isLoaded("curios"); }
    public static void register() { if (isEnabled()) CuriosIntegration.register(); }
    public static net.minecraftforge.common.capabilities.ICapabilityProvider createProvider(ItemStack stack) {
        return isEnabled() ? CuriosIntegration.createProvider(stack) : null;
    }
    public static Optional<ItemStack> equippedRing(Player player) {
        return isEnabled() ? CuriosIntegration.equippedRing(player) : Optional.empty();
    }
    public static boolean equip(Player player, ItemStack stack) {
        return isEnabled() && CuriosIntegration.equip(player, stack);
    }
}
