package com.example.rolusion.modules;

import com.example.rolusion.Category;
import com.example.rolusion.Module;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.Deque;

/** WASD, space and mouse buttons with a clicks-per-second counter. */
public class Keystrokes extends Module {
    private final Deque<Long> lmb = new ArrayDeque<>();
    private final Deque<Long> rmb = new ArrayDeque<>();
    private boolean wasL, wasR;

    public Keystrokes() {
        super("Keystrokes", Category.HUD, GLFW.GLFW_KEY_UNKNOWN);
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.CHAT,
                Identifier.of("rolusion", "keystrokes"),
                (ctx, tickCounter) -> {
                    if (!isEnabled()) return;
                    MinecraftClient mc = MinecraftClient.getInstance();
                    if (mc.player == null || mc.options.hudHidden) return;

                    long win = mc.getWindow().getHandle();
                    boolean l = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
                    boolean r = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
                    long now = System.currentTimeMillis();
                    if (mc.currentScreen == null) {
                        if (l && !wasL) lmb.add(now);
                        if (r && !wasR) rmb.add(now);
                    }
                    wasL = l; wasR = r;
                    while (!lmb.isEmpty() && now - lmb.peekFirst() > 1000) lmb.poll();
                    while (!rmb.isEmpty() && now - rmb.peekFirst() > 1000) rmb.poll();

                    int x = 8, y = ctx.getScaledWindowHeight() / 2 - 30;
                    key(ctx, mc, "W", x + 22, y, 20, 20, mc.options.forwardKey.isPressed());
                    key(ctx, mc, "A", x, y + 22, 20, 20, mc.options.leftKey.isPressed());
                    key(ctx, mc, "S", x + 22, y + 22, 20, 20, mc.options.backKey.isPressed());
                    key(ctx, mc, "D", x + 44, y + 22, 20, 20, mc.options.rightKey.isPressed());
                    key(ctx, mc, "SPACE", x, y + 44, 64, 14, mc.options.jumpKey.isPressed());
                    key(ctx, mc, "L " + lmb.size(), x, y + 60, 31, 16, l);
                    key(ctx, mc, "R " + rmb.size(), x + 33, y + 60, 31, 16, r);
                });
    }

    private static void key(DrawContext ctx, MinecraftClient mc, String label,
                            int x, int y, int w, int h, boolean down) {
        ctx.fill(x, y, x + w, y + h, down ? 0xCCFF6B9D : 0x99101014);
        ctx.fill(x, y + h - 1, x + w, y + h, Category.HUD.color);
        ctx.drawCenteredTextWithShadow(mc.textRenderer, label,
                x + w / 2, y + (h - 8) / 2, down ? 0xFF101014 : 0xFFFFFFFF);
    }
}
