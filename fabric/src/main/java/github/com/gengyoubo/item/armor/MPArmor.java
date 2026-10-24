package github.com.gengyoubo.item.armor;

import github.com.gengyoubo.common.item.armor.MPGArmorItemBase;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;

/** Fabric armor pieces; textures come from the shared manaita_armor equipment asset. */
public class MPArmor extends MPGArmorItemBase {
    protected MPArmor(Item.Properties properties, EquipmentSlot slot) {
        super(properties, slot);
    }

    public static class Helmet extends MPGArmorItemBase.Helmet {
        public Helmet(Item.Properties properties) {
            super(properties);
        }
    }

    public static class Chestplate extends MPGArmorItemBase.Chestplate {
        public Chestplate(Item.Properties properties) {
            super(properties);
        }
    }

    public static class Leggings extends MPGArmorItemBase.Leggings {
        public Leggings(Item.Properties properties) {
            super(properties);
        }
    }

    public static class Boots extends MPGArmorItemBase.Boots {
        public Boots(Item.Properties properties) {
            super(properties);
        }
    }
}
