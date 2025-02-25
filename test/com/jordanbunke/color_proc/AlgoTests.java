package com.jordanbunke.color_proc;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.RNG;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;
import java.util.function.Function;

import static com.jordanbunke.color_proc.ColorProc.RGB_SCALE;

public class AlgoTests {
    @Test
    public void reverseOfAReversedImageIsTheOriginalImage() {
        final GameImage img = drawRandomImage();
        final Function<Color, Color> reverse = c -> new Color(
                RGB_SCALE - c.getRed(), RGB_SCALE - c.getGreen(),
                RGB_SCALE - c.getBlue(), c.getAlpha());

        Assert.assertNotEquals(img, ColorAlgo.run(reverse, img));
        Assert.assertEquals(img,
                ColorAlgo.run(c -> reverse.apply(reverse.apply(c)), img));
    }

    private GameImage drawRandomImage() {
        final int DIM = 20;
        final GameImage img = new GameImage(DIM, DIM);

        for (int x = 0; x < DIM; x++)
            for (int y = 0; y < DIM; y++) {
                final Color c = new Color(
                        RNG.randomInRange(0, RGB_SCALE + 1),
                        RNG.randomInRange(0, RGB_SCALE + 1),
                        RNG.randomInRange(0, RGB_SCALE + 1));
                img.dot(c, x, y);
            }

        return img.submit();
    }
}
