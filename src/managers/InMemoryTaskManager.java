package managers;

import tasks.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private Managers managers = new Managers();
    private HistoryManager historyManager = managers.getDefaultHistory();
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, SubTask> subTasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private int globalId = 1;

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    private int getNewId() {
        return globalId++;
    }

    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic);
        }
        subTasks.clear();

    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);


    }

    @Override
    public SubTask getSubtaskById(int id) {
        historyManager.add(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Task createNewTask(Task task) {
        task.setId(getNewId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public SubTask createNewSubTask(SubTask subTask) {
        subTask.setId(getNewId());
        subTasks.put(subTask.getId(), subTask);
        epics.get(subTask.getEpicId()).getSubtaskIds().add(subTask.getId());
        updateEpicStatus(epics.get(subTask.getEpicId()));
        return subTask;
    }

    @Override
    public Epic createNewEpic(Epic epic) {
        epic.setId(getNewId());
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
        return epic;
    }

    @Override
    public void updateTask(Task updatedTask) {
        tasks.put(updatedTask.getId(), updatedTask);
    }

    @Override
    public void updateSubtask(SubTask updatedSubtask) {
        subTasks.put(updatedSubtask.getId(), updatedSubtask);
        updateEpicStatus(epics.get(updatedSubtask.getEpicId()));
    }

    @Override
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

    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        epics.get(subTasks.get(id).getEpicId()).removeSubtask(id);
        updateEpicStatus(epics.get(subTasks.get(id).getEpicId()));
        subTasks.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        for (int subTaskId : epics.get(id).getSubtaskIds()) {
            subTasks.remove(subTaskId);
        }
        epics.get(id).getSubtaskIds().clear();
        epics.remove(id);
    }

    @Override
    public ArrayList<SubTask> getSubTasksByEpicId(int id) {
        ArrayList<SubTask> subtaskList = new ArrayList<>();
        for (int subTaskId : epics.get(id).getSubtaskIds()) {
            subtaskList.add(subTasks.get(subTaskId));
        }
        return subtaskList;
    }

}
