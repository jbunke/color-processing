package com.jordanbunke.color_proc;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.MathPlus;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

import static com.jordanbunke.color_proc.ColorProc.RGB_SCALE;

public final class ColorAlgo {
    public static Color[] colors(final GameImage source) {
        return colors(source, false);
    }

    public static Color[] colors(final GameImage source, final boolean ignoreTP) {
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

    public static GameImage run(
            final Function<Color, Color> algo,
            final GameImage source
    ) {
        final int w = source.getWidth(), h = source.getHeight();
        final GameImage img = new GameImage(w, h);
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

            final Color worst = new Color(
                    (c.getRed() + (RGB_SCALE / 2)) % RGB_SCALE,
                    (c.getGreen() + (RGB_SCALE / 2)) % RGB_SCALE,
                    (c.getBlue() + (RGB_SCALE / 2)) % RGB_SCALE,
                    (c.getAlpha() + (RGB_SCALE / 2)) % RGB_SCALE);

            return MathPlus.findBest(c, worst, cl -> cl,
                    (c1, c2) -> diffRGBA(c1, c) < diffRGBA(c2, c), palette);
        };
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
