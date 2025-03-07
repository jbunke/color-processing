package com.jordanbunke.color_proc;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.MathPlus;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.jordanbunke.color_proc.ColorProc.RGB_SCALE;

public final class ColorAlgo {
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
                    (c1, c2) -> diff(c1, c) < diff(c2, c), palette);
        };
    }

    public static double diff(final Color a, final Color b) {
        final int MAX_DIFF = RGB_SCALE * 4;

        final int rDiff = Math.abs(a.getRed() - b.getRed()),
                gDiff = Math.abs(a.getGreen() - b.getGreen()),
                bDiff = Math.abs(a.getBlue() - b.getBlue()),
                alphaDiff = Math.abs(a.getAlpha() - b.getAlpha());

        return (rDiff + gDiff + bDiff + alphaDiff) / (double) MAX_DIFF;
    }

    public static double diffRGB(final Color a, final Color b) {
        final int MAX_DIFF = RGB_SCALE * 3;

        final int rDiff = Math.abs(a.getRed() - b.getRed()),
                gDiff = Math.abs(a.getGreen() - b.getGreen()),
                bDiff = Math.abs(a.getBlue() - b.getBlue());

        return (rDiff + gDiff + bDiff) / (double) MAX_DIFF;
    }
}
