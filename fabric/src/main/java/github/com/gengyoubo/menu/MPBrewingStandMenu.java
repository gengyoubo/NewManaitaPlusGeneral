package github.com.gengyoubo.menu;

import github.com.gengyoubo.common.menu.MPGBrewingStandMenuBase;
import github.com.gengyoubo.core.MPMenuCore;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;

public class MPBrewingStandMenu extends MPGBrewingStandMenuBase {
    @SuppressWarnings("unused")
    public MPBrewingStandMenu(int id, Inventory inventory, BlockPos blockPos) {
        this(id, inventory, new SimpleContainer(5), new SimpleContainerData(2));
    }

    public MPBrewingStandMenu(int id, Inventory inventory, Container container, ContainerData data) {
        super(MPMenuCore.BrewingStandManaita.get(), id, inventory, container, data,
                PotionBrewing::isIngredient,
                stack -> (stack.is(Items.POTION) || stack.is(Items.SPLASH_POTION) || stack.is(Items.LINGERING_POTION)) || stack.is(Items.GLASS_BOTTLE),
                (player, stack) -> {
                });
    }
}
