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
    private final int requiredBottleneckLevel;

    public BottleneckSetter(int requiredBottleneckLevel, Rarity rarity) {
        super(new Item.Properties().stacksTo(1).rarity(rarity));
        this.requiredBottleneckLevel = requiredBottleneckLevel;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            ArcaneData.get(player).ifPresent(data -> {
                // Attempt to increment the bottleneck counter
                if (data.setBottleneck(this.requiredBottleneckLevel)) {
                    player.sendSystemMessage(Component.translatable("arcane_mastery.bottleneck.success"));
                    player.getItemInHand(hand).shrink(1); // Consume the item
                } else {
                    // Provide specific feedback on why it failed
                    if (data.getBottleneck() >= this.requiredBottleneckLevel) {
                        player.sendSystemMessage(Component.translatable("arcane_mastery.bottleneck.already_unlocked"));
                    } else {
                        player.sendSystemMessage(Component.translatable("arcane_mastery.bottleneck.wrong_tier"));
                    }
                }
            });
        }
        // Return success to prevent weird client-side animation glitches
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, level, list, flag);
        list.add(Component.translatable("item.arcane_mastery.bottleneck.desc"));
    }
}