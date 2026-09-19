package de.keksuccino.spiffyhud.util.rendering;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityRenderingUtilsTest {

    @BeforeEach
    @AfterEach
    void reset() {
        EntityRenderingUtils.resetLivingEntityOpacities();
    }

    @Test
    void unsubmittedEntityIsOpaque() {
        assertEquals(1.0F, EntityRenderingUtils.getLivingEntityOpacity(new EntityRenderState()));
    }

    @Test
    void entitiesKeepIndependentOpacitiesWithinTheFrame() {
        EntityRenderState first = new EntityRenderState();
        EntityRenderState second = new EntityRenderState();
        EntityRenderingUtils.submitLivingEntityOpacity(first, 0.0F);
        EntityRenderingUtils.submitLivingEntityOpacity(second, 0.75F);
        assertEquals(0.0F, EntityRenderingUtils.getLivingEntityOpacity(first));
        assertEquals(0.75F, EntityRenderingUtils.getLivingEntityOpacity(second));
        EntityRenderingUtils.submitLivingEntityOpacity(first, 0.5F);
        assertEquals(0.5F, EntityRenderingUtils.getLivingEntityOpacity(first));
        assertEquals(0.75F, EntityRenderingUtils.getLivingEntityOpacity(second));
    }

    @Test
    void frameCleanupResetsEveryEntityAndAllowsReuse() {
        EntityRenderState first = new EntityRenderState();
        EntityRenderState second = new EntityRenderState();
        EntityRenderingUtils.submitLivingEntityOpacity(first, 0.0F);
        EntityRenderingUtils.submitLivingEntityOpacity(second, 0.5F);
        EntityRenderingUtils.resetLivingEntityOpacities();
        assertEquals(1.0F, EntityRenderingUtils.getLivingEntityOpacity(first));
        assertEquals(1.0F, EntityRenderingUtils.getLivingEntityOpacity(second));
        EntityRenderingUtils.resetLivingEntityOpacities();
        EntityRenderingUtils.submitLivingEntityOpacity(first, 0.25F);
        assertEquals(0.25F, EntityRenderingUtils.getLivingEntityOpacity(first));
        assertEquals(1.0F, EntityRenderingUtils.getLivingEntityOpacity(second));
    }

}
