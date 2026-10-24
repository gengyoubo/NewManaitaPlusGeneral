package github.com.gengyoubo.common.item.tier;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class MPGToolTierBase implements Tier {
    private final Supplier<Ingredient> repairIngredient;

    public MPGToolTierBase(Supplier<Ingredient> repairIngredient) {
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getUses() {
        return -1;
    }

    @Override
    public float getSpeed() {
        return Float.POSITIVE_INFINITY;
    }

    @Override
    public float getAttackDamageBonus() {
        return Float.POSITIVE_INFINITY;
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public int getLevel() { return 4; }

    @Override
    public @NotNull Ingredient getRepairIngredient() {
        return repairIngredient.get();
    }
}
