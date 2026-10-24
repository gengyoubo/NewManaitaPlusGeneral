package github.com.gengyoubo.MPG.integration;

import github.com.gengyoubo.MPG.item.ring.MPGRingItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICurio;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.InterModComms;
import net.minecraft.core.Direction;
import java.util.Optional;

final class CuriosIntegration {
    private CuriosIntegration() {}
    static void register() {
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE,
                () -> SlotTypePreset.RING.getMessageBuilder().size(2).build());
    }
    static ICapabilityProvider createProvider(ItemStack stack) {
        return new ICapabilityProvider() {
            private final LazyOptional<ICurio> curio = LazyOptional.of(() -> () -> stack);

            @Override
            public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction side) {
                return capability == CuriosCapability.ITEM ? curio.cast() : LazyOptional.empty();
            }
        };
    }
    static Optional<ItemStack> equippedRing(Player player) {
        return CuriosApi.getCuriosHelper().findFirstCurio(player, stack -> stack.getItem() instanceof MPGRingItem)
                .map(result -> result.stack());
    }
    static boolean equip(Player player, ItemStack stack) {
        var handler = CuriosApi.getCuriosHelper().getCuriosHandler(player).resolve()
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
