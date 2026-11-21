package com.example.rolusion.modules;

import com.example.rolusion.Category;
import com.example.rolusion.Module;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

/** Your latency and the number of players online, top-center. */
public class PingHud extends Module {
    public PingHud() {
        super("PingHud", Category.HUD, GLFW.GLFW_KEY_UNKNOWN);
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.CHAT,
                Identifier.of("rolusion", "pinghud"),
                (ctx, tickCounter) -> {
                    if (!isEnabled()) return;
                    MinecraftClient mc = MinecraftClient.getInstance();
                    ClientPlayNetworkHandler nh = mc.getNetworkHandler();
                    if (mc.player == null || nh == null || mc.options.hudHidden) return;
                    PlayerListEntry me = nh.getPlayerListEntry(mc.player.getUuid());
                    String ping = (me == null || mc.isInSingleplayer()) ? "-- ms" : me.getLatency() + " ms";
                    String t = "Ping " + ping + "   Players " + nh.getPlayerList().size();
                    int w = mc.textRenderer.getWidth(t) + 12;
                    int x = (ctx.getScaledWindowWidth() - w) / 2;
                    ctx.fill(x, 4, x + w, 17, 0x99101014);
                    ctx.fill(x, 15, x + w, 17, Category.HUD.color);
                    ctx.drawTextWithShadow(mc.textRenderer, t, x + 6, 6, 0xFFFFFFFF);
                });
    }
}
