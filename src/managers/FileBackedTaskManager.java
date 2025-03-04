package managers;

import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private Path path;

    private FileBackedTaskManager(HistoryManager historyManager, Path path) {
        super(historyManager);
        this.path = path;
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(Managers.getDefaultHistory(), file.toPath());
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, UTF_8))) {
            bufferedReader.readLine();
            while (bufferedReader.ready()) {
                List<String> taskInfo = new ArrayList<>(Arrays.asList(bufferedReader.readLine().split(",")));
                switch (taskInfo.get(1)) {
                    case "TASK":
                        Task task = new Task(taskInfo.get(2), taskInfo.get(4), Status.valueOf(taskInfo.get(3)), Integer.parseInt(taskInfo.get(5)), taskInfo.get(6));
                        task.setId(Integer.parseInt(taskInfo.get(0)));
                        fileBackedTaskManager.addTask(task);
                        break;
                    case "EPIC":
                        Epic epic = new Epic(taskInfo.get(2), taskInfo.get(4), Status.valueOf(taskInfo.get(3)));
                        epic.setId(Integer.parseInt(taskInfo.get(0)));
                        fileBackedTaskManager.addEpic(epic);
                        break;
                    case "SUBTASK":
                        SubTask subTask = new SubTask(taskInfo.get(2), taskInfo.get(4), Status.valueOf(taskInfo.get(3)), Integer.parseInt(taskInfo.get(5)), Integer.parseInt(taskInfo.get(6)), taskInfo.get(7));
                        subTask.setId(Integer.parseInt(taskInfo.get(0)));
                        fileBackedTaskManager.addSubTask(subTask);
                        break;
                }
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("ошибка считывания данных из файла", exception);
        }
        return fileBackedTaskManager;
    }

    private void save() throws ManagerSaveException {
        try (FileWriter fileWriter = new FileWriter(path.toFile(), UTF_8)) {
            fileWriter.write("id,type,name,status,description,epic\n");
            for (Task task : getTasks()) {
                fileWriter.write(task.convertToCSV() + "\n");
            }
            for (Epic epic : getEpics()) {
                fileWriter.write(epic.convertToCSV() + "\n");
            }
            for (SubTask subTask : getSubTasks()) {
                fileWriter.write(subTask.convertToCSV() + "\n");
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("ошибка сохранения файла", exception);
        }
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(SubTask updatedSubtask) {
        super.updateSubtask(updatedSubtask);
        save();
    }

    @Override
    public void updateTask(Task updatedTask) {
        super.updateTask(updatedTask);
        save();
    }

    private void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    private void addEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
        setEpicTime(epic);
    }

    private void addSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        epics.get(subTask.getEpicId()).getSubtaskIds().add(subTask.getId());
        updateEpicStatus(epics.get(subTask.getEpicId()));
        setEpicTime(epics.get(subTask.getEpicId()));
    }

    @Override
    public Epic createNewEpic(Epic epic) {
        super.createNewEpic(epic);
        save();
        return epic;
    }

    @Override
    public SubTask createNewSubTask(SubTask subTask) {
        super.createNewSubTask(subTask);
        save();
        return subTask;
    }

    @Override
    public Task createNewTask(Task task) {
        super.createNewTask(task);
        save();
        return task;
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    public static void main(String[] args) throws IOException {
        Path currentDir = Paths.get(System.getProperty("user.dir"));
        Path path = Files.createTempFile(currentDir, "tempFile", ".csv");
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(path.toFile());

        Task task = new Task("n", "d", Status.NEW, 5, "2019-01-23T22:20:21.413486");
        taskManager.createNewTask(task);
        Epic epic = new Epic("n", "d", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask = new SubTask("n", "d", Status.NEW, epic.getId(), 5, "2020-01-23T22:20:21.413486");
        taskManager.createNewSubTask(subTask);
        SubTask subTask2 = new SubTask("n2", "d2", Status.DONE, 2, 999, "2026-01-23T22:20:21.413486");
        taskManager.createNewSubTask(subTask2);

        TaskManager newTaskManager = FileBackedTaskManager.loadFromFile(path.toFile());

        System.out.println(newTaskManager.getTasks());
        System.out.println(newTaskManager.getEpics());
        System.out.println(newTaskManager.getSubTasks());
        Files.delete(path);
    }

}
