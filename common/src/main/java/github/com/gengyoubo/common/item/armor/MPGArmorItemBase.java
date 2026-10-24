package github.com.gengyoubo.common.item.armor;

import github.com.gengyoubo.common.config.MPGConfigValues;
import github.com.gengyoubo.common.entity.MPGEntityData;
import github.com.gengyoubo.common.item.data.IMPGKey;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPText;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MPGArmorItemBase extends ArmorItem {
    public static final ArmorMaterial MANAITA_ARMOR_MATERIAL = new ArmorMaterial() {
        public int getDurabilityForSlot(EquipmentSlot slot) { return 0; }
        public int getDefenseForSlot(EquipmentSlot slot) { return 0; }
        public int getEnchantmentValue() { return 0; }
        public net.minecraft.sounds.SoundEvent getEquipSound() { return SoundEvents.ARMOR_EQUIP_TURTLE; }
        public Ingredient getRepairIngredient() { return Ingredient.EMPTY; }
        public String getName() { return "manaita_plus_general:manaita_armor"; }
        public float getToughness() { return 0; }
        public float getKnockbackResistance() { return 0; }
    };

    private static final int FAST_REGENERATION_DURATION = 60;
    /** 1.19.2 has no infinite effect duration; refresh long effects well before they expire. */
    private static final int INFINITE_DURATION = 1_000_000;
    private static final int FAST_REGENERATION_AMPLIFIER = 4;
    private static final double[] BOOTS_JUMP_STRENGTH = {
            0.0D, 0.4635D, 0.6175D, 0.6850D, 0.8030D, 0.9095D, 1.0075D, 1.0985D, 1.1850D
    };
    private static final String HELMET_REGENERATION_TAG = "manaita_plus_general.helmet_regeneration";
    private static final String HELMET_WATER_BREATHING_TAG = "manaita_plus_general.helmet_water_breathing";
    private static final String HELMET_NIGHT_VISION_TAG = "manaita_plus_general.helmet_night_vision";
    private static final String CHESTPLATE_FLIGHT_TAG = "manaita_plus_general.chestplate_flight";
    private static final String CHESTPLATE_FALL_TAG = "manaita_plus_general.chestplate_fall";
    private static final String CHESTPLATE_BIG_FALL_TAG = "manaita_plus_general.chestplate_big_fall";
    private static final String LEGGINGS_INVISIBILITY_TAG = "manaita_plus_general.leggings_invisibility";
    private static final java.util.UUID BOOTS_SPEED_MODIFIER_ID = java.util.UUID.nameUUIDFromBytes("manaita_plus_general:boots_speed".getBytes(java.nio.charset.StandardCharsets.UTF_8));
    private static final java.util.UUID BOOTS_JUMP_MODIFIER_ID = java.util.UUID.nameUUIDFromBytes("manaita_plus_general:boots_jump".getBytes(java.nio.charset.StandardCharsets.UTF_8));

    protected MPGArmorItemBase(ArmorMaterial material, EquipmentSlot slot) {
        super(material, slot, new Item.Properties().fireResistant());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull net.minecraft.world.level.Level context,
                                List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal(MPText.manaita_infinity.formatting(text("info.armor"))));
    }

    public static void syncArmorState(Player player) {
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);

        if (!player.getLevel().isClientSide) {
            if (helmet.getItem() instanceof Helmet) {
                applyHelmetEffects(player);
            } else {
                removeHelmetEffects(player);
            }
            syncChestplate(player, chestplate);
            syncLeggings(player, leggings);
            syncBoots(player, boots);
        }
    }

    public static boolean hasManaitaHelmet(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof Helmet;
    }

    public static boolean hasManaitaChestplate(Player player) {
        return player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof Chestplate;
    }

    public static boolean hasManaitaLeggings(Player player) {
        return player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof Leggings;
    }

    public static boolean hasManaitaBoots(Player player) {
        return player.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof Boots;
    }

    public static boolean hasManaitaArmorPiece(Player player) {
        return hasManaitaHelmet(player) || hasManaitaChestplate(player)
                || hasManaitaLeggings(player) || hasManaitaBoots(player);
    }

    public static boolean shouldCancelDamage(Player player, DamageSource source) {
        if (!hasManaitaArmorPiece(player)) {
            return false;
        }
        boolean onlyLeggings = hasManaitaLeggings(player)
                && !hasManaitaHelmet(player)
                && !hasManaitaChestplate(player)
                && !hasManaitaBoots(player);
        return !onlyLeggings || !source.isFire() || !isTouchingFire(player);
    }

    private static void syncChestplate(Player player, ItemStack chestplate) {
        if (chestplate.getItem() instanceof Chestplate) {
            removeHarmfulEffects(player);
            syncChestplateLandingSound(player);
            if (!player.isCreative() && !player.isSpectator()) {
                player.addTag(CHESTPLATE_FLIGHT_TAG);
            }
            if (!player.getAbilities().mayfly) {
                player.getAbilities().mayfly = true;
                updateAbilities(player);
            }
            return;
        }

        if (player.getTags().contains(CHESTPLATE_FLIGHT_TAG)) {
            player.removeTag(CHESTPLATE_FLIGHT_TAG);
            if (!player.isCreative() && !player.isSpectator()
                    && !MPGEntityData.manaita.accept(player)) {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
                updateAbilities(player);
            }
        }
        player.removeTag(CHESTPLATE_FALL_TAG);
        player.removeTag(CHESTPLATE_BIG_FALL_TAG);
    }

    private static void syncChestplateLandingSound(Player player) {
        if (!player.isOnGround() && player.fallDistance > 3.0F) {
            player.addTag(CHESTPLATE_FALL_TAG);
            if (player.fallDistance > 7.0F) {
                player.addTag(CHESTPLATE_BIG_FALL_TAG);
            }
            return;
        }
        if (player.isOnGround() && player.getTags().contains(CHESTPLATE_FALL_TAG)) {
            if (!hasManaitaBoots(player)) {
                boolean bigFall = player.getTags().contains(CHESTPLATE_BIG_FALL_TAG);
                player.playSound(bigFall ? SoundEvents.GENERIC_BIG_FALL : SoundEvents.GENERIC_SMALL_FALL,
                        1.0F, 1.0F);
            }
            player.removeTag(CHESTPLATE_FALL_TAG);
            player.removeTag(CHESTPLATE_BIG_FALL_TAG);
        }
    }

    private static void removeHarmfulEffects(Player player) {
        List<MobEffect> harmfulEffects = new ArrayList<>();
        for (MobEffectInstance effect : player.getActiveEffects()) {
            if (effect.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
                harmfulEffects.add(effect.getEffect());
            }
        }
        harmfulEffects.forEach(player::removeEffect);
    }

    private static void syncLeggings(Player player, ItemStack leggings) {
        if (!(leggings.getItem() instanceof Leggings)) {
            removeHelmetEffect(player, MobEffects.INVISIBILITY, LEGGINGS_INVISIBILITY_TAG);
            return;
        }

        if (!isTouchingFire(player)) {
            player.setRemainingFireTicks(0);
        }

        if (MPGConfigValues.leggings_invisibility_value) {
            addHelmetEffect(player, MobEffects.INVISIBILITY, INFINITE_DURATION,
                    0, LEGGINGS_INVISIBILITY_TAG);
        } else {
            removeHelmetEffect(player, MobEffects.INVISIBILITY, LEGGINGS_INVISIBILITY_TAG);
        }
    }

    private static boolean isTouchingFire(Player player) {
        return player.isInLava()
                || player.getLevel().getBlockState(player.blockPosition()).is(BlockTags.FIRE)
                || player.getLevel().getBlockState(player.blockPosition().above()).is(BlockTags.FIRE);
    }

    private static void syncBoots(Player player, ItemStack boots) {
        AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (!(boots.getItem() instanceof Boots)) {
            if (speedAttribute != null) speedAttribute.removeModifier(BOOTS_SPEED_MODIFIER_ID);
            return;
        }
        int speedLevel = Boots.getSpeed(boots);
        if (speedAttribute != null) {
            AttributeModifier current = speedAttribute.getModifier(BOOTS_SPEED_MODIFIER_ID);
            double amount = speedLevel * 0.2D;
            if (current == null || current.getAmount() != amount) {
                speedAttribute.removeModifier(BOOTS_SPEED_MODIFIER_ID);
                speedAttribute.addTransientModifier(new AttributeModifier(BOOTS_SPEED_MODIFIER_ID,
                        "Manaita boots speed", amount, AttributeModifier.Operation.MULTIPLY_BASE));
            }
        }

        if (MPGConfigValues.boots_auto_jump_value
                && player.isOnGround()
                && player.horizontalCollision
                && !player.isShiftKeyDown()
                && (Math.abs(player.xxa) > 0.01F || Math.abs(player.zza) > 0.01F)) {
            player.jumpFromGround();
        }
    }

    public static void applyBootsJump(Player player) {
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (boots.getItem() instanceof Boots) {
            net.minecraft.world.phys.Vec3 velocity = player.getDeltaMovement();
            double height = BOOTS_JUMP_STRENGTH[Boots.getJump(boots)] + player.getJumpBoostPower();
            player.setDeltaMovement(velocity.x, height, velocity.z);
        }
    }

    private static void updateAbilities(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.onUpdateAbilities();
        }
    }

    private static void applyHelmetEffects(Player player) {
        player.setAirSupply(player.getMaxAirSupply());

        FoodData foodData = player.getFoodData();
        foodData.setFoodLevel(20);
        foodData.setSaturation(20.0F);
        foodData.setExhaustion(0.0F);

        addHelmetEffect(player, MobEffects.REGENERATION, FAST_REGENERATION_DURATION,
                FAST_REGENERATION_AMPLIFIER, HELMET_REGENERATION_TAG);
        addHelmetEffect(player, MobEffects.WATER_BREATHING, INFINITE_DURATION,
                0, HELMET_WATER_BREATHING_TAG);

        if (MPGConfigValues.helmet_night_vision_value) {
            addHelmetEffect(player, MobEffects.NIGHT_VISION, INFINITE_DURATION,
                    0, HELMET_NIGHT_VISION_TAG);
        } else {
            removeHelmetEffect(player, MobEffects.NIGHT_VISION, HELMET_NIGHT_VISION_TAG);
        }
    }

    private static void addHelmetEffect(Player player, MobEffect effect, int duration,
                                        int amplifier, String ownerTag) {
        MobEffectInstance current = player.getEffect(effect);
        boolean needsRefresh = current == null
                || current.getAmplifier() < amplifier
                || duration == INFINITE_DURATION && current.getDuration() < INFINITE_DURATION / 2
                || duration != INFINITE_DURATION && current.getDuration() <= 20;
        if (needsRefresh) {
            player.addEffect(new MobEffectInstance(effect, duration, amplifier, false, false, true));
            player.addTag(ownerTag);
        }
    }

    private static void removeHelmetEffects(Player player) {
        removeHelmetEffect(player, MobEffects.REGENERATION, HELMET_REGENERATION_TAG);
        removeHelmetEffect(player, MobEffects.WATER_BREATHING, HELMET_WATER_BREATHING_TAG);
        removeHelmetEffect(player, MobEffects.NIGHT_VISION, HELMET_NIGHT_VISION_TAG);
    }

    private static void removeHelmetEffect(Player player, MobEffect effect, String ownerTag) {
        if (player.getTags().contains(ownerTag)) {
            player.removeEffect(effect);
            player.removeTag(ownerTag);
        }
    }

    public static class Helmet extends MPGArmorItemBase {
        public Helmet(ArmorMaterial material) {
            super(material, EquipmentSlot.HEAD);
        }

        @Override
        public void appendHoverText(@NotNull ItemStack stack, @NotNull net.minecraft.world.level.Level context,
                                    List<Component> tooltip, @NotNull TooltipFlag flag) {
            tooltip.add(Component.literal(MPText.manaita_mode.formatting(
                    text("mode.nightvision") + ": "
                            + (MPGConfigValues.helmet_night_vision_value
                            ? text("info.on") : text("info.off")))));
            super.appendHoverText(stack, context, tooltip, flag);
        }

        @Override
        public @NotNull Component getName(@NotNull ItemStack stack) {
            return Component.translatable("item.helmet.name");
        }

    }

    public static class Chestplate extends MPGArmorItemBase {
        public Chestplate(ArmorMaterial material) {
            super(material, EquipmentSlot.CHEST);
        }

        @Override
        public @NotNull Component getName(@NotNull ItemStack stack) {
            return Component.translatable("item.chestplate.name");
        }

    }

    public static class Leggings extends MPGArmorItemBase {
        public Leggings(ArmorMaterial material) {
            super(material, EquipmentSlot.LEGS);
        }

        @Override
        public @NotNull Component getName(@NotNull ItemStack stack) {
            return Component.translatable("item.leggings.name");
        }

        @Override
        public void appendHoverText(@NotNull ItemStack stack, @NotNull net.minecraft.world.level.Level context,
                                    List<Component> tooltip, @NotNull TooltipFlag flag) {
            tooltip.add(Component.literal(MPText.manaita_mode.formatting(
                    text("mode.invisibility") + ": "
                            + (MPGConfigValues.leggings_invisibility_value
                            ? text("info.on") : text("info.off")))));
            super.appendHoverText(stack, context, tooltip, flag);
        }
    }

    public static class Boots extends MPGArmorItemBase implements IMPGKey {
        public Boots(ArmorMaterial material) {
            super(material, EquipmentSlot.FEET);
        }

        @Override
        public @NotNull Component getName(@NotNull ItemStack stack) {
            return Component.translatable("item.boots.name");
        }

        public static int getSpeed(ItemStack itemStack) {
            int storedLevel = MPGItemStackData.getInt(itemStack, "Speed");
            return clampLevel(storedLevel > 0 ? storedLevel : MPGConfigValues.boots_speed_level_value);
        }

        public static int getJump(ItemStack itemStack) {
            int storedLevel = MPGItemStackData.getInt(itemStack, "Jump");
            return clampLevel(storedLevel > 0 ? storedLevel : MPGConfigValues.boots_jump_level_value);
        }

        @Override
        public void onManaitaKeyPress(ItemStack itemStack) {
            MPGItemStackData.putInt(itemStack, "Speed", nextLevel(getSpeed(itemStack)));
            MPGItemStackData.putInt(itemStack, "Jump", nextLevel(getJump(itemStack)));
        }

        @Override
        public void onManaitaKeyPressOnClient(ItemStack itemStack, Player player) {
            onManaitaKeyPress(itemStack);
            player.displayClientMessage(Component.literal(MPText.manaita_mode.formatting(String.format(
                    "[%s] %s: %d, %s: %d", text("item.boots.name"),
                    text("mode.speed"), getSpeed(itemStack), text("mode.jump"), getJump(itemStack)))),
                    messageUsesOverlay());
        }

        @Override
        public void appendHoverText(@NotNull ItemStack stack, @NotNull net.minecraft.world.level.Level context,
                                    List<Component> tooltip, @NotNull TooltipFlag flag) {
            tooltip.add(Component.literal(MPText.manaita_mode.formatting(
                    text("mode.autojump") + ": "
                            + (MPGConfigValues.boots_auto_jump_value ? text("info.on") : text("info.off")))));
            tooltip.add(Component.literal(MPText.manaita_mode.formatting(
                    text("mode.speed") + ": " + getSpeed(stack))));
            tooltip.add(Component.literal(MPText.manaita_mode.formatting(
                    text("mode.jump") + ": " + getJump(stack))));
            super.appendHoverText(stack, context, tooltip, flag);
        }

        private static int nextLevel(int level) {
            return level >= 8 ? 1 : level + 1;
        }

        private static int clampLevel(int level) {
            return Math.max(1, Math.min(8, level));
        }

        protected boolean messageUsesOverlay() {
            return true;
        }
    }

    private static String text(String translationKey) {
        return Component.translatable(translationKey).getString();
    }
}
