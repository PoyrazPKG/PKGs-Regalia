package pkg_tales_of_the_force_wars.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import pkg_tales_of_the_force_wars.PKGTalesoftheForceWars;
import pkg_tales_of_the_force_wars.capability.JediDataProvider;
import pkg_tales_of_the_force_wars.network.AbilitySlotPacket;
import pkg_tales_of_the_force_wars.network.JediDataSyncPacket;

public class ForceCommand {
    private static final String[] ABILITIES = new String[]{
            // 1-15
            "force_push", "force_pull", "force_jump", "enhanced_reflexes", "force_speed",
            "force_stun", "force_slow", "mind_trick", "force_persuasion", "force_sense",
            "force_vision", "battle_meditation", "force_heal", "group_heal", "force_rejuvenation",
            // 16-30
            "force_life_mapping", "force_immunity", "force_barrier", "force_absorb", "force_deflection",
            "force_reflection", "force_wave", "force_suppression", "force_cloak", "force_camouflage",
            "force_stealth", "force_link", "force_empathy", "force_guidance", "force_guardianship",
            // 31-45
            "force_inspiration", "force_valor", "force_stamina", "force_endurance", "force_clarity",
            "force_projection", "force_ghost_manifestation", "force_telepathy", "force_tranquility", "force_sanctuary",
            "force_hearing", "force_touch", "force_light", "force_silence", "force_memory_recollection",
            // 46-52
            "force_bond", "force_guidance_of_the_blade", "force_inspired_healing", "force_symbiosis", "force_hymn",
            "force_coalescence", "force_relic_sense"
    };

