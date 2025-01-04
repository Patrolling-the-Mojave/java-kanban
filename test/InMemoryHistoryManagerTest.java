import managers.HistoryManager;
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

public class InMemoryHistoryManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void setTaskManager() {
        taskManager = Managers.getDefault(Managers.getDefaultHistory());
    }

    public Task buildTask() {
        return new Task("name", "description", Status.NEW);
    }

    @Test
    public void add_ShouldAddTaskToHistory_IfCalledGetByIdMethod() {
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

        Assertions.assertEquals(history, taskManager.getHistory());
    }

    @Test
    public void add_ShouldRemoveFirstElement_IfHistoryIsFull() {

        Task task1 = buildTask();
        taskManager.createNewTask(task1);

        Task task2 = buildTask();
        taskManager.createNewTask(task2);

        Task task3 = buildTask();
        taskManager.createNewTask(task3);

        Task task4 = buildTask();
        taskManager.createNewTask(task4);

        Task task5 = buildTask();
        taskManager.createNewTask(task5);

        Task task6 = buildTask();
        taskManager.createNewTask(task6);

        Task task7 = buildTask();
        taskManager.createNewTask(task7);

        Task task8 = buildTask();
        taskManager.createNewTask(task8);

        Task task9 = buildTask();
        taskManager.createNewTask(task9);

        Task task10 = buildTask();
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
        Assertions.assertEquals(1, taskManager.getHistory().get(0).getId());

        Task task11 = buildTask();
        taskManager.createNewTask(task11);
        taskManager.getTaskById(11);

        //фиксируем изменение первого элемента в истории просмотренных задач
        Assertions.assertEquals(2, taskManager.getHistory().get(0).getId());
    }


}
