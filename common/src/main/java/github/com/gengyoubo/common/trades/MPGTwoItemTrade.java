package github.com.gengyoubo.common.trades;

import java.util.Random;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.NotNull;

public record MPGTwoItemTrade(ItemStack firstInput, ItemStack secondInput, ItemStack output,
                              int maxUses, int xp, float priceMultiplier)
        implements VillagerTrades.ItemListing {
    @Override
    public MerchantOffer getOffer(@NotNull Entity trader, @NotNull Random random) {
        return new MerchantOffer(
                firstInput.copy(),
                secondInput.copy(),
                output.copy(), maxUses, xp, priceMultiplier);
    }
}
