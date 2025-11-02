package tests;

import com.microsoft.playwright.Page;
import framework.browser.BrowserManager;
import framework.pages.PracticeFormPage;
import framework.utils.AllureHelper;
import framework.utils.DataGenerator;
import framework.utils.VisualComparator;
import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PracticeFormTest {

    private static final Path EXPECTED_SCREENSHOT = Paths.get("src/test/resources/screenshot/expected/practice_form.png");
    private static final Path ACTUAL_SCREENSHOT = Paths.get("build/screenshots/actual_practice_form.png");
    private static final Path DIFF_SCREENSHOT = Paths.get("build/screenshots/diff_practice_form.png");

    @AfterAll
    static void tearDown() {
        BrowserManager.close();
    }

    @Test
    @Order(1)
    @DisplayName("UI — заполнение Practice Form и сравнение скриншота")
    void fillPracticeFormAndCompareScreenshot() throws Exception {
        Page page = BrowserManager.newPage();
        PracticeFormPage form = new PracticeFormPage(page);
        form.open();

        Map<String, String> data = DataGenerator.userData();
        String fullName = data.get("firstName") + " " + data.get("lastName");

        form.fillFirstName(data.get("firstName"));
        form.fillLastName(data.get("lastName"));
        form.fillEmail(data.get("email"));
        form.selectRandomGender();
        form.fillPhone(data.get("phone"));
        page.waitForTimeout(800);
        form.fillRandomSubject();
        form.selectRandomHobby();
        form.fillAddress(data.get("address"));
        form.selectRandomStateAndCity();

        Files.createDirectories(EXPECTED_SCREENSHOT.getParent());
        Files.createDirectories(ACTUAL_SCREENSHOT.getParent());

        byte[] actual = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
        Files.write(ACTUAL_SCREENSHOT, actual);

        AllureHelper.attachScreenshot(page, "Финальный скриншот", true);

        if (!Files.exists(EXPECTED_SCREENSHOT)) {
            Files.write(EXPECTED_SCREENSHOT, actual);
            AllureHelper.step("Создан expected скриншот");
        } else {
            double diffPercent = VisualComparator.compareAndHighlight(
                    EXPECTED_SCREENSHOT.toString(),
                    ACTUAL_SCREENSHOT.toString(),
                    DIFF_SCREENSHOT.toString()
            );

            AllureHelper.attachImage("Expected (ожидаемый)", EXPECTED_SCREENSHOT);
            AllureHelper.attachImage("Actual (текущий)", ACTUAL_SCREENSHOT);
            AllureHelper.attachImage("Diff — различия на скриншоте", DIFF_SCREENSHOT);

            AllureHelper.step(String.format("Различия между expected и actual: %.2f%%", diffPercent));

            Assertions.assertTrue(diffPercent <= 0.5, "Найдены различия: " + diffPercent + "%");
        }

        AllureHelper.step("Тест успешно завершён: " + fullName);
        page.close();
    }
}
