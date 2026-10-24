package github.com.gengyoubo.common.util;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

/** Keeps multiplied inventories intact beyond the vanilla 1.20.1 byte count limit. */
public final class MPGInventoryData {
    private MPGInventoryData() {}

    public static void saveAllItems(CompoundTag tag, NonNullList<ItemStack> items) {
        ListTag list = new ListTag();
        for (int slot = 0; slot < items.size(); slot++) {
            ItemStack stack = items.get(slot);
            if (!stack.isEmpty()) {
                CompoundTag entry = stack.save(new CompoundTag());
                entry.putByte("Slot", (byte) slot);
                entry.putInt("ManaitaCount", stack.getCount());
                list.add(entry);
            }
        }
        tag.put("Items", list);
    }

    public static void loadAllItems(CompoundTag tag, NonNullList<ItemStack> items) {
        ListTag list = tag.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            int slot = entry.getByte("Slot") & 255;
            if (slot >= items.size()) continue;
            CompoundTag vanilla = entry.copy();
            if (entry.contains("ManaitaCount", Tag.TAG_INT)) vanilla.putByte("Count", (byte) 1);
            ItemStack stack = ItemStack.of(vanilla);
            if (entry.contains("ManaitaCount", Tag.TAG_INT)) stack.setCount(entry.getInt("ManaitaCount"));
            items.set(slot, stack);
        }
    }
}
