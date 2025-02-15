package managers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileBackedTaskManagerTest {
    private TaskManager taskManager;
    private HistoryManager historyManager;
    private static Path savedData;

    @TempDir
    static Path tempDir;

    @BeforeAll
    static void fileSetup() {
        savedData = tempDir.resolve("saveData.csv");
    }

    @BeforeEach
    void resetManagers() throws IOException {
        historyManager = Managers.getDefaultHistory();
        taskManager = new FileBackedTaskManager(historyManager, savedData);

    }

    @Test
    void add_addTasksToFile_IfTaskIsCreated() throws IOException {
        Task task = new Task("n", "d", Status.NEW);
        taskManager.createNewTask(task);
        Epic epic = new Epic("n", "d", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask = new SubTask("n", "d", Status.NEW, 2);
        taskManager.createNewSubTask(subTask);
        try (BufferedReader bufferedReader = new BufferedReader(
                new FileReader(savedData.toFile(), UTF_8))) {
            Assertions.assertEquals(bufferedReader.readLine(), "id,type,name,status,description,epic");
            Assertions.assertEquals(bufferedReader.readLine(), task.convertToCSV());
            Assertions.assertEquals(bufferedReader.readLine(), epic.convertToCSV());
            Assertions.assertEquals(subTask.convertToCSV(), bufferedReader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    void add_addTasksToFile_IfTaskIsUpdated() {
        Task task = new Task("n", "d", Status.NEW);
        taskManager.createNewTask(task);
        Epic epic = new Epic("n", "d", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask = new SubTask("n", "d", Status.NEW, 2);
        taskManager.createNewSubTask(subTask);
        Task newTask = new Task("n2", "d2", Status.DONE);
        newTask.setId(1);
        taskManager.updateTask(newTask);
        SubTask newSubTask = new SubTask("n2", "d2", Status.DONE, 2);
        newSubTask.setId(3);
        taskManager.updateSubtask(newSubTask);
        Epic newEpic = new Epic("n2", "d2", Status.DONE);
        newEpic.setId(2);
        taskManager.updateEpic(newEpic);
        try (BufferedReader bufferedReader = new BufferedReader(
                new FileReader(savedData.toFile(), UTF_8))) {
            Assertions.assertEquals("id,type,name,status,description,epic", bufferedReader.readLine());
            Assertions.assertEquals(newTask.convertToCSV(), bufferedReader.readLine());
            Assertions.assertEquals(newEpic.convertToCSV(), bufferedReader.readLine());
            Assertions.assertEquals(newSubTask.convertToCSV(), bufferedReader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    void remove_removeTaskFromFile_ifCalledRemoveMethod() {
        Task task = new Task("n", "d", Status.NEW);
        taskManager.createNewTask(task);
        Epic epic = new Epic("n", "d", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask = new SubTask("n", "d", Status.NEW, 2);
        taskManager.createNewSubTask(subTask);
        Task task2 = new Task("n2", "d2", Status.DONE);
        taskManager.createNewTask(task2);

        taskManager.removeTaskById(1);
        taskManager.removeEpicById(2);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(savedData.toFile(), UTF_8))) {
            Assertions.assertEquals("id,type,name,status,description,epic", bufferedReader.readLine());
            Assertions.assertEquals(task2.convertToCSV(), bufferedReader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void remove_removeTasks_ifCalledRemoveAllMethod() {
        Task task = new Task("n", "d", Status.NEW);
        taskManager.createNewTask(task);
        Epic epic = new Epic("n", "d", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask = new SubTask("n", "d", Status.NEW, 2);
        taskManager.createNewSubTask(subTask);
        Task task2 = new Task("n2", "d2", Status.DONE);
        taskManager.createNewTask(task2);

        taskManager.removeAllTasks();
        taskManager.removeAllEpics();
        taskManager.removeAllSubtasks();

        Task task3 = new Task("n3", "d3", Status.DONE);
        taskManager.createNewTask(task3);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(savedData.toFile(), UTF_8))) {
            Assertions.assertEquals("id,type,name,status,description,epic", bufferedReader.readLine());
            Assertions.assertEquals(task3.convertToCSV(), bufferedReader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    void add_addTasksToTaskMapFromFile() {
        try (FileWriter fileWriter = new FileWriter(savedData.toFile(), UTF_8)) {
            fileWriter.write("id,type,name,status,description,epic\n");
            fileWriter.write("3,TASK,t1,NEW,d1\n");
            fileWriter.write("4,EPIC,e1,NEW,d1\n");
            fileWriter.write("6,SUBTASK,s1,NEW,d1,4\n");


        } catch (IOException e) {
            e.printStackTrace();
        }
        Task task = new Task("t1", "d1", Status.NEW);
        task.setId(3);
        Epic epic = new Epic("e1", "d1", Status.NEW);
        epic.setId(4);
        SubTask subTask = new SubTask("s1", "d1", Status.NEW, 4);
        subTask.setId(6);
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(savedData.toFile());
        Assertions.assertEquals(task, fileBackedTaskManager.getTasks().get(0));
        Assertions.assertEquals(epic, fileBackedTaskManager.getEpics().get(0));
        Assertions.assertEquals(subTask, fileBackedTaskManager.getSubTasks().get(0));
    }

    @Test
    void check_checkCollisionInTaskMap() {
        try (FileWriter fileWriter = new FileWriter(savedData.toFile(), UTF_8)) {
            fileWriter.write("id,type,name,status,description,epic\n");
            fileWriter.write("1,TASK,t1,NEW,d1\n");
            fileWriter.write("4,EPIC,e1,NEW,d1\n");
            fileWriter.write("6,SUBTASK,s1,NEW,d1,4\n");


        } catch (IOException e) {
            e.printStackTrace();
        }
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(savedData.toFile());
        Task task = new Task("anotherTask", "d1", Status.NEW);
        taskManager.createNewTask(task);
        Assertions.assertEquals(task, taskManager.getTaskById(2)); //getNewId() ищет первый свободный id - 2
    }

}
