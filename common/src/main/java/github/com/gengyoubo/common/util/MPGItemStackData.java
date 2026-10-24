package github.com.gengyoubo.common.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/** Loader-independent access to Minecraft 1.19.2 item NBT. */
public final class MPGItemStackData {
    private MPGItemStackData() {
    }

    public static boolean hasTag(ItemStack stack) { return stack.hasTag(); }

    public static @Nullable CompoundTag getTag(ItemStack stack) { return stack.getTag(); }

    public static CompoundTag getOrCreateTag(ItemStack stack) { return stack.getOrCreateTag(); }

    public static void setTag(ItemStack stack, @Nullable CompoundTag tag) { stack.setTag(tag); }

    /** 1.19.2 has no {@code ItemStack#copyWithCount}. */
    public static ItemStack copyWithCount(ItemStack stack, int count) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack copy = stack.copy();
        copy.setCount(count);
        return copy;
    }

    public static void editTag(ItemStack stack, Consumer<CompoundTag> editor) {
        CompoundTag tag = getOrCreateTag(stack);
        editor.accept(tag);
        setTag(stack, tag);
    }

    public static int getInt(ItemStack stack, String key) {
        CompoundTag tag = getTag(stack);
        return tag != null ? tag.getInt(key) : 0;
    }

    public static boolean getBoolean(ItemStack stack, String key) {
        CompoundTag tag = getTag(stack);
        return tag != null && tag.getBoolean(key);
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
