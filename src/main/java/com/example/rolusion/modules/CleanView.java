package com.example.rolusion.modules;

import com.example.rolusion.Category;
import com.example.rolusion.Module;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

/** Turns off FOV effects (sprint/speed FOV change) and screen distortion for a steadier view. */
public class CleanView extends Module {
    private double oldFov = 1.0, oldDistortion = 1.0;

    public CleanView() { super("CleanView", Category.RENDER, GLFW.GLFW_KEY_UNKNOWN); }

    @Override
    public void onEnable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        oldFov = mc.options.getFovEffectScale().getValue();
        oldDistortion = mc.options.getDistortionEffectScale().getValue();
        mc.options.getFovEffectScale().setValue(0.0);
        mc.options.getDistortionEffectScale().setValue(0.0);
    }

    @Override
    public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.options.getFovEffectScale().setValue(oldFov);
        mc.options.getDistortionEffectScale().setValue(oldDistortion);
    }
}
