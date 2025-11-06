package framework.utils;

import io.qameta.allure.Allure;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;

/**
 * 🔍 Унифицированный визуальный компаратор baseline ↔ actual.
 * Добавляет изображения и diff в Allure-отчет, возвращает процент отличий.
 */
public final class VisualComparator {

    private static final Color OVERLAY = new Color(255, 0, 0, 120); // полупрозрачный красный

    private VisualComparator() {}

    /**
     * Сравнивает baseline и actual, сохраняет diff и прикрепляет все результаты в Allure.
     *
     * @param expectedPath путь к baseline-изображению
     * @param actualPath   путь к актуальному изображению
     * @param diffPath     путь, куда сохранить diff
     * @param browserName  имя браузера для логов
     * @param threshold    порог (%) допустимых расхождений
     * @return процент отличающихся пикселей (0..100)
     * @throws IOException если не удалось прочитать/записать файлы
     */
    public static double compareAndAttach(Path expectedPath,
                                          Path actualPath,
                                          Path diffPath,
                                          String browserName,
                                          double threshold) throws IOException {

        BufferedImage expected = ImageIO.read(expectedPath.toFile());
        BufferedImage actual = ImageIO.read(actualPath.toFile());

        int width = Math.min(expected.getWidth(), actual.getWidth());
        int height = Math.min(expected.getHeight(), actual.getHeight());

        BufferedImage diff = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        long diffPixels = 0L;
        float alpha = OVERLAY.getAlpha() / 255f;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgbBase = expected.getRGB(x, y);
                int rgbAct = actual.getRGB(x, y);
                if (rgbBase != rgbAct) {
                    Color base = new Color(rgbAct, true);
                    int r = (int) ((1 - alpha) * base.getRed() + alpha * OVERLAY.getRed());
                    int g = (int) ((1 - alpha) * base.getGreen() + alpha * OVERLAY.getGreen());
                    int b = (int) ((1 - alpha) * base.getBlue() + alpha * OVERLAY.getBlue());
                    int a = base.getAlpha();
                    int rgba = (a << 24) | (r << 16) | (g << 8) | b;
                    diff.setRGB(x, y, rgba);
                    diffPixels++;
                } else {
                    diff.setRGB(x, y, actual.getRGB(x, y));
                }
            }
        }

        // Сохраняем diff
        Files.createDirectories(diffPath.getParent());
        ImageIO.write(diff, "png", diffPath.toFile());

        double diffPercent = ((double) diffPixels / (width * height)) * 100.0;

        // Добавляем в Allure
        Allure.addAttachment("Expected (" + browserName + ")", "image/png",
                Files.newInputStream(expectedPath), ".png");
        Allure.addAttachment("Actual (" + browserName + ")", "image/png",
                Files.newInputStream(actualPath), ".png");
        Allure.addAttachment("Diff (" + browserName + ")", "image/png",
                Files.newInputStream(diffPath), ".png");

        String message = String.format(
                "[%s] Различия между expected и actual: %.2f%% (порог %.2f%%)\n Diff: %s",
                browserName, diffPercent, threshold, diffPath.toAbsolutePath()
        );

        if (diffPercent > threshold) {
            Allure.step("❌ " + message);
            System.err.println("❌ " + message);
            throw new AssertionError("❌ Верстка изменилась (" + browserName + "): " + diffPercent + "% отличий");
        } else {
            Allure.step("✅ " + message);
            System.out.println("✅ " + message);
        }

        return diffPercent;
    }
}
