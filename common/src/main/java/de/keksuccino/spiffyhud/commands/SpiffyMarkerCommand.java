package de.keksuccino.spiffyhud.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import de.keksuccino.fancymenu.networking.PacketHandler;
import de.keksuccino.konkrete.command.CommandUtils;
import de.keksuccino.spiffyhud.customization.actions.marker.MarkerActionConfig;
import de.keksuccino.spiffyhud.customization.actions.marker.MarkerRemovalConfig;
import de.keksuccino.spiffyhud.networking.packets.markercommand.MarkerCommandOperation;
import de.keksuccino.spiffyhud.networking.packets.markercommand.command.MarkerCommandPacket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SpiffyMarkerCommand {

    private static final int REMOTE_PERMISSION_LEVEL = 2;

    private static final SimpleCommandExceptionType MUST_BE_PLAYER_EXCEPTION =
            new SimpleCommandExceptionType(Component.translatable("spiffyhud.commands.marker.requires_player"));
    private static final SimpleCommandExceptionType INVALID_TARGET_ELEMENT =
            new SimpleCommandExceptionType(Component.translatable("spiffyhud.commands.marker.error.invalid_target"));
    private static final SimpleCommandExceptionType INVALID_MARKER_NAME =
            new SimpleCommandExceptionType(Component.translatable("spiffyhud.commands.marker.error.invalid_marker"));
    private static final SimpleCommandExceptionType INVALID_POSITION =
            new SimpleCommandExceptionType(Component.translatable("spiffyhud.commands.marker.error.position_required"));
    private static final SimpleCommandExceptionType INVALID_OPTIONS =
            new SimpleCommandExceptionType(Component.translatable("spiffyhud.commands.marker.error.options_required"));
    private static final SimpleCommandExceptionType TARGET_PERMISSION_EXCEPTION =
            new SimpleCommandExceptionType(Component.translatable("spiffyhud.commands.marker.error.target_admin_only"));

    public static final Map<String, List<String>> CACHED_GROUP_SUGGESTIONS = Collections.synchronizedMap(new HashMap<>());

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

    private static net.minecraft.commands.arguments.RequiredArgumentBuilder<CommandSourceStack, String> targetElementArgument() {
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

    private static net.minecraft.commands.arguments.RequiredArgumentBuilder<CommandSourceStack, String> optionsArgument() {
        return Commands.argument("options", StringArgumentType.greedyString());
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> buildAddCommand() {
        return Commands.literal("add")
                .then(targetElementArgument()
                        .then(optionsArgument().executes(SpiffyMarkerCommand::executeAdd)));
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> buildEditCommand() {
        return Commands.literal("edit")
                .then(targetElementArgument()
                        .then(optionsArgument().executes(SpiffyMarkerCommand::executeEdit)));
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> buildRemoveCommand() {
        return Commands.literal("remove")
                .then(targetElementArgument()
                        .then(optionsArgument().executes(SpiffyMarkerCommand::executeRemove)));
    }

    private static int executeAdd(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String targetElement = StringArgumentType.getString(context, "target_element").trim();
        Map<String, String> options = parseOptions(context);
        String displayName = sanitizeRequired(resolveOption(options, "name", "display", "display_name", "marker"));
        String posX = sanitizeRequired(resolveOption(options, "x", "posx", "position_x"));
        String posZ = sanitizeRequired(resolveOption(options, "z", "posz", "position_z"));
        String color = sanitizeOptional(resolveOption(options, "color", "colour"));
        Boolean showAsNeedle = parseOptionalBoolean(resolveOption(options, "needle", "show_as_needle"));
        String dotTexture = sanitizeOptional(resolveOption(options, "dot", "dot_texture"));
        String needleTexture = sanitizeOptional(resolveOption(options, "needletex", "needle_texture"));
        Collection<ServerPlayer> explicitTargets = parseTargetOption(source, options);
        return dispatchAdd(source, explicitTargets, targetElement, displayName, posX, posZ, color, showAsNeedle, dotTexture, needleTexture);
    }

    private static int executeEdit(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String targetElement = StringArgumentType.getString(context, "target_element").trim();
        Map<String, String> options = parseOptions(context);
        String displayName = sanitizeRequired(resolveOption(options, "name", "display", "display_name", "marker"));
        String posX = sanitizeRequired(resolveOption(options, "x", "posx", "position_x"));
        String posZ = sanitizeRequired(resolveOption(options, "z", "posz", "position_z"));
        String color = sanitizeOptional(resolveOption(options, "color", "colour"));
        Boolean showAsNeedle = parseOptionalBoolean(resolveOption(options, "needle", "show_as_needle"));
        String dotTexture = sanitizeOptional(resolveOption(options, "dot", "dot_texture"));
        String needleTexture = sanitizeOptional(resolveOption(options, "needletex", "needle_texture"));
        Collection<ServerPlayer> explicitTargets = parseTargetOption(source, options);
        return dispatchEdit(source, explicitTargets, targetElement, displayName, posX, posZ, color, showAsNeedle, dotTexture, needleTexture);
    }

    private static int executeRemove(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String targetElement = StringArgumentType.getString(context, "target_element").trim();
        Map<String, String> options = parseOptions(context);
        String markerName = sanitizeRequired(resolveOption(options, "name", "marker", "remove", "remove_name"));
        Collection<ServerPlayer> explicitTargets = parseTargetOption(source, options);
        return dispatchRemove(source, explicitTargets, targetElement, markerName);
    }

    private static int dispatchAdd(CommandSourceStack source,
                                   @Nullable Collection<ServerPlayer> explicitTargets,
                                   String targetElement,
                                   String displayName,
                                   String posX,
                                   String posZ,
                                   @Nullable String color,
                                   @Nullable Boolean showAsNeedle,
                                   @Nullable String dotTexture,
                                   @Nullable String needleTexture) throws CommandSyntaxException {
        validateTargetElement(targetElement);
        validateMarkerName(displayName, INVALID_MARKER_NAME);
        validatePosition(posX);
        validatePosition(posZ);
        MarkerActionConfig config = MarkerActionConfig.defaultConfig();
        config.targetElementIdentifier = targetElement;
        config.displayName = displayName;
        config.lookupMarkerName = displayName;
        config.positionX = posX;
        config.positionZ = posZ;
        config.colorHex = Objects.requireNonNullElse(color, "");
        config.dotTexture = Objects.requireNonNullElse(dotTexture, "");
        config.needleTexture = Objects.requireNonNullElse(needleTexture, "");
        config.showAsNeedle = showAsNeedle != null && showAsNeedle;
        config.normalize();
        return sendPacket(source, explicitTargets, MarkerCommandOperation.ADD, config, null,
                "spiffyhud.commands.marker.add.sent", displayName, targetElement);
    }

    private static int dispatchEdit(CommandSourceStack source,
                                    @Nullable Collection<ServerPlayer> explicitTargets,
                                    String targetElement,
                                    String displayName,
                                    String posX,
                                    String posZ,
                                    @Nullable String color,
                                    @Nullable Boolean showAsNeedle,
                                    @Nullable String dotTexture,
                                    @Nullable String needleTexture) throws CommandSyntaxException {
        validateTargetElement(targetElement);
        validateMarkerName(displayName, INVALID_MARKER_NAME);
        validatePosition(posX);
        validatePosition(posZ);
        MarkerActionConfig config = MarkerActionConfig.defaultConfig();
        config.targetElementIdentifier = targetElement;
        config.lookupMarkerName = displayName;
        config.displayName = displayName;
        config.positionX = posX;
        config.positionZ = posZ;
        config.colorHex = Objects.requireNonNullElse(color, "");
        config.dotTexture = Objects.requireNonNullElse(dotTexture, "");
        config.needleTexture = Objects.requireNonNullElse(needleTexture, "");
        config.showAsNeedle = showAsNeedle != null && showAsNeedle;
        config.normalize();
        return sendPacket(source, explicitTargets, MarkerCommandOperation.EDIT, config, null,
                "spiffyhud.commands.marker.edit.sent", displayName, targetElement);
    }

    private static int dispatchRemove(CommandSourceStack source,
                                      @Nullable Collection<ServerPlayer> explicitTargets,
                                      String targetElement,
                                      String markerName) throws CommandSyntaxException {
        validateTargetElement(targetElement);
        validateMarkerName(markerName, INVALID_MARKER_NAME);
        MarkerRemovalConfig config = MarkerRemovalConfig.defaultConfig();
        config.targetElementIdentifier = targetElement;
        config.markerName = markerName;
        config.normalize();
        return sendPacket(source, explicitTargets, MarkerCommandOperation.REMOVE, null, config,
                "spiffyhud.commands.marker.remove.sent", markerName, targetElement);
    }

    private static int sendPacket(CommandSourceStack source,
                                  @Nullable Collection<ServerPlayer> explicitTargets,
                                  @NotNull MarkerCommandOperation operation,
                                  @Nullable MarkerActionConfig actionConfig,
                                  @Nullable MarkerRemovalConfig removalConfig,
                                  @NotNull String successTranslationKey,
                                  Object... successArgs) throws CommandSyntaxException {
        List<ServerPlayer> targets = resolveTargets(source, explicitTargets);
        int sent = 0;
        for (ServerPlayer player : targets) {
            if (!PacketHandler.isFancyMenuClient(player)) {
                source.sendFailure(Component.translatable("spiffyhud.commands.marker.target_not_supported", player.getDisplayName()));
                continue;
            }
            MarkerCommandPacket packet = new MarkerCommandPacket();
            packet.operation = operation;
            packet.actionConfig = actionConfig == null ? null : actionConfig.copy();
            packet.removalConfig = removalConfig == null ? null : removalConfig.copy();
            PacketHandler.sendToClient(player, packet);
            sent++;
        }
        if (sent > 0) {
            Object[] augmented = new Object[successArgs.length + 1];
            System.arraycopy(successArgs, 0, augmented, 0, successArgs.length);
            augmented[augmented.length - 1] = sent;
            source.sendSuccess(() -> Component.translatable(successTranslationKey, augmented), true);
            return sent;
        }
        throw new SimpleCommandExceptionType(Component.translatable("spiffyhud.commands.marker.error.no_targets")).create();
    }

    private static Map<String, String> parseOptions(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String raw = StringArgumentType.getString(context, "options");
        if (raw == null || raw.isBlank()) {
            throw INVALID_OPTIONS.create();
        }
        Map<String, String> parsed = new LinkedHashMap<>();
        StringReader reader = new StringReader(raw);
        while (reader.canRead()) {
            reader.skipWhitespace();
            if (!reader.canRead()) {
                break;
            }
            int keyStart = reader.getCursor();
            while (reader.canRead() && reader.peek() != '=') {
                reader.skip();
            }
            if (!reader.canRead()) {
                throw INVALID_OPTIONS.create();
            }
            String key = reader.getString().substring(keyStart, reader.getCursor()).trim().toLowerCase(Locale.ROOT);
            reader.skip();
            if (!reader.canRead()) {
                parsed.put(key, "");
                break;
            }
            String value = reader.readString();
            parsed.put(key, value);
        }
        return parsed;
    }

    @Nullable
    private static String resolveOption(@NotNull Map<String, String> options, String... aliases) {
        for (String alias : aliases) {
            String normalized = alias.toLowerCase(Locale.ROOT);
            if (options.containsKey(normalized)) {
                return options.get(normalized);
            }
        }
        return null;
    }

    private static Collection<ServerPlayer> parseTargetOption(CommandSourceStack source, Map<String, String> options) throws CommandSyntaxException {
        String selector = resolveOption(options, "target", "player", "players");
        if (selector == null || selector.isBlank()) {
            return null;
        }
        if (!source.hasPermission(REMOTE_PERMISSION_LEVEL)) {
            throw TARGET_PERMISSION_EXCEPTION.create();
        }
        EntitySelector entitySelector = EntityArgument.players().parse(new StringReader(selector));
        List<ServerPlayer> players = entitySelector.findPlayers(source);
        if (players.isEmpty()) {
            throw EntityArgument.NO_PLAYERS_FOUND.create();
        }
        return players;
    }

    private static List<ServerPlayer> resolveTargets(CommandSourceStack source, @Nullable Collection<ServerPlayer> explicitTargets) throws CommandSyntaxException {
        if (explicitTargets != null && !explicitTargets.isEmpty()) {
            return new ArrayList<>(explicitTargets);
        }
        try {
            return List.of(source.getPlayerOrException());
        } catch (CommandSyntaxException ex) {
            throw MUST_BE_PLAYER_EXCEPTION.create();
        }
    }

    private static void validateTargetElement(@Nullable String targetElement) throws CommandSyntaxException {
        if (targetElement == null || targetElement.isBlank()) {
            throw INVALID_TARGET_ELEMENT.create();
        }
    }

    private static void validateMarkerName(@Nullable String markerName, SimpleCommandExceptionType exceptionType) throws CommandSyntaxException {
        if (markerName == null || markerName.isBlank()) {
            throw exceptionType.create();
        }
    }

    private static void validatePosition(@Nullable String value) throws CommandSyntaxException {
        if (value == null || value.isBlank()) {
            throw INVALID_POSITION.create();
        }
    }

    @NotNull
    private static String sanitizeRequired(@Nullable String input) throws CommandSyntaxException {
        if (input == null) {
            throw INVALID_OPTIONS.create();
        }
        return input.trim();
    }

    @Nullable
    private static String sanitizeOptional(@Nullable String input) {
        if (input == null) {
            return null;
        }
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        if (trimmed.equalsIgnoreCase("none") || trimmed.equalsIgnoreCase("null") || trimmed.equals("-")) {
            return null;
        }
        return trimmed;
    }

    @Nullable
    private static Boolean parseOptionalBoolean(@Nullable String value) throws CommandSyntaxException {
        if (value == null) {
            return null;
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        if (normalized.isEmpty()) {
            return null;
        }
        if (normalized.equals("true") || normalized.equals("1") || normalized.equals("yes") || normalized.equals("on")) {
            return true;
        }
        if (normalized.equals("false") || normalized.equals("0") || normalized.equals("no") || normalized.equals("off")) {
            return false;
        }
        throw INVALID_OPTIONS.create();
    }
}
