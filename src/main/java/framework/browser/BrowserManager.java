package framework.browser;

import com.microsoft.playwright.*;

public class BrowserManager {
    private static Playwright playwright;
    private static Browser browser;

    private BrowserManager() {
    }

    public static Browser getBrowser() {
        if (browser == null) {
            playwright = Playwright.create();
            browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(false)
            );
        }
        return browser;
    }

    public static Page newPage() {
        BrowserContext context = getBrowser().newContext();
        return context.newPage();
    }

    public static void close() {
        if (browser != null) {
            browser.close();
            playwright.close();
            browser = null;
            playwright = null;
        }
    }
}
