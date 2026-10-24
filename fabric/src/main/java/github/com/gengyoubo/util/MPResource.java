package github.com.gengyoubo.util;

import github.com.gengyoubo.MPG;
import net.minecraft.resources.Identifier;

public final class MPResource {
    private MPResource() {
    }

    public static Identifier id(String namespace, String path) {
        return Identifier.fromNamespaceAndPath(namespace, path);
    }

    public static Identifier id(String path) {
        return id(MPG.MODID, path);
    }

    public static Identifier parse(String id) {
        return Identifier.parse(id);
    }
}

