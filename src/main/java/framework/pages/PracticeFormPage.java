package framework.pages;

import com.microsoft.playwright.Page;
import framework.utils.AllureHelper;

import java.util.Random;

/**
 * Page Object для страницы https://demoqa.com/automation-practice-form
 */
public class PracticeFormPage {

    private final Page page;

    public PracticeFormPage(Page page) {
        this.page = page;
    }

    public void open() {
        page.navigate("https://demoqa.com/automation-practice-form");
        AllureHelper.step("Открыта страница Practice Form");

        // Удаляем баннеры и рекламу
        page.evaluate("document.querySelectorAll('#fixedban, .Advertisement, iframe').forEach(e => e.remove())");
    }

    public void fillFirstName(String firstName) {
        page.fill("#firstName", firstName);
    }

    public void fillLastName(String lastName) {
        page.fill("#lastName", lastName);
    }

    public void fillEmail(String email) {
        page.fill("#userEmail", email);
    }

    public void selectRandomGender() {
        String[] genders = {"Male", "Female", "Other"};
        int genderIndex = new Random().nextInt(genders.length);
        page.locator("label[for='gender-radio-" + (genderIndex + 1) + "']").click();
    }

    public void fillPhone(String phone) {
        page.fill("#userNumber", phone);
    }

    public void fillRandomSubject() {
        String[] subjects = {"Maths", "English", "Physics", "Economics"};
        String subject = subjects[new Random().nextInt(subjects.length)];
        page.locator("#subjectsInput").fill(subject);
        page.waitForTimeout(500);
        page.keyboard().press("Enter");
    }

    public void selectRandomHobby() {
        int randomHobbyIndex = new Random().nextInt(3) + 1;
        page.locator("label[for='hobbies-checkbox-" + randomHobbyIndex + "']").click();
    }

    public void fillAddress(String address) {
        page.fill("#currentAddress", address);
    }

    public void selectRandomStateAndCity() {
        page.click("#state");
        page.locator("#state .css-26l3qy-menu div").nth(new Random().nextInt(4)).click();

        page.click("#city");
        page.waitForSelector("#city .css-26l3qy-menu div");
        page.locator("#city .css-26l3qy-menu div").nth(new Random().nextInt(4)).click();
        page.waitForTimeout(500);
        page.keyboard().press("Enter");
    }

    public void submit() {
        page.click("#submit");
        AllureHelper.step("Форма отправлена");
    }

    public void close() {
        page.click("#close");
        AllureHelper.step("Форма отправлена");
    }

    public Page getPage() {
        return page;
    }
}
