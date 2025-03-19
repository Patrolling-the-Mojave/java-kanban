package servers.handlers;

import com.google.gson.Gson;
import exceptions.OverLappingTimeException;
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

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PrioritizedTasksHandlerTest {
    static final Gson gson = new BaseHttpHandler().getGson();
    static final URI uri = URI.create("http://localhost:8080/prioritized");
    TaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
    HttpTaskServer taskServer;

    @BeforeEach
    void startServer() throws IOException {
        taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
        taskServer = new HttpTaskServer(taskManager);
        taskServer.start();
    }

    @AfterEach
    void stopServer() {
        taskServer.stop();
    }

    @Test
    void getPrioritizedTasks() throws OverLappingTimeException, IOException, InterruptedException {
        Task task1 = new Task("n", "d", Status.NEW, 10, "2025-01-23T23:20:21.413486");
        taskManager.createNewTask(task1);
        Task task2 = new Task("n", "d", Status.NEW, 10, "2026-01-23T23:20:21.413486");
        taskManager.createNewTask(task2);

        String prioritizedTasks = gson.toJson(taskManager.getPrioritizedTasks());

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(prioritizedTasks, response.body());
        Assertions.assertEquals(200, response.statusCode());
    }
}
