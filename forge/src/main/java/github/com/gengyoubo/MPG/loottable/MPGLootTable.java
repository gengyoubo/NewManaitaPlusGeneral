package github.com.gengyoubo.MPG.loottable;

import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import github.com.gengyoubo.MPG.core.MPGItemCore;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.minecraft.world.level.storage.loot.BuiltInLootTables.END_CITY_TREASURE;

public class MPGLootTable extends LootTableProvider {
    public MPGLootTable(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected @NotNull List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        return List.of(Pair.of(ManaitaPlusLoot::new, LootContextParamSets.CHEST));
    }

    @Override
    protected void validate(@NotNull Map<ResourceLocation, LootTable> map, @NotNull ValidationContext context) {
    }

    public static class ManaitaPlusLoot implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
        public ManaitaPlusLoot() {
        }

        @Override
        public void accept(BiConsumer<ResourceLocation, LootTable.Builder> p_249643_) {
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
