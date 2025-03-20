package servers;

import com.google.gson.Gson;
import exceptions.OverLappingTimeException;
import managers.InMemoryTaskManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servers.handlers.BaseHttpHandler;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

public class SerializationTest {
    final Gson gson = new BaseHttpHandler().getGson();
    TaskManager taskManager;

    @BeforeEach
    void resetManager() {
        taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
    }

    @Test
    void serialize_serializeTaskToJson() {
        Task task = new Task("task", "desc", Status.NEW, 400, "2020-01-23T23:20:21.413486");
        Task task2 = new Task("task", "desc", Status.NEW);
        Assertions.assertEquals("{\"id\":0,\"taskName\":\"task\",\"description\":\"desc\",\"status\":\"NEW\",\"duration\":400,\"startTime\":\"2020-01-23T23:20:21.413486\"}", gson.toJson(task));
        Assertions.assertEquals("{\"id\":0,\"taskName\":\"task\",\"description\":\"desc\",\"status\":\"NEW\",\"duration\":null,\"startTime\":null}", gson.toJson(task2));
    }

    @Test
    void serialize_serializeSubTaskToJson() {
        SubTask subTask = new SubTask("sub", "desc", Status.NEW, 1, 400, "2020-01-23T23:20:21.413486");
        SubTask subTask2 = new SubTask("sub", "desc", Status.NEW, 1);
        Assertions.assertEquals("{\"epicId\":1,\"id\":0,\"taskName\":\"sub\",\"description\":\"desc\",\"status\":\"NEW\",\"duration\":400,\"startTime\":\"2020-01-23T23:20:21.413486\"}", gson.toJson(subTask));
        Assertions.assertEquals("{\"epicId\":1,\"id\":0,\"taskName\":\"sub\",\"description\":\"desc\",\"status\":\"NEW\",\"duration\":null,\"startTime\":null}", gson.toJson(subTask2));
    }

    @Test
    void serialize_serializeEpicToJson() throws OverLappingTimeException {
        Epic epic = new Epic("epic", "description", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask2 = new SubTask("sub", "desc", Status.NEW, 1);
        taskManager.createNewSubTask(subTask2);
        SubTask subTask = new SubTask("sub", "desc", Status.NEW, 1, 400, "2020-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask);
        Assertions.assertEquals("{\"subtaskIds\":[2,3],\"endTime\":\"2020-01-24T06:00:21.413486\",\"id\":1,\"taskName\":\"epic\",\"description\":\"description\",\"status\":\"NEW\",\"duration\":400,\"startTime\":\"2020-01-23T23:20:21.413486\"}", gson.toJson(epic));
    }
}
