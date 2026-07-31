package it.niedermann.nextcloud.deck.domain.model;

import java.io.Serializable;

public record Color(int argb) implements Serializable {
    public Color(int r, int g, int b) {
        this(r, g, b, 255);
    }

    public Color(int r, int g, int b, int a) {
        this(((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF));
    }

    public Color(byte r, byte g, byte b) {
        this(r & 0xFF, g & 0xFF, b & 0xFF);
    }

    public int getRed() {
        return (argb >> 16) & 0xFF;
    }

    public int getGreen() {
        return (argb >> 8) & 0xFF;
    }

    public int getBlue() {
        return argb & 0xFF;
    }

    public int getAlpha() {
        return (argb >> 24) & 0xFF;
    }

    public static Color decode(String nm) {
        if (nm.startsWith("#")) {
            nm = nm.substring(1);
        }
        return new Color((int) Long.parseLong(nm, 16));
    }
}
