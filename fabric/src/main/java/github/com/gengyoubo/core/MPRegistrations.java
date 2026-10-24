package github.com.gengyoubo.core;

import github.com.gengyoubo.MPG;
import github.com.gengyoubo.util.MPResource;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

/** Assign registry IDs before constructing items and blocks, as required by 26.1. */
final class MPRegistrations {
    private MPRegistrations() {}

    static RegistryObject<Item> item(String path, Function<Item.Properties, Item> factory) {
        return MPG.ITEMS.register(path, () -> factory.apply(new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, MPResource.id(MPG.MODID, path)))));
    }

    static RegistryObject<Block> block(String path, Function<BlockBehaviour.Properties, Block> factory) {
        return MPG.BLOCKS.register(path, () -> factory.apply(BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, MPResource.id(MPG.MODID, path)))));
    }
}
