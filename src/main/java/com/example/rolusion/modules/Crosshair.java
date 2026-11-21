package com.example.rolusion.modules;

import com.example.rolusion.Category;
import com.example.rolusion.Module;
import com.example.rolusion.NumberSetting;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

/** Replaces the vanilla crosshair with an adjustable plus-shaped one. */
public class Crosshair extends Module {
    private final NumberSetting size = new NumberSetting("Size", 2, 12, 5);
    private final NumberSetting gap = new NumberSetting("Gap", 0, 8, 2);
    private final NumberSetting thick = new NumberSetting("Thickness", 1, 3, 1);

    public Crosshair() {
        super("Crosshair", Category.RENDER, GLFW.GLFW_KEY_UNKNOWN);
        settings.add(size); settings.add(gap); settings.add(thick);

        HudElementRegistry.replaceElement(VanillaHudElements.CROSSHAIR, original ->
                (ctx, tickCounter) -> {
                    MinecraftClient mc = MinecraftClient.getInstance();
                    if (!isEnabled()) { original.render(ctx, tickCounter); return; }
                    if (mc.options.hudHidden || mc.player == null) return;
                    int cx = ctx.getScaledWindowWidth() / 2, cy = ctx.getScaledWindowHeight() / 2;
                    int s = size.value, g = gap.value, t = thick.value, o = t / 2;
                    int col = Colors.on();
                    ctx.fill(cx - g - s, cy - o, cx - g, cy - o + t, col);
                    ctx.fill(cx + g, cy - o, cx + g + s, cy - o + t, col);
                    ctx.fill(cx - o, cy - g - s, cx - o + t, cy - g, col);
                    ctx.fill(cx - o, cy + g, cx - o + t, cy + g + s, col);
                });
    }
}
