package zettasword.arcane_mastery.cap;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

public class ArcaneData {
    public static @NotNull LazyOptional<ArcaneDataHolder> get(Player player){
       return player.getCapability(ArcaneDataHolder.INSTANCE);
    }
}
