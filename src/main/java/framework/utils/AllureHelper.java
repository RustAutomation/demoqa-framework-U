package framework.utils;

import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

/**
 * Небольшая обёртка для Allure вложений и шагов.
 */
public final class AllureHelper {

    private AllureHelper() {}

    public static void step(String message) {
        Allure.step(message);
    }

    public static void attachJson(String name, String json) {
        if (json == null) json = "";
        Allure.addAttachment(name, "application/json",
                new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), ".json");
    }

    public static void attachText(String name, String text) {
        if (text == null) text = "";
        Allure.addAttachment(name, "text/plain",
                new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)), ".txt");
    }

    public static void attachScreenshot(Page page, String name, boolean fullPage) {
        byte[] bytes = page.screenshot(new Page.ScreenshotOptions().setFullPage(fullPage));
        Allure.addAttachment(name, new ByteArrayInputStream(bytes));
    }

    public static void attachImage(String name, Path path) {
        try {
            Allure.addAttachment(name, new ByteArrayInputStream(Files.readAllBytes(path)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to attach image: " + path, e);
        }
    }
}
