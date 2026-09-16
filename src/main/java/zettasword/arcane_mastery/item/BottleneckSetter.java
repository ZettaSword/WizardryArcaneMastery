package zettasword.arcane_mastery.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import zettasword.arcane_mastery.cap.ArcaneData;

import java.util.List;

public class BottleneckSetter extends Item {
    private final int bottle;
    public BottleneckSetter(int bottle, Rarity rarity) {
        super(new Item.Properties().stacksTo(1).rarity(rarity));
        this.bottle = bottle;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()){
            ArcaneData.get(player).ifPresent(data -> {
                if (data.setBottleneck(this.bottle)){
                    player.sendSystemMessage(Component.translatable("arcane_mastery.bottleneck_changed"));
                    player.getItemInHand(hand).shrink(1);
                }
            });
        }
        return super.use(level, player, hand);
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> list, TooltipFlag p_41424_) {
        super.appendHoverText(p_41421_, p_41422_, list, p_41424_);
        list.add(Component.translatable("item.arcane_mastery.bottleneck.desc"));
    }
}
