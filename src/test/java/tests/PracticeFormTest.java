package tests;

import com.microsoft.playwright.*;
import framework.browser.BrowserManager;
import framework.pages.PracticeFormPage;
import framework.utils.*;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 🧪 UI-тест Practice Form с визуальным сравнением и Allure-отчетом.
 * Выполняется параллельно во всех доступных браузерах.
 */
@Epic("Visual Testing")
@Feature("Practice Form visual validation")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PracticeFormTest {

    private static Playwright playwright;
    private static List<String> availableBrowsers;

    private static final Path EXPECTED_DIR = Paths.get("src/test/resources/screenshot/expected");
    private static final Path ACTUAL_DIR = Paths.get("build/screenshots/actual");
    private static final Path DIFF_DIR = Paths.get("build/screenshots/diff");

    @BeforeAll
    static void setup() throws Exception {
        playwright = Playwright.create();
        availableBrowsers = BrowserManager.getAvailableBrowserNames();
        Files.createDirectories(EXPECTED_DIR);
        Files.createDirectories(ACTUAL_DIR);
        Files.createDirectories(DIFF_DIR);
    }

    @AfterAll
    static void tearDown() {
        if (playwright != null) playwright.close();
    }

    static Stream<String> params() {
        return availableBrowsers.stream();
    }

    @ParameterizedTest(name = "Practice Form — визуальная проверка ({0})")
    @MethodSource("params")
    @Order(1)
    @DisplayName("UI — заполнение Practice Form и визуальное сравнение во всех браузерах")
    void testPracticeFormInAllBrowsers(String browserName) {
        Allure.step("Запуск теста в браузере: " + browserName);
        try {
            runFormTest(browserName);
            Allure.step("Тест успешно завершён: " + browserName);
        } catch (Throwable e) {
            Allure.step("Ошибка в браузере " + browserName + ": " + e.getMessage());
            Assertions.fail("Ошибка в браузере " + browserName, e);
        }
    }

    /**
     * Основной сценарий — заполнение формы и сравнение верстки.
     */
    @Step("Проверка Practice Form в {browserName}")
    private void runFormTest(String browserName) throws Exception {
        BrowserContext context = BrowserManager.launchBrowser(playwright, browserName, false);
        Page page = context.newPage();

        PracticeFormPage form = new PracticeFormPage(page);
        form.open();

        // Очистка страницы от баннеров и футеров
        Tools.removeBanners(page);

        Map<String, String> data = DataGenerator.userData();
        String fullName = data.get("firstName") + " " + data.get("lastName");

        form.fillFirstName(data.get("firstName"));
        form.fillLastName(data.get("lastName"));
        form.fillEmail(data.get("email"));
        form.selectRandomGender();
        form.fillPhone(data.get("phone"));
        page.waitForTimeout(500);
        form.fillRandomSubject();
        form.selectRandomHobby();
        form.fillAddress(data.get("address"));
        form.selectRandomStateAndCity();
        form.submit();

        // Приводим страницу к единому виду перед сравнением
        Tools.preparePageForScreenshot(page);

        Path expectedPath = EXPECTED_DIR.resolve(browserName + "_practice_form.png");
        Path actualPath = ACTUAL_DIR.resolve(browserName + "_practice_form_actual.png");
        Path diffPath = DIFF_DIR.resolve(browserName + "_practice_form_diff.png");

        // Сохраняем актуальный скриншот
        byte[] actual = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
        Files.write(actualPath, actual);
        AllureHelper.attachScreenshot(page, "Финальный скриншот (" + browserName + ")", true);

        if (!Files.exists(expectedPath)) {
            Files.write(expectedPath, actual);
            Allure.step("📸 Создан baseline для " + browserName);
        } else {
            double diffPercent = VisualComparator.compareAndAttach(
                    expectedPath,
                    actualPath,
                    diffPath,
                    browserName,
                    5.5
            );

            String message = String.format(
                    "[%s] Различие верстки: %.2f%% (порог 5.5%%)",
                    browserName, diffPercent
            );

            if (diffPercent > 5.5) {
                String error = String.format(
                        "ВЕРСТКА ИЗМЕНИЛАСЬ (%s): %.2f%% отличий\n🖼 Diff: %s",
                        browserName, diffPercent, diffPath.toAbsolutePath()
                );
                Allure.step(error);
                System.err.println(error);
                Assertions.fail(error);
            } else {
                String ok = String.format(
                        "Верстка совпадает (%s): %.2f%% отличий",
                        browserName, diffPercent
                );
                Allure.step(ok);
                System.out.println(ok);
                assertTrue(true);
            }
        }

        context.close();
        Allure.step("Тест завершён для пользователя: " + fullName + " (" + browserName + ")");
    }
}
