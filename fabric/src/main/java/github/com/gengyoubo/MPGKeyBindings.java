package github.com.gengyoubo;

import com.mojang.blaze3d.platform.InputConstants;
import github.com.gengyoubo.core.MPKeyBoardCore;
import github.com.gengyoubo.common.event.MPGClientEventLogic;
import github.com.gengyoubo.network.MPNetworking;
import github.com.gengyoubo.common.network.payload.MPGKeyPressPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public final class MPGKeyBindings {
    private MPGKeyBindings() {
    }

    public static void init() {
        MPKeyBoardCore.MESSAGE_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.manaita",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_X,
                KeyMapping.Category.MISC
        ));
        MPKeyBoardCore.MESSAGE_ARMOR_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.manaita.armor",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                KeyMapping.Category.MISC
        ));
        MPKeyBoardCore.PAXEL_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.manaita.doubling",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                KeyMapping.Category.MISC
        ));

        ClientTickEvents.END_CLIENT_TICK.register(MPGKeyBindings::onClientTick);
    }

    private static void onClientTick(Minecraft client) {
        if (client.player == null) {
            return;
        }

        while (MPKeyBoardCore.MESSAGE_KEY.consumeClick()) {
            MPGClientEventLogic.handleMainHandKey(client.player);
            sendKeyPacket((byte) 0);
        }

        while (MPKeyBoardCore.MESSAGE_ARMOR_KEY.consumeClick()) {
            MPGClientEventLogic.handleArmorKey(client.player);
            sendKeyPacket((byte) 1);
        }

        while (MPKeyBoardCore.PAXEL_KEY.consumeClick()) {
            MPGClientEventLogic.handlePaxelKey(client.player);
            sendKeyPacket((byte) 2);
        }
    }

    private static void sendKeyPacket(byte keyCode) {
        ClientPlayNetworking.send(new MPGKeyPressPayload(keyCode));
    }
}
