package github.com.gengyoubo.item;

import net.minecraft.world.item.Item;
import github.com.gengyoubo.common.item.MPGBowItemBase;
import github.com.gengyoubo.entity.MPGEntityArrow;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MPBowItem extends MPGBowItemBase {
    private static final int CHARGE_TICKS = 10;
    private static final int RAPID_FIRE_INTERVAL = 2;

    public MPBowItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult use(Level level, @NotNull Player player,
                                                            @NotNull InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public void onUseTick(Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack stack,
                          int remainingUseDuration) {
        if (level.isClientSide() || !(livingEntity instanceof Player player)) {
            return;
        }

        int elapsed = getUseDuration(stack) - remainingUseDuration;
        if (elapsed >= CHARGE_TICKS && (elapsed - CHARGE_TICKS) % RAPID_FIRE_INTERVAL == 0) {
            shootArrow(level, player, stack, player.getUsedItemHand());
        }
    }

    public int getUseDuration(@NotNull ItemStack stack) {
        return 72000;
    }

    public @NotNull ItemUseAnimation getUseAnimation(@NotNull ItemStack stack) {
        return ItemUseAnimation.BOW;
    }

    @Override
    protected boolean keyMessageUsesOverlay() {
        return true;
    }

    private void shootArrow(Level level, Player player, ItemStack stack, InteractionHand hand) {
        AbstractArrow arrow = MPGEntityArrow.create(level, player);
        arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 10.0F, 1.0F);
        arrow.setCritArrow(true);
        arrow.setBaseDamage(isDoubling(stack) ? 40.0D : 20.0D);
        arrow.setSilent(true);
        level.addFreshEntity(arrow);
        stack.hurtAndBreak(1, player, hand.asEquipmentSlot());
    }
}
