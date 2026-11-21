package com.example.rolusion.modules;

import com.example.rolusion.Category;
import com.example.rolusion.Module;
import com.example.rolusion.NumberSetting;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

/** Changes the accent color of the click GUI and crosshair. Off = red. */
public class Colors extends Module {
    private static Colors instance;
    private final NumberSetting hue = new NumberSetting("Hue", 0, 360, 280);

    public Colors() {
        super("Colors", Category.HUD, GLFW.GLFW_KEY_UNKNOWN);
        settings.add(hue);
        instance = this;
    }

    private static float h() {
        return (instance != null && instance.isEnabled() ? instance.hue.value : 0) / 360f;
    }

    private static int hsv(float s, float v) {
        return 0xFF000000 | MathHelper.hsvToRgb(h(), s, v);
    }

    public static int on()     { return hsv(0.76f, 1.00f); }
    public static int border() { return hsv(0.68f, 0.70f); }
    public static int header() { return hsv(0.64f, 0.64f); }
}
