package managers;

import exceptions.ManagerSaveException;
import org.junit.jupiter.api.Assertions;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private TaskManager taskManager;
    private static Path savedData;

    @TempDir
    static Path tempDir;

    @BeforeEach
    void resetManagers() throws IOException {
        savedData = Files.createTempFile(tempDir, "tempFile", ".csv");
        taskManager = FileBackedTaskManager.loadFromFile(savedData.toFile());
    }

    @Test
    void add_addTasksToFile_IfTaskIsCreated() {
        Task task = new Task("n", "d", Status.NEW, 5, "2023-01-23T23:20:21.413486");
        taskManager.createNewTask(task);
        Epic epic = new Epic("n", "d", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask = new SubTask("n", "d", Status.NEW, 2, 5, "2025-01-23T23:20:21.413486");
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
        Task task = new Task("n", "d", Status.NEW, 5, "2025-01-23T23:20:21.413486");
        taskManager.createNewTask(task);
        Epic epic = new Epic("n", "d", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask = new SubTask("n", "d", Status.NEW, 2, 5, "2020-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask);

        Task newTask = new Task("n2", "d2", Status.DONE, 10, "2023-01-23T23:20:21.413486");
        newTask.setId(1);
        taskManager.updateTask(newTask);
        SubTask newSubTask = new SubTask("n2", "d2", Status.DONE, 2, 10, "2021-01-23T23:20:21.413486");
        newSubTask.setId(3);
        taskManager.updateSubtask(newSubTask);
        Epic newEpic = new Epic("n2", "d2", Status.DONE);
        newEpic.setId(2);
        newEpic.setSubtaskIds(Set.of(newSubTask.getId()));
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
        Task task = new Task("n", "d", Status.NEW, 5, "2021-01-23T23:20:21.413486");
        taskManager.createNewTask(task);
        Epic epic = new Epic("n", "d", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask = new SubTask("n", "d", Status.NEW, 2, 5, "2022-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask);
        Task task2 = new Task("n2", "d2", Status.DONE, 10, "2025-01-23T23:20:21.413486");
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
    void remove_removeTasks_ifCalledRemoveAllMethod() throws ManagerSaveException {
        Task task = new Task("n", "d", Status.NEW, 5, "2025-01-23T23:20:21.413486");
        taskManager.createNewTask(task);
        Epic epic = new Epic("n", "d", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask = new SubTask("n", "d", Status.NEW, 2, 5, "2020-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask);
        Task task2 = new Task("n2", "d2", Status.DONE, 10, "2021-01-23T23:20:21.413486");
        taskManager.createNewTask(task2);

        taskManager.removeAllTasks();
        taskManager.removeAllEpics();
        taskManager.removeAllSubtasks();

        Task task3 = new Task("n3", "d3", Status.DONE, 10, LocalDateTime.now().toString());
        taskManager.createNewTask(task3);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(savedData.toFile(), UTF_8))) {
            Assertions.assertEquals("id,type,name,status,description,epic", bufferedReader.readLine());
            Assertions.assertEquals(task3.convertToCSV(), bufferedReader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    void add_addTasksToTaskMapFromFile() throws ManagerSaveException {
        try (FileWriter fileWriter = new FileWriter(savedData.toFile(), UTF_8)) {
            fileWriter.write("id,type,name,status,description,epic,duration,startTime\n");
            fileWriter.write("3,TASK,t1,NEW,d1,10,2025-01-23T23:20:21.413486\n");
            fileWriter.write("4,EPIC,e1,NEW,d1\n");
            fileWriter.write("6,SUBTASK,s1,NEW,d1,4,15,2025-01-23T23:20:21.413486\n");


        } catch (IOException e) {
            e.printStackTrace();
        }
        Task task = new Task("t1", "d1", Status.NEW, 10, "2025-01-23T23:20:21.413486");
        task.setId(3);
        Epic epic = new Epic("e1", "d1", Status.NEW);
        epic.setId(4);
        SubTask subTask = new SubTask("s1", "d1", Status.NEW, 4, 15, "2025-01-23T23:20:21.413486");
        subTask.setId(6);
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(savedData.toFile());
        Assertions.assertEquals(task, fileBackedTaskManager.getTasks().get(0));
        Assertions.assertEquals(epic, fileBackedTaskManager.getEpics().get(0));
        Assertions.assertEquals(subTask, fileBackedTaskManager.getSubTasks().get(0));
    }


    @Test
    void check_checkCollisionInTaskMap() throws ManagerSaveException {
        try (FileWriter fileWriter = new FileWriter(savedData.toFile(), UTF_8)) {
            fileWriter.write("id,type,name,status,description,epic,duration,startTime\n");
            fileWriter.write("1,TASK,t1,NEW,d1,10,2025-01-23T23:20:21.413486\n");
            fileWriter.write("4,EPIC,e1,NEW,d1\n");
            fileWriter.write("6,SUBTASK,s1,NEW,d1,4,15,2025-01-23T23:20:21.413486\n");


        } catch (IOException e) {
            e.printStackTrace();
        }
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(savedData.toFile());
        Task task = new Task("anotherTask", "d1", Status.NEW, 10, "2025-01-23T23:20:21.413486");
        taskManager.createNewTask(task);
        Assertions.assertEquals(task, taskManager.getTaskById(2).get()); //getNewId() ищет первый свободный id - 2
    }

    @Test
    void subtaskUpdate_shouldntAddIdMoreThanOnce() {
        Epic epic = new Epic("e1", "d1", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask = new SubTask("s1", "d1", Status.NEW, 1, 10, "2021-01-23T23:20:21.413486");
        taskManager.createNewSubTask(subTask);

        SubTask subTask2 = new SubTask("s2", "d1", Status.NEW, 1, 10, "2022-01-23T23:20:21.413486");
        subTask2.setId(2);
        taskManager.updateSubtask(subTask2);

        SubTask subTask3 = new SubTask("s3", "d1", Status.NEW, 1, 10, "2023-01-23T23:20:21.413486");
        subTask3.setId(2);
        taskManager.updateSubtask(subTask3);
        Assertions.assertEquals(1, epic.getSubtaskIds().size());
    }

    @Test
    void loadTask_loadUntimelyTaskFromFile() throws IOException {
        try (FileWriter fileWriter = new FileWriter(savedData.toFile(), UTF_8)) {
            fileWriter.write("id,type,name,status,description,epic,duration,startTime\n");
            fileWriter.write("1,TASK,t1,NEW,d1\n");
            fileWriter.write("4,EPIC,e1,NEW,d1\n");
            fileWriter.write("6,SUBTASK,s1,NEW,d1,4\n");
        }
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(savedData.toFile());

        Task task = new Task("t1", "d1", Status.NEW);
        task.setId(1);

        Epic epic = new Epic("e1", "d1", Status.NEW);
        epic.setId(4);

        SubTask subTask = new SubTask("s1", "d1", Status.NEW, 4);
        subTask.setId(6);

        Assertions.assertEquals(task, taskManager.getTaskById(1).get());
        Assertions.assertEquals(epic, taskManager.getEpicById(4).get());
        Assertions.assertEquals(subTask, taskManager.getSubtaskById(6).get());
    }

    @Test
    void throwException_throwOverlappingTimeException_ifTimeIsIntersects() throws IOException {
        try (FileWriter fileWriter = new FileWriter(savedData.toFile(), UTF_8)) {
            fileWriter.write("id,type,name,status,description,epic,duration,startTime\n");
            fileWriter.write("1,TASK,t1,NEW,d1,400,\n");
            fileWriter.write("4,EPIC,e1,NEW,d1\n");
            fileWriter.write("6,SUBTASK,s1,NEW,d1,4\n");
        }
    }

}
