package com.example.rolusion.modules;

import com.example.rolusion.Category;
import com.example.rolusion.Module;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

/** FPS, coordinates and facing, top-left. */
public class InfoHud extends Module {
    public InfoHud() {
        super("InfoHud", Category.HUD, GLFW.GLFW_KEY_H);
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.CHAT,
                Identifier.of("rolusion", "infohud"),
                (ctx, tickCounter) -> {
                    if (!isEnabled()) return;
                    MinecraftClient mc = MinecraftClient.getInstance();
                    if (mc.player == null || mc.options.hudHidden) return;
                    String a = mc.getCurrentFps() + " FPS";
                    String b = String.format("XYZ  %.1f  %.1f  %.1f",
                            mc.player.getX(), mc.player.getY(), mc.player.getZ());
                    String f = mc.player.getHorizontalFacing().asString();
                    String c = "Facing  " + Character.toUpperCase(f.charAt(0)) + f.substring(1);
                    int w = Math.max(mc.textRenderer.getWidth(b), mc.textRenderer.getWidth(c)) + 12;
                    ctx.fill(4, 4, 4 + w, 41, 0x99101014);
                    ctx.fill(4, 4, 6, 41, Category.HUD.color);
                    ctx.drawTextWithShadow(mc.textRenderer, a, 10, 7, 0xFFFFFFFF);
                    ctx.drawTextWithShadow(mc.textRenderer, b, 10, 18, 0xFFBBBBBB);
                    ctx.drawTextWithShadow(mc.textRenderer, c, 10, 29, 0xFFBBBBBB);
                });
    }
}
