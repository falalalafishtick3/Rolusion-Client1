package com.example.rolusion.modules;

import com.example.rolusion.Category;
import com.example.rolusion.Module;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/** Active potion effects with level and time left, on the left side. */
public class PotionHud extends Module {
    public PotionHud() {
        super("PotionHud", Category.HUD, GLFW.GLFW_KEY_UNKNOWN);
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.CHAT,
                Identifier.of("rolusion", "potionhud"),
                (ctx, tickCounter) -> {
                    if (!isEnabled()) return;
                    MinecraftClient mc = MinecraftClient.getInstance();
                    if (mc.player == null || mc.options.hudHidden) return;
                    List<StatusEffectInstance> effects = new ArrayList<>(mc.player.getStatusEffects());
                    if (effects.isEmpty()) return;
                    int y = ctx.getScaledWindowHeight() / 2 + 56;
                    for (StatusEffectInstance e : effects) {
                        String name = e.getEffectType().value().getName().getString()
                                + " " + (e.getAmplifier() + 1);
                        int secs = e.getDuration() / 20;
                        String time = e.isInfinite() ? "inf" : String.format("%d:%02d", secs / 60, secs % 60);
                        int w = mc.textRenderer.getWidth(name) + mc.textRenderer.getWidth(time) + 22;
                        ctx.fill(4, y, 4 + w, y + 11, 0x99101014);
                        ctx.fill(4, y, 6, y + 11, Category.HUD.color);
                        ctx.drawTextWithShadow(mc.textRenderer, name, 10, y + 2, 0xFFFFFFFF);
                        ctx.drawTextWithShadow(mc.textRenderer, time,
                                4 + w - 6 - mc.textRenderer.getWidth(time), y + 2, 0xFFBBBBBB);
                        y += 12;
                    }
                });
    }
}
