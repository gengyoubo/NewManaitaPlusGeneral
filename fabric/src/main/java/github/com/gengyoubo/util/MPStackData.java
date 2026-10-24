package github.com.gengyoubo.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public final class MPStackData {
    private MPStackData() {
    }

    public static CompoundTag getTag(ItemStack stack) {
        return stack.hasTag() ? stack.getTag() : new CompoundTag();
    }

    public static boolean hasTag(ItemStack stack) {
        return stack.hasTag();
    }

    public static void setTag(ItemStack stack, CompoundTag tag) {
        stack.setTag(tag);
    }

    public static int getInt(ItemStack stack, String key) {
        return getTag(stack).getInt(key);
    }

    public static int getInt(ItemStack stack, String key, int fallback) {
        CompoundTag tag = getTag(stack);
        return tag.contains(key) ? tag.getInt(key) : fallback;
    }

    public static void putInt(ItemStack stack, String key, int value) {
        stack.getOrCreateTag().putInt(key, value);
    }

    public static boolean getBoolean(ItemStack stack, String key) {
        return getTag(stack).getBoolean(key);
    }

    public static boolean getBoolean(ItemStack stack, String key, boolean fallback) {
        CompoundTag tag = getTag(stack);
        return tag.contains(key) ? tag.getBoolean(key) : fallback;
    }

    public static void putBoolean(ItemStack stack, String key, boolean value) {
        stack.getOrCreateTag().putBoolean(key, value);
    }
}
