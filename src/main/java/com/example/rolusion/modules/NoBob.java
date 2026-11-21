package com.example.rolusion.modules;

import com.example.rolusion.Category;
import com.example.rolusion.Module;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class NoBob extends Module {
    private boolean old = true;

    public NoBob() { super("NoBob", Category.RENDER, GLFW.GLFW_KEY_UNKNOWN); }

    @Override
    public void onEnable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        old = mc.options.getBobView().getValue();
        mc.options.getBobView().setValue(false);
    }

    @Override
    public void onDisable() {
        MinecraftClient.getInstance().options.getBobView().setValue(old);
    }
}
