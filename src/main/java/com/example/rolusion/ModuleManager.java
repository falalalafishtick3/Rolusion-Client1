package com.example.rolusion;

import com.example.rolusion.modules.*;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ModuleManager {
    public static final List<Module> modules = new ArrayList<>();
    private static final Map<Module, KeyBinding> keys = new LinkedHashMap<>();

    public static void init() {
        // Movement
        modules.add(new Sprint());
        modules.add(new ToggleSneak());
        // Render
        modules.add(new Fullbright());
        modules.add(new Zoom());
        modules.add(new NoBob());
        modules.add(new CleanView());
        modules.add(new Crosshair());
        modules.add(new FpsBoost());
        // World
        modules.add(new Waypoints());
        // HUD
        modules.add(new InfoHud());
        modules.add(new ArrayListHud());
        modules.add(new Keystrokes());
        modules.add(new ArmorHud());
        modules.add(new Clock());
        modules.add(new PingHud());
        modules.add(new PotionHud());
        modules.add(new TargetInfo());
        modules.add(new Colors());

        for (Module m : modules) {
            keys.put(m, KeyBindingHelper.registerKeyBinding(new KeyBinding(
                    "key.rolusion." + m.name.toLowerCase(),
                    InputUtil.Type.KEYSYM,
                    m.defaultKey,
                    RolusionClient.CATEGORY)));
        }

        if (!Config.load()) Config.pending.add("ArrayList"); // first-run default
    }

    public static List<Module> in(Category c) {
        List<Module> out = new ArrayList<>();
        for (Module m : modules) if (m.category == c) out.add(m);
        return out;
    }

    public static void tick(MinecraftClient mc) {
        if (mc.player != null && !Config.pending.isEmpty()) {
            for (Module m : modules) if (Config.pending.contains(m.name)) m.setEnabled(true);
            Config.pending.clear();
        }
        for (Module m : modules) {
            while (keys.get(m).wasPressed()) m.toggle();
            if (m.isEnabled() && mc.player != null) m.onTick(mc);
        }
    }
}
