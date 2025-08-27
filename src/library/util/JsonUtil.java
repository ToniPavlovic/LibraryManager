package library.util;

import com.google.gson.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonUtil {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(java.time.LocalDate.class,
                    (JsonSerializer<java.time.LocalDate>) (src, _, _) ->
                    new JsonPrimitive(src.toString()))
            .registerTypeAdapter(java.time.LocalDate.class,
                    (JsonDeserializer<java.time.LocalDate>) (json, _, _) ->
                    java.time.LocalDate.parse(json.getAsString()))
            .create();

    public static <T> void saveToFile(String fileName, T data) {
        try (FileWriter writer = new FileWriter(fileName)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            throw new RuntimeException("Error saving file: " + e.getMessage());
        }
    }

    public static <T> T loadFromFile(String fileName, Class<T> clazz){
        try {
            String content = new String(Files.readAllBytes(Paths.get(fileName)));
            return gson.fromJson(content, clazz);
        } catch (IOException e){
            throw new RuntimeException("Error loading file: " + e.getMessage());
        }
    }
}
