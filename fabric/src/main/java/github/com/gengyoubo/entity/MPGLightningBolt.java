package github.com.gengyoubo.entity;

import github.com.gengyoubo.common.entity.MPGLightningBoltBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class MPGLightningBolt extends MPGLightningBoltBase {
    @Override
    public net.minecraft.network.protocol.Packet<?> getAddEntityPacket() {
        return new net.minecraft.network.protocol.game.ClientboundAddEntityPacket(this);
    }

    public MPGLightningBolt(EntityType<? extends MPGLightningBolt> type, Level level) {
        super(type, level);
    }
}
