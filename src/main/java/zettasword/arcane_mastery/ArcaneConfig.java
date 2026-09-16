package zettasword.arcane_mastery;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = ArcaneMastery.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ArcaneConfig
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue RENDER_TIER_TEXT = BUILDER
            .comment("Render tier text when holding a wand?")
            .define("renderTierText", true);

    private static final ForgeConfigSpec.IntValue TEXT_X = BUILDER
            .comment("Tier Text X position on screen.")
            .defineInRange("text_x", 10, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue TEXT_Y = BUILDER
            .comment("Tier Text Y position on screen.")
            .defineInRange("text_y", 20, 0, Integer.MAX_VALUE);


    private static final ForgeConfigSpec.BooleanValue RENDER_MANA_TEXT = BUILDER
            .comment("Render tier text when holding a wand?")
            .define("renderManaText", true);

    private static final ForgeConfigSpec.IntValue TEXT_MANA_X = BUILDER
            .comment("Tier Text X position on screen.")
            .defineInRange("text_mana_x", 10, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue TEXT_MANA_Y = BUILDER
            .comment("Tier Text Y position on screen.")
            .defineInRange("text_mana_y", 30, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue REGEN_MANA_INTERVAL = BUILDER
            .comment("Every X ticks regen Y mana (20 ticks = 1 second)")
            .defineInRange("regen_mana_interval", 4, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue REGEN_MANA = BUILDER
            .comment("How much mana we regen (Y) each X ticks.")
            .defineInRange("regen_mana", 1, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue DEFAULT_MAX_MANA = BUILDER
            .comment("Max Mana that caster appears first in the world.")
            .defineInRange("default_max_mana", 50, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue PROGRESS_TO_GAIN = BUILDER
            .comment("Progress to gain 1 Max mana per this amount of successful casts. Is 10 by default.")
            .defineInRange("progress_to_gain", 10, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue TIER_APPRENTICE = BUILDER
            .comment("How much max mana must caster have to count as Apprentice.")
            .defineInRange("tier_apprentice", 100, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue TIER_ADVANCED = BUILDER
            .comment("How much max mana must caster have to count as Advanced.")
            .defineInRange("tier_advanced", 150, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue TIER_MASTER = BUILDER
            .comment("How much max mana must caster have to count as Master.")
            .defineInRange("tier_master", 500, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.BooleanValue ANTI_CHEESE = BUILDER
            .comment("Don't count mastery after casting same spell 3 or more times. Is made to prevent spamming one spell to get higher max mana.")
            .define("antiCheese", true);

    private static final ForgeConfigSpec.BooleanValue BOTTLENECKS = BUILDER
            .comment("To progress to next tier you need use an item?")
            .define("bottlenecks", true);

    private static final ForgeConfigSpec.BooleanValue HIDE_MANA_WHEN_FULL = BUILDER
            .comment("Hide Mana bar when it's full?")
            .define("hideManaWhenFull", true);


    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean renderTierText;
    public static int text_x;
    public static int text_y;

    public static boolean renderManaText;
    public static int text_mana_x;
    public static int text_mana_y;

    public static int regen_mana_interval;
    public static int regen_mana;

    public static int progress_to_gain;
    public static int tier_apprentice;
    public static int tier_advanced;
    public static int tier_master;

    public static int default_max_mana;

    public static boolean antiCheese;
    public static boolean bottlenecks;
    public static boolean hideManaWhenFull;


    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        renderTierText = RENDER_TIER_TEXT.get();
        text_x = TEXT_X.get();
        text_y = TEXT_Y.get();

        renderManaText = RENDER_MANA_TEXT.get();
        text_mana_x = TEXT_MANA_X.get();
        text_mana_y = TEXT_MANA_Y.get();

        regen_mana_interval = REGEN_MANA_INTERVAL.get();
        regen_mana = REGEN_MANA.get();

        progress_to_gain = PROGRESS_TO_GAIN.get();
        tier_apprentice = TIER_APPRENTICE.get();
        tier_advanced = TIER_ADVANCED.get();
        tier_master = TIER_MASTER.get();

        default_max_mana = DEFAULT_MAX_MANA.get();
        antiCheese = ANTI_CHEESE.get();
        bottlenecks = BOTTLENECKS.get();
        hideManaWhenFull = HIDE_MANA_WHEN_FULL.get();
    }
}
