package tests;

import com.microsoft.playwright.Page;
import framework.browser.BrowserManager;
import framework.utils.AllureHelper;
import framework.utils.DataGenerator;
import framework.utils.VisualComparator;
import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Random;

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
        page.navigate("https://demoqa.com/automation-practice-form");
        AllureHelper.step("Открыта страница Practice Form");

        // Удаляем рекламу
        page.evaluate("document.querySelectorAll('#fixedban, .Advertisement, iframe').forEach(e => e.remove())");

        Map<String, String> data = DataGenerator.userData();
        String fullName = data.get("firstName") + " " + data.get("lastName");

        page.fill("#firstName", data.get("firstName"));
        page.fill("#lastName", data.get("lastName"));
        page.fill("#userEmail", data.get("email"));

        // Пол
        String[] genders = {"Male", "Female", "Other"};
        int genderIndex = new Random().nextInt(genders.length);
        page.locator("label[for='gender-radio-" + (genderIndex + 1) + "']").click();

        page.fill("#userNumber", data.get("phone"));

        // Предмет
        String[] subjects = {"Maths", "English", "Physics", "Economics"};
        String subject = subjects[new Random().nextInt(subjects.length)];
        page.locator("#subjectsInput").fill(subject);
        page.waitForTimeout(500);
        page.keyboard().press("Enter");

        // Хобби
        int randomHobbyIndex = new Random().nextInt(3) + 1;
        page.locator("label[for='hobbies-checkbox-" + randomHobbyIndex + "']").click();

        // Адрес
        page.fill("#currentAddress", data.get("address"));

        // State / City
        page.click("#state");
        page.locator("#state .css-26l3qy-menu div").nth(new Random().nextInt(4)).click();

        page.click("#city");
        page.waitForSelector("#city .css-26l3qy-menu div");
        page.locator("#city .css-26l3qy-menu div").nth(new Random().nextInt(4)).click();

        page.click("#submit");

        // Делаем скриншоты
        Files.createDirectories(EXPECTED_SCREENSHOT.getParent());
        Files.createDirectories(ACTUAL_SCREENSHOT.getParent());

        byte[] actual = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
        Files.write(ACTUAL_SCREENSHOT, actual);

        AllureHelper.attachScreenshot(page, "Финальный скриншот", true);

        if (!Files.exists(EXPECTED_SCREENSHOT)) {
            Files.write(EXPECTED_SCREENSHOT, actual);
            AllureHelper.step("Создан expected скриншот");
        } else {
            // 🔥 Сравнение с подсветкой различий
            double diffPercent = VisualComparator.compareAndHighlight(
                    EXPECTED_SCREENSHOT.toString(),
                    ACTUAL_SCREENSHOT.toString(),
                    DIFF_SCREENSHOT.toString()
            );

            // 📎 Прикладываем все 3 изображения в Allure
            AllureHelper.attachImage("Expected (ожидаемый)", EXPECTED_SCREENSHOT);
            AllureHelper.attachImage("Actual (текущий)", ACTUAL_SCREENSHOT);
            AllureHelper.attachImage("Diff — различия на скриншоте", DIFF_SCREENSHOT);

            // 💬 Логируем результат
            AllureHelper.step(String.format("Различия между expected и actual: %.2f%%", diffPercent));

            if (diffPercent > 0.5) {
                throw new AssertionError("Найдены различия: " + diffPercent + "%");
            }
        }

        AllureHelper.step("Тест успешно завершён: " + fullName);
        page.close();
    }
}
