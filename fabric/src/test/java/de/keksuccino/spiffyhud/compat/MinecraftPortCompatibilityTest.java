package de.keksuccino.spiffyhud.compat;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Checks binary contracts without initializing Minecraft's client or graphics backend. */
class MinecraftPortCompatibilityTest {

    private static final String MOD_ROOT = "de/keksuccino/spiffyhud/";
    private static final String MIXIN_ROOT = MOD_ROOT + "mixin/mixins/common/client/";
    private static final String GRAPHICS = "net/minecraft/client/gui/GuiGraphicsExtractor";
    private static final String PIPELINE = "Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;";

    @Test
    void graphicsExclusionInjectionsResolveAgainstMinecraft() throws IOException {
        ClassNode target = readClass(GRAPHICS);
        int checked = 0;
        for (MethodNode method : readClass(MIXIN_ROOT + "MixinGuiGraphics").methods) {
            for (AnnotationNode annotation : annotations(method)) {
                if (!annotation.desc.equals("Lorg/spongepowered/asm/mixin/injection/Inject;")) continue;
                @SuppressWarnings("unchecked")
                List<String> selectors = (List<String>) value(annotation, "method");
                for (String selector : selectors) {
                    assertTrue(target.methods.stream().anyMatch(candidate -> selector.equals(candidate.name + candidate.desc) || selector.equals(candidate.name)), selector);
                    checked++;
                }
            }
        }
        assertTrue(checked > 0, "Must discover the exclusion injections");
    }

    @Test
    void mirroredBlitShadowResolvesToTheRenderPearlOverload() throws IOException {
        MethodNode shadow = method(readClass(MIXIN_ROOT + "MixinGuiGraphics"), "innerBlit");
        assertTrue(shadow.desc.startsWith("(" + PIPELINE));
        assertTrue(readClass(GRAPHICS).methods.stream().anyMatch(candidate -> candidate.name.equals(shadow.name) && candidate.desc.equals(shadow.desc)));
    }

    @Test
    void croppedSpriteInvokerResolvesToTheRenderPearlOverload() throws IOException {
        MethodNode invoker = method(readClass(MIXIN_ROOT + "IMixinGuiGraphics"), "invoke_private_blitSprite_Spiffy");
        assertTrue(invoker.desc.startsWith("(" + PIPELINE));
        assertTrue(readClass(GRAPHICS).methods.stream().anyMatch(candidate -> candidate.name.equals("blitSprite") && candidate.desc.equals(invoker.desc)));
    }

    @Test
    void heartBlinkUsesVanillasDamageCooldownInsteadOfInvulnerability() throws IOException {
        MethodNode vanilla = method(readClass("net/minecraft/client/gui/Hud"), "extractPlayerHealth");
        MethodNode custom = method(readClass(MOD_ROOT + "customization/elements/vanillalike/playerhealth/VanillaLikePlayerHealthElement"), "renderPlayerHealthInternal");
        assertTrue(readFields(vanilla).contains("damageCooldownTime"));
        assertTrue(readFields(custom).contains("damageCooldownTime"));
        assertFalse(readFields(custom).contains("invulnerableTime"));
        assertFalse(calledMethods(custom).contains("getInvulnerableTime"));
    }

    @Test
    void structureEditorForwardsTheCurrentKeyEventFields() throws IOException {
        ClassNode screen = readClass(MOD_ROOT + "customization/requirements/IsPlayerInStructureRequirement$IsPlayerInStructureValueConfigScreen");
        MethodNode handler = screen.methods.stream().filter(candidate -> candidate.name.equals("keyPressed") && candidate.desc.equals("(Lnet/minecraft/client/input/KeyEvent;)Z")).findFirst().orElseThrow();
        List<String> eventReads = new ArrayList<>();
        ClassNode event = readClass("net/minecraft/client/input/KeyEvent");
        for (var instruction : handler.instructions) {
            if (instruction instanceof MethodInsnNode call && call.owner.equals(event.name)) {
                assertTrue(event.methods.stream().anyMatch(candidate -> candidate.name.equals(call.name) && candidate.desc.equals(call.desc)));
                eventReads.add(call.name);
            }
        }
        // The second integer is now the shortcut keycode, which FancyMenu forwards unchanged.
        assertEquals(List.of("key", "keycode", "modifiers"), eventReads);
    }

    @Test
    void opacityCleanupMatchesTheNoArgumentRenderLifecycle() throws IOException {
        MethodNode render = method(readClass("net/minecraft/client/renderer/GameRenderer"), "render");
        assertEquals("()V", render.desc);
        MethodNode cleanup = method(readClass(MIXIN_ROOT + "MixinGameRenderer"), "after_render_Spiffy");
        assertEquals("(Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V", cleanup.desc);
        assertTrue(calledMethods(cleanup).contains("resetLivingEntityOpacities"));
    }

    private static ClassNode readClass(String name) throws IOException {
        try (InputStream input = MinecraftPortCompatibilityTest.class.getClassLoader().getResourceAsStream(name + ".class")) {
            assertNotNull(input, name);
            ClassNode node = new ClassNode();
            new ClassReader(input).accept(node, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            return node;
        }
    }

    private static MethodNode method(ClassNode owner, String name) {
        return owner.methods.stream().filter(candidate -> candidate.name.equals(name)).findFirst().orElseThrow();
    }

    private static List<AnnotationNode> annotations(MethodNode method) {
        List<AnnotationNode> result = new ArrayList<>();
        if (method.visibleAnnotations != null) result.addAll(method.visibleAnnotations);
        if (method.invisibleAnnotations != null) result.addAll(method.invisibleAnnotations);
        return result;
    }

    private static Object value(AnnotationNode annotation, String key) {
        for (int i = 0; i < annotation.values.size(); i += 2) {
            if (annotation.values.get(i).equals(key)) return annotation.values.get(i + 1);
        }
        throw new AssertionError(key);
    }

    private static List<String> readFields(MethodNode method) {
        List<String> result = new ArrayList<>();
        for (var instruction : method.instructions) {
            if (instruction instanceof FieldInsnNode field && field.getOpcode() == Opcodes.GETFIELD) result.add(field.name);
        }
        return result;
    }

    private static List<String> calledMethods(MethodNode method) {
        List<String> result = new ArrayList<>();
        for (var instruction : method.instructions) {
            if (instruction instanceof MethodInsnNode call) result.add(call.name);
        }
        return result;
    }

}
