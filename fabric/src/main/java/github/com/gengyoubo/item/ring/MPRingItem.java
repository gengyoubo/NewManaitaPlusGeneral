package github.com.gengyoubo.item.ring;

import github.com.gengyoubo.common.item.data.IMPGKey;
import github.com.gengyoubo.integration.MPAccessories;
import github.com.gengyoubo.item.portable.MPGPortableItem;
import github.com.gengyoubo.item.portable.MPFurnacePortable;
import github.com.gengyoubo.item.portable.MPBrewingPortable;
import github.com.gengyoubo.menu.MPCraftingMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class MPRingItem extends MPGPortableItem implements IMPGKey {
    public enum Kind { CRAFTING, FURNACE, BREWING }
    private final Kind kind;
    public MPRingItem(Kind kind) {
        super(switch (kind) {
            case CRAFTING -> "item.ringCrafting.";
            case FURNACE -> "item.ringFurnace.";
            case BREWING -> "item.ringBrewing.";
        });
        this.kind = kind;
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!MPAccessories.isEnabled()) return InteractionResultHolder.fail(stack);
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide && MPAccessories.equip(player, stack))
                return InteractionResultHolder.sidedSuccess(stack, false);
            return InteractionResultHolder.pass(stack);
        }
        return super.use(level, player, hand);
    }
    @Override
    public void onManaitaKeyPress(ItemStack stack, Player player) {
        if (MPAccessories.isEnabled() && player instanceof ServerPlayer serverPlayer)
            openPortableMenu(serverPlayer, stack, player.level());
    }
    @Override
    protected void openPortableMenu(ServerPlayer player, ItemStack stack, Level level) {
        switch (kind) {
            case CRAFTING -> openPortableScreen(player, stack, level, "container.crafting_manaita",
                    (id, inventory, owner, held, world) -> new MPCraftingMenu(id, inventory, world));
            case FURNACE -> openPortableScreen(player, stack, level, "container.furnace_manaita",
                    (id, inventory, owner, held, world) ->
                            new MPFurnacePortable.MPFurnaceBlockEntity(owner, held).createMenu(id, inventory));
            case BREWING -> openPortableScreen(player, stack, level, "container.brewing_manaita",
                    (id, inventory, owner, held, world) ->
                            new MPBrewingPortable.MPBrewingStandBlockEntity(owner, held).createMenu(id, inventory));
        }
    }
}
