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
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SubTaskHandlerTest {
    static final Gson gson = new BaseHttpHandler().getGson();
    static final URI uri = URI.create("http://localhost:8080/subtasks");
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
    void getSubTasks_getAllSubTasks() throws IOException, InterruptedException, OverLappingTimeException {
        Epic epic = new Epic("epic", "epicDesc", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask1 = new SubTask("name2", "desc2", Status.NEW, 1, 400, "2020-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask1);

        String subtasks = gson.toJson(taskManager.getSubTasks());

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(subtasks, response.body());
    }

    @Test
    void getSubTask_getSubTaskByPathId() throws IOException, InterruptedException, OverLappingTimeException {
        Epic epic = new Epic("epic", "epicDesc", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask1 = new SubTask("name2", "desc2", Status.NEW, 1, 400, "2020-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask1);

        String task = gson.toJson(subTask1);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(uri.toString() + "/2")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(task, response.body());

    }

    @Test
    void deleteSubTask_deleteSubTaskByPathId() throws IOException, InterruptedException, OverLappingTimeException {
        Epic epic = new Epic("epic", "epicDesc", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask1 = new SubTask("name2", "desc2", Status.NEW, 1, 400, "2020-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask1);
        SubTask subTask2 = new SubTask("name2", "desc2", Status.NEW, 1);
        taskManager.createNewSubTask(subTask2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(URI.create(uri.toString() + "/2")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(1, taskManager.getSubTasks().size());
    }

    @Test
    void createSubTask_createRequestBodySubTask() throws IOException, InterruptedException, OverLappingTimeException {
        Epic epic = new Epic("epic", "epicDesc", Status.NEW);
        taskManager.createNewEpic(epic);

        SubTask subTask1 = new SubTask("name2", "desc2", Status.NEW, 1, 400, "2020-01-23T23:20:21.413486");

        String requestBody = gson.toJson(subTask1);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(requestBody)).uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(1, taskManager.getSubTasks().size());
        Assertions.assertEquals(201, response.statusCode());
    }

    @Test
    void updateSubTask_updateRequestBodySubTask() throws IOException, InterruptedException, OverLappingTimeException {
        Epic epic = new Epic("epic", "epicDesc", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask1 = new SubTask("name2", "desc2", Status.NEW, 1, 400, "2020-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask1);

        SubTask subTask2 = new SubTask("name2", "desc2", Status.NEW, 1);
        subTask2.setId(2);

        String requestTask = gson.toJson(subTask2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(requestTask)).uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(subTask2, taskManager.getSubtaskById(2).get());
        Assertions.assertEquals(201, response.statusCode());
    }

    @Test
    void throwOverlappingTimeException_ifCreatedSubTaskIsOverlapping() throws IOException, InterruptedException, OverLappingTimeException {
        Epic epic = new Epic("epic", "epicDesc", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask1 = new SubTask("name2", "desc2", Status.NEW, 1, 400, "2020-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask1);

        SubTask subTask2 = new SubTask("name2", "desc2", Status.NEW, 1, 200, "2020-01-23T23:20:21.413486");
        String requestBody = gson.toJson(subTask2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(requestBody)).uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(406, response.statusCode());
    }

    @Test
    void throwOverlappingTimeException_ifUpdatedSubTaskIsOverlapping() throws IOException, InterruptedException, OverLappingTimeException {
        Epic epic = new Epic("epic", "epicDesc", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask1 = new SubTask("name2", "desc2", Status.NEW, 1, 400, "2020-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask1);
        SubTask subTask2 = new SubTask("name2", "desc2", Status.NEW, 1, 400, "2021-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask2);

        SubTask subTask3 = new SubTask("name2", "desc2", Status.NEW, 1, 200, "2021-01-23T23:20:21.413486");
        subTask3.setId(2);

        String requestBody = gson.toJson(subTask3);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(requestBody)).uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(406, response.statusCode());
    }
}
