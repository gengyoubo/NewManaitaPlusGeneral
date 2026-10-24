package github.com.gengyoubo.core;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import github.com.gengyoubo.block.entity.MPBrewingStandBlockEntity;
import github.com.gengyoubo.block.entity.MPCraftingBlockEntity;
import github.com.gengyoubo.block.entity.MPFurnaceBlockEntity;

import static github.com.gengyoubo.MPG.BLOCK_ENTITY_TYPES;

public class MPBlockEntityCore {
    public static final RegistryObject<BlockEntityType<MPFurnaceBlockEntity>> FURNACE_BLOCK_ENTITY =
            cast(BLOCK_ENTITY_TYPES.register("furnace_block_entity", () -> buildType(net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.create(MPFurnaceBlockEntity::new, MPBlockCore.FurnaceBlock.get()))));
    public static final RegistryObject<BlockEntityType<MPBrewingStandBlockEntity>> BREWING_BLOCK_ENTITY =
            cast(BLOCK_ENTITY_TYPES.register("brewing_block_entity", () -> buildType(net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.create(MPBrewingStandBlockEntity::new, MPBlockCore.BrewingBlock.get()))));
    public static final RegistryObject<BlockEntityType<MPCraftingBlockEntity>> CRAFTING_BLOCK_ENTITY =
            cast(BLOCK_ENTITY_TYPES.register("crafting_entity", () -> buildType(net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.create(MPCraftingBlockEntity::new, MPBlockCore.CraftingBlock.get()))));

    private static <T extends net.minecraft.world.level.block.entity.BlockEntity> BlockEntityType<T> buildType(net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder<T> builder) {
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private static <T> RegistryObject<T> cast(RegistryObject<?> value) {
        return (RegistryObject<T>) value;
    }

    public static void init() {
    }

}

