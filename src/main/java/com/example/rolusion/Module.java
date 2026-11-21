package com.example.rolusion;

import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    public final String name;
    public final Category category;
    public final int defaultKey;
    public final List<NumberSetting> settings = new ArrayList<>();

    // GUI state
    public float anim;          // 0..1 fade for the enabled highlight
    public boolean expanded;    // settings drawer open

    private boolean enabled;

    public Module(String name, Category category, int defaultKey) {
        this.name = name;
        this.category = category;
        this.defaultKey = defaultKey;
    }

    public void toggle() {
        enabled = !enabled;
        if (enabled) onEnable(); else onDisable();
    }

    public void setEnabled(boolean e) { if (e != enabled) toggle(); }
    public boolean isEnabled() { return enabled; }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick(MinecraftClient mc) {}
    public void onSettingChanged() {}
}
