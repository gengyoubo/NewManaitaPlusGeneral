package github.com.gengyoubo.MPG.integration;

import github.com.gengyoubo.MPG.core.MPGItemCore;
import github.com.gengyoubo.MPG.item.ring.MPGRingItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import java.util.Optional;

final class CuriosIntegration {
    private CuriosIntegration() {}
    static void register() {
        ICurioItem behavior = new ICurioItem() {};
        CuriosApi.registerCurio(MPGItemCore.ManaitaCraftingRing.get(), behavior);
        CuriosApi.registerCurio(MPGItemCore.ManaitaFurnaceRing.get(), behavior);
        CuriosApi.registerCurio(MPGItemCore.ManaitaBrewingRing.get(), behavior);
    }
    static Optional<ItemStack> equippedRing(Player player) {
        return CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(inventory -> inventory.findFirstCurio(stack -> stack.getItem() instanceof MPGRingItem))
                .map(result -> result.stack());
    }
    static boolean equip(Player player, ItemStack stack) {
        var handler = CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(inventory -> inventory.getStacksHandler("ring"));
        if (handler.isEmpty()) return false;
        var slots = handler.get().getStacks();
        for (int slot = 0; slot < slots.getSlots(); slot++) {
            if (!slots.insertItem(slot, stack.copy(), true).isEmpty()) continue;
            slots.insertItem(slot, stack.copy(), false);
            stack.setCount(0);
            return true;
        }
        return false;
    }
}
