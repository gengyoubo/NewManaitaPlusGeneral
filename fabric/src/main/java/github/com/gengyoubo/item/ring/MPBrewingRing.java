package github.com.gengyoubo.item.ring;

import dev.emi.trinkets.api.Trinket;
import github.com.gengyoubo.common.item.data.IMPGKey;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import github.com.gengyoubo.compat.TrinketsCompat;
import github.com.gengyoubo.item.portable.MPBrewingPortable;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/** Portable brewing ring worn in a Trinkets ring slot; only registered when Trinkets is installed. */
public class MPBrewingRing extends MPBrewingPortable implements Trinket, IMPGKey {
    public MPBrewingRing() {
        TrinketsCompat.registerRing(this);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return Component.translatable("item.ringBrewing." + MPGItemStackData.getInt(stack, MPGNBTData.ItemType) + ".name");
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (player.isShiftKeyDown()) {
            return TrinketsCompat.equipFromUse(level, player, player.getItemInHand(hand));
        }
        return super.use(level, player, hand);
    }

    @Override
    public void onManaitaKeyPress(ItemStack itemStack, Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            openPortableMenu(serverPlayer, itemStack, player.getLevel());
        }
    }
}
