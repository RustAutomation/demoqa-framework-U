package tests;

import framework.api.ApiClient;
import framework.utils.AllureHelper;
import kong.unirest.HttpResponse;
import org.junit.jupiter.api.Test;
import framework.api.DTO.UserRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Пример API-тестов с использованием DTO и Allure-логирования.
 */
public class ApiExampleTest {

    private final ApiClient api = new ApiClient();
    private static final String BASE_URL = "https://reqres.in/api/users";

    @Test
    void getUser() {
        AllureHelper.step("Получение пользователя");
        HttpResponse<String> response = api.request("GET", BASE_URL + "/2", null, null, null);
        assertEquals(200, response.getStatus());
    }

    @Test
    void createUser() {
        AllureHelper.step("Создание пользователя через DTO");
        UserRequest body = UserRequest.builder()
                .name("Rustam")
                .job("QA Engineer")
                .build();

        HttpResponse<String> response = api.request("POST", BASE_URL, null, body, null);
        assertEquals(201, response.getStatus());
    }

    @Test
    void updateUser() {
        AllureHelper.step("Обновление пользователя через DTO");
        UserRequest body = UserRequest.builder()
                .name("Rustam")
                .job("Senior QA")
                .build();

        HttpResponse<String> response = api.request("PUT", BASE_URL + "/2", null, body, null);
        assertEquals(200, response.getStatus());
    }

    @Test
    void deleteUser() {
        AllureHelper.step("Удаление пользователя");
        HttpResponse<String> response = api.request("DELETE", BASE_URL + "/2", null, null, null);
        assertEquals(204, response.getStatus());
    }
}
