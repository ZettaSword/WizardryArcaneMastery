package zettasword.arcane_mastery;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import zettasword.arcane_mastery.item.ArcaneItems;

public class ArcaneTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ArcaneMastery.MODID);

    public static final RegistryObject<CreativeModeTab> ARCANE_MASTERY_TAB = CREATIVE_MODE_TABS.register("arcane_mastery_tab",
            () -> CreativeModeTab.builder()
            .title(Component.translatable("creativetab.arcane_mastery"))
            .icon(() -> ArcaneItems.BOTTLENECK_MASTER.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ArcaneItems.BOTTLENECK_APPRENTICE.get());
                output.accept(ArcaneItems.BOTTLENECK_ADVANCED.get());
                output.accept(ArcaneItems.BOTTLENECK_MASTER.get());
            }).build());

}
