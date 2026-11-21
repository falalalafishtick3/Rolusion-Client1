package com.example.rolusion.modules;

import com.example.rolusion.Category;
import com.example.rolusion.Module;
import com.example.rolusion.ModuleManager;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.Comparator;
import java.util.List;

/** Sorted list of enabled modules, top-right. */
public class ArrayListHud extends Module {
    public ArrayListHud() {
        super("ArrayList", Category.HUD, GLFW.GLFW_KEY_UNKNOWN);
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.CHAT,
                Identifier.of("rolusion", "arraylist"),
                (ctx, tickCounter) -> {
                    if (!isEnabled()) return;
                    MinecraftClient mc = MinecraftClient.getInstance();
                    if (mc.options.hudHidden) return;
                    List<Module> on = ModuleManager.modules.stream()
                            .filter(m -> m.isEnabled() && m != this)
                            .sorted(Comparator.comparingInt(
                                    (Module m) -> -mc.textRenderer.getWidth(m.name)))
                            .toList();
                    int right = ctx.getScaledWindowWidth() - 4;
                    int y = 4;
                    for (Module m : on) {
                        int w = mc.textRenderer.getWidth(m.name);
                        ctx.fill(right - w - 8, y, right, y + 11, 0x99101014);
                        ctx.fill(right - 2, y, right, y + 11, m.category.color);
                        ctx.drawTextWithShadow(mc.textRenderer, m.name,
                                right - w - 5, y + 2, m.category.color);
                        y += 11;
                    }
                });
    }
}
