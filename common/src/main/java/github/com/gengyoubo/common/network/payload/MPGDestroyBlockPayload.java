package github.com.gengyoubo.common.network.payload;

import github.com.gengyoubo.common.MPGCommon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import github.com.gengyoubo.common.network.MPGPacketCodec;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public record MPGDestroyBlockPayload(BlockPos blockPos, int range, Item item) {
    public static final ResourceLocation TYPE = new ResourceLocation(MPGCommon.MOD_ID, "destroy_block");
    public static final MPGPacketCodec<MPGDestroyBlockPayload> STREAM_CODEC = new MPGPacketCodec<>() {
        @Override
        public @NotNull MPGDestroyBlockPayload decode(FriendlyByteBuf buffer) {
            BlockPos position = buffer.readBlockPos();
            int range = buffer.readInt();
            Item item = Registry.ITEM.get(buffer.readResourceLocation());
            return new MPGDestroyBlockPayload(position, range, item);
        }

        @Override
        public void encode(FriendlyByteBuf buffer, MPGDestroyBlockPayload payload) {
            buffer.writeBlockPos(payload.blockPos());
            buffer.writeInt(payload.range());
            buffer.writeResourceLocation(Registry.ITEM.getKey(payload.item()));
        }
    };

}
