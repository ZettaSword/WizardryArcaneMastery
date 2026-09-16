package zettasword.arcane_mastery.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import zettasword.arcane_mastery.ArcaneMastery;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1.0";
    public static SimpleChannel INSTANCE;

    public static void register() {
        INSTANCE = NetworkRegistry.newSimpleChannel(
                ResourceLocation.fromNamespaceAndPath(ArcaneMastery.MODID, "main"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );

        int id = 0;
        // In PacketHandler.register():
        INSTANCE.messageBuilder(ArcaneDataSyncPacketS2C.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ArcaneDataSyncPacketS2C::encode)
                .decoder(ArcaneDataSyncPacketS2C::decode)
                .consumerMainThread(ArcaneDataSyncPacketS2C::handle)
                .add();
    }
}