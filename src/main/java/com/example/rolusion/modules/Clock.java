package com.example.rolusion.modules;

import com.example.rolusion.Category;
import com.example.rolusion.Module;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** Real-world clock under the info box. */
public class Clock extends Module {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public Clock() {
        super("Clock", Category.HUD, GLFW.GLFW_KEY_UNKNOWN);
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.CHAT,
                Identifier.of("rolusion", "clock"),
                (ctx, tickCounter) -> {
                    if (!isEnabled()) return;
                    MinecraftClient mc = MinecraftClient.getInstance();
                    if (mc.player == null || mc.options.hudHidden) return;
                    String t = LocalTime.now().format(FMT);
                    int w = mc.textRenderer.getWidth(t) + 12;
                    ctx.fill(4, 45, 4 + w, 59, 0x99101014);
                    ctx.fill(4, 45, 6, 59, Category.HUD.color);
                    ctx.drawTextWithShadow(mc.textRenderer, t, 10, 48, 0xFFFFFFFF);
                });
    }
}
