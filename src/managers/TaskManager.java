package managers;

import exceptions.OverLappingTimeException;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TaskManager {

    List<Task> getTasks();

    List<SubTask> getSubTasks();

    List<Epic> getEpics();

    void removeAllTasks();

    void removeAllSubtasks();

    void removeAllEpics();

    Optional<Task> getTaskById(int id);

    Optional<SubTask> getSubtaskById(int id);

    Optional<Epic> getEpicById(int id);

    Task createNewTask(Task task) throws OverLappingTimeException;

    SubTask createNewSubTask(SubTask subTask) throws OverLappingTimeException;

    Epic createNewEpic(Epic epic);

    void updateTask(Task updatedTask) throws OverLappingTimeException;

    void updateSubtask(SubTask updatedSubtask) throws OverLappingTimeException;

    void updateEpic(Epic epic);

    void removeTaskById(int id);

    void removeSubtaskById(int id);

    void removeEpicById(int id);

    List<SubTask> getSubTasksByEpicId(int id);

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();
}
