package managers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManagerTest {
    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    void setTaskManager() {
        historyManager = new InMemoryHistoryManager();
        taskManager = Managers.getDefault(historyManager);
    }

    public Task buildTask() {
        return new Task("name", "description", Status.NEW,10,"2025-01-23T23:20:21.413486");
    }

    @Test
    void remove_RemoveTaskFromHistory_IfCalledRemoveMethodInHistoryManager() {
        Task task1 = new Task("n", "d", Status.NEW,10,"2024-01-23T23:20:21.413486");
        taskManager.createNewTask(task1);
        Epic epic = new Epic("n", "d", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask1 = new SubTask("n", "d", Status.NEW, 2,10,"2025-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask1);

        taskManager.getTaskById(1);
        taskManager.getSubtaskById(3);
        taskManager.getEpicById(2);

        historyManager.remove(1);
        historyManager.remove(2);

        ArrayList<Task> history = new ArrayList<>(List.of(subTask1));

        Assertions.assertEquals(history, taskManager.getHistory());
    }

    @Test
    void add_AddNodeOnTheTop_ifAlreadyInHistoryAndCalledGetMethod() {
        Task task1 = new Task("n", "d", Status.NEW,10,"2023-01-23T23:20:21.413486");
        taskManager.createNewTask(task1);
        Epic epic1 = new Epic("n", "d", Status.NEW);
        taskManager.createNewEpic(epic1);
        SubTask subTask1 = new SubTask("n", "d", Status.NEW, 2,10,"2025-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask1);

        taskManager.getTaskById(1);
        taskManager.getSubtaskById(3);
        taskManager.getEpicById(2);
        taskManager.getTaskById(1);

        List<Task> tasks = new ArrayList<>(List.of(subTask1, epic1, task1));

        Assertions.assertEquals(tasks, taskManager.getHistory());
    }

    @Test
    void increase_sizeWillIncrease_ifNodeISCreated() {
        Task task1 = new Task("n", "d", Status.NEW,10,"2025-01-23T23:20:21.413486");
        taskManager.createNewTask(task1);
        Epic epic1 = new Epic("n", "d", Status.NEW);
        taskManager.createNewEpic(epic1);

        taskManager.getTaskById(1);
        Assertions.assertEquals(1, historyManager.getSize());
        taskManager.getEpicById(2);
        Assertions.assertEquals(2, historyManager.getSize());

        taskManager.removeTaskById(1);
        Assertions.assertEquals(1, historyManager.getSize());

    }


}
