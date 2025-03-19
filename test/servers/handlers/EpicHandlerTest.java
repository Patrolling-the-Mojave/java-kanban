package servers.handlers;

import com.google.gson.Gson;
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

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class EpicHandlerTest {
    static final Gson gson = new BaseHttpHandler().getGson();
    static final URI uri = URI.create("http://localhost:8080/epics");
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
    void getEpics_getAllEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epicDesc", Status.NEW);
        taskManager.createNewEpic(epic);
        Epic epic2 = new Epic("epic2", "epicDesc2", Status.NEW);
        taskManager.createNewEpic(epic2);

        String epics = gson.toJson(taskManager.getEpics());

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(epics, response.body());
    }

    @Test
    void getEpic_getEpicByPathId() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epicDesc", Status.NEW);
        taskManager.createNewEpic(epic);

        String epicRequest = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(uri.toString() + "/1")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(epicRequest, response.body());
    }

    @Test
    void deleteEpic_deleteEpicByPathId() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epicDesc", Status.NEW);
        taskManager.createNewEpic(epic);
        Epic epic2 = new Epic("epic", "epicDesc", Status.NEW);
        taskManager.createNewEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(URI.create(uri.toString() + "/1")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(1, taskManager.getEpics().size());
    }

    @Test
    void createEpic_createRequestBodyEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epicDesc", Status.NEW);

        String requestBody = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(requestBody)).uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(1, taskManager.getEpics().size());
        Assertions.assertEquals(201, response.statusCode());
    }

    @Test
    void updateEpic_updateRequestBodyEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epicDesc", Status.NEW);
        taskManager.createNewEpic(epic);

        Epic epic2 = new Epic("epic2", "epicDesc2", Status.DONE);
        epic2.setId(1);
        String requestTask = gson.toJson(epic2);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(requestTask)).uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(epic2, taskManager.getEpicById(1).get());
        Assertions.assertEquals(201, response.statusCode());
    }

}
