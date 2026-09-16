package zettasword.arcane_mastery.cap;

import com.binaris.wizardry.api.content.spell.SpellTier;
import com.binaris.wizardry.registry.EBRegistriesForge;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkDirection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import zettasword.arcane_mastery.ArcaneConfig;
import zettasword.arcane_mastery.ArcaneMastery;
import zettasword.arcane_mastery.network.ArcaneDataSyncPacketS2C;
import zettasword.arcane_mastery.network.PacketHandler;


public class ArcaneDataHolder implements INBTSerializable<CompoundTag>, IArcaneData {
    public static final ResourceLocation LOCATION = ResourceLocation.fromNamespaceAndPath(ArcaneMastery.MODID,"arcane_data");
    public static final Capability<ArcaneDataHolder> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {
    });

    /** Spell tier defines what spells you can and cannot cast. Once you achieve Master tier you can cast any spells whatsoever.*/
    private SpellTier spellTier = SpellTiers.NOVICE;

    /** Mana is resource consumed to cast spells, it is used first, then if you're out of mana - Mana is consumed from wand.
    *   Mana can charge mana flasks. Mana regenerates by amount per X ticks (20 ticks = 1 second), all is defined by Config.*/
    private int mana=0;

    /** Max mana is how much mana player body/soul can handle at once. It slowly increases by 1 from casting spells, or using mana in
     * other ways. How much mana = 1 max mana is defined by Config.
     */
    private int maxMana= ArcaneConfig.default_max_mana;

    /** Stores how much progress you gained from using mana to get 1 Max Mana. Just utility. */
    private int progress=0;

    /** Bottleneck counter. Only can increase! */
    private int bottleneck=0;


    private final Player provider;

    public ArcaneDataHolder(Player player) {
        this.provider = player;
    }

    @Override
    public void sync() {
        if (!this.provider.level().isClientSide()) {
            CompoundTag tag = this.serializeNBT();

            ArcaneDataSyncPacketS2C packet = new ArcaneDataSyncPacketS2C(tag);
            PacketHandler.INSTANCE.sendTo(packet, ((ServerPlayer)this.provider).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    @Override
    public int getMana() {
        return this.mana;
    }

    @Override
    public void setMana(int set) {
        this.mana = Math.min(set, this.maxMana);
        sync();
    }

    @Override
    public void addMana(int add) {
        this.mana = Math.min(this.mana + add, this.maxMana);
        sync();
    }

    @Override
    public int getMaxMana() {
        return this.maxMana;
    }

    @Override
    public void setMaxMana(int set) {
        this.maxMana = set;
        sync();
    }

    @Override
    public void addMaxMana(int add) {
        this.maxMana += add;
        sync();
    }

    @Override
    public SpellTier getCurrentTier() {
        return this.spellTier;
    }

    @Override
    public void setTier(SpellTier tier) {
        this.spellTier=tier;
        sync();
    }

    @Override
    public int getProgress() {
        return this.progress;
    }

    @Override
    public void setProgress(int set) {
        this.progress=set;
        sync();
    }

    @Override
    public void addProgress(int add) {
        this.progress+=add;
        sync();
    }

    @Override
    public void addMaxManaRemoveProgress(int max_mana_add, int set_progress) {
        this.maxMana+=max_mana_add;
        this.progress=set_progress;
        sync();
    }

    @Override
    public boolean setBottleneck(int set) {
        int oldBottleneck = this.bottleneck;
        boolean changed = false;
        if (this.bottleneck == set - 1) this.bottleneck=set;
        if (oldBottleneck != this.bottleneck) changed = true;
        if (changed) {
            sync();
            return true;
        }
        return false;
    }

    @Override
    public int getBottleneck() {
        return this.bottleneck;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        //tag.putBoolean("discovered_magic", this.discovered_magic);
        tag.putString("tier",this.spellTier.toString());
        tag.putInt("mana",this.mana);
        tag.putInt("max_mana",this.maxMana);
        tag.putInt("progress",this.progress);
        tag.putInt("bottleneck",this.bottleneck);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag.contains("tier"))
            this.spellTier = EBRegistriesForge.TIER.get().getValue(ResourceLocation.parse(tag.getString("tier")));
        if (tag.contains("mana"))
            this.mana = tag.getInt("mana");
        if (tag.contains("max_mana"))
            this.maxMana = tag.getInt("max_mana");
        if (tag.contains("progress"))
            this.progress = tag.getInt("progress");
        if (tag.contains("bottleneck"))
            this.bottleneck = tag.getInt("bottleneck");
        //this.discovered_magic = tag.getBoolean("discovered_magic");
    }

    public void copyFrom(@NotNull ArcaneDataHolder old) {
        this.spellTier = old.spellTier;
        this.mana = old.mana;
        this.maxMana = old.maxMana;
        this.progress = old.progress;
        this.bottleneck = old.bottleneck;
        //this.discovered_magic = old.discovered_magic;
        //this.spellsDiscovered.clear();
        //this.spellsDiscovered.addAll(old.spellsDiscovered);

        //this.spellData.clear();
        //this.spellData.putAll(old.spellData);
    }

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final LazyOptional<ArcaneDataHolder> dataHolder;

        public Provider(Player player) {
            this.dataHolder = LazyOptional.of(() -> new ArcaneDataHolder(player));
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
            return ArcaneDataHolder.INSTANCE.orEmpty(capability, dataHolder.cast());
        }

        @Override
        public CompoundTag serializeNBT() {
            return dataHolder.orElseThrow(NullPointerException::new).serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag arg) {
            dataHolder.orElseThrow(NullPointerException::new).deserializeNBT(arg);
        }
    }
}
