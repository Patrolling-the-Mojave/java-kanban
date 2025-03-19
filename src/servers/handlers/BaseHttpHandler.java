package servers.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import lombok.Getter;
import tasks.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


public class BaseHttpHandler {
    protected final TypeAdapter<LocalDateTime> ldtAdapter = new TypeAdapter<LocalDateTime>() {
        @Override
        public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
            jsonWriter.value(localDateTime != null ? localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
        }

        @Override
        public LocalDateTime read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            }
            return LocalDateTime.parse(jsonReader.nextString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    };

    protected final TypeAdapter<Duration> durationAdapter = new TypeAdapter<Duration>() {
        @Override
        public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
            jsonWriter.value(duration != null ? duration.toMinutes() : null);
        }

        @Override
        public Duration read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            }
            return Duration.ofMinutes(jsonReader.nextInt());
        }
    };

    @Getter
    protected final Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, ldtAdapter)
            .registerTypeAdapter(Duration.class, durationAdapter)
            .create();

    protected final Type taskCollectionToken = new TypeToken<List<Task>>() {
    }.getType();


    protected void sendText(HttpExchange exchange, String text) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, 0);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(text.getBytes());
        }
    }

    protected void sendNotFound(HttpExchange exchange, String text) throws IOException {
        exchange.sendResponseHeaders(404, 0);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(text.getBytes());
        }
    }

    protected void sendHasInteractions(HttpExchange exchange, String text) throws IOException {
        exchange.sendResponseHeaders(406, 0);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(text.getBytes());
        }
    }

    protected Optional<Integer> getIdFromPath(HttpExchange exchange) throws NumberFormatException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        if (("subtasks".equals(pathParts[1]) || "tasks".equals(pathParts[1]) || "epics".equals(pathParts[1])) && pathParts.length == 3) {
            return Optional.of(Integer.parseInt(pathParts[2]));
        }
        return Optional.empty();
    }

    protected String getRequestBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }


}
