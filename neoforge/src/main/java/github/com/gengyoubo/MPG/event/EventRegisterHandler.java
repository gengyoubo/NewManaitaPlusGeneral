package github.com.gengyoubo.MPG.event;

import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import github.com.gengyoubo.MPG.MPG;
import github.com.gengyoubo.MPG.core.MPGAttributeCore;

@EventBusSubscriber(modid = MPG.MODID)
public class EventRegisterHandler {
    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        event.getTypes().forEach(entityType -> event.add(entityType, MPGAttributeCore.Type));
    }
}
