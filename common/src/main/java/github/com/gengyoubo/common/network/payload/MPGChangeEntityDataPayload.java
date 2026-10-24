package github.com.gengyoubo.common.network.payload;

import github.com.gengyoubo.common.MPGCommon;
import net.minecraft.network.FriendlyByteBuf;
import github.com.gengyoubo.common.network.MPGPacketCodec;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record MPGChangeEntityDataPayload(int entityId, int flags) {
    public static final ResourceLocation TYPE = new ResourceLocation(MPGCommon.MOD_ID, "change_entity_data");
    public static final MPGPacketCodec<MPGChangeEntityDataPayload> STREAM_CODEC = new MPGPacketCodec<>() {
        @Override
        public @NotNull MPGChangeEntityDataPayload decode(FriendlyByteBuf buffer) {
            return new MPGChangeEntityDataPayload(buffer.readInt(), buffer.readInt());
        }

        @Override
        public void encode(FriendlyByteBuf buffer, MPGChangeEntityDataPayload payload) {
            buffer.writeInt(payload.entityId());
            buffer.writeInt(payload.flags());
        }
    };

}
