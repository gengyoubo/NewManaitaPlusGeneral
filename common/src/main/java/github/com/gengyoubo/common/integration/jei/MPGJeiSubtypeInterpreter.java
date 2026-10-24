package github.com.gengyoubo.common.integration.jei;

import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;

public final class MPGJeiSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
    public static final MPGJeiSubtypeInterpreter INSTANCE = new MPGJeiSubtypeInterpreter();

    private MPGJeiSubtypeInterpreter() {
    }

    @Override
    public String apply(ItemStack ingredient, UidContext context) {
        return "type:" + MPGItemStackData.getInt(ingredient, MPGNBTData.ItemType);
    }
}