    private static final SuggestionProvider<CommandSourceStack> ABILITY_SUGGESTIONS = (context, builder) -> {
        try {
            ServerPlayer target = EntityArgument.getPlayer(context, "player");
            final String[] list = ABILITIES;
            return SharedSuggestionProvider.suggest(list, builder);
        } catch (Exception e) {
            return SharedSuggestionProvider.suggest(ABILITIES, builder);
        }
    };
    private static final SuggestionProvider<CommandSourceStack> FORCE_STATUS_SUGGEST = (context, builder) ->
            SharedSuggestionProvider.suggest(new String[]{"force_user", "non_force"}, builder);
    // Slot'a atanamayan pasif/alt-dal yetenekler
    private static final java.util.Set<String> PASSIVE_ONLY = new java.util.HashSet<>(java.util.Arrays.asList(
            "force_life_mapping", "enhanced_reflexes"
    ));
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("force")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("get")
                                .executes(ForceCommand::getJediData))
                        .then(Commands.literal("use")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("status", StringArgumentType.string())
                                                .suggests(FORCE_STATUS_SUGGEST)
                                                .executes(ctx -> setForceUse(ctx, EntityArgument.getPlayer(ctx, "player"), StringArgumentType.getString(ctx, "status")))
                                        )
                                )
                        )
                        .then(Commands.literal("add")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0))
                                                .executes(context -> addForceValue(context, EntityArgument.getPlayer(context, "player"), DoubleArgumentType.getDouble(context, "amount")))
                                        )
                                )
                        )
                        .then(Commands.literal("set")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0))
                                                .executes(context -> setForceValue(context, EntityArgument.getPlayer(context, "player"), DoubleArgumentType.getDouble(context, "amount")))
                                        )
                                )
                        )
                        .then(Commands.literal("slot")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("slot", IntegerArgumentType.integer(1, 4))
                                                .then(Commands.argument("ability", StringArgumentType.string())
                                                        .suggests(ABILITY_SUGGESTIONS)
                                                        .executes(context -> setAbilitySlot(context, EntityArgument.getPlayer(context, "player"), IntegerArgumentType.getInteger(context, "slot"), StringArgumentType.getString(context, "ability")))
                                                )
                                        )
                                )
                        )
                        .executes(ForceCommand::showJediData)
        );
    }

    private static int getJediData(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = context.getSource().getPlayerOrException();
            player.getCapability(JediDataProvider.JEDI_DATA).ifPresent(data -> {
                String stage = data.getStage();
                double forceValue = data.getForceValue();
                context.getSource().sendSuccess(new TextComponent("Rank: " + stage + "\nForce Value: " + String.format("%.2f", forceValue)), false);
            });
            return 1;
        } catch (CommandSyntaxException e) {
            context.getSource().sendFailure(new TextComponent("Error: " + e.getMessage()));
            return 0;
        }
    }

    private static int showJediData(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = context.getSource().getPlayerOrException();
            player.getCapability(JediDataProvider.JEDI_DATA).ifPresent(data -> {
                String[] slots = data.getAbilitySlots();
                if (slots == null) slots = new String[4];
                PKGTalesoftheForceWars.CHANNEL.sendToServer(new JediDataSyncPacket(data.getStage(), data.getForceValue(), slots, data.getUnlockedAbilities()));
            });
            return 1;
        } catch (CommandSyntaxException e) {
            context.getSource().sendFailure(new TextComponent("Error: " + e.getMessage()));
            return 0;
        }
    }

    private static int addForceValue(CommandContext<CommandSourceStack> context, ServerPlayer player, double amount) {
        try {
            player.getCapability(JediDataProvider.JEDI_DATA).ifPresent(data -> {
                data.addForceValue(amount);
                String[] slots = data.getAbilitySlots();
                if (slots == null) slots = new String[4];
                PKGTalesoftheForceWars.CHANNEL.sendToServer(new JediDataSyncPacket(data.getStage(), data.getForceValue(), slots, data.getUnlockedAbilities()));
            });
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(new TextComponent("Error: " + e.getMessage()));
            return 0;
        }
    }

    private static int setForceUse(CommandContext<CommandSourceStack> context, ServerPlayer player, String statusArg) {
        try {
            String status = statusArg.equalsIgnoreCase("force_user") ? "force_sensitive" : "non_force";
            player.getCapability(JediDataProvider.JEDI_DATA).ifPresent(data -> {
                data.setForceStatus(status);
                data.setForceEvaluated(true); // Kalıcı seçim
                String[] slots = data.getAbilitySlots();
                if (slots == null) slots = new String[4];
                PKGTalesoftheForceWars.CHANNEL.sendToServer(new JediDataSyncPacket(data.getStage(), data.getForceValue(), slots, data.getUnlockedAbilities()));
            });
            context.getSource().sendSuccess(new TextComponent("Force status set to: " + status), true);
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(new TextComponent("Error: " + e.getMessage()));
            return 0;
        }
    }

    private static int setForceValue(CommandContext<CommandSourceStack> context, ServerPlayer player, double amount) {
        try {
            player.getCapability(JediDataProvider.JEDI_DATA).ifPresent(data -> {
                data.setForceValue(amount);
                String[] slots = data.getAbilitySlots();
                if (slots == null) slots = new String[4];
                PKGTalesoftheForceWars.CHANNEL.sendToServer(new JediDataSyncPacket(data.getStage(), data.getForceValue(), slots, data.getUnlockedAbilities()));
            });
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(new TextComponent("Error: " + e.getMessage()));
            return 0;
        }
    }

    private static int setAbilitySlot(CommandContext<CommandSourceStack> context, ServerPlayer player, int slot, String ability) {
        try {
            // Force-Sensitive değilse reddet
            boolean allowed = player.getCapability(JediDataProvider.JEDI_DATA).map(d -> "force_sensitive".equals(d.getForceStatus())).orElse(false);
            if (!allowed) {
                context.getSource().sendFailure(new TextComponent("Player is not Force-Sensitive"));
                return 0;
            }
            // Pasif/alt dal yetenekler slot'a atanamaz
            if (PASSIVE_ONLY.contains(ability)) {
                context.getSource().sendFailure(new TextComponent("Passive ability cannot be slotted: " + ability));
                return 0;
            }
            // Geçerli yetenek doğrulaması: 52'lik tam listeye göre kontrol et
            boolean valid = false;
            for (String s : ABILITIES) {
                if (s.equals(ability)) { valid = true; break; }
            }
            if (!valid) {
                context.getSource().sendFailure(new TextComponent("Invalid ability: " + ability));
                return 0;
            }
            player.getCapability(JediDataProvider.JEDI_DATA).ifPresent(data -> {
                data.setAbilitySlot(slot - 1, ability);
                PKGTalesoftheForceWars.CHANNEL.sendToServer(new AbilitySlotPacket(slot - 1, ability));
                String[] slots = data.getAbilitySlots();
                if (slots == null) slots = new String[4];
                PKGTalesoftheForceWars.CHANNEL.sendToServer(new JediDataSyncPacket(data.getStage(), data.getForceValue(), slots, data.getUnlockedAbilities()));
            });
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(new TextComponent("Error: " + e.getMessage()));
            return 0;
        }
    }
}