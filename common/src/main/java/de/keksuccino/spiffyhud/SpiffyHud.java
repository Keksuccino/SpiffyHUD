package de.keksuccino.spiffyhud;

import java.io.File;
import de.keksuccino.fancymenu.customization.layout.LayoutHandler;
import de.keksuccino.fancymenu.customization.overlay.CustomizationOverlay;
import de.keksuccino.spiffyhud.customization.SpiffyOverlayScreen;
import de.keksuccino.spiffyhud.customization.actions.Actions;
import de.keksuccino.spiffyhud.customization.elements.Elements;
import de.keksuccino.spiffyhud.customization.elements.playernbthelper.PlayerNbtPathHelpScreen;
import de.keksuccino.spiffyhud.customization.elements.slot.SlotIdHelpScreen;
import de.keksuccino.spiffyhud.customization.placeholders.Placeholders;
import de.keksuccino.fancymenu.customization.ScreenCustomization;
import de.keksuccino.fancymenu.customization.screen.identifier.UniversalScreenIdentifierRegistry;
import de.keksuccino.fancymenu.util.event.acara.EventHandler;
import de.keksuccino.spiffyhud.customization.requirements.Requirements;
import de.keksuccino.spiffyhud.networking.packets.Packets;
import de.keksuccino.spiffyhud.platform.Services;
import de.keksuccino.fancymenu.util.file.FileUtils;
import de.keksuccino.fancymenu.util.file.GameDirectoryUtils;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class SpiffyHud {

	private static final Logger LOGGER = LogManager.getLogger();

	public static final String VERSION = "3.1.0";
	public static final String MOD_LOADER = Services.PLATFORM.getPlatformName();
	public static final String MOD_ID = "spiffyhud";
	public static final File MOD_DIR = createDirectory(new File(GameDirectoryUtils.getGameDirectory(), "/config/spiffyhud"));

	private static Options options;

	public static void init() {

		if (Services.PLATFORM.isOnClient()) {
			LOGGER.info("[SPIFFY HUD] Loading v" + VERSION + " in client-side mode on " + MOD_LOADER.toUpperCase() + "!");
		} else {
			LOGGER.info("[SPIFFY HUD] Loading v" + VERSION + " in server-side mode on " + MOD_LOADER.toUpperCase() + "!");
		}

		if (Services.PLATFORM.isOnClient()) {

			EventHandler.INSTANCE.registerListenersOf(new SpiffyEvents());

			//Register universal identifier for Drippy screen
			UniversalScreenIdentifierRegistry.register("spiffy_overlay", SpiffyOverlayScreen.class.getName());

			//Disable customization for all Spiffy screens that shouldn't be editable
			ScreenCustomization.addScreenBlacklistRule(s -> s.equals(SlotIdHelpScreen.class.getName()));
			ScreenCustomization.addScreenBlacklistRule(s -> s.equals(PlayerNbtPathHelpScreen.class.getName()));

			//Register custom placeholders
			Placeholders.registerAll();

			//Register custom requirements
			Requirements.registerAll();

			//Register custom element types
			Elements.registerAll();

			//Register custom actions
			Actions.registerAll();

			//Disable universal layouts for the HUD / Spiffy Overlay
			LayoutHandler.registerUniversalLayoutInclusionRule(screenIdentifier -> !SpiffyUtils.isSpiffyIdentifier(screenIdentifier));

			//Don't show FancyMenu's customization overlay in the HUD
			CustomizationOverlay.registerOverlayVisibilityController(screen -> {
				if (Minecraft.getInstance().screen instanceof SpiffyOverlayScreen s) return s.showFancyMenuOverlay;
				return true;
			});

			EventHandler.INSTANCE.registerListenersOf(new Test());

		}

		Packets.registerAll();

	}

	public static Options getOptions() {
		if (options == null) {
			reloadOptions();
		}
		return options;
	}

	public static void reloadOptions() {
		options = new Options();
	}

	private static File createDirectory(@NotNull File directory) {
		return FileUtils.createDirectory(directory);
	}

}
