package com.example.rolusion.modules;

import com.example.rolusion.Category;
import com.example.rolusion.Module;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

/** Name and health of the mob or player you are looking at, below the crosshair. */
public class TargetInfo extends Module {
    public TargetInfo() {
        super("TargetInfo", Category.HUD, GLFW.GLFW_KEY_UNKNOWN);
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.CHAT,
                Identifier.of("rolusion", "targetinfo"),
                (ctx, tickCounter) -> {
                    if (!isEnabled()) return;
                    MinecraftClient mc = MinecraftClient.getInstance();
                    if (mc.player == null || mc.options.hudHidden) return;
                    if (!(mc.targetedEntity instanceof LivingEntity le) || !le.isAlive()) return;
                    String n = le.getName().getString();
                    String hp = String.format("%.1f / %.1f", le.getHealth(), le.getMaxHealth());
                    int w = Math.max(mc.textRenderer.getWidth(n), mc.textRenderer.getWidth(hp)) + 16;
                    int x = (ctx.getScaledWindowWidth() - w) / 2;
                    int y = ctx.getScaledWindowHeight() / 2 + 18;
                    ctx.fill(x, y, x + w, y + 29, 0x99101014);
                    ctx.fill(x, y, x + 2, y + 29, Category.HUD.color);
                    ctx.drawTextWithShadow(mc.textRenderer, n, x + 8, y + 4, 0xFFFFFFFF);
                    ctx.drawTextWithShadow(mc.textRenderer, hp, x + 8, y + 14, 0xFFBBBBBB);
                    float r = Math.max(0f, Math.min(1f, le.getHealth() / Math.max(1f, le.getMaxHealth())));
                    ctx.fill(x + 8, y + 25, x + w - 8, y + 27, 0xFF333333);
                    ctx.fill(x + 8, y + 25, x + 8 + Math.round((w - 16) * r), y + 27, Colors.on());
                });
    }
}
