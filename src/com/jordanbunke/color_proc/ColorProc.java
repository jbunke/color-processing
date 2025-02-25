package com.jordanbunke.color_proc;

import com.jordanbunke.delta_time.utility.math.MathPlus;

import java.awt.*;

public final class ColorProc {
    private static final Color DEFAULT = new Color(0x000000);
    public static final int RGB_SCALE = 0xff, HUE_SCALE = 360;

    public static Color fromHSV(
            final double[] hsv
    ) {
        if (hsv.length < 3)
            return DEFAULT;

        return fromHSV(hsv[0], hsv[1], hsv[2]);
    }

    public static Color rgbOnly(final Color c) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue());
    }

    public static Color fromHSV(
            final double hue, final double sat,
            final double val
    ) {
        return fromHSV(hue, sat, val, RGB_SCALE);
    }

    public static Color fromHSV(
            final double hue, final double sat,
            final double val, final int alpha
    ) {
        final double SIX = 6d, c = sat * val,
                x = c * (1d - Math.abs(((SIX * hue) % 2) - 1)),
                m = val - c, r, g, b;

        if (hue < 1 / SIX) {
            r = c;
            g = x;
            b = 0d;
        } else if (hue < 2 / SIX) {
            r = x;
            g = c;
            b = 0d;
        } else if (hue < 3 / SIX) {
            r = 0d;
            g = c;
            b = x;
        } else if (hue < 4 / SIX) {
            r = 0d;
            g = x;
            b = c;
        } else if (hue < 5 / SIX) {
            r = x;
            g = 0d;
            b = c;
        } else {
            r = c;
            g = 0d;
            b = x;
        }

        return new Color(scaleUpChannel(r + m),
                scaleUpChannel(g + m), scaleUpChannel(b + m), alpha);
    }

    public static double rgbToHue(final Color c) {
        final int R = 0, G = 1, B = 2;
        final double[] rgb = rgbAsArray(c);
        final double max = getMaxOfRGB(rgb), range = getRangeOfRGB(rgb),
                multiplier = 1 / 6d;

        if (range == 0d)
            return 0d;

        if (max == rgb[R]) {
            // red maximum case
            double value = (rgb[G] - rgb[B]) / range;

            while (value < 0)
                value += 6;
            while (value >= 6)
                value -= 6;

            return multiplier * value;
        } else if (max == rgb[G]) {
            // green maximum case
            return multiplier * (((rgb[B] - rgb[R]) / range) + 2);

        } else if (max == rgb[B]) {
            // blue maximum case
            return multiplier * (((rgb[R] - rgb[G]) / range) + 4);
        }

        return 0d;
    }

    public static double rgbToSat(final Color c) {
        final double[] rgb = rgbAsArray(c);
        final double max = getMaxOfRGB(rgb);

        if (max == 0d)
            return 0;
        else
            return getRangeOfRGB(rgb) / max;
    }

    public static double rgbToValue(final Color c) {
        return getMaxOfRGB(rgbAsArray(c));
    }

    private static int scaleUpRGBAHSV(final double n, final int scale) {
        return MathPlus.bounded(0, (int) Math.round(n * scale), scale);
    }

    public static int hue(final Color c) {
        return scaleUpRGBAHSV(rgbToHue(c), HUE_SCALE);
    }

    public static int sat(final Color c) {
        return scaleUpChannel(rgbToSat(c));
    }

    public static int val(final Color c) {
        return scaleUpChannel(rgbToValue(c));
    }

    public static int scaleUpChannel(final double n) {
        return scaleUpRGBAHSV(n, RGB_SCALE);
    }

    public static double normalizeHue(double hue) {
        while (hue > 1.0) hue -= 1.0;
        while (hue < 0.0) hue += 1.0;

        return hue;
    }

    private static double getRangeOfRGB(final double[] rgb) {
        return getMaxOfRGB(rgb) - getMinOfRGB(rgb);
    }

    private static double getMaxOfRGB(final double[] rgb) {
        return MathPlus.max(rgb);
    }

    private static double getMinOfRGB(final double[] rgb) {
        return MathPlus.min(rgb);
    }

    private static double[] rgbAsArray(final Color c) {
        return new double[] {
                c.getRed() / (double) RGB_SCALE,
                c.getGreen() / (double) RGB_SCALE,
                c.getBlue() / (double) RGB_SCALE
        };
    }
}
