package net.minecraftforge.common.extensions;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public final class IForgeMenuType {
    private IForgeMenuType() {
    }

    public static <T extends AbstractContainerMenu> MenuType<T> create(MenuFactory<T> factory) {
        return new ExtendedScreenHandlerType<>((id, inventory, buf) -> factory.create(id, inventory, buf.readBlockPos()));
    }

    @FunctionalInterface
    public interface MenuFactory<T extends AbstractContainerMenu> {
        T create(int syncId, Inventory inventory, BlockPos pos);
    }
}
