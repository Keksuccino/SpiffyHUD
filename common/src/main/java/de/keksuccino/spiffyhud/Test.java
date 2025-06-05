package de.keksuccino.spiffyhud;

import de.keksuccino.fancymenu.events.screen.RenderScreenEvent;
import de.keksuccino.fancymenu.util.event.acara.EventListener;
import de.keksuccino.spiffyhud.util.rendering.exclusion.ExclusionAreaExample;
import net.minecraft.client.Minecraft;

public class Test {

    @EventListener
    public void onRenderPost(RenderScreenEvent.Post e) {

        ExclusionAreaExample.renderWithBasicExclusion(e.getGraphics(), Minecraft.getInstance().font);

    }

}
