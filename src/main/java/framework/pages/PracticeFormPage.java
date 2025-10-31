package framework.pages;

import com.microsoft.playwright.Page;
import framework.utils.AllureHelper;

import java.util.List;
import java.util.Map;

public class PracticeFormPage {
    private final Page page;
    private final String URL = "https://demoqa.com/automation-practice-form";

    public PracticeFormPage(Page page) {
        this.page = page;
    }

    public PracticeFormPage open() {
        AllureHelper.step("Open practice form page");
        page.navigate(URL, new Page.NavigateOptions().setTimeout(60000));
        // hide fixed banner if present
        page.evaluate("() => { const el = document.querySelector('#fixedban'); if(el) el.remove(); }");
        return this;
    }

    public PracticeFormPage fillRequiredFields(Map<String, String> data) {
        AllureHelper.step("Fill required fields");
        page.fill("#firstName", data.get("firstName"));
        page.fill("#lastName", data.get("lastName"));
        page.fill("#userEmail", data.get("email"));

        // choose random visible gender
        List<String> labels = page.locator("label[for^='gender-radio']").allInnerTexts();
        if (!labels.isEmpty()) {
            // click first label (for simplicity)
            page.click("label[for='gender-radio-1']");
        }
        page.fill("#userNumber", data.get("phone"));
        return this;
    }

    public PracticeFormPage submit() {
        AllureHelper.step("Submit form");
        page.locator("#submit").scrollIntoViewIfNeeded();
        page.click("#submit");
        return this;
    }

    public java.util.Map<String,String> readResult() {
        page.waitForSelector(".modal-content", new Page.WaitForSelectorOptions().setTimeout(5000));
        java.util.Map<String,String> result = new java.util.HashMap<>();
        for (var row : page.querySelectorAll(".modal-content tbody tr")) {
            var cols = row.querySelectorAll("td");
            if (cols.size() >= 2) {
                result.put(cols.get(0).innerText().trim(), cols.get(1).innerText().trim());
            }
        }
        return result;
    }
}
