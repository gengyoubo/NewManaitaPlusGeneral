package github.com.gengyoubo.common.menu;

import github.com.gengyoubo.common.config.MPGConfigValues;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/** Shared 3x3 crafting menu; mirrors the vanilla crafting table but multiplies every result. */
public abstract class MPGCraftingMenuBase extends AbstractCraftingMenu {
    private final ContainerLevelAccess access;
    private final Player player;
    private final Block craftingBlock;
    private final boolean alwaysValid;
    private boolean placingRecipe;

    protected MPGCraftingMenuBase(MenuType<?> menuType, int containerId, Inventory inventory,
                                  ContainerLevelAccess access, Block craftingBlock, boolean alwaysValid) {
        super(menuType, containerId, 3, 3);
        this.access = access;
        this.player = inventory.player;
        this.craftingBlock = craftingBlock;
        this.alwaysValid = alwaysValid;

        addResultSlot(player, 124, 35);
        addCraftingGridSlots(30, 17);
        addStandardInventorySlots(inventory, 8, 84);
    }

    private void slotChangedCraftingGrid(ServerLevel level, @Nullable RecipeHolder<CraftingRecipe> recipeHint) {
        CraftingInput input = craftSlots.asCraftInput();
        ServerPlayer serverPlayer = (ServerPlayer) player;
        ItemStack result = ItemStack.EMPTY;
        Optional<RecipeHolder<CraftingRecipe>> recipe = level.getServer().getRecipeManager()
                .getRecipeFor(RecipeType.CRAFTING, input, level, recipeHint);
        if (recipe.isPresent() && resultSlots.setRecipeUsed(serverPlayer, recipe.get())) {
            ItemStack assembled = recipe.get().value().assemble(input);
            if (assembled.isItemEnabled(level.enabledFeatures())) {
                long multiplied = (long) assembled.getCount() * MPGConfigValues.crafting_doubling_value;
                assembled.setCount((int) Math.min(Integer.MAX_VALUE, multiplied));
                result = assembled;
            }
        }
        resultSlots.setItem(0, result);
        setRemoteSlot(0, result);
        serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), 0, result));
    }

    @Override
    public void slotsChanged(@NotNull Container container) {
        if (!placingRecipe) {
            access.execute((level, pos) -> {
                if (level instanceof ServerLevel serverLevel) {
                    slotChangedCraftingGrid(serverLevel, null);
                }
            });
        }
    }

    @Override
    protected void beginPlacingRecipe() {
        placingRecipe = true;
    }

    @Override
    protected void finishPlacingRecipe(@NotNull ServerLevel level, @NotNull RecipeHolder<CraftingRecipe> recipe) {
        placingRecipe = false;
        slotChangedCraftingGrid(level, recipe);
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        access.execute((level, pos) -> clearContainer(player, craftSlots));
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = slots.get(slotIndex);
        if (!slot.hasItem()) {
            return original;
        }
        ItemStack moving = slot.getItem();
        original = moving.copy();
        if (slotIndex == 0) {
            moving.getItem().onCraftedBy(moving, player);
            if (!moveItemStackTo(moving, 10, 46, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(moving, original);
        } else if (slotIndex >= 10 && slotIndex < 46) {
            if (!moveItemStackTo(moving, 1, 10, false)) {
                if (slotIndex < 37) {
                    if (!moveItemStackTo(moving, 37, 46, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!moveItemStackTo(moving, 10, 37, false)) {
                    return ItemStack.EMPTY;
                }
            }
        } else if (!moveItemStackTo(moving, 10, 46, false)) {
            return ItemStack.EMPTY;
        }

        if (moving.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (moving.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, moving);
        if (slotIndex == 0) {
            player.drop(moving, false);
        }
        return original;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return alwaysValid || stillValid(access, player, craftingBlock);
    }

    @Override
    public boolean canTakeItemForPickAll(@NotNull ItemStack stack, @NotNull Slot slot) {
        return slot.container != resultSlots && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public @NotNull Slot getResultSlot() {
        return slots.get(0);
    }

    @Override
    public @NotNull List<Slot> getInputGridSlots() {
        return slots.subList(1, 10);
    }

    @Override
    public @NotNull RecipeBookType getRecipeBookType() {
        return RecipeBookType.CRAFTING;
    }

    @Override
    protected @NotNull Player owner() {
        return player;
    }
}
