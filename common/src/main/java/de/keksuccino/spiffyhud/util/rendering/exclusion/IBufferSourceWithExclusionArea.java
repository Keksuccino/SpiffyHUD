package de.keksuccino.spiffyhud.util.rendering.exclusion;

/**
 * Interface to mark BufferSources that can apply exclusion areas.
 * Implemented by MixinBufferSource.
 */
public interface IBufferSourceWithExclusionArea {
    void spiffyHud$setExclusionAreaStack(ExclusionAreaStack stack);
    ExclusionAreaStack spiffyHud$getExclusionAreaStack();
}
