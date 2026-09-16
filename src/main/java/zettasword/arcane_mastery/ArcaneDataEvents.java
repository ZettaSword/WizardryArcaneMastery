package zettasword.arcane_mastery;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import zettasword.arcane_mastery.cap.ArcaneDataHolder;
import zettasword.arcane_mastery.cap.IArcaneData;

@Mod.EventBusSubscriber(modid = ArcaneMastery.MODID)
public class ArcaneDataEvents {
    @SubscribeEvent
    public static void onCapRegister(RegisterCapabilitiesEvent event){
        event.register(IArcaneData.class);
    }

    @SubscribeEvent
    public static void attachCapability(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            event.addCapability(ArcaneDataHolder.LOCATION, new ArcaneDataHolder.Provider(player));
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        // Revive the original player's capabilities to be able to read them
        event.getOriginal().reviveCaps();

        event.getOriginal().getCapability(ArcaneDataHolder.INSTANCE).ifPresent(old ->
                event.getEntity().getCapability(ArcaneDataHolder.INSTANCE).ifPresent(holder -> holder.copyFrom(old)));

        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        // Sync all capabilities to client after respawn to ensure the client has the correct data
        Player player = event.getEntity();
        if (!player.level().isClientSide()) {
            player.getCapability(ArcaneDataHolder.INSTANCE).ifPresent(ArcaneDataHolder::sync);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        // Sync all capabilities to client after dimension change to ensure the client has the correct data
        Player player = event.getEntity();
        if (!player.level().isClientSide()) {
            player.getCapability(ArcaneDataHolder.INSTANCE).ifPresent(ArcaneDataHolder::sync);
        }
    }

    @SubscribeEvent
    public static void onPlayerEnter(PlayerEvent.PlayerLoggedInEvent event){
        Player player = event.getEntity();
        if (!player.level().isClientSide()) {
            player.getCapability(ArcaneDataHolder.INSTANCE).ifPresent(ArcaneDataHolder::sync);
        }
    }
}
