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
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HistoryHandlerTest {
    static final Gson gson = new BaseHttpHandler().getGson();
    static final URI uri = URI.create("http://localhost:8080/history");
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

    @Test
    @SneakyThrows
    void getHistory() {
        Task task = new Task("n", "d", Status.NEW, 5, "2022-01-23T23:20:21.413486");
        taskManager.createNewTask(task);
        Epic epic = new Epic("n", "d", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask = new SubTask("n", "d", Status.NEW, 2, 10, "2025-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask);

        taskManager.getTaskById(task.getId());
        taskManager.getSubtaskById(subTask.getId());
        taskManager.getEpicById(epic.getId());

        String historyTasks = gson.toJson(taskManager.getHistory());

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(historyTasks, response.body());
        Assertions.assertEquals(200, response.statusCode());
    }
}
