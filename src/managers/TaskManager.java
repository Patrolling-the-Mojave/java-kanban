package managers;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    List<Task> getTasks();

    List<SubTask> getSubTasks();

    List<Epic> getEpics();

    void removeAllTasks();

    void removeAllSubtasks();

    void removeAllEpics();

    Task getTaskById(int id);

    SubTask getSubtaskById(int id);

    Epic getEpicById(int id);

    Task createNewTask(Task task);

    SubTask createNewSubTask(SubTask subTask);

    Epic createNewEpic(Epic epic);

    void updateTask(Task updatedTask);

    void updateSubtask(SubTask updatedSubtask);

    void updateEpic(Epic epic);

    void removeTaskById(int id);

    void removeSubtaskById(int id);

    void removeEpicById(int id);

    ArrayList<SubTask> getSubTasksByEpicId(int id);

    List<Task> getHistory();

}
