import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TaskManager taskManager = new TaskManager();
        while (true) {
            printMenu();
            int cmd = scanner.nextInt();
            switch (cmd) {
                case 1:
                    System.out.println("список всех задач и подзадач:");
                    System.out.println(taskManager.getTasks());
                    break;
                case 2:
                    taskManager.removeAllTasks();
                    System.out.println("все задачи удалены");
                    break;
                case 3:
                    System.out.println("введите идентификатор нужной задачи:");
                    int requiredTask = scanner.nextInt();
                    System.out.println(taskManager.getTaskById(requiredTask));
                    break;
                case 4:
                    System.out.println("выберите тип задачи");
                    System.out.println("1 - простая задача(Task)");
                    System.out.println("2 - подзадача(SubTask)");
                    System.out.println("3 - епик(EpicTask)");
                    int taskType = scanner.nextInt();
                    System.out.println("введите название задачи");
                    String taskName = scanner.nextLine();
                    scanner.nextLine();
                    System.out.println("введите описание задачи:");
                    String taskDescription = scanner.nextLine();
                    if (taskType == 1) {
                        taskManager.createNewTask(new Task(taskName, taskDescription, Status.NEW));
                    } else if (taskType == 2) {
                        System.out.println("введите id эпика:");
                        int epicId = scanner.nextInt();
                        taskManager.createNewSubTask(new SubTask(taskName, taskDescription, Status.NEW, taskManager.getTaskById(epicId)));

                    } else if (taskType == 3) {
                        taskManager.createNewTask(new EpicTask(taskName, taskDescription, Status.NEW));
                    }
                    break;
                case 5:
                    System.out.println("введите идентификатор обновляемой задачи");
                    int input = scanner.nextInt();
//                    Проверка для простой задачи
//                    Task updatedTask = new Task("updatedName", "updatedDesc", Status.DONE);
//                    updatedTask.setId(input);
//                    taskManager.updateTask(updatedTask);

//                    Проверка для эпика
//                    EpicTask updatedEpicTask = new EpicTask("updatedEpicName","updatedEpicDesc",Status.NEW);
//                    updatedEpicTask.setId(input);
//                    taskManager.updateTask(updatedEpicTask);
//                    Проверка для подзадачи
                    SubTask updatedSubTask = new SubTask("updatedSubTaskName", "updatedSubTaskDesc",Status.DONE,((SubTask) taskManager.getTaskById(input)).getEpicTask());
                    updatedSubTask.setId(input);
                    taskManager.updateTask(updatedSubTask);
                    break;
                case 6:
                    System.out.println("введите идентификатор");
                    int taskId = scanner.nextInt();
                    taskManager.removeById(taskId);
                    break;
                case 7:
                    System.out.println("введите идентификатор эпика");
                    int epicId = scanner.nextInt();
                    System.out.println(taskManager.getSubTasksByEpicId(epicId));
                    break;
                default:
                    System.out.println("такой команды пока нет(");
            }
        }
    }

    public static void printMenu() {
        System.out.println("введите команду из списка:");
        System.out.println("1 - Получение списка всех задач. ");
        System.out.println("2 - Удаление всех задач.");
        System.out.println("3 - Получение по идентификатору.");
        System.out.println("4 - Создание задачи");
        System.out.println("5 - Обновление задачи");
        System.out.println("6 - Удаление по идентификатору.");
        System.out.println("7 - Получение списка всех подзадач определённого эпика.");
    }
}
