package zettasword.arcane_mastery.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import zettasword.arcane_mastery.cap.ArcaneData;

public class SetMaxManaCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("setArcaneMaxMana")
                .requires(source -> source.hasPermission(2)) // operator only
                    .then(Commands.argument("target", EntityArgument.player())
                        .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                            .executes(SetMaxManaCommand::setMaxMana))));
    }

    private static int setMaxMana(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "target");
        int amount = IntegerArgumentType.getInteger(context, "amount");

        ArcaneData.get(target).ifPresent(data -> {
            data.setMaxMana(amount);
        });

        context.getSource().sendSuccess(
            () -> Component.literal("Set max mana of " + target.getName().getString() + " to " + amount),
            true
        );

        return 1;
    }
}