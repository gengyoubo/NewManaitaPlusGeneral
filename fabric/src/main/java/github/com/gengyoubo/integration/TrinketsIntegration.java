package github.com.gengyoubo.integration;

import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import github.com.gengyoubo.core.MPItemCore;
import github.com.gengyoubo.item.ring.MPRingItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import java.util.Optional;

final class TrinketsIntegration {
    private TrinketsIntegration() {}
    static void register() {
        Trinket behavior = new Trinket() {};
        TrinketsApi.registerTrinket(MPItemCore.ManaitaCraftingRing.get(), behavior);
        TrinketsApi.registerTrinket(MPItemCore.ManaitaFurnaceRing.get(), behavior);
        TrinketsApi.registerTrinket(MPItemCore.ManaitaBrewingRing.get(), behavior);
    }
    static Optional<ItemStack> equippedRing(Player player) {
        return TrinketsApi.getTrinketComponent(player)
                .flatMap(component -> component.getEquipped(stack -> stack.getItem() instanceof MPRingItem)
                        .stream().findFirst().map(pair -> pair.getB()));
    }
    static boolean equip(Player player, ItemStack stack) { return TrinketItem.equipItem(player, stack); }
}
