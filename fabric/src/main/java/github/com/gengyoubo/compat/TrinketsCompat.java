package github.com.gengyoubo.compat;

import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import github.com.gengyoubo.common.item.data.IMPGKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Trinkets integration. Only call this class after checking {@code MPItemCore.isTrinketsLoaded()}. */
public final class TrinketsCompat {
    public static final String MOD_ID = "trinkets";

    private TrinketsCompat() {
    }

    public static <T extends Item & Trinket> void registerRing(T ring) {
        TrinketsApi.registerTrinket(ring, ring);
    }

    public static ItemStack findEquippedRing(Player player) {
        return TrinketsApi.getTrinketComponent(player)
                .flatMap(component -> component.getEquipped(stack -> stack.getItem() instanceof Trinket
                        && stack.getItem() instanceof IMPGKey).stream().findFirst())
                .map(Tuple::getB)
                .orElse(ItemStack.EMPTY);
    }

    /** Shift-use equips the ring into the first free Trinkets ring slot. */
    public static InteractionResultHolder<ItemStack> equipFromUse(Level level, Player player, ItemStack stack) {
        if (!level.isClientSide && TrinketItem.equipItem(player, stack)) {
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        return InteractionResultHolder.fail(stack);
    }
}
