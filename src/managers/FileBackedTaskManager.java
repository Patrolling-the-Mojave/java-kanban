package managers;

import Exceptions.ManagerSaveException;
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

    public FileBackedTaskManager(HistoryManager historyManager, Path path) {
        super(historyManager);
        this.path = path;
    }

    static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(Managers.getDefaultHistory(), file.toPath());
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, UTF_8))) {
            bufferedReader.readLine();
            while (bufferedReader.ready()) {
                List<String> taskInfo = new ArrayList<>(Arrays.asList(bufferedReader.readLine().split(",")));
                switch (taskInfo.get(1)) {
                    case "TASK":
                        Task task = new Task(taskInfo.get(2), taskInfo.get(4), Status.valueOf(taskInfo.get(3)));
                        task.setId(Integer.parseInt(taskInfo.get(0)));
                        fileBackedTaskManager.updateTask(task);
                        break;
                    case "EPIC":
                        Epic epic = new Epic(taskInfo.get(2), taskInfo.get(4), Status.valueOf(taskInfo.get(3)));
                        epic.setId(Integer.parseInt(taskInfo.get(0)));
                        fileBackedTaskManager.updateEpic(epic);
                        break;
                    case "SUBTASK":
                        SubTask subTask = new SubTask(taskInfo.get(2), taskInfo.get(4), Status.valueOf(taskInfo.get(3)), Integer.parseInt(taskInfo.get(5)));
                        subTask.setId(Integer.parseInt(taskInfo.get(0)));
                        fileBackedTaskManager.updateSubtask(subTask);
                        break;
                }
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("ошибка считывания данных из файла");
        }
        return fileBackedTaskManager;
    }

    private void save() {
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
            throw new ManagerSaveException("ошибка сохранения файла");
        }
    }


    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public ArrayList<SubTask> getSubTasksByEpicId(int id) {
        return super.getSubTasksByEpicId(id);
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
    public Epic getEpicById(int id) {
        return super.getEpicById(id);
    }

    @Override
    public SubTask getSubtaskById(int id) {
        return super.getSubtaskById(id);
    }

    @Override
    public Task getTaskById(int id) {
        return super.getTaskById(id);
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

    @Override
    public List<Epic> getEpics() {
        return super.getEpics();
    }

    @Override
    public List<SubTask> getSubTasks() {
        return super.getSubTasks();
    }

    @Override
    public List<Task> getTasks() {
        return super.getTasks();
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    public static void main(String[] args) throws IOException {
        Path currentDir = Paths.get(System.getProperty("user.dir"));
        Path path = Files.createTempFile(currentDir, "tempFile", ".csv");
        TaskManager taskManager = new FileBackedTaskManager(Managers.getDefaultHistory(), path);

        Task task = new Task("n", "d", Status.NEW);
        taskManager.createNewTask(task);
        Epic epic = new Epic("n", "d", Status.NEW);
        taskManager.createNewEpic(epic);
        SubTask subTask = new SubTask("n", "d", Status.NEW, epic.getId());
        taskManager.createNewSubTask(subTask);
        SubTask subTask2 = new SubTask("n2", "d2", Status.DONE, 2);
        taskManager.createNewSubTask(subTask2);

        TaskManager newTaskManager = FileBackedTaskManager.loadFromFile(path.toFile());

        System.out.println(newTaskManager.getTasks());
        System.out.println(newTaskManager.getEpics());
        System.out.println(newTaskManager.getSubTasks());

        Files.delete(path);


    }

}
