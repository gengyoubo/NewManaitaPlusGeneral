package github.com.gengyoubo.MPG.item;

import net.minecraft.world.entity.EntitySpawnReason;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import github.com.gengyoubo.MPG.entity.MPGLightningBolt;
import github.com.gengyoubo.common.item.data.IMPGDoubling;
import github.com.gengyoubo.common.item.data.IMPGKey;
import github.com.gengyoubo.common.item.tier.MPGToolMaterials;
import github.com.gengyoubo.MPG.network.Networking;
import github.com.gengyoubo.common.network.payload.MPGChangeEntityDataPayload;
import github.com.gengyoubo.common.entity.MPGEntityData;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import github.com.gengyoubo.common.util.MPText;
import github.com.gengyoubo.MPG.util.MPUtils;

import java.util.List;
import java.util.Random;

import static github.com.gengyoubo.MPG.core.MPGEntityCore.ManaitaLightningBolt;

public class MPGGodSwordItem extends Item implements IMPGKey, IMPGDoubling {
    public MPGGodSwordItem(Item.Properties properties) {
        super(MPGToolMaterials.sword(properties).enchantable(1));
    }




    @Override
    public boolean onDroppedByPlayer(ItemStack item, Player player) {
        MPGEntityData.manaita.remove(player);
        Networking.sendToSameLevelPlayers(player.level(), new MPGChangeEntityDataPayload(player.getId(), -MPGEntityData.death.getFlag()));
        return super.onDroppedByPlayer(item, player);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack p_41404_, @NotNull ServerLevel p_41405_, @NotNull Entity p_41406_, @Nullable EquipmentSlot slot) {
        if (p_41406_ instanceof  Player player) {
            player.getAbilities().mayfly = true;
            player.setHealth(player.getMaxHealth());
        }
        MPGEntityData.manaita.add(p_41406_);
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity, InteractionHand hand) {
        if (entity instanceof Player player) {
            MPUtils.godKill(player,isRemove(stack),player.isShiftKeyDown());
        }
        return false;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        MPUtils.attack(entity, player,isRemove(stack));
        return true;
    }

    @Override
    public @NotNull InteractionResult use(Level p_41432_, Player player, @NotNull InteractionHand p_41434_) {
        ItemStack itemstack = player.getItemInHand(p_41434_);
        player.startUsingItem(p_41434_);
        if (!p_41432_.isClientSide()) {
            Random random = new Random();
            Vec3 position = player.position();
            for (int i = 0; i < 100; i++) {
                MPGLightningBolt bolt = ManaitaLightningBolt.get().create(p_41432_, EntitySpawnReason.TRIGGERED);
                if (bolt != null) {
                    float angle = random.nextFloat() * 62.831852F;
                    double distance = random.nextGaussian() * 100.0D;
                    double x = Mth.sin(angle) * distance + position.x;
                    double z = Mth.cos(angle) * distance + position.z;

                    int y = p_41432_.getHeight(Heightmap.Types.WORLD_SURFACE_WG, (int) x, (int) z);

                    bolt.setPos(x, y, z);
                    p_41432_.addFreshEntity(bolt);
                }
            }
        }
        MPUtils.godKill(player,isRemove(itemstack),player.isShiftKeyDown());
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack p_41421_, @NotNull TooltipContext context,
            @NotNull TooltipDisplay display, @NotNull Consumer<Component> p_41423_, @NotNull TooltipFlag p_41424_) {
        super.appendHoverText(p_41421_, context, display, p_41423_, p_41424_);
        p_41423_.accept(Component.literal(MPText.manaita_mode.formatting(Component.translatable("mode.doubling").getString() + ":" + (isDoubling(p_41421_) ? Component.translatable("info.on").getString() : Component.translatable("info.off").getString()))));
        p_41423_.accept(Component.literal(MPText.manaita_mode.formatting(Component.translatable("mode.remove.name").getString() + ":" + (isRemove(p_41421_) ? Component.translatable("info.on").getString() : Component.translatable("info.off").getString()))));
        p_41423_.accept(Component.empty());
        p_41423_.accept(Component.literal(MPText.manaita_enchantment.formatting(Component.translatable("info.item.manaita_sword_god.1").getString())));
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack p_41458_) {
        return Component.literal(MPText.manaita_infinity.formatting(Component.translatable("item.manaita_sword_god.name").getString()));
    }

    @Override
    public int getUseDuration(@NotNull ItemStack p_41454_, @NotNull LivingEntity entity) {
        return 72000;
    }

    @Override
    public @NotNull ItemUseAnimation getUseAnimation(@NotNull ItemStack p_41452_) {
        return ItemUseAnimation.BLOCK;
    }

    @Override
    public boolean isFoil(@NotNull ItemStack p_41453_) {
        return true;
    }

// --娉ㄩ噴鎺夋鏌?START (2026/4/24 23:35):
//    public void onManaitaKeyPress(ItemStack itemStack, Player player) {
//        if (player.isShiftKeyDown()) {
//            boolean remove = !isRemove(itemStack);
//            setRemove(itemStack, remove);
//        } else {
//            boolean doubling = !isDoubling(itemStack);
//            setDoubling(itemStack, doubling);
//        }
//    }
// --娉ㄩ噴鎺夋鏌?STOP (2026/4/24 23:35)

    @Override
    public void onManaitaKeyPress(ItemStack itemStack) {
        toggleDoubling(itemStack);
    }

    @Override
    public void onManaitaKeyPressOnClient(ItemStack itemStack, Player player) {
        if (player.isShiftKeyDown()) {
            boolean remove = !isRemove(itemStack);
            setRemove(itemStack, remove);
            MPUtils.chat(player, Component.literal(MPText.manaita_mode.formatting(String.format("[%s] %s: %s", Component.translatable("item.manaita_sword_god.name").getString(), Component.translatable("mode.remove.name").getString(), (remove ? Component.translatable("info.on").getString() : Component.translatable("info.off").getString())))));
        } else {
            boolean doubling = toggleDoubling(itemStack);
            MPUtils.chat(player, Component.literal(MPText.manaita_mode.formatting(String.format("[%s] %s: %s", Component.translatable("item.manaita_sword_god.name").getString(), Component.translatable("mode.doubling").getString(), (doubling ? Component.translatable("info.on").getString() : Component.translatable("info.off").getString())))));
        }
    }


    public static boolean isRemove(ItemStack itemStack) {
        return MPGItemStackData.getBoolean(itemStack, MPGNBTData.Remove);
    }

    public static void setRemove(ItemStack itemStack,boolean remove) {
        MPGItemStackData.putBoolean(itemStack, MPGNBTData.Remove, remove);
    }
}
