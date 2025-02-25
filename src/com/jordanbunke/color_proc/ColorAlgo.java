package com.jordanbunke.color_proc;

import com.jordanbunke.delta_time.image.GameImage;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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
}
