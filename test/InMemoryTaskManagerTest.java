import managers.InMemoryTaskManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void setTaskManager() {
        taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
    }

    public Task buildTask() {
        return new Task("name", "description", Status.NEW);
    }

    @Test
    public void tasksWithEqualIdShouldBeEqual() {
        Task task1 = buildTask();
        taskManager.createNewTask(task1);

        Task task2 = buildTask();
        taskManager.createNewTask(task2);

        task2.setId(task1.getId());

        Assertions.assertTrue(Objects.equals(task2, task1));
    }

    @Test
    public void subTasksWithEqualIdShouldBeEqual() {
        Epic epic = new Epic("epicName", "desc", Status.NEW);
        taskManager.createNewEpic(epic);

        SubTask subTask1 = new SubTask("name1", "desc1", Status.NEW, 1);
        taskManager.createNewSubTask(subTask1);

        SubTask subTask2 = new SubTask("name2", "desc2", Status.NEW, 1);
        taskManager.createNewSubTask(subTask2);

        subTask2.setId(subTask1.getId());

        Assertions.assertTrue(Objects.equals(subTask2, subTask1));
    }

    @Test
    public void epicsWithEqualIdShouldBeEqual() {
        Epic epic1 = new Epic("name1", "desc1", Status.NEW);
        taskManager.createNewEpic(epic1);

        Epic epic2 = new Epic("name1", "desc1", Status.NEW);
        taskManager.createNewEpic(epic2);

        epic2.setId(epic1.getId());

        Assertions.assertTrue(Objects.equals(epic2, epic1));
    }

    @Test
    public void getSubTasksByEpicId_ShouldReturnSubtasksListOfEpic() {
        Epic epic = new Epic("epic", "d", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask1 = new SubTask("sub1", "desc1", Status.NEW, 1);
        taskManager.createNewSubTask(subTask1);
        SubTask subTask2 = new SubTask("sub2", "desc2", Status.NEW, 1);
        taskManager.createNewSubTask(subTask2);

        List<SubTask> subTaskList = new ArrayList<>();
        subTaskList.add(subTask1);
        subTaskList.add(subTask2);

        Assertions.assertEquals(subTaskList, taskManager.getSubTasksByEpicId(1));
    }

    @Test
    public void removeEpicById_ShouldDeleteSubtasksWhenDeletingTheirEpic() {
        Epic epic = new Epic("epic1", "desc1", Status.NEW);
        taskManager.createNewEpic(epic);

        SubTask subTask = new SubTask("sub1", "desc1", Status.NEW, 1);
        taskManager.createNewSubTask(subTask);
        SubTask subTask2 = new SubTask("sub1", "desc1", Status.NEW, 1);
        taskManager.createNewSubTask(subTask2);

        taskManager.removeEpicById(1);

        Assertions.assertTrue(taskManager.getSubTasks().isEmpty());
    }

    @Test
    public void getTasks_ShouldReturnTasksList() {
        Task task = new Task("task", "description", Status.NEW);
        taskManager.createNewTask(task);

        List<Task> tasks = new ArrayList<>(List.of(task));

        Assertions.assertEquals(tasks, taskManager.getTasks());
    }

    @Test
    public void getSubTasks_ShouldReturnSubtasksList() {
        Epic epic = new Epic("epic", "descriotion", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask = new SubTask("subtask", "description", Status.NEW, 1);
        taskManager.createNewSubTask(subTask);

        List<SubTask> subTasks = new ArrayList<>(List.of(subTask));

        Assertions.assertEquals(subTasks, taskManager.getSubTasks());
    }

    @Test
    public void getEpics_ShouldReturnEpicsList() {
        Epic epic = new Epic("epic", "descriotion", Status.NEW);
        taskManager.createNewEpic(epic);

        List<Epic> epics = new ArrayList<>(List.of(epic));

        Assertions.assertEquals(epics, taskManager.getEpics());
    }


    @Test
    public void updateEpicStatus_ShouldChangeStatusWhenAddingASubTask() {
        Epic epic = new Epic("epic", "d", Status.NEW);
        taskManager.createNewEpic(epic);

        SubTask subTask = new SubTask("sub1", "desc1", Status.NEW, 1);
        taskManager.createNewSubTask(subTask);
        Assertions.assertEquals(Status.NEW, epic.getStatus());

        SubTask subTask1 = new SubTask("sub1", "desc1", Status.DONE, 1);
        taskManager.createNewSubTask(subTask1);
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }


}
