package framework.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImageStorage {

    private static final String EXPECTED_DIR = "src/test/resources/screenshots/expected/";

    public static byte[] getExpectedScreenshot(String name) {
        try {
            Path path = Path.of(EXPECTED_DIR + name + ".png");
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException("Expected screenshot not found for: " + name, e);
        }
    }
}
