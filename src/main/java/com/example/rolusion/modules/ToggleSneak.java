package com.example.rolusion.modules;

import com.example.rolusion.Category;
import com.example.rolusion.Module;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

/** Holds sneak for you (like the vanilla "toggle sneak" accessibility option). */
public class ToggleSneak extends Module {
    public ToggleSneak() { super("ToggleSneak", Category.MOVEMENT, GLFW.GLFW_KEY_UNKNOWN); }

    @Override
    public void onTick(MinecraftClient mc) {
        mc.options.sneakKey.setPressed(true);
    }

    @Override
    public void onDisable() {
        MinecraftClient.getInstance().options.sneakKey.setPressed(false);
    }
}
