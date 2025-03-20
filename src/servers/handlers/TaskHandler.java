package servers.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.OverLappingTimeException;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET":
                handleGetMethod(exchange);
                break;
            case "DELETE":
                handleDeleteMethod(exchange);
                break;
            case "POST":
                handlePostMethod(exchange);
                break;
        }
    }

    private void handleGetMethod(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getIdFromPath(exchange);
        if (id.isPresent()) {
            Optional<Task> task = taskManager.getTaskById(id.get());
            if (task.isPresent()) {
                sendText(exchange, gson.toJson(task.get()));
            } else {
                sendNotFound(exchange, "Задача с id " + id.get() + " не найдена");
            }
        } else {
            sendText(exchange, gson.toJson(taskManager.getTasks(), taskCollectionToken));
        }
    }

    public void handleDeleteMethod(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getIdFromPath(exchange);
        if (id.isPresent()) {
            Optional<Task> task = taskManager.getTaskById(id.get());
            if (task.isPresent()) {
                taskManager.removeTaskById(id.get());
                sendText(exchange, "задача " + id.get() + " успешно удалена");
            } else {
                sendNotFound(exchange, "Задача с id " + id.get() + " не найдена");
            }
        } else {
            sendNotFound(exchange, "возникла ошибка в процессе передачи id");
        }
    }

    public void handlePostMethod(HttpExchange exchange) throws IOException {
        String requestBody = getRequestBody(exchange);
        try {
            Task task = gson.fromJson(requestBody, Task.class);
            if (task.getId() == 0) {
                taskManager.createNewTask(task);
                exchange.sendResponseHeaders(201, 0);
                System.out.println("создали новую задачу");
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write("задача успешно создана".getBytes());
                }
            } else {
                taskManager.updateTask(task);
                exchange.sendResponseHeaders(201, 0);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write("задача успешно обновлена".getBytes());
                }
            }
        } catch (OverLappingTimeException e) {
            sendHasInteractions(exchange, "задача пересекается по времени");
        } catch (JsonSyntaxException e) {
            exchange.sendResponseHeaders(400, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write("произошла ошибка при сериализации".getBytes());
            }
        }

    }
}

