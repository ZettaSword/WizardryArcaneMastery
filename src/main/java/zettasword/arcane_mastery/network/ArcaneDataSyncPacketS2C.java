package zettasword.arcane_mastery.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ArcaneDataSyncPacketS2C {

    private final CompoundTag data;

    public ArcaneDataSyncPacketS2C(CompoundTag data) {
        this.data = data;
    }

    public static ArcaneDataSyncPacketS2C decode(FriendlyByteBuf buf) {
        return new ArcaneDataSyncPacketS2C(buf.readNbt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(data);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                        ClientPacketHandlers.applyFirstEnterSync(data)
                )
        );
        ctx.get().setPacketHandled(true);
    }

    public CompoundTag getData() {
        return data;
    }
}