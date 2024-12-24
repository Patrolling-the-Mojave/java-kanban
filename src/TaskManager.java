import java.util.*;

public class TaskManager {

    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, SubTask> subTasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private static int globalId = 1;

    public List<Task> getTasks() {
        List<Task> taskList = new ArrayList<>();
        for (Task task : tasks.values()) {
            taskList.add(task);
        }
        return taskList;
    }

    public int getNewId() {
        return globalId++;
    }

    public List<SubTask> getSubTasks() {
        List<SubTask> subtaskList = new ArrayList<>();
        for (SubTask subTask : subTasks.values()) {
            subtaskList.add(subTask);
        }
        return subtaskList;
    }

    public List<Epic> getEpics() {
        List<Epic> epicList = new ArrayList<>();
        for (Epic epic : epics.values()) {
            epicList.add(epic);
        }
        return epicList;
    }


    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic);
        }
        subTasks.clear();

    }

    public void removeAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);

    }

    public SubTask getSubtaskById(int id) {
        return subTasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Task createNewTask(Task task) {
        task.setId(getNewId());
        tasks.put(task.getId(), task);
        return task;
    }

    public SubTask createNewSubTask(SubTask subTask) {
        subTask.setId(getNewId());
        subTasks.put(subTask.getId(), subTask);
        epics.get(subTask.getEpicId()).getSubtaskIds().add(subTask.getId());
        updateEpicStatus(epics.get(subTask.getEpicId()));
        return subTask;
    }

    public Epic createNewEpic(Epic epic) {
        epic.setId(getNewId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    public void updateTask(Task updatedTask) {
        tasks.put(updatedTask.getId(), updatedTask);
    }

    public void updateSubtask(SubTask updatedSubtask) {
        subTasks.put(updatedSubtask.getId(), updatedSubtask);
        updateEpicStatus(epics.get(updatedSubtask.getEpicId()));
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
    }

    private void updateEpicStatus(Epic epic) {
        boolean isNewTask = true;
        boolean isDoneTask = true;
        for (int subTaskId : epic.getSubtaskIds()) {
            if (subTasks.get(subTaskId).getStatus() != Status.NEW) {
                isNewTask = false;
            }
            if (subTasks.get(subTaskId).getStatus() != Status.DONE) {
                isDoneTask = false;
            }
        }
        if (isNewTask) {
            epic.setStatus(Status.NEW);
        } else if (isDoneTask) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeSubtaskById(int id) {
        epics.get(subTasks.get(id).getEpicId()).removeSubtask(id);
        updateEpicStatus(epics.get(subTasks.get(id).getEpicId()));
        subTasks.remove(id);
    }

    public void removeEpicById(int id) {
        for (int subTaskId : epics.get(id).getSubtaskIds()) {
            subTasks.remove(subTaskId);
        }
        epics.get(id).getSubtaskIds().clear();
        epics.remove(id);
    }

    public ArrayList<SubTask> getSubTasksByEpicId(int id) {
        ArrayList<SubTask> subtaskList = new ArrayList<>();
        for (int subTaskId : epics.get(id).getSubtaskIds()) {
            subtaskList.add(subTasks.get(subTaskId));
        }
        return subtaskList;
    }

}
