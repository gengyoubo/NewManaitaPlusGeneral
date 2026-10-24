package github.com.gengyoubo.MPG.network;

import github.com.gengyoubo.MPG.MPG;
import github.com.gengyoubo.MPG.compat.CuriosCompat;
import github.com.gengyoubo.MPG.core.MPGItemCore;
import github.com.gengyoubo.common.network.MPGKeyPressLogic;
import github.com.gengyoubo.common.network.payload.MPGChangeEntityDataPayload;
import github.com.gengyoubo.common.network.payload.MPGDestroyBlockPayload;
import github.com.gengyoubo.common.network.payload.MPGKeyPressPayload;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class Networking {
    public static final String VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MPG.MODID, "main"), () -> VERSION, VERSION::equals, VERSION::equals);

    public static void registerMessage() {
        CHANNEL.messageBuilder(MPGKeyPressPayload.class, 0, NetworkDirection.PLAY_TO_SERVER)
                .encoder((packet, buf) -> MPGKeyPressPayload.STREAM_CODEC.encode(buf, packet))
                .decoder(MPGKeyPressPayload.STREAM_CODEC::decode)
                .consumerMainThread(Networking::handleKeyPress)
                .add();
        CHANNEL.messageBuilder(MPGDestroyBlockPayload.class, 2, NetworkDirection.PLAY_TO_CLIENT)
                .encoder((packet, buf) -> MPGDestroyBlockPayload.STREAM_CODEC.encode(buf, packet))
                .decoder(MPGDestroyBlockPayload.STREAM_CODEC::decode)
                .consumerMainThread(Networking::handleDestroyBlock)
                .add();
        CHANNEL.messageBuilder(MPGChangeEntityDataPayload.class, 3, NetworkDirection.PLAY_TO_CLIENT)
                .encoder((packet, buf) -> MPGChangeEntityDataPayload.STREAM_CODEC.encode(buf, packet))
                .decoder(MPGChangeEntityDataPayload.STREAM_CODEC::decode)
                .consumerMainThread(Networking::handleChangeEntityData)
                .add();
    }

    private static void handleKeyPress(MPGKeyPressPayload payload, Supplier<NetworkEvent.Context> contextSupplier) {
        var context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isClient() || context.getSender() == null) {
                return;
            }
            ServerPlayer player = context.getSender();
            if (payload.keyCode() == 0 && MPGItemCore.isCuriosLoaded()) {
                ItemStack ring = CuriosCompat.findEquippedRing(player);
                if (MPGKeyPressLogic.invoke(ring, player)) {
                    return;
                }
            }
            MPGKeyPressLogic.handle(player, payload.keyCode());
        });
        context.setPacketHandled(true);
    }

    private static void handleDestroyBlock(MPGDestroyBlockPayload payload, Supplier<NetworkEvent.Context> contextSupplier) {
        var context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isClient()) {
                ClientPacketHandlers.handleDestroyBlock(payload);
            }
        });
        context.setPacketHandled(true);
    }

    private static void handleChangeEntityData(MPGChangeEntityDataPayload payload, Supplier<NetworkEvent.Context> contextSupplier) {
        var context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isClient()) {
                ClientPacketHandlers.handleChangeEntityData(payload);
            }
        });
        context.setPacketHandled(true);
    }

    public static void sendToSameLevelPlayers(Level level, Object packet) {
        if (level instanceof ServerLevel serverLevel) {
            CHANNEL.send(PacketDistributor.DIMENSION.with(serverLevel::dimension), packet);
        }
    }

// --濞夈劑鍣撮幒澶嬵梾閺?START (2026/4/24 23:35):
//    public static void sendToNearByPlayers(Level level, Player center, Object packet, int range) {
//        if (level instanceof ServerLevel serverLevel) {
//            if (center == null) {
//                sendToSameLevelPlayers(level, packet);
//                return;
//            }
//            double finalRange = (double) range * (double) range;
//            for (ServerPlayer serverPlayer : serverLevel.players()) {
//                if (serverPlayer.distanceToSqr(center) <= finalRange) {
//                    Networking.INSTANCE.send(
//                            PacketDistributor.PLAYER.with(() -> serverPlayer),
//                            packet
//                    );
//                }
//            }
//        }
//    }
// --濞夈劑鍣撮幒澶嬵梾閺?STOP (2026/4/24 23:35)

    @Deprecated
    public static void sendToNearByPlayers(Level level, Object packet, int range) {
        sendToSameLevelPlayers(level, packet);
    }

    public static void sendToServer(Object packet) {
        CHANNEL.sendToServer(packet);
    }

    public static void sendToTrackBySeen(Level level, Player player, Object packet) {
        if (level instanceof ServerLevel) {
            CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> player), packet);
        }
    }
}
