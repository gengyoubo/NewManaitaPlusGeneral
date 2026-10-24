package github.com.gengyoubo.common.network;

import net.minecraft.network.FriendlyByteBuf;

/** Shared packet encoding for the 1.19.2 loader networking APIs. */
public interface MPGPacketCodec<T> {
    T decode(FriendlyByteBuf buffer);
    void encode(FriendlyByteBuf buffer, T packet);
}
