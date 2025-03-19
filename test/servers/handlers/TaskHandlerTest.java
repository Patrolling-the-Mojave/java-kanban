package servers.handlers;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import managers.InMemoryTaskManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servers.HttpTaskServer;
import tasks.Status;
import tasks.Task;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TaskHandlerTest {
    static final Gson gson = new BaseHttpHandler().getGson();
    static final URI uri = URI.create("http://localhost:8080/tasks");
    TaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
    HttpTaskServer taskServer;

    @SneakyThrows
    @BeforeEach
    void startServer() {
        taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
        taskServer = new HttpTaskServer(taskManager);
        taskServer.start();
    }

    @AfterEach
    void stopServer() {
        taskServer.stop();
    }

    @SneakyThrows
    @Test
    void getTasks_getAllTasks() {
        Task task1 = new Task("n", "d", Status.NEW, 10, "2025-01-23T23:20:21.413486");
        taskManager.createNewTask(task1);
        Task task2 = new Task("n", "d", Status.NEW);
        taskManager.createNewTask(task2);

        String tasks = gson.toJson(taskManager.getTasks());

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(tasks, response.body());
    }

    @SneakyThrows
    @Test
    void getTask_getTaskByPathId() {
        Task task1 = new Task("n", "d", Status.NEW, 10, "2025-01-23T23:20:21.413486");
        taskManager.createNewTask(task1);
        Task task2 = new Task("n", "d", Status.NEW);
        taskManager.createNewTask(task2);

        String task = gson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(uri.toString() + "/1")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(task, response.body());

    }

    @Test
    @SneakyThrows
    void deleteTask_deleteTaskByPathId() {
        Task task1 = new Task("n", "d", Status.NEW, 10, "2025-01-23T23:20:21.413486");
        taskManager.createNewTask(task1);
        Task task2 = new Task("n", "d", Status.NEW);
        taskManager.createNewTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(URI.create(uri.toString() + "/2")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(1, taskManager.getTasks().size());
    }

    @Test
    @SneakyThrows
    void createTask_createRequestBodyTask() {
        Task task1 = new Task("n", "d", Status.NEW, 10, "2025-01-23T23:20:21.413486");
        String requestBody = gson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(requestBody)).uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(1, taskManager.getTasks().size());
        Assertions.assertEquals(201, response.statusCode());
    }

    @Test
    @SneakyThrows
    void updateTask_updateRequestBodyTask() {
        Task task1 = new Task("n", "d", Status.NEW, 10, "2025-01-23T23:20:21.413486");
        taskManager.createNewTask(task1);

        Task task2 = new Task("n2", "d2", Status.NEW, 20, "2026-01-23T23:20:21.413486");
        task2.setId(1);
        String requestTask = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(requestTask)).uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(task2, taskManager.getTaskById(1).get());
        Assertions.assertEquals(201, response.statusCode());
    }

    @Test
    @SneakyThrows
    void throwOverlappingTimeException_ifTaskIsOverlapping() {
        Task task2 = new Task("n", "d", Status.NEW, 10, "2025-01-23T23:20:21.413486");
        taskManager.createNewTask(task2);
        Task task1 = new Task("n", "d", Status.NEW, 10, "2025-01-23T23:20:21.413486");
        String requestBody = gson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(requestBody)).uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(406, response.statusCode());
    }

    @Test
    @SneakyThrows
    void throwOverlappingTimeException_ifUpdatedTaskIsOverlapping() {
        Task task3 = new Task("n", "d", Status.NEW, 10, "2025-01-23T23:20:21.413486");
        taskManager.createNewTask(task3);
        Task task2 = new Task("n", "d", Status.NEW);
        taskManager.createNewTask(task2);
        Task task1 = new Task("n", "d", Status.NEW, 10, "2025-01-23T23:20:21.413486");
        task1.setId(2);

        String requestBody = gson.toJson(task1);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(requestBody)).uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(406, response.statusCode());
    }
}