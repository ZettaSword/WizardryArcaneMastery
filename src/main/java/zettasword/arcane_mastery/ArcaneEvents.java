package zettasword.arcane_mastery;

import com.binaris.wizardry.api.content.data.WizardData;
import com.binaris.wizardry.api.content.event.SpellBindEvent;
import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellTier;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.core.event.WizardryEventBus;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import zettasword.arcane_mastery.cap.ArcaneData;
import zettasword.arcane_mastery.cap.ArcaneDataHolder;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = ArcaneMastery.MODID)
public class ArcaneEvents {

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event){
        if (event.phase == TickEvent.Phase.START && !event.player.level().isClientSide()) {
            if (event.player.tickCount % ArcaneConfig.regen_mana_interval == 0) {
                int bonus = event.player.getDeltaMovement().length() > 0 ?  0 : 5;
                ArcaneData.get(event.player).ifPresent(data -> data.addMana(ArcaneConfig.regen_mana + bonus));
            }
        }
    }

    public static void register() {
        WizardryEventBus bus = WizardryEventBus.getInstance();
        bus.register(SpellCastEvent.Pre.class, ArcaneEvents::onPreCast);
        bus.register(SpellCastEvent.Tick.class, ArcaneEvents::onTickCast);
        bus.register(SpellCastEvent.Post.class, ArcaneEvents::onAfterCast);
        bus.register(SpellBindEvent.class, ArcaneEvents::onSpellBind);
    }

    public static void onPreCast(SpellCastEvent.Pre event){
        if (event.getSource() == SpellCastEvent.Sources.WAND && event.getSpell().isInstantCast()
                && !event.isCanceled() && event.getCaster() instanceof Player player){
            if (player.isCreative()) return;
            ArcaneData.get(player).ifPresent(data -> {
                Spell spell = event.getSpell();
                if (data.getCurrentTier() != SpellTiers.MASTER && data.getCurrentTier().getLevel() < spell.getTier().getLevel()){
                    event.setCanceled(true);
                    if (event.getLevel().isClientSide()) {
                        player.displayClientMessage(Component.translatable("arcane_mastery.not_enough_mastery"), true);
                    }
                    return;
                }
                float cost = event.getModifiers().get(SpellModifiers.COST, spell.getCost() + 0.1F);
                if (data.getMana() >= cost){
                    if (!event.getLevel().isClientSide()) {
                        data.addMana((int) Math.floor(-cost));
                    }
                    SpellModifiers modifiers = new SpellModifiers();
                    modifiers.set(SpellModifiers.COST, 0);
                    event.getModifiers().combine(modifiers);
                }else{
                    event.setCanceled(true);
                    // Show player that, well, there is not enough mana :)
                    if (event.getLevel().isClientSide()) {
                        player.displayClientMessage(Component.translatable("arcane_mastery.not_enough_mana"), true);
                    }
                }
            });
        }
    }

    public static boolean castCost(Player player, Spell spell, SpellModifiers spellModifiers) {
        Optional<ArcaneDataHolder> data = ArcaneData.get(player).resolve();
        if (data.isPresent()){
            float cost = spellModifiers.get(SpellModifiers.COST, spell.getCost() + 0.1F);
            if (data.get().getMana() >= cost){
                if (!player.level().isClientSide()) {
                    data.get().addMana((int) Math.floor(-cost));
                }
                SpellModifiers modifiers = new SpellModifiers();
                modifiers.set(SpellModifiers.COST, 0);
                spellModifiers.combine(modifiers);

                return true;
            }else{
                if (player.level().isClientSide()) {
                    player.displayClientMessage(Component.translatable("arcane_mastery.not_enough_mana"), true);
                }
            }
        }
        return false;
    }

    public static void onTickCast(SpellCastEvent.Tick event){
        if (event.getSource() == SpellCastEvent.Sources.WAND && !event.getSpell().isInstantCast()
                && event.getCaster() instanceof Player player){
            if (player.isCreative()) return;
            Spell spell = event.getSpell();
            SpellTier tier = ArcaneData.get(player).map(ArcaneDataHolder::getCurrentTier).orElse(SpellTiers.NOVICE);
            if (tier != SpellTiers.MASTER && tier.getLevel() < spell.getTier().getLevel()){
                event.setCanceled(true);
                if (event.getLevel().isClientSide()) {
                    player.displayClientMessage(Component.translatable("arcane_mastery.not_enough_mastery"), true);
                }
                event.getContext().castingTicks(0);
                return;
            }

            SpellModifiers spellModifiers = event.getModifiers();
            if(!castCost(player, spell, spellModifiers))
                event.setCanceled(true);
        }
    }

    public static void onAfterCast(SpellCastEvent.Post event){
        if (event.getSource() == SpellCastEvent.Sources.WAND
                && event.getCaster() instanceof Player player) {
            WizardData wizardData = Services.OBJECT_DATA.getWizardData(player);
            if (player.isCreative()) return;
            if (!event.getLevel().isClientSide()) {
                ArcaneData.get(player).ifPresent(data -> {
                    SpellTier tier;
                    if (data.getMaxMana() >= ArcaneConfig.tier_apprentice){
                        tier = SpellTiers.APPRENTICE;
                        if (data.getMaxMana() >= ArcaneConfig.tier_advanced){
                            tier = SpellTiers.ADVANCED;
                            if (data.getMaxMana() >= ArcaneConfig.tier_master){
                                tier = SpellTiers.MASTER;
                            }
                        }
                        data.setTier(tier);
                    }

                    if (data.getProgress() < ArcaneConfig.progress_to_gain) {
                        // Additional variables.
                        if ((wizardData != null && ArcaneConfig.antiCheese)) {
                            if (wizardData.countRecentCasts(event.getSpell()) <= 3) {
                                data.addProgress(1);
                            }
                        }else{
                            data.addProgress(1);
                        }

                        //ArcaneMastery.LOGGER.warn("ARCANE: ADDED PROGRESS {}", data.getProgress());
                    } else {
                        if (ArcaneConfig.bottlenecks) {
                            int req = getTierReq(data.getCurrentTier());
                            if (req - 1 == data.getMaxMana()) { // If we hit bottleneck
                                if (data.getBottleneck() >= data.getCurrentTier().getLevel()) {
                                    data.addMaxManaRemoveProgress(1, 0);
                                }
                            }else{
                                data.addMaxManaRemoveProgress(1, 0);
                            }
                        }else{
                            data.addMaxManaRemoveProgress(1, 0);
                        }
                        //ArcaneMastery.LOGGER.warn("ARCANE: ADDED MAX MANA {} progress, {} max mana", data.getProgress(), data.getMaxMana());
                    }

                    // Bonus for Novice tier to get Apprentice faster.
                    if (data.getCurrentTier() == SpellTiers.NOVICE && data.getMaxMana() < ArcaneConfig.tier_apprentice-1){
                        data.addProgress(1);
                        //ArcaneMastery.LOGGER.warn("ARCANE: NOVICE BONUS! {}", data.getProgress());
                    }

                    // TODO: Randomly make quotes appear like: You feel progress; Your senses become better; Your soul becomes stronger; etc.

                });
            }
        }
    }

    public static int getTierReq(SpellTier tier){
        if (tier == SpellTiers.NOVICE) return ArcaneConfig.tier_apprentice;
        if (tier == SpellTiers.APPRENTICE) return ArcaneConfig.tier_advanced;
        if (tier == SpellTiers.ADVANCED) return ArcaneConfig.tier_master;
        return ArcaneConfig.tier_master;
    }

    public static void onSpellBind(SpellBindEvent event){
        if (event.getPlayer().isCreative()) return;
        if (!event.isCanceled()) {
            ArcaneData.get(event.getPlayer()).ifPresent(data -> {
                if (data.getMana() >= 10){
                    if (!event.getPlayer().level().isClientSide()) {
                        data.addMana(-10);
                    }
                }else{
                    event.setCanceled(true);
                    if (event.getPlayer().level().isClientSide()) {
                        event.getPlayer().displayClientMessage(
                                Component.translatable("arcane_mastery.not_enough_mana"), true);
                    }
                }
            });
        }
    }
}
