package com.example.rolusion;

import com.example.rolusion.modules.Colors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * Classic flat "column list" click GUI: red-bordered category columns with a
 * header (name + module count) and one row per module.
 *  - Left click a module: toggle it (enabled = red text)
 *  - Right click a module with settings: open/close its sliders
 *  - Drag a header to move a column, right click a header to collapse it
 * Mouse input is polled from GLFW, so it doesn't depend on Screen's mouse
 * callback signatures (which change between Minecraft versions).
 */
public class ModuleScreen extends Screen {
    private static final int PW = 100, HH = 12, RH = 14, SH = 14;

    private static final int BORDER = 0xFFB33A3A;
    private static final int HEADER = 0xFFA43C3C;
    private static final int ROW = 0xFF1E1E1E;
    private static final int ROW_HOVER = 0xFF2B2B2B;
    private static final int SUB = 0xFF171717;
    private static final int OFF = 0xFFB4B4B4;
    private static final int ON = 0xFFFF3C3C;
    private static final int TRACK = 0xFF333333;

    private static class Panel {
        final Category cat; int x, y; boolean open = true;
        Panel(Category c, int x, int y) { cat = c; this.x = x; this.y = y; }
    }

    private static final List<Panel> panels = new ArrayList<>();

    private Panel dragging;
    private int dragDx, dragDy;
    private NumberSetting sliding;
    private Module slidingOwner;
    private int slideX;
    private boolean wasLeft, wasRight;

    public ModuleScreen() {
        super(Text.literal("Rolusion Client"));
        if (panels.isEmpty()) {
            int x = 4;
            for (Category c : Category.values()) {
                panels.add(new Panel(c, x, 20));
                x += PW + 4;
            }
        }
    }

    @Override public boolean shouldPause() { return false; }

    @Override public void removed() { Config.save(); }

    @Override
    public void renderBackground(DrawContext ctx, int mx, int my, float delta) {
        ctx.fill(0, 0, width, height, 0x55000000);
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        renderBackground(ctx, mx, my, delta);

        long win = MinecraftClient.getInstance().getWindow().getHandle();
        boolean left = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean right = GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        boolean leftClick = left && !wasLeft;
        boolean rightClick = right && !wasRight;
        wasLeft = left; wasRight = right;

        if (!left) { dragging = null; sliding = null; }
        if (dragging != null) {
            dragging.x = Math.max(0, Math.min(width - PW, mx - dragDx));
            dragging.y = Math.max(0, Math.min(height - HH, my - dragDy));
        }
        if (sliding != null) {
            double t = Math.max(0, Math.min(1, (mx - slideX) / (double) (PW - 10)));
            int v = sliding.min + (int) Math.round(t * (sliding.max - sliding.min));
            if (v != sliding.value) { sliding.value = v; slidingOwner.onSettingChanged(); }
        }

        boolean consumed = false;
        for (Panel p : panels) consumed |= drawPanel(ctx, p, mx, my, leftClick, rightClick, consumed);

        ctx.drawTextWithShadow(textRenderer, "Rolusion Client", 6, height - 12, Colors.on());
    }

    private boolean drawPanel(DrawContext ctx, Panel p, int mx, int my,
                              boolean leftClick, boolean rightClick, boolean consumed) {
        List<Module> mods = ModuleManager.in(p.cat);
        int total = HH;
        if (p.open)
            for (Module m : mods) total += RH + (m.expanded ? m.settings.size() * SH : 0);

        int x = p.x, y = p.y;
        ctx.fill(x - 1, y - 1, x + PW + 1, y + total + 1, Colors.border());
        ctx.fill(x, y, x + PW, y + total, ROW);
        ctx.fill(x, y, x + PW, y + HH, Colors.header());

        ctx.drawTextWithShadow(textRenderer, p.cat.label, x + 3, y + 2, 0xFFFFFFFF);
        String count = "[" + mods.size() + "]";
        ctx.drawTextWithShadow(textRenderer, count,
                x + PW - 3 - textRenderer.getWidth(count), y + 2, 0xFFE8C8C8);

        boolean used = false;
        boolean overHeader = !consumed && mx >= x && mx < x + PW && my >= y && my < y + HH;
        if (overHeader && leftClick) { dragging = p; dragDx = mx - x; dragDy = my - y; used = true; }
        if (overHeader && rightClick) { p.open = !p.open; used = true; }
        if (!p.open) return used;

        int ry = y + HH;
        for (Module m : mods) {
            boolean hover = !consumed && !used && mx >= x && mx < x + PW && my >= ry && my < ry + RH;
            if (hover) ctx.fill(x, ry, x + PW, ry + RH, ROW_HOVER);
            ctx.drawTextWithShadow(textRenderer, m.name, x + 5, ry + 3, m.isEnabled() ? Colors.on() : OFF);
            if (!m.settings.isEmpty())
                ctx.drawTextWithShadow(textRenderer, m.expanded ? "-" : "+",
                        x + PW - 9, ry + 3, 0xFF6E6E6E);

            if (hover && leftClick) { m.toggle(); used = true; }
            if (hover && rightClick && !m.settings.isEmpty()) { m.expanded = !m.expanded; used = true; }
            ry += RH;

            if (m.expanded) {
                for (NumberSetting s : m.settings) {
                    boolean over = !consumed && !used && mx >= x && mx < x + PW && my >= ry && my < ry + SH;
                    ctx.fill(x, ry, x + PW, ry + SH, SUB);
                    ctx.drawTextWithShadow(textRenderer, s.name, x + 5, ry + 1, 0xFF8C8C8C);
                    String v = String.valueOf(s.value);
                    ctx.drawTextWithShadow(textRenderer, v,
                            x + PW - 5 - textRenderer.getWidth(v), ry + 1, 0xFFFFFFFF);
                    int tx1 = x + 5, tx2 = x + PW - 5, ty = ry + SH - 4;
                    ctx.fill(tx1, ty, tx2, ty + 2, TRACK);
                    double t = (s.value - s.min) / (double) (s.max - s.min);
                    ctx.fill(tx1, ty, tx1 + (int) Math.round(t * (tx2 - tx1)), ty + 2, Colors.on());
                    if (over && leftClick) { sliding = s; slidingOwner = m; slideX = tx1; used = true; }
                    ry += SH;
                }
            }
        }
        return used;
    }
}
