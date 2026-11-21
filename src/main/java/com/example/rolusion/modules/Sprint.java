package com.example.rolusion.modules;

import com.example.rolusion.Category;
import com.example.rolusion.Module;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class Sprint extends Module {
    public Sprint() { super("Sprint", Category.MOVEMENT, GLFW.GLFW_KEY_R); }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.options.forwardKey.isPressed() && !mc.player.horizontalCollision
                && !mc.player.isSneaking()) {
            mc.player.setSprinting(true);
        }
    }
}
