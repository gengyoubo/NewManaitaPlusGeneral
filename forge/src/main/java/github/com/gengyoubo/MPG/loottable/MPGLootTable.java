package github.com.gengyoubo.MPG.loottable;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import github.com.gengyoubo.MPG.core.MPGItemCore;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import static net.minecraft.world.level.storage.loot.BuiltInLootTables.END_CITY_TREASURE;

public class MPGLootTable extends LootTableProvider {
    public MPGLootTable(DataGenerator generator) { super(generator); }

    @Override
    protected java.util.List<com.mojang.datafixers.util.Pair<java.util.function.Supplier<java.util.function.Consumer<BiConsumer<net.minecraft.resources.ResourceLocation, LootTable.Builder>>>, net.minecraft.world.level.storage.loot.parameters.LootContextParamSet>> getTables() {
        return List.of(com.mojang.datafixers.util.Pair.of(ManaitaPlusLoot::new, LootContextParamSets.CHEST));
    }

    @Override
    protected void validate(java.util.Map<net.minecraft.resources.ResourceLocation, LootTable> tables,
                            net.minecraft.world.level.storage.loot.ValidationContext context) {
        tables.forEach((id, table) -> net.minecraft.world.level.storage.loot.LootTables.validate(context, id, table));
    }

    public static class ManaitaPlusLoot implements java.util.function.Consumer<BiConsumer<net.minecraft.resources.ResourceLocation, LootTable.Builder>> {
        public ManaitaPlusLoot() {
        }

        @Override
        public void accept(BiConsumer<net.minecraft.resources.ResourceLocation, LootTable.Builder> p_249643_) {
            p_249643_.accept(
                    END_CITY_TREASURE,
                    LootTable.lootTable()
                            .withPool(LootPool.lootPool()
                                    .setRolls(ConstantValue.exactly(1))
                                    .add(LootItem.lootTableItem(MPGItemCore.ManaitaBow.get())
                                            .setWeight(1)
                                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                                    .when(LootItemRandomChanceCondition.randomChance(0.1F)))
            );
        }
    }
}
