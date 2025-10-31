package framework.models;

import java.util.Map;
import java.util.HashMap;

public class UserRequest {
    private final String name;
    private final String job;

    public UserRequest(String name, String job) {
        this.name = name;
        this.job = job;
    }

    public Map<String, Object> toMap() {
        Map<String,Object> m = new HashMap<>();
        m.put("name", name);
        m.put("job", job);
        return m;
    }

    public String toJson() {
        // Простая формировка JSON (для примера). В проде — используйте Jackson/Gson.
        return String.format("{\"name\":\"%s\",\"job\":\"%s\"}", name, job);
    }
}
