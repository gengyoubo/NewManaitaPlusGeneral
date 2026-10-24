package github.com.gengyoubo.MPG.recipe.condition;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public record EasyModeCondition(boolean value) implements ICondition {
    public static final ResourceLocation ID = new ResourceLocation("manaita_plus_general", "easy_mode");
    @Override public ResourceLocation getID() { return ID; }
    @Override public boolean test(IContext context) { return github.com.gengyoubo.MPG.MPGConfig.easy_mode_value == value; }
    public static class Serializer implements IConditionSerializer<EasyModeCondition> {
        @Override public void write(JsonObject json, EasyModeCondition condition) { json.addProperty("value", condition.value()); }
        @Override public EasyModeCondition read(JsonObject json) { return new EasyModeCondition(net.minecraft.util.GsonHelper.getAsBoolean(json, "value", true)); }
        @Override public ResourceLocation getID() { return ID; }
    }
}
