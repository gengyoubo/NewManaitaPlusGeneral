package github.com.gengyoubo.MPG.item.tool;

import net.minecraft.world.item.Item;
import github.com.gengyoubo.common.item.tool.MPGShearsItemBase;

public class MPGShearsItem extends MPGShearsItemBase {
    public MPGShearsItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    protected boolean messageUsesOverlay() {
        return false;
    }
}
