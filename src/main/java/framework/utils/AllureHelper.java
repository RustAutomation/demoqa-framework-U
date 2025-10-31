package framework.utils;

import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Утилита для логирования шагов, вложений и визуальных сравнений в Allure.
 */
public class AllureHelper {

    /** Логирует шаг в Allure */
    public static void step(String message) {
        Allure.step(message);
    }

    /** Добавляет JSON как вложение */
    public static void attachJson(String name, String json) {
        Allure.addAttachment(name, "application/json",
                new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), ".json");
    }

    /** Добавляет текст как вложение */
    public static void attachText(String name, String text) {
        Allure.addAttachment(name, "text/plain",
                new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)), ".txt");
    }

    /** Прикрепляет скриншот страницы */
    public static void attachScreenshot(Page page, String name, boolean fullPage) {
        byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(fullPage));
        Allure.addAttachment(name, new ByteArrayInputStream(screenshot));
    }

    /** Прикрепляет полный скриншот страницы */
    public static void attachFullPageScreenshot(Page page, String name) {
        attachScreenshot(page, name, true);
    }

    /** Сравнивает текущий скриншот страницы с эталонным */
    public static void compareWithExpected(Page page, String name) {
        byte[] actualScreenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
        byte[] expectedScreenshot = ImageStorage.getExpectedScreenshot(name);

        boolean result = compareScreenshots(expectedScreenshot, actualScreenshot);
        if (!result) {
            Allure.addAttachment(name + " - mismatch", new ByteArrayInputStream(actualScreenshot));
            throw new AssertionError("Screenshots do not match for: " + name);
        }
    }

    /** Побайтное сравнение двух массивов скриншотов */
    public static boolean compareScreenshots(byte[] expected, byte[] actual) {
        try {
            BufferedImage img1 = ImageIO.read(new ByteArrayInputStream(expected));
            BufferedImage img2 = ImageIO.read(new ByteArrayInputStream(actual));

            if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
                return false;
            }

            for (int y = 0; y < img1.getHeight(); y++) {
                for (int x = 0; x < img1.getWidth(); x++) {
                    if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
                        return false;
                    }
                }
            }
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Error comparing screenshots", e);
        }
    }

    /**
     * 🔥 Перегруженный метод — сравнение скриншотов по путям.
     * Прикладывает оба изображения в Allure и пишет результат сравнения.
     *
     * @param expectedPath путь к эталонному скрину
     * @param actualPath   путь к текущему скрину
     * @param title        заголовок шага/вложения
     */
    public static void compareScreenshots(Path expectedPath, Path actualPath, String title) {
        try {
            byte[] expected = Files.readAllBytes(expectedPath);
            byte[] actual = Files.readAllBytes(actualPath);

            boolean result = compareScreenshots(expected, actual);

            Allure.addAttachment("Expected - " + title, new ByteArrayInputStream(expected));
            Allure.addAttachment("Actual - " + title, new ByteArrayInputStream(actual));

            if (!result) {
                throw new AssertionError("Screenshots differ: " + title);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading screenshots from path", e);
        }
    }

    public static void attachImage(String name, Path imagePath) {
        try {
            Allure.addAttachment(name, new ByteArrayInputStream(Files.readAllBytes(imagePath)));
        } catch (IOException e) {
            throw new RuntimeException("Error attaching image: " + imagePath, e);
        }
    }

}
