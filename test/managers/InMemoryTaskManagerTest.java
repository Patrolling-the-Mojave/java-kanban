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


public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    void setTaskManager() {
        historyManager = Managers.getDefaultHistory();
        taskManager = new InMemoryTaskManager(historyManager);
    }

    @Test
    void removeEpicById_DeleteSubtasksWhenDeletingTheirEpic() {
        Epic epic = new Epic("epic1", "desc1", Status.NEW);
        taskManager.createNewEpic(epic);

        SubTask subTask = new SubTask("sub1", "desc1", Status.NEW, 1,10,"2022-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask);
        SubTask subTask2 = new SubTask("sub1", "desc1", Status.NEW, 1,10,"2025-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask2);

        taskManager.removeEpicById(1);

        Assertions.assertTrue(taskManager.getSubTasks().isEmpty());

    }

    @Test
    void remove_removeTaskFormHistory_IfDeleteHisId() {
        Task task1 = new Task("n", "d", Status.NEW,10,"2024-01-23T23:20:21.413486");
        taskManager.createNewTask(task1);
        Epic epic1 = new Epic("n", "d", Status.NEW);
        taskManager.createNewEpic(epic1);
        SubTask subTask1 = new SubTask("n", "d", Status.NEW, 2,10,"2025-01-23T23:20:21.413486");
         taskManager.createNewSubTask(subTask1);

        taskManager.getTaskById(1);
        taskManager.getSubtaskById(3);
        taskManager.getEpicById(2);

        taskManager.removeTaskById(1);
        taskManager.removeSubtaskById(3);

        List<Task> tasks = new ArrayList<>(List.of(epic1));

        Assertions.assertEquals(tasks, historyManager.getHistory());
    }

    @Test
    void delete_deleteSubtaskFromHistory_ifDeleteHisEpic() {
        Epic epic1 = new Epic("n", "d", Status.NEW);
        taskManager.createNewEpic(epic1);
        SubTask subTask1 = new SubTask("n", "d", Status.NEW, 1,10,"2024-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask1);
        SubTask subTask2 = new SubTask("n", "d", Status.DONE, 1,10,"2026-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask2);
        Task task1 = new Task("n", "d", Status.NEW,10,"2020-01-23T23:20:21.413486");
        taskManager.createNewTask(task1);


        taskManager.getEpicById(1);
        taskManager.getSubtaskById(2);
        taskManager.getSubtaskById(3);
        taskManager.getTaskById(4);

        taskManager.removeEpicById(1);

        Assertions.assertEquals(1, historyManager.getSize());
    }
}
