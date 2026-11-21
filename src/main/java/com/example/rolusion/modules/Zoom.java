package com.example.rolusion.modules;

import com.example.rolusion.Category;
import com.example.rolusion.Module;
import com.example.rolusion.NumberSetting;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class Zoom extends Module {
    private final NumberSetting fov = new NumberSetting("FOV", 30, 70, 30);
    private int oldFov = 70;

    public Zoom() {
        super("Zoom", Category.RENDER, GLFW.GLFW_KEY_C);
        settings.add(fov);
    }

    @Override
    public void onEnable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        oldFov = mc.options.getFov().getValue();
        mc.options.getFov().setValue(fov.value);
    }

    @Override
    public void onDisable() {
        MinecraftClient.getInstance().options.getFov().setValue(oldFov);
    }

    @Override
    public void onSettingChanged() {
        if (isEnabled()) MinecraftClient.getInstance().options.getFov().setValue(fov.value);
    }
}
