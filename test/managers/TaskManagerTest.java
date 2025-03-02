package managers;

import exceptions.OverLappingTimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class TaskManagerTest<T extends TaskManager> {
    TaskManager taskManager;
    HistoryManager historyManager;

    @BeforeEach
    void resetManager(){
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault(historyManager);
    }

    @Test
    void return_returnPrioritizedByStartTimeTasks_getPrioritizedTasks(){
        Task task = new Task("n", "d", Status.NEW,5, "2022-01-23T23:20:21.413486");
        taskManager.createNewTask(task);
        Epic epic = new Epic("n", "d", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask = new SubTask("n", "d", Status.NEW, 2,10,"2025-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask);
        SubTask subTask2 = new SubTask("n2", "d2", Status.NEW, 2,10,"2024-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask2);

        List<Task> tasksList = List.of(task, epic, subTask2, subTask);

        Assertions.assertArrayEquals(tasksList.toArray(), taskManager.getPrioritizedTasks().toArray());
    }

    @Test
    void skip_shouldSkipNewEpicInPrioritizedTasks(){
        Task task = new Task("n", "d", Status.NEW,5, "2022-01-23T23:20:21.413486");
        taskManager.createNewTask(task);
        Epic epic = new Epic("n", "d", Status.NEW);
        taskManager.createNewEpic(epic);

        List<Task> tasks = List.of(task);

        Assertions.assertArrayEquals(tasks.toArray(), taskManager.getPrioritizedTasks().toArray());
    }

    @Test
    void throw_shouldThrowOverLappingException_ifTaskIntersects(){
        Task task = new Task("n", "d", Status.NEW,5, "2022-01-23T23:20:21.413486");
        taskManager.createNewTask(task);
        Epic epic = new Epic("n", "d", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask = new SubTask("n", "d", Status.NEW, 2,10,"2025-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask);
        SubTask subTask2 = new SubTask("n2", "d2", Status.NEW, 2,10,"2020-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask2);

        Task overLappingTask = new Task("n2", "d2", Status.NEW,1000, "2020-01-23T23:20:21.413486");

        Assertions.assertThrows(OverLappingTimeException.class, () -> taskManager.createNewTask(overLappingTask));
    }

    @Test
    void throw_shouldThrowOverLappingException_ifSubTaskIntersects(){
        Task task = new Task("n", "d", Status.NEW,5, "2022-01-23T23:20:21.413486");
        taskManager.createNewTask(task);
        Epic epic = new Epic("n", "d", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask = new SubTask("n", "d", Status.NEW, 2,10,"2025-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask);
        SubTask subTask2 = new SubTask("n2", "d2", Status.NEW, 2,10,"2020-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask2);

        SubTask overLappingSubTask = new SubTask("n2", "d2", Status.NEW,2,2, "2022-01-23T23:20:21.413486");

        Assertions.assertThrows(OverLappingTimeException.class, () -> taskManager.createNewTask(overLappingSubTask));
    }

    @Test
    void throw_shouldThrowOverLappingException_ifEpicIntersects(){
        Task task = new Task("n", "d", Status.NEW,5, "2022-01-23T23:20:21.413486");
        taskManager.createNewTask(task);
        Epic epic = new Epic("n", "d", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask = new SubTask("n", "d", Status.NEW, 2,10,"2025-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask);
        SubTask subTask2 = new SubTask("n2", "d2", Status.NEW, 2,10,"2020-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask2);

        SubTask overLappingSubTask = new SubTask("n2", "d2", Status.NEW,2,2, "2022-01-23T23:20:21.413486");

        Assertions.assertThrows(OverLappingTimeException.class, () -> taskManager.createNewTask(overLappingSubTask));
    }

    @Test
    void throw_shouldThrowOverLappingException_ifUpdatedTaskIntersects(){
        Task task = new Task("n", "d", Status.NEW,5, "2022-01-23T23:20:21.413486");
        taskManager.createNewTask(task);
        Epic epic = new Epic("n", "d", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask = new SubTask("n", "d", Status.NEW, 2,10,"2025-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask);
        SubTask subTask2 = new SubTask("n2", "d2", Status.NEW, 2,10,"2020-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask2);

        Task overLappingTask = new Task("n2", "d2", Status.NEW,1000, "2020-01-23T23:20:21.413486");
        overLappingTask.setId(task.getId());

        Assertions.assertThrows(OverLappingTimeException.class, () -> taskManager.updateTask(overLappingTask));
    }

    @Test
    void throw_shouldThrowOverLappingException_ifUpdatedSubTaskIntersects(){
        Task task = new Task("n", "d", Status.NEW,5, "2022-01-23T23:20:21.413486");
        taskManager.createNewTask(task);
        Epic epic = new Epic("n", "d", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask = new SubTask("n", "d", Status.NEW, 2,10,"2025-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask);
        SubTask subTask2 = new SubTask("n2", "d2", Status.NEW, 2,10,"2020-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask2);


        SubTask overLappingSubTask = new SubTask("n2", "d2", Status.NEW,2,10, "2020-01-23T23:20:21.413486");
        overLappingSubTask.setId(3);

        Assertions.assertThrows(OverLappingTimeException.class, () -> taskManager.updateSubtask(overLappingSubTask));
    }

    @Test
    void tasksWithEqualIdShouldBeEqual() throws OverLappingTimeException {
        Task task = new Task("n", "d", Status.NEW,5, "2024-01-23T23:20:21.413486");
        taskManager.createNewTask(task);

        Task task2 = new Task("n", "d", Status.NEW,5, "2022-01-23T23:20:21.413486");
        taskManager.createNewTask(task2);

        task2.setId(task.getId());

        Assertions.assertTrue(Objects.equals(task2, task));
    }

    @Test
    void subTasksWithEqualIdShouldBeEqual() {
        Epic epic = new Epic("epicName", "desc", Status.NEW);
        taskManager.createNewEpic(epic);

        SubTask subTask1 = new SubTask("name1", "desc1", Status.NEW, 1,10,"2024-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask1);

        SubTask subTask2 = new SubTask("name2", "desc2", Status.NEW, 1,10,"2025-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask2);

        subTask2.setId(subTask1.getId());

        Assertions.assertTrue(Objects.equals(subTask2, subTask1));
    }

    @Test
    void epicsWithEqualIdShouldBeEqual() {
        Epic epic1 = new Epic("name1", "desc1", Status.NEW);
        taskManager.createNewEpic(epic1);

        Epic epic2 = new Epic("name1", "desc1", Status.NEW);
        taskManager.createNewEpic(epic2);

        epic2.setId(epic1.getId());

        Assertions.assertTrue(Objects.equals(epic2, epic1));
    }

    @Test
    void getSubTasksByEpicId_ReturnSubtasksListOfEpic() {
        Epic epic = new Epic("epic", "d", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask1 = new SubTask("sub1", "desc1", Status.NEW, 1,10,"2026-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask1);
        SubTask subTask2 = new SubTask("sub2", "desc2", Status.NEW, 1,10,"2025-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask2);

        List<SubTask> subTaskList = new ArrayList<>();
        subTaskList.add(subTask2);
        subTaskList.add(subTask1);

        Assertions.assertEquals(subTaskList, taskManager.getSubTasksByEpicId(1));
    }

    @Test
    void getTasks_ReturnTasksList() {
        Task task = new Task("task", "description", Status.NEW,10,"2025-01-23T23:20:21.413486");
        taskManager.createNewTask(task);

        List<Task> tasks = new ArrayList<>(List.of(task));

        Assertions.assertEquals(tasks, taskManager.getTasks());
    }

    @Test
    void getSubTasks_ReturnSubtasksList() {
        Epic epic = new Epic("epic", "descriotion", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask = new SubTask("subtask", "description", Status.NEW, 1,10,"2025-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask);

        List<SubTask> subTasks = new ArrayList<>(List.of(subTask));

        Assertions.assertEquals(subTasks, taskManager.getSubTasks());
    }

    @Test
    void getEpics_ReturnEpicsList() {
        Epic epic = new Epic("epic", "descriotion", Status.NEW);
        taskManager.createNewEpic(epic);

        List<Epic> epics = new ArrayList<>(List.of(epic));

        Assertions.assertEquals(epics, taskManager.getEpics());
    }

    @Test
    void updateEpicStatus_ChangeStatusWhenAddingASubTask() {
        Epic epic = new Epic("epic", "d", Status.NEW);
        taskManager.createNewEpic(epic);

        SubTask subTask = new SubTask("sub1", "desc1", Status.NEW, 1,10,"2021-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask);
        Assertions.assertEquals(Status.NEW, epic.getStatus());

        SubTask subTask1 = new SubTask("sub1", "desc1", Status.DONE, 1,10,"2025-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask1);
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());

        SubTask newSubTask = new SubTask("newsub1", "newDesc1", Status.DONE, 1,12,"2021-01-23T23:20:21.413486");
        newSubTask.setId(2);
        taskManager.updateSubtask(newSubTask);

        Assertions.assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void add_AddTaskToHistory_IfCalledGetByIdMethod() {
        Task task1 = new Task("n", "d", Status.NEW,10,"2024-01-23T23:20:21.413486");
        taskManager.createNewTask(task1);
        Epic epic = new Epic("n", "d", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask1 = new SubTask("n", "d", Status.NEW, 2,10,"2025-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask1);

        taskManager.getTaskById(1);
        taskManager.getSubtaskById(3);
        taskManager.getEpicById(2);

        ArrayList<Task> history = new ArrayList<>();
        history.add(task1);
        history.add(subTask1);
        history.add(epic);

        Assertions.assertEquals(history, taskManager.getHistory());
    }
}
