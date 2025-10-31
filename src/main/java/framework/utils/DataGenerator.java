package framework.utils;

import com.github.javafaker.Faker;
import java.util.HashMap;
import java.util.Map;

public class DataGenerator {
    private static final Faker faker = new Faker();

    public static Map<String, String> userData() {
        Map<String, String> data = new HashMap<>();
        data.put("firstName", faker.name().firstName());
        data.put("lastName", faker.name().lastName());
        data.put("email", faker.internet().emailAddress());
        data.put("address", faker.address().fullAddress());
        data.put("phone", faker.phoneNumber().subscriberNumber(10));
        return data;
    }
}
