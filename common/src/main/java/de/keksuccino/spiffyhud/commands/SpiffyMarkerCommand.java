package de.keksuccino.spiffyhud.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import de.keksuccino.fancymenu.networking.PacketHandler;
import de.keksuccino.konkrete.command.CommandUtils;
import de.keksuccino.spiffyhud.customization.actions.marker.MarkerActionConfig;
import de.keksuccino.spiffyhud.customization.actions.marker.MarkerRemovalConfig;
import de.keksuccino.spiffyhud.networking.packets.markercommand.MarkerCommandEditField;
import de.keksuccino.spiffyhud.networking.packets.markercommand.MarkerCommandOperation;
import de.keksuccino.spiffyhud.networking.packets.markercommand.command.MarkerCommandPacket;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SpiffyMarkerCommand {

    private static final SimpleCommandExceptionType MUST_BE_PLAYER_EXCEPTION =
            new SimpleCommandExceptionType(Component.translatable("spiffyhud.commands.marker.requires_player"));
    private static final SimpleCommandExceptionType INVALID_TARGET_ELEMENT =
            new SimpleCommandExceptionType(Component.translatable("spiffyhud.commands.marker.error.invalid_target"));
    private static final SimpleCommandExceptionType INVALID_MARKER_NAME =
            new SimpleCommandExceptionType(Component.translatable("spiffyhud.commands.marker.error.invalid_marker"));
    private static final SimpleCommandExceptionType INVALID_POSITION =
            new SimpleCommandExceptionType(Component.translatable("spiffyhud.commands.marker.error.position_required"));
    private static final SimpleCommandExceptionType CLIENT_NOT_SUPPORTED =
            new SimpleCommandExceptionType(Component.translatable("spiffyhud.commands.marker.self_not_supported"));

    private static final Map<String, List<String>> CACHED_GROUP_SUGGESTIONS = java.util.Collections.synchronizedMap(new HashMap<>());
    private static final DecimalFormat POSITION_FORMAT = buildPositionFormat();

    private SpiffyMarkerCommand() {
    }

    public static void register(@NotNull CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("spiffymarker")
                .then(buildAddCommand())
                .then(buildEditCommand())
                .then(buildRemoveCommand()));
    }

    public static void cacheSuggestions(@NotNull UUID playerUuid, @NotNull List<String> groups) {
        CACHED_GROUP_SUGGESTIONS.put(playerUuid.toString(), new ArrayList<>(groups));
    }

    private static RequiredArgumentBuilder<CommandSourceStack, String> targetElementArgument() {
        return Commands.argument("target_element", StringArgumentType.string())
                .suggests((context, builder) -> {
                    try {
                        ServerPlayer player = context.getSource().getPlayerOrException();
                        List<String> values = Objects.requireNonNullElse(CACHED_GROUP_SUGGESTIONS.get(player.getUUID().toString()), List.of());
                        if (values.isEmpty()) {
                            return CommandUtils.getStringSuggestions(builder, "<no_marker_groups>");
                        }
                        return CommandUtils.getStringSuggestions(builder, values.toArray(new String[0]));
                    } catch (CommandSyntaxException ex) {
                        return CommandUtils.getStringSuggestions(builder, "<player_only>");
                    }
                });
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildAddCommand() {
        return Commands.literal("add")
                .then(silenceArgument()
                        .then(targetElementArgument()
                        .then(Commands.argument("marker_name", StringArgumentType.string())
                                .then(positionAndOptionals(SpiffyMarkerCommand::executeAdd)))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildEditCommand() {
        return Commands.literal("edit")
                .then(silenceArgument()
                        .then(targetElementArgument()
                        .then(Commands.argument("marker_name", StringArgumentType.string())
                                .then(positionAndOptionals(SpiffyMarkerCommand::executeEdit)))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildRemoveCommand() {
        return Commands.literal("remove")
                .then(silenceArgument()
                        .then(targetElementArgument()
                        .then(Commands.argument("marker_name", StringArgumentType.string())
                                .executes(SpiffyMarkerCommand::executeRemove))));
    }

    private static RequiredArgumentBuilder<CommandSourceStack, Boolean> silenceArgument() {
        return Commands.argument("silence_command_chat_output", BoolArgumentType.bool());
    }

    private static RequiredArgumentBuilder<CommandSourceStack, String> optionalColorArgument() {
        return Commands.argument("color", StringArgumentType.string());
    }

    private static RequiredArgumentBuilder<CommandSourceStack, Boolean> optionalNeedleArgument() {
        return Commands.argument("show_as_needle", BoolArgumentType.bool());
    }

    private static RequiredArgumentBuilder<CommandSourceStack, String> optionalDotTextureArgument() {
        return Commands.argument("dot_texture", StringArgumentType.string());
    }

    private static RequiredArgumentBuilder<CommandSourceStack, String> optionalNeedleTextureArgument() {
        return Commands.argument("needle_texture", StringArgumentType.string());
    }

    private static RequiredArgumentBuilder<CommandSourceStack, ?> positionAndOptionals(Command<CommandSourceStack> executor) {
        return positionArgument()
                .executes(executor)
                .then(optionalColorArgument().executes(executor)
                        .then(optionalNeedleArgument().executes(executor)
                                .then(optionalDotTextureArgument().executes(executor)
                                        .then(optionalNeedleTextureArgument().executes(executor)))));
    }

    private static int executeAdd(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        boolean silence = BoolArgumentType.getBool(context, "silence_command_chat_output");
        try {
            ServerPlayer player = getCommandPlayer(context);
            String targetElement = sanitizeTarget(StringArgumentType.getString(context, "target_element"));
            String markerName = sanitizeMarkerName(StringArgumentType.getString(context, "marker_name"));
            Vec2 pos = Vec2Argument.getVec2(context, "position");
            String posX = formatPosition(pos.x);
            String posZ = formatPosition(pos.y);
            String color = sanitizeOptional(getOptionalString(context, "color"));
            Boolean showAsNeedle = getOptionalBool(context, "show_as_needle");
            String dotTexture = sanitizeOptional(getOptionalString(context, "dot_texture"));
            String needleTexture = sanitizeOptional(getOptionalString(context, "needle_texture"));
            return dispatchAdd(context.getSource(), player, targetElement, markerName, posX, posZ, color, showAsNeedle, dotTexture, needleTexture, silence);
        } catch (CommandSyntaxException ex) {
            if (silence) {
                return 0;
            }
            throw ex;
        }
    }

    private static int executeEdit(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        boolean silence = BoolArgumentType.getBool(context, "silence_command_chat_output");
        try {
            ServerPlayer player = getCommandPlayer(context);
            String targetElement = sanitizeTarget(StringArgumentType.getString(context, "target_element"));
            String markerName = sanitizeMarkerName(StringArgumentType.getString(context, "marker_name"));
            Vec2 pos = Vec2Argument.getVec2(context, "position");
            String posX = formatPosition(pos.x);
            String posZ = formatPosition(pos.y);
            EnumSet<MarkerCommandEditField> overrides = EnumSet.noneOf(MarkerCommandEditField.class);

            String color = null;
            if (wasArgumentProvided(context, "color")) {
                color = sanitizeOptional(StringArgumentType.getString(context, "color"));
                overrides.add(MarkerCommandEditField.COLOR);
            }

            Boolean showAsNeedle = null;
            if (wasArgumentProvided(context, "show_as_needle")) {
                showAsNeedle = BoolArgumentType.getBool(context, "show_as_needle");
                overrides.add(MarkerCommandEditField.SHOW_AS_NEEDLE);
            }

            String dotTexture = null;
            if (wasArgumentProvided(context, "dot_texture")) {
                dotTexture = sanitizeOptional(StringArgumentType.getString(context, "dot_texture"));
                overrides.add(MarkerCommandEditField.DOT_TEXTURE);
            }

            String needleTexture = null;
            if (wasArgumentProvided(context, "needle_texture")) {
                needleTexture = sanitizeOptional(StringArgumentType.getString(context, "needle_texture"));
                overrides.add(MarkerCommandEditField.NEEDLE_TEXTURE);
            }

            return dispatchEdit(context.getSource(), player, targetElement, markerName, posX, posZ, color, showAsNeedle, dotTexture, needleTexture, overrides, silence);
        } catch (CommandSyntaxException ex) {
            if (silence) {
                return 0;
            }
            throw ex;
        }
    }

    private static int executeRemove(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        boolean silence = BoolArgumentType.getBool(context, "silence_command_chat_output");
        try {
            ServerPlayer player = getCommandPlayer(context);
            String targetElement = sanitizeTarget(StringArgumentType.getString(context, "target_element"));
            String markerName = sanitizeMarkerName(StringArgumentType.getString(context, "marker_name"));
            return dispatchRemove(context.getSource(), player, targetElement, markerName, silence);
        } catch (CommandSyntaxException ex) {
            if (silence) {
                return 0;
            }
            throw ex;
        }
    }

    private static int dispatchAdd(CommandSourceStack source,
                                   ServerPlayer player,
                                   String targetElement,
                                   String markerName,
                                   String posX,
                                   String posZ,
                                   @Nullable String color,
                                   @Nullable Boolean showAsNeedle,
                                   @Nullable String dotTexture,
                                   @Nullable String needleTexture,
                                   boolean silence) throws CommandSyntaxException {
        MarkerActionConfig config = MarkerActionConfig.defaultConfig();
        config.targetElementIdentifier = targetElement;
        config.displayName = markerName;
        config.lookupMarkerName = markerName;
        config.positionX = posX;
        config.positionZ = posZ;
        config.colorHex = Objects.requireNonNullElse(color, "");
        config.dotTexture = Objects.requireNonNullElse(dotTexture, "");
        config.needleTexture = Objects.requireNonNullElse(needleTexture, "");
        config.showAsNeedle = showAsNeedle != null && showAsNeedle;
        config.normalize();
        return sendPacket(source, player, MarkerCommandOperation.ADD, config, null, null, silence,
                "spiffyhud.commands.marker.add.sent", markerName, targetElement);
    }

    private static int dispatchEdit(CommandSourceStack source,
                                    ServerPlayer player,
                                    String targetElement,
                                    String markerName,
                                    String posX,
                                    String posZ,
                                    @Nullable String color,
                                    @Nullable Boolean showAsNeedle,
                                    @Nullable String dotTexture,
                                    @Nullable String needleTexture,
                                    @NotNull Set<MarkerCommandEditField> overrides,
                                    boolean silence) throws CommandSyntaxException {
        MarkerActionConfig config = MarkerActionConfig.defaultConfig();
        config.targetElementIdentifier = targetElement;
        config.displayName = markerName;
        config.lookupMarkerName = markerName;
        config.positionX = posX;
        config.positionZ = posZ;
        config.colorHex = Objects.requireNonNullElse(color, "");
        config.dotTexture = Objects.requireNonNullElse(dotTexture, "");
        config.needleTexture = Objects.requireNonNullElse(needleTexture, "");
        config.showAsNeedle = showAsNeedle != null && showAsNeedle;
        config.normalize();
        return sendPacket(source, player, MarkerCommandOperation.EDIT, config, null, overrides, silence,
                "spiffyhud.commands.marker.edit.sent", markerName, targetElement);
    }

    private static int dispatchRemove(CommandSourceStack source,
                                      ServerPlayer player,
                                      String targetElement,
                                      String markerName,
                                      boolean silence) throws CommandSyntaxException {
        MarkerRemovalConfig config = MarkerRemovalConfig.defaultConfig();
        config.targetElementIdentifier = targetElement;
        config.markerName = markerName;
        config.normalize();
        return sendPacket(source, player, MarkerCommandOperation.REMOVE, null, config, null, silence,
                "spiffyhud.commands.marker.remove.sent", markerName, targetElement);
    }

    private static int sendPacket(CommandSourceStack source,
                                  ServerPlayer player,
                                  @NotNull MarkerCommandOperation operation,
                                  @Nullable MarkerActionConfig actionConfig,
                                  @Nullable MarkerRemovalConfig removalConfig,
                                  @Nullable Set<MarkerCommandEditField> editFields,
                                  boolean silence,
                                  @NotNull String successTranslationKey,
                                  Object... successArgs) throws CommandSyntaxException {
        if (!PacketHandler.isFancyMenuClient(player)) {
            if (silence) {
                return 0;
            }
            throw CLIENT_NOT_SUPPORTED.create();
        }
        MarkerCommandPacket packet = new MarkerCommandPacket();
        packet.operation = operation;
        packet.actionConfig = actionConfig == null ? null : actionConfig.copy();
        packet.removalConfig = removalConfig == null ? null : removalConfig.copy();
        packet.editFields = editFields == null ? null : (editFields.isEmpty() ? Collections.emptySet() : EnumSet.copyOf(editFields));
        packet.silenceClientFeedback = silence;
        PacketHandler.sendToClient(player, packet);
        if (!silence) {
            source.sendSuccess(() -> Component.translatable(successTranslationKey, successArgs), true);
        }
        return 1;
    }

    private static ServerPlayer getCommandPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        try {
            return context.getSource().getPlayerOrException();
        } catch (CommandSyntaxException ex) {
            throw MUST_BE_PLAYER_EXCEPTION.create();
        }
    }

    @NotNull
    private static String sanitizeTarget(@Nullable String value) throws CommandSyntaxException {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isEmpty()) {
            throw INVALID_TARGET_ELEMENT.create();
        }
        return trimmed;
    }

    @NotNull
    private static String sanitizeMarkerName(@Nullable String value) throws CommandSyntaxException {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isEmpty()) {
            throw INVALID_MARKER_NAME.create();
        }
        return trimmed;
    }

    private static String formatPosition(double value) {
        return POSITION_FORMAT.format(value);
    }

    @Nullable
    private static String sanitizeOptional(@Nullable String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty() || trimmed.equals("-") || trimmed.equalsIgnoreCase("none") || trimmed.equalsIgnoreCase("null")) {
            return null;
        }
        return trimmed;
    }

    @Nullable
    private static String getOptionalString(CommandContext<CommandSourceStack> context, String name) {
        try {
            return StringArgumentType.getString(context, name);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    @Nullable
    private static Boolean getOptionalBool(CommandContext<CommandSourceStack> context, String name) {
        try {
            return BoolArgumentType.getBool(context, name);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private static boolean wasArgumentProvided(@NotNull CommandContext<CommandSourceStack> context, @NotNull String argumentName) {
        for (ParsedCommandNode<CommandSourceStack> node : context.getNodes()) {
            if (node.getNode() != null && argumentName.equals(node.getNode().getName())) {
                return true;
            }
        }
        return false;
    }

    private static RequiredArgumentBuilder<CommandSourceStack, ?> positionArgument() {
        return Commands.argument("position", Vec2Argument.vec2());
    }

    private static DecimalFormat buildPositionFormat() {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(java.util.Locale.ROOT);
        DecimalFormat format = new DecimalFormat("0.########", symbols);
        format.setGroupingUsed(false);
        return format;
    }

}
