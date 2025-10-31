package framework.api;

import framework.utils.AllureHelper;
import kong.unirest.*;

import java.util.Map;

public class ApiClient {

    private final UnirestInstance unirest = Unirest.spawnInstance();

    public HttpResponse<String> request(String method, String endpoint, Map<String, Object> params, String jsonBody, Map<String, String> headers) {
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
            ((HttpRequestWithBody) req).body(jsonBody).header("Content-Type", "application/json");
        }

        HttpResponse<String> resp = req.asString();

        AllureHelper.attachText("Response status", String.valueOf(resp.getStatus()));
        AllureHelper.attachJson("Response body", resp.getBody());
        return resp;
    }
}
