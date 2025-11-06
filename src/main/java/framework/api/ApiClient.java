package framework.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import framework.utils.AllureHelper;
import kong.unirest.*;

import java.util.Map;

/**
 * Универсальный API-клиент с поддержкой DTO (автоматическая сериализация через Jackson).
 */
public class ApiClient {

    private final UnirestInstance unirest = Unirest.spawnInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Базовый метод: принимает JSON-строку.
     */
    public HttpResponse<String> request(
            String method,
            String endpoint,
            Map<String, Object> params,
            String jsonBody,
            Map<String, String> headers
    ) {
        AllureHelper.step("API Request: " + method + " " + endpoint);
        if (jsonBody != null) {
            AllureHelper.attachJson("Request body", jsonBody);
        }

        HttpRequest req;
        switch (method.toUpperCase()) {
            case "POST":
                req = unirest.post(endpoint);
                break;
            case "PUT":
                req = unirest.put(endpoint);
                break;
            case "DELETE":
                req = unirest.delete(endpoint);
                break;
            case "GET":
                req = unirest.get(endpoint);
                break;
            default:
                throw new IllegalArgumentException("Unsupported method: " + method);
        }

        if (headers != null) headers.forEach(req::header);
        if (params != null) req.queryString(params);
        if (req instanceof HttpRequestWithBody && jsonBody != null) {
            ((HttpRequestWithBody) req)
                    .body(jsonBody)
                    .header("Content-Type", "application/json");
        }

        HttpResponse<String> resp = req.asString();
        AllureHelper.attachText("Response status", String.valueOf(resp.getStatus()));
        AllureHelper.attachJson("Response body", resp.getBody());
        return resp;
    }

    /**
     * 🔥 Перегруженный метод: принимает DTO и сам конвертирует его в JSON.
     */
    public HttpResponse<String> request(
            String method,
            String endpoint,
            Map<String, Object> params,
            Object bodyDto,
            Map<String, String> headers
    ) {
        try {
            String jsonBody = null;
            if (bodyDto != null) {
                jsonBody = objectMapper.writeValueAsString(bodyDto);
            }
            return request(method, endpoint, params, jsonBody, headers);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сериализации DTO: " + e.getMessage(), e);
        }
    }
}
