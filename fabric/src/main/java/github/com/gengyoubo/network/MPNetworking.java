package github.com.gengyoubo.network;

import github.com.gengyoubo.common.network.MPGKeyPressLogic;
import github.com.gengyoubo.common.network.payload.MPGChangeEntityDataPayload;
import github.com.gengyoubo.common.network.payload.MPGDestroyBlockPayload;
import github.com.gengyoubo.common.network.payload.MPGKeyPressPayload;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class MPNetworking {
    public static final CustomPayloadId<MPGKeyPressPayload> KEY_PRESS =
            new CustomPayloadId<>(MPGKeyPressPayload.TYPE, MPGKeyPressPayload.STREAM_CODEC);
    public static final CustomPayloadId<MPGDestroyBlockPayload> DESTROY_BLOCK =
            new CustomPayloadId<>(MPGDestroyBlockPayload.TYPE, MPGDestroyBlockPayload.STREAM_CODEC);
    public static final CustomPayloadId<MPGChangeEntityDataPayload> CHANGE_ENTITY_DATA =
            new CustomPayloadId<>(MPGChangeEntityDataPayload.TYPE, MPGChangeEntityDataPayload.STREAM_CODEC);
    private static boolean payloadsRegistered;

    private MPNetworking() {
    }

    public static void initCommon() {
        if (payloadsRegistered) {
            return;
        }

        payloadsRegistered = true;
    }

    public static void initServer() {
        initCommon();
        ServerPlayNetworking.registerGlobalReceiver(KEY_PRESS.type(), (server, player, handler, buf, responseSender) -> {
            MPGKeyPressPayload payload = KEY_PRESS.codec().decode(buf);
            server.execute(() -> MPGKeyPressLogic.handle(player, payload.keyCode()));
        });
    }

    public static void sendToSameLevelPlayers(Level level, MPGDestroyBlockPayload packet) {
        if (level instanceof ServerLevel serverLevel) {
            for (ServerPlayer serverPlayer : serverLevel.players()) {
                send(serverPlayer, packet);
            }
        }
    }

    public static void sendToSameLevelPlayers(Level level, MPGChangeEntityDataPayload packet) {
        if (level instanceof ServerLevel serverLevel) {
            for (ServerPlayer serverPlayer : serverLevel.players()) {
                send(serverPlayer, packet);
            }
        }
    }

    @Deprecated
    public static void sendToNearByPlayers(Level level, MPGDestroyBlockPayload packet, int range) {
        sendToSameLevelPlayers(level, packet);
    }

    public static void sendToTrackBySeen(Level level, Player player, MPGDestroyBlockPayload packet) {
        if (level instanceof ServerLevel serverLevel) {
            for (ServerPlayer serverPlayer : serverLevel.players()) {
                if (serverPlayer == player || serverPlayer.distanceToSqr(player) <= 128.0D * 128.0D) {
                    send(serverPlayer, packet);
                }
            }
        }
    }

    public static net.minecraft.network.FriendlyByteBuf encodeKey(byte keyCode) {
        var buf = PacketByteBufs.create();
        MPGKeyPressPayload.STREAM_CODEC.encode(buf, new MPGKeyPressPayload(keyCode));
        return buf;
    }
    private static void send(ServerPlayer player, MPGDestroyBlockPayload packet) {
        var buf = PacketByteBufs.create();
        MPGDestroyBlockPayload.STREAM_CODEC.encode(buf, packet);
        ServerPlayNetworking.send(player, DESTROY_BLOCK.type(), buf);
    }
    private static void send(ServerPlayer player, MPGChangeEntityDataPayload packet) {
        var buf = PacketByteBufs.create();
        MPGChangeEntityDataPayload.STREAM_CODEC.encode(buf, packet);
        ServerPlayNetworking.send(player, CHANGE_ENTITY_DATA.type(), buf);
    }
    public record CustomPayloadId<T>(net.minecraft.resources.ResourceLocation type,
            github.com.gengyoubo.common.network.MPGPacketCodec<T> codec) {}
}
