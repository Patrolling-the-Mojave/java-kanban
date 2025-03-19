package servers.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import tasks.Epic;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                getHandler(exchange);
                break;
            case "DELETE":
                deleteHandler(exchange);
                break;
            case "POST":
                createHandler(exchange);
                break;
        }
    }

    private void getHandler(HttpExchange exchange) throws IOException {
        Optional<Integer> id;
        try {
            id = getIdFromPath(exchange);
        } catch (NumberFormatException ex) {
            sendNotFound(exchange, "передан некорректный id");
            id = Optional.empty();
        }
        if (id.isPresent()) {
            Optional<Epic> epic = taskManager.getEpicById(id.get());
            if (epic.isPresent()) {
                sendText(exchange, gson.toJson(epic.get()));
            } else {
                sendNotFound(exchange, "Эпик с id " + id.get() + " не найдена");
            }
        } else {
            sendText(exchange, gson.toJson(taskManager.getEpics(), taskCollectionToken));
        }
    }

    public void deleteHandler(HttpExchange exchange) throws IOException {
        Optional<Integer> id;
        try {
            id = getIdFromPath(exchange);
        } catch (NumberFormatException ex) {
            sendNotFound(exchange, "передан некорректный id");
            id = Optional.empty();
        }
        if (id.isPresent()) {
            Optional<Epic> epic = taskManager.getEpicById(id.get());
            if (epic.isPresent()) {
                taskManager.removeEpicById(id.get());
                sendText(exchange, "Эпик " + id.get() + " успешно удален");
            } else {
                sendNotFound(exchange, "Эпик с id " + id.get() + " не найден");
            }
        } else {
            sendNotFound(exchange, "возникла ошибка в процессе передачи id");
        }
    }

    public void createHandler(HttpExchange exchange) throws IOException {
        String requestBody = getRequestBody(exchange);
        try {
            Epic epic = gson.fromJson(requestBody, Epic.class);
            if (epic.getId() == 0) {
                taskManager.createNewEpic(epic);
                exchange.sendResponseHeaders(201, 0);
                System.out.println("создали новый эпик");
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write("эпик успешно создан".getBytes());
                }
            } else {
                taskManager.updateEpic(epic);
                exchange.sendResponseHeaders(201, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write("эпик успешно обновлен".getBytes());
                }
            }
        } catch (JsonSyntaxException e) {
            exchange.sendResponseHeaders(400, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write("произошла ошибка при сериализации".getBytes());
            }
        }

    }
}
