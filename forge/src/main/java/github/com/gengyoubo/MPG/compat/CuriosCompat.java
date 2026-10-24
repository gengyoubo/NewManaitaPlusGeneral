package github.com.gengyoubo.MPG.compat;

import github.com.gengyoubo.MPG.item.ring.MPGRingItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.InterModComms;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

/** Curios integration. Only call this class after checking {@code MPGItemCore.isCuriosLoaded()}. */
public final class CuriosCompat {
    public static final String MOD_ID = "curios";
    public static final String RING_SLOT = "ring";

    private CuriosCompat() {
    }

    public static void registerSlots() {
        InterModComms.sendTo(MOD_ID, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.RING.getMessageBuilder().build());
    }

    public static ItemStack findEquippedRing(Player player) {
        return CuriosApi.getCuriosHelper().findCurios(player, stack -> stack.getItem() instanceof MPGRingItem)
                .stream()
                .findFirst()
                .map(SlotResult::stack)
                .orElse(ItemStack.EMPTY);
    }

    public static boolean equipRing(Player player, ItemStack stack) {
        return CuriosApi.getCuriosHelper().getCuriosHandler(player).resolve()
                .flatMap(handler -> handler.getStacksHandler(RING_SLOT))
                .map(handler -> {
                    IDynamicStackHandler stacks = handler.getStacks();
                    for (int slot = 0; slot < stacks.getSlots(); slot++) {
                        if (stacks.getStackInSlot(slot).isEmpty()) {
                            stacks.setStackInSlot(slot, stack.split(1));
                            return true;
                        }
                    }
                    return false;
                })
                .orElse(false);
    }
}
