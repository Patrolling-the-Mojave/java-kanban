import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault(Managers.getDefaultHistory());

        Task task1 = new Task("n1", "d1", Status.NEW);
        taskManager.createNewTask(task1);
        Task task2 = new Task("n2", "d2", Status.NEW);
        taskManager.createNewTask(task2);

        Epic epic1 = new Epic("n", "d", Status.NEW);
        taskManager.createNewEpic(epic1);

        SubTask subTask1 = new SubTask("n1", "d1", Status.NEW, 3);
        taskManager.createNewSubTask(subTask1);
        SubTask subTask2 = new SubTask("n2", "d2", Status.NEW, 3);
        taskManager.createNewSubTask(subTask2);
        SubTask subTask3 = new SubTask("n3", "d3", Status.NEW, 3);
        taskManager.createNewSubTask(subTask3);

        Epic epic2 = new Epic("n2", "d2", Status.NEW);
        taskManager.createNewEpic(epic2);

        taskManager.getTaskById(1);
        System.out.println(taskManager.getHistory());
        taskManager.getSubtaskById(5);
        System.out.println(taskManager.getHistory());
        taskManager.getSubtaskById(6);
        System.out.println(taskManager.getHistory());
        taskManager.getTaskById(1);
        System.out.println(taskManager.getHistory());
        taskManager.getEpicById(7);
        System.out.println(taskManager.getHistory());
        taskManager.getSubtaskById(5);
        System.out.println(taskManager.getHistory());

        taskManager.removeTaskById(1);
        System.out.println(taskManager.getHistory());//задача 1 удалена

        taskManager.removeEpicById(3);
        System.out.println(taskManager.getHistory()); //подзадачи 5 и 6 удалены


    }
}
