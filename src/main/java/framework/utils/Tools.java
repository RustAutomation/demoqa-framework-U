package framework.utils;

import com.microsoft.playwright.Page;

/**
 * Утилиты для стабилизации страницы перед снятием скриншота.
 * Помогает минимизировать ложные расхождения из-за фокуса, скролла или баннеров.
 */
public class Tools {

    private static final String REMOVE_BANNERS_SCRIPT =
            "() => {" +
                    "  const selectors = ['#fixedban', '.footer', 'footer', '#close-fixedban'];" +
                    "  selectors.forEach(sel => {" +
                    "    const el = document.querySelector(sel);" +
                    "    if (el) el.remove();" +
                    "  });" +
                    "}";

    private static final String RESET_PAGE_STATE_SCRIPT =
            "() => {" +
                    "  window.scrollTo(0, 0);" +
                    "  if (document.activeElement && typeof document.activeElement.blur === 'function') {" +
                    "    document.activeElement.blur();" +
                    "  }" +
                    "  document.body.style.pointerEvents = 'none';" +
                    "  setTimeout(() => document.body.style.pointerEvents = '', 100);" +
                    "}";

    /**
     * Полная подготовка страницы к снятию скриншота:
     * - удаляет баннеры и футеры
     * - сбрасывает фокус
     * - скроллит вверх
     * - ждёт завершения анимаций
     */
    public static void preparePageForScreenshot(Page page) {
        removeBanners(page);
        page.evaluate(RESET_PAGE_STATE_SCRIPT);
        page.waitForTimeout(300);
    }

    /**
     * Удаляет баннеры, футеры и рекламные блоки со страницы.
     */
    public static void removeBanners(Page page) {
        page.evaluate(REMOVE_BANNERS_SCRIPT);
    }

    private Tools() {
        // запрет на создание экземпляров
    }
}
