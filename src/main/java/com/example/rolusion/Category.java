package com.example.rolusion;

public enum Category {
    MOVEMENT("Movement", 0xFF4DD0E1),
    RENDER("Render", 0xFFB06CFF),
    WORLD("World", 0xFF7BD66B),
    HUD("HUD", 0xFFFF6B9D);

    public final String label;
    public final int color;

    Category(String label, int color) { this.label = label; this.color = color; }
}
