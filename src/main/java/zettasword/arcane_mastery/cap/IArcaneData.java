package zettasword.arcane_mastery.cap;

import com.binaris.wizardry.api.content.spell.SpellTier;

public interface IArcaneData {

    /**
     * Synchronises data!
     */
    void sync();

    int getMana();
    void setMana(int set);
    void addMana(int add);

    int getMaxMana();
    void setMaxMana(int set);
    void addMaxMana(int add);

    SpellTier getCurrentTier();
    void setTier(SpellTier tier);

    int getProgress();
    void setProgress(int set);
    void addProgress(int add);

    void addMaxManaRemoveProgress(int max_mana_add, int set_progress);
    boolean setBottleneck(int set);
    int getBottleneck();
}


