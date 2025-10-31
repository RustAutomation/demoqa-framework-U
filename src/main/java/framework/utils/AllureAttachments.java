package framework.utils;

import io.qameta.allure.Allure;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class AllureAttachments {

    public static void attachText(String name, String text) {
        Allure.addAttachment(name, "text/plain", new ByteArrayInputStream(text.getBytes()), ".txt");
    }

    public static void attachJson(String name, String json) {
        Allure.addAttachment(name, "application/json", new ByteArrayInputStream(json.getBytes()), ".json");
    }

    public static void attachBytes(String name, byte[] bytes, String extension) {
        Allure.addAttachment(name, "application/octet-stream", new ByteArrayInputStream(bytes), extension);
    }

    public static void attachFile(String name, File file) throws IOException {
        Allure.addAttachment(name, new ByteArrayInputStream(Files.readAllBytes(file.toPath())));
    }
}
