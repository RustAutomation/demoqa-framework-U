package tests;

import com.microsoft.playwright.Page;
import framework.browser.BrowserManager;
import framework.utils.AllureHelper;
import framework.utils.VisualComparator;
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Epic("Visual Tests")
@Feature("DemoQA Visual Diff")
public class VisualTest {

    @Test
    @Story("Сравнение главной страницы DemoQA с эталоном")
    void testVisualComparison() throws IOException {
        File outDir = new File("build/screenshots");
        if (!outDir.exists() && !outDir.mkdirs())
            throw new IOException("Cannot create dir: " + outDir.getAbsolutePath());

        File baselineFile = new File("src/test/resources/screenshot/expected/homepagebaseline.png");
        File baselineDir = baselineFile.getParentFile();
        if (!baselineDir.exists() && !baselineDir.mkdirs()) {
            throw new IOException("Cannot create dir: " + baselineDir.getAbsolutePath());
        }

        String actual = "build/screenshots/homepageactual.png";
        String diff = "build/screenshots/homepagediff.png";

        Page page = BrowserManager.newPage();
        page.navigate("https://demoqa.com");
        // Удаляем баннеры и рекламу
        page.evaluate("document.querySelectorAll('#fixedban, .Advertisement, iframe').forEach(e => e.remove())");

        page.screenshot(new Page.ScreenshotOptions().setPath(new File(actual).toPath()).setFullPage(true));

        if (!baselineFile.exists()) {
            ImageIO.write(ImageIO.read(new File(actual)), "png", baselineFile);
            AllureHelper.step("Создан baseline для главной страницы");
            return;
        }

        // Вызов сравнения
        double diffPercent = VisualComparator.compareAndHighlight(
                baselineFile.getPath(), actual, diff
        );

        // 📎 Прикладываем все три изображения в Allure
        AllureHelper.attachImage("Baseline (ожидаемый)", baselineFile.toPath());
        AllureHelper.attachImage("Actual (текущий)", Path.of(actual));
        AllureHelper.attachImage("Diff — различия на скриншоте", Path.of(diff));

        // Логируем процент различий
        AllureHelper.step(String.format("Процент различий: %.2f%%", diffPercent));

        // Если есть видимые различия — падаем
        if (diffPercent > 5.5) {
            throw new AssertionError("Найдены различия: " + diffPercent + "%");
        }

        page.close();
    }
}
