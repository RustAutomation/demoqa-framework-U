package framework.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Визуальный компаратор с мягкой полупрозрачной красной подсветкой различий.
 */
public class VisualComparator {

    private static final Color OVERLAY_COLOR = new Color(255, 0, 0, 120); // прозрачный красный

    public static double compareAndHighlight(String baselinePath, String actualPath, String diffPath) throws IOException {
        BufferedImage baseline = ImageIO.read(new File(baselinePath));
        BufferedImage actual = ImageIO.read(new File(actualPath));

        int width = Math.min(baseline.getWidth(), actual.getWidth());
        int height = Math.min(baseline.getHeight(), actual.getHeight());

        BufferedImage diff = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        long diffPixels = 0;

        float alpha = OVERLAY_COLOR.getAlpha() / 255f;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb1 = baseline.getRGB(x, y);
                int rgb2 = actual.getRGB(x, y);

                if (rgb1 != rgb2) {
                    int r2 = (rgb2 >> 16) & 0xFF;
                    int g2 = (rgb2 >> 8) & 0xFF;
                    int b2 = rgb2 & 0xFF;

                    int r = (int) ((1 - alpha) * r2 + alpha * OVERLAY_COLOR.getRed());
                    int g = (int) ((1 - alpha) * g2 + alpha * OVERLAY_COLOR.getGreen());
                    int b = (int) ((1 - alpha) * b2 + alpha * OVERLAY_COLOR.getBlue());

                    int rgba = (0xFF << 24) | (r << 16) | (g << 8) | b;
                    diff.setRGB(x, y, rgba);
                    diffPixels++;
                } else {
                    diff.setRGB(x, y, actual.getRGB(x, y));
                }
            }
        }

        ImageIO.write(diff, "png", new File(diffPath));
        double total = (double) width * height;
        return (diffPixels / total) * 100.0;
    }
}
