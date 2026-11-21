package com.example.rolusion.modules;

import com.example.rolusion.Category;
import com.example.rolusion.Module;
import com.example.rolusion.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.GraphicsMode;
import org.lwjgl.glfw.GLFW;

/**
 * Lowers the settings that cost the most FPS while it is on, and puts your own
 * settings back when it is turned off.
 *  - Fast graphics, no entity shadows, no biome blending (particles unchanged)
 *  - Shorter render, simulation and entity distance (sliders)
 *  - Caps FPS while the game window is not focused
 */
public class FpsBoost extends Module {
    private final NumberSetting renderDist = new NumberSetting("RenderDist", 2, 16, 6);
    private final NumberSetting simDist = new NumberSetting("SimDist", 5, 12, 6);
    private final NumberSetting entityDist = new NumberSetting("EntityDist%", 50, 100, 50);
    private final NumberSetting unfocusedFps = new NumberSetting("UnfocusedFps", 10, 60, 15);

    private int oldView, oldSim, oldBlend, oldMaxFps;
    private double oldEntity;
    private boolean oldShadows;
    private GraphicsMode oldGraphics;

    private boolean throttled;
    private int applyIn = -1;   // debounce so dragging a slider doesn't reload chunks every frame

    public FpsBoost() {
        super("FpsBoost", Category.RENDER, GLFW.GLFW_KEY_UNKNOWN);
        settings.add(renderDist);
        settings.add(simDist);
        settings.add(entityDist);
        settings.add(unfocusedFps);
    }

    @Override
    public void onEnable() {
        GameOptions o = MinecraftClient.getInstance().options;
        oldView = o.getViewDistance().getValue();
        oldSim = o.getSimulationDistance().getValue();
        oldBlend = o.getBiomeBlendRadius().getValue();
        oldEntity = o.getEntityDistanceScaling().getValue();
        oldShadows = o.getEntityShadows().getValue();
        oldGraphics = o.getPreset().getValue();
        apply();
    }

    @Override
    public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        GameOptions o = mc.options;
        if (throttled) { o.getMaxFps().setValue(oldMaxFps); throttled = false; }
        o.getViewDistance().setValue(oldView);
        o.getSimulationDistance().setValue(oldSim);
        o.getBiomeBlendRadius().setValue(oldBlend);
        o.getEntityDistanceScaling().setValue(oldEntity);
        o.getEntityShadows().setValue(oldShadows);
        o.applyGraphicsMode(oldGraphics);
        applyIn = -1;
    }

    private void apply() {
        GameOptions o = MinecraftClient.getInstance().options;
        o.applyGraphicsMode(GraphicsMode.FAST);
        o.getEntityShadows().setValue(false);
        o.getBiomeBlendRadius().setValue(0);
        o.getEntityDistanceScaling().setValue(entityDist.value / 100.0);
        o.getViewDistance().setValue(renderDist.value);
        o.getSimulationDistance().setValue(simDist.value);
    }

    @Override
    public void onSettingChanged() { applyIn = 20; }

    @Override
    public void onTick(MinecraftClient mc) {
        if (applyIn >= 0 && --applyIn < 0) apply();

        boolean focused = mc.isWindowFocused();
        if (!focused && !throttled) {
            oldMaxFps = mc.options.getMaxFps().getValue();
            mc.options.getMaxFps().setValue(unfocusedFps.value);
            throttled = true;
        } else if (focused && throttled) {
            mc.options.getMaxFps().setValue(oldMaxFps);
            throttled = false;
        }
    }
}
