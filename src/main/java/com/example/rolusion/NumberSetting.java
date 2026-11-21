package com.example.rolusion;

public class NumberSetting {
    public final String name;
    public final int min, max;
    public int value;

    public NumberSetting(String name, int min, int max, int value) {
        this.name = name; this.min = min; this.max = max; this.value = value;
    }
}
