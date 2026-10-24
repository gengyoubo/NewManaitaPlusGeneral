package github.com.gengyoubo.common.block.entity;

import net.minecraft.network.chat.TranslatableComponent;
import github.com.gengyoubo.common.block.MPGBrewingStandBlockBase;
import github.com.gengyoubo.common.config.MPGConfigValues;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/** Shared brewing inventory, processing, persistence and automation behavior. */
public abstract class MPGBrewingStandBlockEntityBase extends BaseContainerBlockEntity implements WorldlyContainer {
    public static final int BREW_TIME = 1;
    private static final int INGREDIENT_SLOT = 3;
    private static final int FUEL_SLOT = 4;
    private static final int[] SLOTS_FOR_UP = {INGREDIENT_SLOT};
    private static final int[] SLOTS_FOR_DOWN = {0, 1, 2, INGREDIENT_SLOT};
    private static final int[] SLOTS_FOR_SIDES = {0, 1, 2, FUEL_SLOT};

    protected NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);
    protected int brewTime;
    protected int fuel;
    @Nullable
    protected Item ingredient;
    private boolean[] lastPotionCount;

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> brewTime;
                case 1 -> fuel;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> brewTime = value;
                case 1 -> fuel = value;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    protected MPGBrewingStandBlockEntityBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return new TranslatableComponent("container.brewing_manaita");
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    protected @NotNull NonNullList<ItemStack> getItems() {
        return items;
    }

    protected void setItems(@NotNull NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return items.get(slot); }
    @Override public ItemStack removeItem(int slot, int count) { return ContainerHelper.removeItem(items, slot, count); }
    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(items, slot); }
    @Override public void setItem(int slot, ItemStack stack) { items.set(slot, stack); }
    @Override public boolean stillValid(net.minecraft.world.entity.player.Player player) {
        return level != null && level.getBlockEntity(worldPosition) == this
                && player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64;
    }
    @Override public void clearContent() { items.clear(); }

    public static void serverTick(Level level, BlockPos pos, BlockState state,
                                  MPGBrewingStandBlockEntityBase entity) {
        ItemStack fuelStack = entity.items.get(FUEL_SLOT);
        if (entity.fuel <= 0 && fuelStack.is(Items.BLAZE_POWDER)) {
            entity.fuel = 20;
            fuelStack.shrink(1);
            setChanged(level, pos, state);
        }

        boolean brewable = isBrewable(entity.items);
        ItemStack ingredientStack = entity.items.get(INGREDIENT_SLOT);
        if (entity.brewTime > 0) {
            entity.brewTime--;
            if (entity.brewTime == 0 && brewable) {
                entity.performBrew(level, pos.getX(), pos.getY(), pos.getZ());
            } else if (!brewable || !ingredientStack.is(entity.ingredient)) {
                entity.brewTime = 0;
            }
            setChanged(level, pos, state);
        } else if (brewable && entity.fuel > 0) {
            entity.fuel--;
            entity.brewTime = BREW_TIME;
            entity.ingredient = ingredientStack.getItem();
            setChanged(level, pos, state);
        }

        boolean[] potionBits = entity.getPotionBits();
        if (!Arrays.equals(potionBits, entity.lastPotionCount)) {
            entity.lastPotionCount = potionBits;
            BlockState updatedState = state;
            for (int i = 0; i < MPGBrewingStandBlockBase.HAS_BOTTLE.length; i++) {
                if (updatedState.hasProperty(MPGBrewingStandBlockBase.HAS_BOTTLE[i])) {
                    updatedState = updatedState.setValue(MPGBrewingStandBlockBase.HAS_BOTTLE[i], potionBits[i]);
                }
            }
            level.setBlock(pos, updatedState, 2);
        }
    }

    protected static boolean isBrewable(NonNullList<ItemStack> items) {
        ItemStack ingredient = items.get(INGREDIENT_SLOT);
        if (ingredient.isEmpty() || !PotionBrewing.isIngredient(ingredient)) {
            return false;
        }
        for (int slot = 0; slot < 3; slot++) {
            ItemStack input = items.get(slot);
            if (!input.isEmpty() && PotionBrewing.hasMix(input, ingredient)) {
                return true;
            }
        }
        return false;
    }

    protected final boolean performBrew(Level level, double x, double y, double z) {
        if (beforeBrew(items)) {
            return false;
        }

        ItemStack ingredientStack = items.get(INGREDIENT_SLOT);
        for (int slot = 0; slot < 3; slot++) {
            ItemStack input = items.get(slot);
            if (input.isEmpty() || !PotionBrewing.hasMix(input, ingredientStack)) {
                continue;
            }
            ItemStack brewed = PotionBrewing.mix(ingredientStack, input);
            if (!brewed.isEmpty() && !ItemStack.matches(input, brewed)) {
                long multiplied = (long) brewed.getCount() * MPGConfigValues.brewing_doubling_value;
                brewed.setCount((int) Math.min(Integer.MAX_VALUE, Math.max(1L, multiplied)));
            }
            items.set(slot, brewed);
        }
        finishBrew(level, x, y, z, items);
        level.levelEvent(1035, new BlockPos(x, y, z), 0);
        return true;
    }

    protected boolean beforeBrew(NonNullList<ItemStack> items) {
        return false;
    }

    protected abstract boolean isPotionInput(ItemStack stack);

    protected abstract void finishBrew(Level level, double x, double y, double z, NonNullList<ItemStack> items);

    private boolean[] getPotionBits() {
        boolean[] bits = new boolean[3];
        for (int i = 0; i < bits.length; i++) {
            bits[i] = !items.get(i).isEmpty();
        }
        return bits;
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        github.com.gengyoubo.common.util.MPGInventoryData.loadAllItems(tag, items);
        brewTime = tag.getShort("BrewTime");
        ingredient = brewTime > 0 ? items.get(INGREDIENT_SLOT).getItem() : null;
        fuel = tag.getByte("Fuel");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putShort("BrewTime", (short) brewTime);
        github.com.gengyoubo.common.util.MPGInventoryData.saveAllItems(tag, items);
        tag.putByte("Fuel", (byte) fuel);
    }

    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {

        if (slot == INGREDIENT_SLOT) {
            return PotionBrewing.isIngredient(stack);
        }
        if (slot == FUEL_SLOT) {
            return stack.is(Items.BLAZE_POWDER);
        }
        return isPotionInput(stack) && getItem(slot).isEmpty();
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction direction) {
        return direction == Direction.UP ? SLOTS_FOR_UP : direction == Direction.DOWN ? SLOTS_FOR_DOWN : SLOTS_FOR_SIDES;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, @NotNull ItemStack stack, @Nullable Direction direction) {
        return canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, @NotNull ItemStack stack, @NotNull Direction direction) {
        return slot != INGREDIENT_SLOT || stack.is(Items.GLASS_BOTTLE);
    }
}
