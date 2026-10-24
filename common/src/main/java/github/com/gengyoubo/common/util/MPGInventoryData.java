package github.com.gengyoubo.common.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Optional;

/** Keeps multiplied stacks above the vanilla item codec's 99-item limit intact. */
public final class MPGInventoryData {
    private MPGInventoryData() {}

    private record StoredItem(int slot, ItemStack stack, Optional<Integer> count) {
        private static final Codec<StoredItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.UNSIGNED_BYTE.fieldOf("Slot").orElse(0).forGetter(StoredItem::slot),
                ItemStack.MAP_CODEC.forGetter(StoredItem::stack),
                ExtraCodecs.POSITIVE_INT.optionalFieldOf("ManaitaCount").forGetter(StoredItem::count)
        ).apply(instance, StoredItem::new));
    }

    public static void saveAllItems(ValueOutput output, NonNullList<ItemStack> items) {
        var entries = output.list("Items", StoredItem.CODEC);
        for (int slot = 0; slot < items.size(); slot++) {
            ItemStack stack = items.get(slot);
            if (!stack.isEmpty()) {
                entries.add(new StoredItem(slot, stack.copyWithCount(Math.min(stack.getCount(), 99)),
                        stack.getCount() > 99 ? Optional.of(stack.getCount()) : Optional.empty()));
            }
        }
    }

    public static void loadAllItems(ValueInput input, NonNullList<ItemStack> items) {
        for (StoredItem entry : input.listOrEmpty("Items", StoredItem.CODEC)) {
            if (entry.slot() >= 0 && entry.slot() < items.size()) {
                ItemStack stack = entry.stack();
                entry.count().ifPresent(stack::setCount);
                items.set(entry.slot(), stack);
            }
        }
    }
}
