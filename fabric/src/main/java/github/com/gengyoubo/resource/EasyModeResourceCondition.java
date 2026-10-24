package github.com.gengyoubo.resource;

import github.com.gengyoubo.MPG;
import github.com.gengyoubo.MPGConfig;
import net.minecraft.resources.ResourceLocation;

public final class EasyModeResourceCondition {
    public static final ResourceLocation ID = new ResourceLocation(MPG.MODID, "easy_mode");
    private EasyModeResourceCondition() {}
    public static boolean test(com.google.gson.JsonObject json) { return MPGConfig.easy_mode_value; }
}
