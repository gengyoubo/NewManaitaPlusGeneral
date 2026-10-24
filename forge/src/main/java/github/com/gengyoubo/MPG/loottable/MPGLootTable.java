package github.com.gengyoubo.MPG.loottable;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
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
    public MPGLootTable(PackOutput packOutput) {
        super(packOutput, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(ManaitaPlusLoot::new, LootContextParamSets.CHEST)
        ));
    }

    public static class ManaitaPlusLoot implements LootTableSubProvider {
        public ManaitaPlusLoot() {
        }

        @Override
        public void generate(BiConsumer<net.minecraft.resources.ResourceLocation, LootTable.Builder> p_249643_) {
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
