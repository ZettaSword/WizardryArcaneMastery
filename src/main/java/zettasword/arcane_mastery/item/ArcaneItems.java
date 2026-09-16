package zettasword.arcane_mastery.item;

import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import zettasword.arcane_mastery.ArcaneMastery;

public class ArcaneItems {
    public static final DeferredRegister<Item> ITEMS = 
        DeferredRegister.create(ForgeRegistries.ITEMS, ArcaneMastery.MODID);

    public static final RegistryObject<Item> BOTTLENECK_APPRENTICE = ITEMS.register("bottleneck_apprentice", () ->
            new BottleneckSetter(SpellTiers.APPRENTICE.getLevel(), Rarity.UNCOMMON));
    public static final RegistryObject<Item> BOTTLENECK_ADVANCED= ITEMS.register("bottleneck_advanced", () ->
            new BottleneckSetter(SpellTiers.ADVANCED.getLevel(), Rarity.RARE));
    public static final RegistryObject<Item> BOTTLENECK_MASTER = ITEMS.register("bottleneck_master", () ->
            new BottleneckSetter(SpellTiers.MASTER.getLevel(), Rarity.EPIC));

}