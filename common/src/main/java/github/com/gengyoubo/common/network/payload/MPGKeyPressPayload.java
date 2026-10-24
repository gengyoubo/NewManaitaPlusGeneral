package github.com.gengyoubo.common.network.payload;

import github.com.gengyoubo.common.MPGCommon;
import net.minecraft.network.FriendlyByteBuf;
import github.com.gengyoubo.common.network.MPGPacketCodec;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record MPGKeyPressPayload(byte keyCode) {
    public static final ResourceLocation TYPE = new ResourceLocation(MPGCommon.MOD_ID, "key_press");
    public static final MPGPacketCodec<MPGKeyPressPayload> STREAM_CODEC = new MPGPacketCodec<>() {
        @Override
        public @NotNull MPGKeyPressPayload decode(FriendlyByteBuf buffer) {
            return new MPGKeyPressPayload(buffer.readByte());
        }

        @Override
        public void encode(FriendlyByteBuf buffer, MPGKeyPressPayload payload) {
            buffer.writeByte(payload.keyCode());
        }
    };

}
