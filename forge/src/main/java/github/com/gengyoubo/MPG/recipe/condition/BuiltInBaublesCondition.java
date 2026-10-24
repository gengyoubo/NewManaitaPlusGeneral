package github.com.gengyoubo.MPG.recipe.condition;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public record BuiltInBaublesCondition() implements ICondition {
    public static final ResourceLocation ID = new ResourceLocation("manaita_plus_general", "built_in_baubles");
    @Override public ResourceLocation getID() { return ID; }
    @Override public boolean test(IContext context) { return github.com.gengyoubo.MPG.baubles.common.capability.BaublesCapability.isEnabled(); }
    public static class Serializer implements IConditionSerializer<BuiltInBaublesCondition> {
        @Override public void write(JsonObject json, BuiltInBaublesCondition condition) {  }
        @Override public BuiltInBaublesCondition read(JsonObject json) { return new BuiltInBaublesCondition(); }
        @Override public ResourceLocation getID() { return ID; }
    }
}
