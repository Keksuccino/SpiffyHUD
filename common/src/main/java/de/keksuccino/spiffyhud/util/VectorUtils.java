package de.keksuccino.spiffyhud.util;

import org.jetbrains.annotations.NotNull;

public class VectorUtils {

    @NotNull
    public static com.mojang.math.Vector3f toMojangVector3f(@NotNull org.joml.Vector3f jomlVector3f) {
        return new com.mojang.math.Vector3f(
                jomlVector3f.x(),
                jomlVector3f.y(),
                jomlVector3f.z()
        );
    }

}
