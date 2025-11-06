package framework.browser;

import com.microsoft.playwright.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Менеджер Playwright-браузеров.
 * Определяет доступные браузеры и управляет их запуском/закрытием.
 */
public class BrowserManager {

    private static final List<String> SUPPORTED_BROWSERS = List.of("chromium", "firefox", "webkit");

    /**
     * Возвращает список доступных браузеров по именам.
     */
    public static List<String> getAvailableBrowserNames() {
        return new ArrayList<>(SUPPORTED_BROWSERS);
    }

    /**
     * Запускает браузер с нужными настройками.
     */
    public static BrowserContext launchBrowser(Playwright playwright, String browserName, boolean headless) {
        BrowserType browserType;
        switch (browserName) {
            case "chromium":
                browserType = playwright.chromium();
                break;
            case "firefox":
                browserType = playwright.firefox();
                break;
            case "webkit":
                browserType = playwright.webkit();
                break;
            default:
                throw new IllegalArgumentException("Неизвестный браузер: " + browserName);
        }

        Browser browser = browserType.launch(new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setSlowMo(50));

        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1920, 1080)
                .setRecordVideoDir(Paths.get("videos/" + browserName))
                .setIgnoreHTTPSErrors(true));

        System.out.println("Запущен браузер: " + browserName);
        return context;
    }

    /**
     * Закрывает браузер корректно.
     */
    public static void closeAll(BrowserContext context) {
        if (context != null) {
            context.close();
            System.out.println("Контекст браузера закрыт.");
        }
    }
}
