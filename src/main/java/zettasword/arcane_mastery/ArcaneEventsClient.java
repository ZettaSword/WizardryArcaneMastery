package zettasword.arcane_mastery;

import com.binaris.wizardry.api.content.item.ICastItem;
import com.binaris.wizardry.api.content.spell.SpellTier;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import zettasword.arcane_mastery.cap.ArcaneData;

import java.awt.*;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = ArcaneMastery.MODID)
public class ArcaneEventsClient {

    private static final Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    public static void onRenderEvent(RegisterGuiOverlaysEvent event){
        event.registerBelow(VanillaGuiOverlay.CROSSHAIR.id(), "mana_overlay", (forgeGui, guiGraphics, partialTicks, width, height) -> {
            Player player = mc.player;
            if (player == null) return;

            ItemStack wand = player.getMainHandItem();
            boolean mainHand;

            if (!(wand.getItem() instanceof ICastItem castingItem && castingItem.showSpellHUD(player, wand))) {
                wand = player.getOffhandItem();
                mainHand = false;
                if (!(wand.getItem() instanceof ICastItem castingItem && castingItem.showSpellHUD(player, wand)))
                    return;
            } else {
                mainHand = true;
            }

            ItemStack finalWand = wand;
            renderText(guiGraphics, player, finalWand);
        });
    }

    public static Component getTier(SpellTier tier){
        if (tier == SpellTiers.NOVICE) return Component.translatable("arcane_mastery.novice");
        if (tier == SpellTiers.APPRENTICE) return Component.translatable("arcane_mastery.apprentice");
        if (tier == SpellTiers.ADVANCED) return Component.translatable("arcane_mastery.advanced");
        if (tier == SpellTiers.MASTER) return Component.translatable("arcane_mastery.master");
        return Component.translatable("arcane_mastery.master");
    }

    // Give next tier task!
    public static int getTierReq(SpellTier tier){
        if (tier == SpellTiers.NOVICE) return ArcaneConfig.tier_apprentice;
        if (tier == SpellTiers.APPRENTICE) return ArcaneConfig.tier_advanced;
        if (tier == SpellTiers.ADVANCED) return ArcaneConfig.tier_master;
        return ArcaneConfig.tier_master;
    }

    // We will remove this from both before dividing them, so we can get actual percentage.
    public static int getTierCur(SpellTier tier){
        if (tier == SpellTiers.NOVICE) return ArcaneConfig.default_max_mana;
        if (tier == SpellTiers.APPRENTICE) return ArcaneConfig.tier_apprentice;
        if (tier == SpellTiers.ADVANCED) return ArcaneConfig.tier_advanced;
        if (tier == SpellTiers.MASTER) return ArcaneConfig.tier_master;
        return ArcaneConfig.tier_master;
    }

    public static void renderText(GuiGraphics guiGraphics, Player player, ItemStack wand) {
        if (player.isSpectator()) return;
        if (mc.options.renderDebug) return;
        if (!(wand.getItem() instanceof ICastItem))
            throw new IllegalArgumentException("The given stack must contain an ISpellCastingItem!");

        Font font = mc.font;
        ArcaneData.get(player).ifPresent(data -> {
            if (ArcaneConfig.renderTierText) {
                SpellTier tier = data.getCurrentTier();
                Component tiered = getTier(tier);

                int requirement = getTierReq(tier);
                int cur_req = getTierCur(tier);
                float count = ((float)(data.getMaxMana() - cur_req) / (float)(requirement - cur_req)) * 100F;
                int mastery = (int) Math.floor(count);

                if (tier != SpellTiers.MASTER) {
                    // Check if player is exactly 1 point away from the next tier
                    boolean isAtBottleneckThreshold = (data.getMaxMana() == requirement - 1);
                    // Check if they haven't cleared the bottleneck yet
                    boolean needsScroll = data.getBottleneck() < (tier.getLevel() + 1);

                    if (isAtBottleneckThreshold && needsScroll && ArcaneConfig.bottlenecks) {
                        drawScaledStringToWidth(guiGraphics, font, Component.translatable("arcane_mastery.tier_bottleneck", tiered),
                                ArcaneConfig.text_x, ArcaneConfig.text_y,
                                2.0F, 0xffd700, 120, false);
                    } else {
                        // Normal progression text in WHITE
                        drawScaledStringToWidth(guiGraphics, font, Component.translatable("arcane_mastery.tier", tiered, mastery),
                                ArcaneConfig.text_x, ArcaneConfig.text_y,
                                2.0F, java.awt.Color.WHITE.getRGB(), 50, false);
                    }
                } else {
                    drawScaledStringToWidth(guiGraphics, font, Component.translatable("arcane_mastery.tier_mastered", tiered),
                            ArcaneConfig.text_x, ArcaneConfig.text_y,
                            2.0F, java.awt.Color.WHITE.getRGB(), 50, false);
                }
            }

            if (ArcaneConfig.renderManaText) {
                if (ArcaneConfig.hideManaWhenFull) {
                    if (data.getMana() != data.getMaxMana()) {
                        drawScaledStringToWidth(guiGraphics, font, Component.literal(data.getMana() + " / " + data.getMaxMana() + " MP")
                                , ArcaneConfig.text_mana_x, ArcaneConfig.text_mana_y,
                                1.0F, Color.WHITE.getRGB(), 50, false);
                    }
                }else{
                    drawScaledStringToWidth(guiGraphics, font, Component.literal(data.getMana() + " / " + data.getMaxMana() + " MP")
                            , ArcaneConfig.text_mana_x, ArcaneConfig.text_mana_y,
                            1.0F, Color.WHITE.getRGB(), 50, false);
                }
            }
        });
    }

    private static void drawScaledStringToWidth(GuiGraphics guiGraphics, Font font, Component text, float x, float y, float scale, int colour, float width, boolean alignR) {
        float textWidth = font.width(text) * scale;
        float textHeight = font.lineHeight * scale;

        // If the text is wider than the desired width, adjust the scale
        if (textWidth > width) {
            scale *= width / textWidth;
            font.width(text);
        } else if (alignR) {
            x += width - textWidth;
        }

        y += (font.lineHeight - textHeight) / 2;

        PoseStack stack = guiGraphics.pose();
        stack.pushPose();
        RenderSystem.enableBlend();
        stack.scale(scale, scale, scale);

        float adjustedX = x / scale;
        float adjustedY = y / scale;

        guiGraphics.drawString(font, text, (int) adjustedX, (int) adjustedY, colour);

        RenderSystem.disableBlend();
        stack.popPose();
    }
}
