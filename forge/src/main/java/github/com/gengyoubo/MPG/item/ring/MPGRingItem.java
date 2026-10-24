package github.com.gengyoubo.MPG.item.ring;

import github.com.gengyoubo.MPG.compat.CuriosCompat;
import github.com.gengyoubo.common.item.data.IMPGKey;
import github.com.gengyoubo.MPG.item.portable.MPGPortableItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

/** Portable Manaita ring worn in a Curios ring slot; only registered when Curios is installed. */
public abstract class MPGRingItem extends MPGPortableItem implements ICurioItem, IMPGKey {
    protected MPGRingItem(String translationPrefix) {
        super(translationPrefix);
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return CuriosCompat.RING_SLOT.equals(slotContext.identifier());
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide && CuriosCompat.equipRing(player, itemInHand)) {
                return InteractionResultHolder.sidedSuccess(itemInHand, level.isClientSide);
            }
            return InteractionResultHolder.fail(itemInHand);
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
