package framework.pages;

import com.microsoft.playwright.Page;
import framework.utils.AllureHelper;

import java.util.Random;

/**
 * Page Object для формы https://demoqa.com/automation-practice-form
 */
public class PracticeFormPage {

    private final Page page;
    private final Random rnd = new Random();

    public PracticeFormPage(Page page) {
        this.page = page;
    }

    public void open() {
        page.navigate("https://demoqa.com/automation-practice-form");
        AllureHelper.step("Открыта страница Practice Form");
        // удаляем возможные баннеры/iframes, мешающие кликам
        page.evaluate("document.querySelectorAll('#fixedban, .Advertisement, iframe').forEach(e => e.remove())");
    }

    public void fillFirstName(String firstName) {
        page.fill("#firstName", firstName);
        AllureHelper.step("Заполнено firstName");
    }

    public void fillLastName(String lastName) {
        page.fill("#lastName", lastName);
        AllureHelper.step("Заполнено lastName");
    }

    public void fillEmail(String email) {
        page.fill("#userEmail", email);
        AllureHelper.step("Заполнено email");
    }

    public void selectRandomGender() {
        int idx = rnd.nextInt(3) + 1; // 1..3
        String selector = "label[for='gender-radio-" + idx + "']";
        page.locator(selector).click();
        AllureHelper.step("Выбран пол: " + idx);
    }

    public void fillPhone(String phone) {
        page.fill("#userNumber", phone);
        AllureHelper.step("Заполнен phone");
    }

    public void fillRandomSubject() {
        String[] subjects = {"Maths", "English", "Physics", "Economics"};
        String subject = subjects[rnd.nextInt(subjects.length)];
        page.locator("#subjectsInput").fill(subject);
        page.waitForTimeout(300);
        page.keyboard().press("Enter");
        AllureHelper.step("Добавлен subject: " + subject);
    }

    public void selectRandomHobby() {
        int hobbyIndex = rnd.nextInt(3) + 1;
        page.locator("label[for='hobbies-checkbox-" + hobbyIndex + "']").click();
        AllureHelper.step("Выбран hobby: " + hobbyIndex);
    }

    public void fillAddress(String address) {
        page.fill("#currentAddress", address);
        AllureHelper.step("Заполнен address");
    }

    public void selectRandomStateAndCity() {
        page.click("#state");
        page.waitForSelector("#state .css-26l3qy-menu div");
        int stateIndex = rnd.nextInt(4);
        page.locator("#state .css-26l3qy-menu div").nth(stateIndex).click();
        AllureHelper.step("Выбран state index " + stateIndex);

        page.click("#city");
        page.waitForSelector("#city .css-26l3qy-menu div");
        int cityIndex = rnd.nextInt(4);
        page.waitForSelector("#city .css-26l3qy-menu div");
        page.locator("#city .css-26l3qy-menu div").nth(cityIndex).click();
        AllureHelper.step("Выбран city index " + cityIndex);
    }

    public void submit() {
        page.click("#submit");
        AllureHelper.step("Нажат submit");
    }

    public Page getPage() {
        return page;
    }
}
