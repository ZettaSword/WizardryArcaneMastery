package zettasword.arcane_mastery;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import zettasword.arcane_mastery.commands.SetManaCommand;
import zettasword.arcane_mastery.commands.SetMaxManaCommand;
import zettasword.arcane_mastery.item.ArcaneItems;
import zettasword.arcane_mastery.network.PacketHandler;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ArcaneMastery.MODID)
public class ArcaneMastery
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "arcane_mastery";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public ArcaneMastery(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        ArcaneItems.ITEMS.register(modEventBus);
        ArcaneTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ArcaneEvents.register();

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, ArcaneConfig.SPEC);
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        SetMaxManaCommand.register(event.getDispatcher());
        SetManaCommand.register(event.getDispatcher());
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(PacketHandler::register);
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
        }
    }
}
