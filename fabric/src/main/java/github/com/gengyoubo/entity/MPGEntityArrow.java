package github.com.gengyoubo.entity;

import github.com.gengyoubo.MPG;
import github.com.gengyoubo.common.entity.MPGEntityArrowBase;
import github.com.gengyoubo.common.entity.MPGEntityData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MPGEntityArrow extends MPGEntityArrowBase {
    private static final Identifier ENTITY_ID =
            github.com.gengyoubo.util.MPResource.id(MPG.MODID, "manaita_arrow");

    public MPGEntityArrow(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);
    }

    public static AbstractArrow create(Level level, LivingEntity owner) {
        if (BuiltInRegistries.ENTITY_TYPE.containsKey(ENTITY_ID)) {
            Entity created = BuiltInRegistries.ENTITY_TYPE.getValue(ENTITY_ID).create(level, net.minecraft.world.entity.EntitySpawnReason.TRIGGERED);
            if (created instanceof AbstractArrow arrow) {
                arrow.setOwner(owner);
                arrow.setPos(owner.getX(), owner.getEyeY() - 0.1D, owner.getZ());
                return arrow;
            }
        }
        return new Arrow(level, owner, ItemStack.EMPTY, ItemStack.EMPTY);
    }

    @Override
    protected void markForDeath(Entity target) {
        MPGEntityData.death.add(target);
    }
}
