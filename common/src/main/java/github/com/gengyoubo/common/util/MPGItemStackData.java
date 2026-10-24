package github.com.gengyoubo.common.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Loader-independent access to Minecraft 26.1 item custom data. */
public final class MPGItemStackData {
    private MPGItemStackData() {
    }

    /** Worn armor stacks (feet to head); the inventory no longer exposes an armor list. */
    public static List<ItemStack> armorStacks(LivingEntity entity) {
        List<ItemStack> stacks = new ArrayList<>(4);
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                stacks.add(entity.getItemBySlot(slot));
            }
        }
        return stacks;
    }

    public static boolean hasTag(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null && !data.isEmpty();
    }

    public static @Nullable CompoundTag getTag(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null || data.isEmpty() ? null : data.copyTag();
    }

    public static CompoundTag getOrCreateTag(ItemStack stack) {
        CompoundTag tag = getTag(stack);
        return tag == null ? new CompoundTag() : tag;
    }

    public static void setTag(ItemStack stack, @Nullable CompoundTag tag) {
        if (tag == null || tag.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
            syncModelType(stack, 0);
            return;
        }
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag.copy()));
        syncModelType(stack, tag.getIntOr(MPGNBTData.ItemType, 0));
    }

    /**
     * Item models can no longer read custom data, so the Manaita tier is mirrored into
     * {@code custom_model_data} (float index 0) for the {@code range_dispatch} item model definitions.
     */
    private static void syncModelType(ItemStack stack, int type) {
        if (type == 0) {
            if (stack.has(DataComponents.CUSTOM_MODEL_DATA)) {
                stack.remove(DataComponents.CUSTOM_MODEL_DATA);
            }
            return;
        }
        stack.set(DataComponents.CUSTOM_MODEL_DATA,
                new CustomModelData(List.of((float) type), List.of(), List.of(), List.of()));
    }

    public static void editTag(ItemStack stack, Consumer<CompoundTag> editor) {
        CompoundTag tag = getOrCreateTag(stack);
        editor.accept(tag);
        setTag(stack, tag);
    }

    public static int getInt(ItemStack stack, String key) {
        CompoundTag tag = getTag(stack);
        return tag != null ? tag.getIntOr(key, 0) : 0;
    }

    public static boolean getBoolean(ItemStack stack, String key) {
        CompoundTag tag = getTag(stack);
        return tag != null && tag.getBooleanOr(key, false);
    }

    public static boolean contains(ItemStack stack, String key) {
        CompoundTag tag = getTag(stack);
        return tag != null && tag.contains(key);
    }

    public static void putInt(ItemStack stack, String key, int value) {
        editTag(stack, tag -> tag.putInt(key, value));
    }

    public static void putBoolean(ItemStack stack, String key, boolean value) {
        editTag(stack, tag -> tag.putBoolean(key, value));
    }
}
