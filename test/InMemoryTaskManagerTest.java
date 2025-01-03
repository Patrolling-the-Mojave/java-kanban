import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import tasks.*;

public class InMemoryTaskManagerTest {
    Managers managers = new Managers();
    TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = managers.getDefault();
    }

    public Task createTask() {
        return new Task("name", "description", Status.NEW);
    }

    @Test
    public void shouldAddTaskToHistoryIfCalledGetByIdMethod() {
        Task task1 = new Task("n", "d", Status.NEW);
        taskManager.createNewTask(task1);
        Epic epic = new Epic("n", "d", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask1 = new SubTask("n", "d", Status.NEW, 2);
        taskManager.createNewSubTask(subTask1);

        taskManager.getTaskById(1);
        taskManager.getSubtaskById(3);
        taskManager.getEpicById(2);

        ArrayList<Task> history = new ArrayList<>();
        history.add(task1);
        history.add(subTask1);
        history.add(epic);

        Assertions.assertEquals(history, taskManager.getHistoryManager().getHistory());
    }

    @Test
    public void shouldRemoveFirstElementIfHistoryIsFull() {

        Task task1 = createTask();
        taskManager.createNewTask(task1);

        Task task2 = createTask();
        taskManager.createNewTask(task2);

        Task task3 = createTask();
        taskManager.createNewTask(task3);

        Task task4 = createTask();
        taskManager.createNewTask(task4);

        Task task5 = createTask();
        taskManager.createNewTask(task5);

        Task task6 = createTask();
        taskManager.createNewTask(task6);

        Task task7 = createTask();
        taskManager.createNewTask(task7);

        Task task8 = createTask();
        taskManager.createNewTask(task8);

        Task task9 = createTask();
        taskManager.createNewTask(task9);

        Task task10 = createTask();
        taskManager.createNewTask(task10);

        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(3);
        taskManager.getTaskById(4);
        taskManager.getTaskById(5);
        taskManager.getTaskById(6);
        taskManager.getTaskById(7);
        taskManager.getTaskById(8);
        taskManager.getTaskById(9);
        taskManager.getTaskById(10);
        //первый элемент в списке просмотренных - первая задача, вызванная через getTaskById()
        Assertions.assertEquals(1, taskManager.getHistoryManager().getHistory().get(0).getId());

        Task task11 = createTask();
        taskManager.createNewTask(task11);
        taskManager.getTaskById(11);

        //фиксируем изменение первого элемента в истории просмотренных задач
        Assertions.assertEquals(2, taskManager.getHistoryManager().getHistory().get(0).getId());
    }

    @Test
    public void tasksWithEqualIdShouldBeEqual() {
        Task task1 = createTask();
        taskManager.createNewTask(task1);

        Task task2 = createTask();
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
    public void shouldReturnSubtasksListByEpicId() {
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
    public void shouldDeleteSubtasksWhenDeletingTheirEpic() {
        Epic epic = new Epic("epic1", "desc1", Status.NEW);
        taskManager.createNewEpic(epic);

        SubTask subTask = new SubTask("sub1", "desc1", Status.NEW, 1);
        taskManager.createNewSubTask(subTask);
        SubTask subTask2 = new SubTask("sub1", "desc1", Status.NEW, 1);
        taskManager.createNewSubTask(subTask2);

        taskManager.removeEpicById(1);

        Assertions.assertTrue(taskManager.getEpics().isEmpty());
        Assertions.assertTrue(taskManager.getSubTasks().isEmpty());
    }

    @Test
    public void shouldReturnTasksIfGetMethodIsCalled() {
        Task task = new Task("task", "description", Status.NEW);
        taskManager.createNewTask(task);
        Epic epic = new Epic("epic", "descriotion", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask = new SubTask("subtask", "description", Status.NEW, 2);
        taskManager.createNewSubTask(subTask);

        List<Task> tasks = new ArrayList<>(List.of(task));
        List<Epic> epics = new ArrayList<>(List.of(epic));
        List<SubTask> subTasks = new ArrayList<>(List.of(subTask));

        Assertions.assertEquals(tasks, taskManager.getTasks());
        Assertions.assertEquals(subTasks, taskManager.getSubTasks());
        Assertions.assertEquals(epics, taskManager.getEpics());

    }

    @Test
    public void shouldChangeStatusWhenAddingASubTask() {
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
