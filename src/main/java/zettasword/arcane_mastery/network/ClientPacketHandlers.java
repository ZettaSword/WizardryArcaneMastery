package zettasword.arcane_mastery.network;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zettasword.arcane_mastery.cap.ArcaneDataHolder;

@OnlyIn(Dist.CLIENT)
public class ClientPacketHandlers {

    public static void applyFirstEnterSync(CompoundTag data) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;
        //player.getCapability(FirstEnterHolder.INSTANCE).ifPresent(cap -> {cap.discoverMagic(true);});
        if (data != null){
            player.getCapability(ArcaneDataHolder.INSTANCE).ifPresent(cap -> {
                cap.deserializeNBT(data);
            });
        }
    }

}