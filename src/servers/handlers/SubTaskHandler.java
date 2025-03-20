package servers.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.OverLappingTimeException;
import managers.TaskManager;
import tasks.Epic;
import tasks.SubTask;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

public class SubTaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public SubTaskHandler(TaskManager taskManager) {
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
            Optional<SubTask> subTask = taskManager.getSubtaskById(id.get());
            if (subTask.isPresent()) {
                sendText(exchange, gson.toJson(subTask.get()));
            } else {
                sendNotFound(exchange, "Подзадача с id " + id.get() + " не найдена");
            }
        } else {
            sendText(exchange, gson.toJson(taskManager.getSubTasks(), taskCollectionToken));
        }
    }

    public void handleDeleteMethod(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getIdFromPath(exchange);
        if (id.isPresent()) {
            Optional<SubTask> subTask = taskManager.getSubtaskById(id.get());
            if (subTask.isPresent()) {
                taskManager.removeSubtaskById(id.get());
                sendText(exchange, "Подзадача " + id.get() + " успешно удалена");
            } else {
                sendNotFound(exchange, "Подзадача с id " + id.get() + " не найдена");
            }
        } else {
            sendNotFound(exchange, "возникла ошибка в процессе передачи id");
        }
    }

    public void handlePostMethod(HttpExchange exchange) throws IOException {
        String requestBody = getRequestBody(exchange);
        try {
            SubTask subTask = gson.fromJson(requestBody, SubTask.class);
            if (subTask.getId() == 0) {
                Optional<Epic> epic = taskManager.getEpicById(subTask.getEpicId());
                if (epic.isPresent()) {
                    taskManager.createNewSubTask(subTask);
                    exchange.sendResponseHeaders(201, 0);
                    System.out.println("создали новую Подзадачу");
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write("Подзадача успешно создана".getBytes());
                    }
                } else {
                    sendNotFound(exchange, "epic с id " + subTask.getEpicId() + " не найден");
                }
            } else {
                try {
                    taskManager.updateSubtask(subTask);
                } catch (IllegalArgumentException e) {
                    sendNotFound(exchange, "Подзадача с id " + subTask.getId() + " не найдена");
                }
                exchange.sendResponseHeaders(201, 0);
                System.out.println("Обновили подзадачу");
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write("Подзадача успешно обновлена".getBytes());
                }
            }
        } catch (OverLappingTimeException e) {
            sendHasInteractions(exchange, "Подзадача пересекается по времени");
        } catch (JsonSyntaxException e) {
            exchange.sendResponseHeaders(400, 0);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write("произошла ошибка при сериализации".getBytes());
            }
        }

    }
}
