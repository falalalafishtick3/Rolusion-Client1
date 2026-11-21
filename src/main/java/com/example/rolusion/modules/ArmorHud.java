package com.example.rolusion.modules;

import com.example.rolusion.Category;
import com.example.rolusion.Module;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

/** Worn armor with durability bars, on the right edge. */
public class ArmorHud extends Module {
    private static final EquipmentSlot[] SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };

    public ArmorHud() {
        super("ArmorHud", Category.HUD, GLFW.GLFW_KEY_UNKNOWN);
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.CHAT,
                Identifier.of("rolusion", "armorhud"),
                (ctx, tickCounter) -> {
                    if (!isEnabled()) return;
                    MinecraftClient mc = MinecraftClient.getInstance();
                    if (mc.player == null || mc.options.hudHidden) return;
                    int x = ctx.getScaledWindowWidth() - 24;
                    int y = ctx.getScaledWindowHeight() / 2 - 38;
                    ctx.fill(x - 3, y - 3, x + 19, y + 71, 0x99101014);
                    ctx.fill(x + 17, y - 3, x + 19, y + 71, Category.HUD.color);
                    for (EquipmentSlot slot : SLOTS) {
                        ItemStack s = mc.player.getEquippedStack(slot);
                        if (!s.isEmpty()) {
                            ctx.drawItem(s, x, y);
                            ctx.drawStackOverlay(mc.textRenderer, s, x, y);
                        }
                        y += 18;
                    }
                });
    }
}
