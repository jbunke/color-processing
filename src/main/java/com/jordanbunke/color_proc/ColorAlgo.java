package com.jordanbunke.color_proc;

import com.jordanbunke.grundstein.util.GraphicsImage;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

import static com.jordanbunke.color_proc.ColorProc.RGB_SCALE;

public final class ColorAlgo {
    public static Color[] colors(final GraphicsImage source) {
        return colors(source, false);
    }

    public static Color[] colors(final GraphicsImage source, final boolean ignoreTP) {
        final int w = source.getWidth(), h = source.getHeight();
        final Set<Color> cSet = new HashSet<>();
        final List<Color> cs = new LinkedList<>();

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                final Color c = source.getColorAt(x, y);

                if (ignoreTP && c.getAlpha() == 0) continue;

                if (!cSet.contains(c)) {
                    cSet.add(c);
                    cs.add(c);
                }
            }
        }

        return cs.toArray(Color[]::new);
    }

    public static GraphicsImage run(
            final Function<Color, Color> algo,
            final GraphicsImage source
    ) {
        final int w = source.getWidth(), h = source.getHeight();
        final GraphicsImage img = new GraphicsImage(w, h);
        final Map<Color, Color> replacements = new HashMap<>();

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                final Color c = source.getColorAt(x, y);

                if (replacements.containsKey(c))
                    img.setRGB(x, y, replacements.get(c).getRGB());
                else {
                    final Color out = algo.apply(c);
                    replacements.put(c, out);
                    img.setRGB(x, y, out.getRGB());
                }
            }
        }

        return img.submit();
    }

    public static Function<Color, Color> quantizeToPalette(
            final Color[] palette
    ) {
        return c -> {
            if (palette.length == 0 || c.getAlpha() == 0)
                return c;

            Color closest = palette[0];
            double diff = diffRGBA(closest, c), closestDiff = diff;

            for (final Color p : palette) {
                diff = diffRGBA(p, c);

                if (diff < closestDiff) {
                    closestDiff = diff;
                    closest = p;
                }
            }

            return closest;
        };
    }

    public static Color lerp(
            final Color a, final Color b, final double t
    ) {
        if (t <= 0d)
            return a;
        else if (t >= 1d)
            return b;

        final int baseR = a.getRed(), baseG = a.getGreen(),
                baseB = a.getBlue(), baseA = a.getAlpha(),
                deltaR = b.getRed() - a.getRed(),
                deltaG = b.getGreen() - a.getGreen(),
                deltaB = b.getBlue() - a.getBlue(),
                deltaA = b.getAlpha() - a.getAlpha();

        return new Color(
                baseR + (int)Math.round(deltaR * t),
                baseG + (int)Math.round(deltaG * t),
                baseB + (int)Math.round(deltaB * t),
                baseA + (int)Math.round(deltaA * t)
        );
    }

    public static double diffRGBA(final Color a, final Color b) {
        return diff(a, b, Color::getRed, Color::getGreen,
                Color::getBlue, Color::getAlpha);
    }

    public static double diffRGB(final Color a, final Color b) {
        return diff(a, b, Color::getRed, Color::getGreen, Color::getBlue);
    }

    @SafeVarargs
    private static double diff(
            final Color a, final Color b,
            final Function<Color, Integer>... channels
    ) {
        final int MAX_DIFF = RGB_SCALE * channels.length;

        int cumulativeDiff = 0;

        for (Function<Color, Integer> channel : channels)
            cumulativeDiff += Math.abs(channel.apply(a) - channel.apply(b));

        return cumulativeDiff / (double) MAX_DIFF;
    }
}
