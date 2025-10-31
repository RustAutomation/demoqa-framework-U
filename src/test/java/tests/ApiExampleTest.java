package tests;

import framework.api.ApiClient;
import framework.utils.AllureHelper;
import kong.unirest.HttpResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApiExampleTest {

    private final ApiClient api = new ApiClient();

    @Test
    void getUser() {
        AllureHelper.step("Получение пользователя");
        HttpResponse<String> response = api.request("GET", "https://reqres.in/api/users/2", null, null, null);
        assertEquals(200, response.getStatus());
    }

    @Test
    void createUser() {
        AllureHelper.step("Создание пользователя");
        String body = "{\"name\": \"Rustam\", \"job\": \"QA\"}";
        HttpResponse<String> response = api.request("POST", "https://reqres.in/api/users", null, body, null);
        assertEquals(201, response.getStatus());
    }

    @Test
    void updateUser() {
        AllureHelper.step("Обновление пользователя");
        String body = "{\"name\": \"Rustam\", \"job\": \"Senior QA\"}";
        HttpResponse<String> response = api.request("PUT", "https://reqres.in/api/users/2", null, body, null);
        assertEquals(200, response.getStatus());
    }

    @Test
    void deleteUser() {
        AllureHelper.step("Удаление пользователя");
        HttpResponse<String> response = api.request("DELETE", "https://reqres.in/api/users/2", null, null, null);
        assertEquals(204, response.getStatus());
    }
}
