package github.com.gengyoubo.MPG.event;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import github.com.gengyoubo.MPG.MPG;
import github.com.gengyoubo.MPG.core.MPGAttributeCore;
import github.com.gengyoubo.MPG.datagen.MPBlockStateProvider;
import github.com.gengyoubo.MPG.datagen.MPItemModelProvider;
import github.com.gengyoubo.MPG.loottable.MPGLootTable;

@Mod.EventBusSubscriber(modid = MPG.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventRegisterHandler {
    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        event.getTypes().forEach(entityType -> event.add(entityType, MPGAttributeCore.Type.get()));
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        if (event.includeServer()) gen.addProvider(new MPGLootTable(gen));
        if (event.includeClient()) {
            gen.addProvider(new MPBlockStateProvider(gen, event.getExistingFileHelper()));
            gen.addProvider(new MPItemModelProvider(gen, event.getExistingFileHelper()));
        }
    }

}
