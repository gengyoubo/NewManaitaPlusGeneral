package github.com.gengyoubo.common.block.entity;

import net.minecraft.network.chat.TranslatableComponent;
import github.com.gengyoubo.common.config.MPGConfigValues;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;

import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Shared unlimited-furnace inventory, processing, persistence and experience behavior. */
public abstract class MPGFurnaceBlockEntityBase extends AbstractFurnaceBlockEntity {
    private static final int[] SLOTS_FOR_UP = {0};
    private static final int[] SLOTS_FOR_DOWN = {2, 1};
    private static final int[] SLOTS_FOR_SIDES = {1};

    private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();

    protected MPGFurnaceBlockEntityBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, RecipeType.SMELTING);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return new TranslatableComponent("container.furnace_manaita");
    }

    @Override
    public int getMaxStackSize() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        github.com.gengyoubo.common.util.MPGInventoryData.loadAllItems(tag, items);
        recipesUsed.clear();
        CompoundTag recipesTag = tag.getCompound("RecipesUsed");
        for (String key : recipesTag.getAllKeys()) {
            recipesUsed.put(new ResourceLocation(key), recipesTag.getInt(key));
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        github.com.gengyoubo.common.util.MPGInventoryData.saveAllItems(tag, items);
        CompoundTag recipesTag = new CompoundTag();
        recipesUsed.forEach((id, count) -> recipesTag.putInt(id.toString(), count));
        tag.put("RecipesUsed", recipesTag);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MPGFurnaceBlockEntityBase entity) {
        if (entity.processAll(level)) {
            setChanged(level, pos, state);
        }
    }

    protected final boolean processAll(Level level) {
        boolean changed = false;
        ItemStack input = items.get(0);
        if (!input.isEmpty()) {
            AbstractCookingRecipe recipe = level.getRecipeManager()
                    .getRecipeFor(RecipeType.SMELTING, new net.minecraft.world.SimpleContainer(input), level)
                    .orElse(null);
            while (canBurn(level.registryAccess(), recipe)) {
                burn(level.registryAccess(), recipe);
                setRecipeUsed(recipe);
                changed = true;
            }
        }
        return changed;
    }

    private boolean canBurn(RegistryAccess registryAccess, @Nullable net.minecraft.world.item.crafting.Recipe<?> recipe) {
        ItemStack input = items.get(0);
        if (input.isEmpty()) {
            return false;
        }
        ItemStack assembled = MPGFurnaceLogic.assemble(recipe, input, registryAccess);
        if (assembled.isEmpty()) {
            return false;
        }
        ItemStack output = items.get(2);
        if (!output.isEmpty() && !ItemStack.isSameItemSameTags(output, assembled)) {
            return false;
        }
        long added = (long) assembled.getCount() * MPGConfigValues.furnace_doubling_value;
        return added <= Integer.MAX_VALUE
                && (output.isEmpty() || (long) output.getCount() + added <= Integer.MAX_VALUE);
    }

    private void burn(RegistryAccess registryAccess, net.minecraft.world.item.crafting.Recipe<?> recipe) {
        ItemStack result = MPGFurnaceLogic.assemble(recipe, items.get(0), registryAccess);
        int resultCount = Math.multiplyExact(result.getCount(), MPGConfigValues.furnace_doubling_value);
        ItemStack output = items.get(2);
        if (output.isEmpty()) {
            ItemStack copy = result.copy();
            copy.setCount(resultCount);
            items.set(2, copy);
        } else {
            output.grow(resultCount);
        }
        items.get(0).shrink(1);
    }

    @Override
    protected int getBurnDuration(@NotNull ItemStack stack) {
        return 0;
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction direction) {
        return direction == Direction.DOWN ? SLOTS_FOR_DOWN : direction == Direction.UP ? SLOTS_FOR_UP : SLOTS_FOR_SIDES;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, @NotNull ItemStack stack, @Nullable Direction direction) {
        return canPlaceItem(slot, stack);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack current = items.get(slot);
        boolean same = !stack.isEmpty() && ItemStack.isSameItemSameTags(current, stack);
        items.set(slot, stack);
        if (slot == 0 && !same) {
            setChanged();
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        return slot != 2;
    }

    @Override
    public void setRecipeUsed(@Nullable net.minecraft.world.item.crafting.Recipe<?> recipe) {
        if (recipe != null) {
            recipesUsed.addTo(recipe.getId(), 1);
        }
    }

    @Override
    public void awardUsedRecipesAndPopExperience(@NotNull ServerPlayer player) {
        MPGFurnaceLogic.awardRecipesAndExperience(player, items, recipesUsed);
    }

    @Override
    public @NotNull List<net.minecraft.world.item.crafting.Recipe<?>> getRecipesToAwardAndPopExperience(@NotNull ServerLevel level,
                                                                            @NotNull Vec3 pos) {
        return MPGFurnaceLogic.recipesAndExperience(level, pos, recipesUsed);
    }
}
