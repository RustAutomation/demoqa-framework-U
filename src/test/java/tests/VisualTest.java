package tests;

import com.microsoft.playwright.*;
import framework.browser.BrowserManager;
import framework.utils.Tools;
import framework.utils.VisualComparator;
import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.nio.file.*;
import java.util.stream.Stream;

/**
 * Параллельные визуальные тесты Playwright с Allure и единым сравнением верстки.
 */
@Epic("Visual Testing")
@Feature("Cross-browser layout comparison")
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class VisualTest {

    private static Playwright playwright;

    private static final Path EXPECTED_DIR = Paths.get("src/test/resources/screenshot/expected");
    private static final Path ACTUAL_DIR = Paths.get("build/screenshots/actual");
    private static final Path DIFF_DIR = Paths.get("build/screenshots/diff");

    @BeforeAll
    static void setUp() throws Exception {
        playwright = Playwright.create();
        Files.createDirectories(EXPECTED_DIR);
        Files.createDirectories(ACTUAL_DIR);
        Files.createDirectories(DIFF_DIR);
    }

    static Stream<String> browserProvider() {
        return BrowserManager.getAvailableBrowserNames().stream();
    }

    @ParameterizedTest(name = "Визуальное сравнение demoqa.com ({0})")
    @MethodSource("browserProvider")
    void visualLayoutTest(String browserName) throws Exception {
        BrowserContext context = BrowserManager.launchBrowser(playwright, browserName, false);
        Page page = context.newPage();

        page.navigate("https://demoqa.com");
        page.waitForLoadState();

        Path expected = EXPECTED_DIR.resolve("demoqa_" + browserName + ".png");
        Path actual = ACTUAL_DIR.resolve("demoqa_actual_" + browserName + ".png");
        Path diff = DIFF_DIR.resolve("demoqa_diff_" + browserName + ".png");

        // Очистка страницы от баннеров и футеров
        Tools.removeBanners(page);

        // Приводим страницу к единому виду перед сравнением
        Tools.preparePageForScreenshot(page);

        byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
        Files.write(actual, screenshot);

        Allure.addAttachment("Actual Screenshot (" + browserName + ")", "image/png",
                new ByteArrayInputStream(screenshot), ".png");

        if (!Files.exists(expected)) {
            Files.write(expected, screenshot);
            Allure.step("Создан baseline для " + browserName);
        } else {
            // Используем общий метод для проверки
            VisualComparator.compareAndAttach(expected, actual, diff, browserName, 1.0);
        }

        context.close();
        Allure.step("Проверка завершена для браузера: " + browserName);
    }

    @AfterAll
    static void tearDown() {
        if (playwright != null) playwright.close();
    }
}
