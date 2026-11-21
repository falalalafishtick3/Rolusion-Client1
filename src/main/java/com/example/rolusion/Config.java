package com.example.rolusion;

import com.example.rolusion.modules.Waypoints;
import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/** Saves module on/off states and settings to config/rolusion.json. */
public class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /** Modules to switch on once the player exists (their onEnable may touch game options). */
    public static final Set<String> pending = new HashSet<>();

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("rolusion.json");
    }

    public static void save() {
        try {
            JsonObject mods = new JsonObject();
            for (Module m : ModuleManager.modules) {
                JsonObject o = new JsonObject();
                o.addProperty("enabled", m.isEnabled());
                for (NumberSetting s : m.settings) o.addProperty(s.name, s.value);
                mods.add(m.name, o);
            }
            JsonObject root = new JsonObject();
            root.add("modules", mods);
            root.add("waypoints", Waypoints.toJson());
            Files.writeString(file(), GSON.toJson(root));
        } catch (Exception e) {
            System.err.println("[Rolusion] Could not save config: " + e);
        }
    }

    /** @return true if a config file existed. */
    public static boolean load() {
        Path f = file();
        if (!Files.exists(f)) return false;
        try {
            JsonObject root = JsonParser.parseString(Files.readString(f)).getAsJsonObject();
            if (root.has("waypoints")) Waypoints.fromJson(root.getAsJsonArray("waypoints"));
            JsonObject mods = root.getAsJsonObject("modules");
            for (Module m : ModuleManager.modules) {
                if (!mods.has(m.name)) continue;
                JsonObject o = mods.getAsJsonObject(m.name);
                for (NumberSetting s : m.settings)
                    if (o.has(s.name)) s.value = Math.max(s.min, Math.min(s.max, o.get(s.name).getAsInt()));
                if (o.has("enabled") && o.get("enabled").getAsBoolean()) pending.add(m.name);
            }
        } catch (Exception e) {
            System.err.println("[Rolusion] Could not read config: " + e);
        }
        return true;
    }
}
