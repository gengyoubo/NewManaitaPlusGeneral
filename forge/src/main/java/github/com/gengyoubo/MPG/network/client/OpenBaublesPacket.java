package github.com.gengyoubo.MPG.network.client;

import net.minecraft.network.chat.TranslatableComponent;
import github.com.gengyoubo.MPG.MPG;
import github.com.gengyoubo.MPG.baubles.common.capability.BaublesCapability;
import github.com.gengyoubo.MPG.baubles.common.lib.PlayerHandler;
import github.com.gengyoubo.MPG.menu.MPGBaublesMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import github.com.gengyoubo.common.network.MPGPacketCodec;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public record OpenBaublesPacket() {
    public static final MPGPacketCodec<OpenBaublesPacket> STREAM_CODEC = new MPGPacketCodec<>() {
        public OpenBaublesPacket decode(FriendlyByteBuf buf) { return new OpenBaublesPacket(); }
        public void encode(FriendlyByteBuf buf, OpenBaublesPacket packet) {}
    };

    public static void handle(OpenBaublesPacket payload, Supplier<NetworkEvent.Context> contextSupplier) {
        var context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getSender() == null || !BaublesCapability.isEnabled()) {
                return;
            }
            ServerPlayer sender = context.getSender();
            sender.openMenu(new net.minecraft.world.SimpleMenuProvider(
                    (containerId, inventory, player) -> new MPGBaublesMenu(containerId, inventory, PlayerHandler.getPlayerBaubles(player)),
                    new TranslatableComponent("container.manaita_baubles")
            ));
        });
        context.setPacketHandled(true);
    }

}
