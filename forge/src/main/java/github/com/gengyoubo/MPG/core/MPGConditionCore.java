package github.com.gengyoubo.MPG.core;

import github.com.gengyoubo.MPG.recipe.condition.EasyModeCondition;
import net.minecraftforge.common.crafting.CraftingHelper;

public final class MPGConditionCore {
    private MPGConditionCore() {}
    public static void init() {
        CraftingHelper.register(new EasyModeCondition.Serializer());
    }
}
