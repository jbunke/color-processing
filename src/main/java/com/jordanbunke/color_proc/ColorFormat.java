package com.jordanbunke.color_proc;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.jordanbunke.color_proc.ColorAlgo.*;

public final class ColorFormat {
    public static String percentageSimilarity(
            final Color a, final Color b,
            final boolean alpha, final int decimals
    ) {
        final double diff = alpha ? diffRGBA(a, b) : diffRGB(a, b),
                sim = (1d - diff) * 100;

        return BigDecimal.valueOf(sim)
                .setScale(decimals, RoundingMode.HALF_UP) + "%";
    }
}
