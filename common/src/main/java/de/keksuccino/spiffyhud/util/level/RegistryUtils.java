package de.keksuccino.spiffyhud.util.level;

import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import org.jetbrains.annotations.Nullable;

public class RegistryUtils {

    @Nullable
    public static RegistryAccess.Frozen getRegistryAccess() {
        RegistryAccess.Frozen access = null;
        if (Minecraft.getInstance().hasSingleplayerServer()) {
            access = Minecraft.getInstance().getSingleplayerServer().registryAccess();
        } else if ((Minecraft.getInstance().level != null) && (Minecraft.getInstance().level.getServer() != null)) {
            access = Minecraft.getInstance().level.getServer().registryAccess();
        }
        return access;
    }

}
