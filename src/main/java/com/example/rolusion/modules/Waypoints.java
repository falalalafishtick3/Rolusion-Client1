package com.example.rolusion.modules;

import com.example.rolusion.Category;
import com.example.rolusion.Module;
import com.example.rolusion.RolusionClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Personal waypoints. While the module is on: press the "Add waypoint" key (B) to save
 * your current position, and the "Clear waypoints" key (unbound) to remove them.
 * The 5 nearest waypoints in your current dimension show on the HUD with distance.
 */
public class Waypoints extends Module {
    public static class Wp { String name, dim; double x, y, z; }

    private static final List<Wp> list = new ArrayList<>();
    private final KeyBinding addKey, clearKey;

    public Waypoints() {
        super("Waypoints", Category.WORLD, GLFW.GLFW_KEY_UNKNOWN);
        addKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.rolusion.waypoint_add", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, RolusionClient.CATEGORY));
        clearKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.rolusion.waypoint_clear", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, RolusionClient.CATEGORY));

        HudElementRegistry.attachElementAfter(
                VanillaHudElements.CHAT,
                Identifier.of("rolusion", "waypoints"),
                (ctx, tickCounter) -> {
                    if (!isEnabled()) return;
                    MinecraftClient mc = MinecraftClient.getInstance();
                    if (mc.player == null || mc.world == null || mc.options.hudHidden) return;
                    String dim = mc.world.getRegistryKey().getValue().toString();
                    List<Wp> here = list.stream().filter(w -> w.dim.equals(dim))
                            .sorted(Comparator.comparingDouble(w -> dist(mc, w)))
                            .limit(5).toList();
                    if (here.isEmpty()) return;
                    int y = 64;
                    int w = 0;
                    List<String> lines = new ArrayList<>();
                    for (Wp p : here) {
                        String s = p.name + "  " + Math.round(dist(mc, p)) + "m";
                        lines.add(s);
                        w = Math.max(w, mc.textRenderer.getWidth(s));
                    }
                    ctx.fill(4, y, 4 + w + 12, y + 3 + lines.size() * 11, 0x99101014);
                    ctx.fill(4, y, 6, y + 3 + lines.size() * 11, Category.WORLD.color);
                    int ty = y + 3;
                    for (String s : lines) {
                        ctx.drawTextWithShadow(mc.textRenderer, s, 10, ty, 0xFFFFFFFF);
                        ty += 11;
                    }
                });
    }

    private static double dist(MinecraftClient mc, Wp w) {
        double dx = mc.player.getX() - w.x, dy = mc.player.getY() - w.y, dz = mc.player.getZ() - w.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.world == null) return;
        while (addKey.wasPressed()) {
            Wp w = new Wp();
            w.name = "WP" + (list.size() + 1);
            w.dim = mc.world.getRegistryKey().getValue().toString();
            w.x = mc.player.getX(); w.y = mc.player.getY(); w.z = mc.player.getZ();
            list.add(w);
            mc.player.sendMessage(Text.literal("Saved " + w.name + " at "
                    + Math.round(w.x) + " " + Math.round(w.y) + " " + Math.round(w.z)), true);
        }
        while (clearKey.wasPressed()) {
            list.clear();
            mc.player.sendMessage(Text.literal("Waypoints cleared"), true);
        }
    }

    public static JsonArray toJson() {
        JsonArray a = new JsonArray();
        for (Wp w : list) {
            JsonObject o = new JsonObject();
            o.addProperty("name", w.name); o.addProperty("dim", w.dim);
            o.addProperty("x", w.x); o.addProperty("y", w.y); o.addProperty("z", w.z);
            a.add(o);
        }
        return a;
    }

    public static void fromJson(JsonArray a) {
        list.clear();
        for (JsonElement e : a) {
            JsonObject o = e.getAsJsonObject();
            Wp w = new Wp();
            w.name = o.get("name").getAsString(); w.dim = o.get("dim").getAsString();
            w.x = o.get("x").getAsDouble(); w.y = o.get("y").getAsDouble(); w.z = o.get("z").getAsDouble();
            list.add(w);
        }
    }
}
