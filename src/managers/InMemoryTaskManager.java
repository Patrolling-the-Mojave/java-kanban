package managers;


import exceptions.OverLappingTimeException;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private HistoryManager historyManager;
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, SubTask> subTasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime).thenComparing(Task::getId));
    private int globalId = 1;


    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    private int getNewId() {
        if (!(tasks.containsKey(globalId) || subTasks.containsKey(globalId) || epics.containsKey(globalId))) {
            return globalId;
        }
        globalId++;
        return getNewId();
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
        tasks.keySet().stream().forEach(historyManager::remove);
        prioritizedTasks.removeAll(tasks.values());
        tasks.clear();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    @Override
    public void removeAllSubtasks() {
        epics.clear();
        subTasks.keySet().stream().forEach(historyManager::remove);
        prioritizedTasks.removeAll(subTasks.values());
        subTasks.clear();

    }

    @Override
    public void removeAllEpics() {
        subTasks.keySet().stream().forEach(historyManager::remove);
        epics.keySet().stream().forEach(historyManager::remove);
        prioritizedTasks.removeAll(subTasks.values());
        epics.clear();
        subTasks.clear();
    }

    @Override
    public Optional<Task> getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public Optional<SubTask> getSubtaskById(int id) {
        historyManager.add(subTasks.get(id));
        return Optional.ofNullable(subTasks.get(id));
    }

    @Override
    public Optional<Epic> getEpicById(int id) {
        historyManager.add(epics.get(id));
        return Optional.ofNullable(epics.get(id));
    }

    @Override
    public Task createNewTask(Task task) throws OverLappingTimeException {
        task.setId(getNewId());
        if (task.getStartTime() != null) {
            if (isTaskOverLapping(task)) {
                throw new OverLappingTimeException("задача " + task + " пересекается с другими задачами");
            }
            prioritizedTasks.add(task);
        }
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public SubTask createNewSubTask(SubTask subTask) throws OverLappingTimeException {
        subTask.setId(getNewId());
        if (subTask.getStartTime() != null) {
            if (isTaskOverLapping(subTask)) {
                throw new OverLappingTimeException("подзадача " + subTask + " пересекается с другими задачами");
            }
            prioritizedTasks.add(subTask);
        }
        subTasks.put(subTask.getId(), subTask);
        epics.get(subTask.getEpicId()).getSubtaskIds().add(subTask.getId());
        updateEpicParameters(epics.get(subTask.getEpicId()));
        return subTask;
    }

    @Override
    public Epic createNewEpic(Epic epic) {
        epic.setId(getNewId());
        updateEpicParameters(epic);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public void updateTask(Task updatedTask) throws OverLappingTimeException {
        Task oldTask = tasks.get(updatedTask.getId());
        if (oldTask == null) {
            throw new IllegalArgumentException("задача с id " + updatedTask.getId() + " не найдена.");
        }
        if (isTaskOverLapping(updatedTask)) {
            throw new OverLappingTimeException("новая задача " + updatedTask + " пересекается с другими задачами");
        }
        tasks.put(updatedTask.getId(), updatedTask);
        prioritizedTasks.remove(oldTask);
        prioritizedTasks.add(updatedTask);
    }

    @Override
    public void updateSubtask(SubTask updatedSubtask) throws OverLappingTimeException {
        SubTask oldSubTask = subTasks.get(updatedSubtask.getId());
        if (oldSubTask == null) {
            throw new IllegalArgumentException("Подзадача с id " + updatedSubtask.getId() + " не найдена.");
        }
        if (updatedSubtask.getStartTime() != null) {
            if (isTaskOverLapping(updatedSubtask)) {
                throw new OverLappingTimeException("новая подзадача " + updatedSubtask + " пересекается с другими задачами");
            }
            prioritizedTasks.remove(oldSubTask);
            prioritizedTasks.add(updatedSubtask);
        }
        subTasks.put(updatedSubtask.getId(), updatedSubtask);
        epics.get(updatedSubtask.getEpicId()).getSubtaskIds().add(updatedSubtask.getId());
        updateEpicParameters(epics.get(updatedSubtask.getEpicId()));
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic oldEpic = epics.get(epic.getId());
        if (oldEpic == null) {
            throw new IllegalArgumentException("Эпик с id " + epic.getId() + " не найден");
        }
        updateEpicParameters(epic);
        epics.put(epic.getId(), epic);
    }

    public void updateEpicParameters(Epic epic) {
        updateEpicStatus(epic);
        setEpicTime(epic);
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

    private void setEpicTime(Epic epic) {
        epic.setDuration(0);
        List<SubTask> subTasksList = epic.getSubtaskIds().stream()
                .map(subTasks::get)
                .filter(subTask -> subTask.getStartTime() != null)
                .sorted(Comparator.comparing(SubTask::getStartTime))
                .peek(subtask -> epic.setDuration(epic.getDuration() + subtask.getDuration()))
                .toList();
        if (!subTasksList.isEmpty()) {
            epic.setStartTime(subTasksList.getFirst().getStartTime());
            epic.setEndTime(subTasksList.getLast().getEndTime());
        } else {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(0);
        }
    }

    private boolean isTasksOverlap(Task task1, Task task2) {
        return !(task1.getEndTime().isBefore(task2.getStartTime()) || task1.getStartTime().isAfter(task2.getEndTime()));
    }

    private boolean isTaskOverLapping(Task task) {
        return prioritizedTasks.stream().filter(streamTask -> !streamTask.equals(task)).anyMatch(streamTask -> isTasksOverlap(task, streamTask));
    }

    @Override
    public void removeTaskById(int id) {
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        prioritizedTasks.remove(subTasks.get(id));
        epics.get(subTasks.get(id).getEpicId()).removeSubtask(id);
        updateEpicStatus(epics.get(subTasks.get(id).getEpicId()));
        subTasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        prioritizedTasks.remove(epics.get(id));
        epics.get(id).getSubtaskIds().stream()
                .peek(subTasks::remove)
                .forEach(historyManager::remove);
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public List<SubTask> getSubTasksByEpicId(int id) {
        return epics.get(id).getSubtaskIds().stream()
                .map(subTasks::get)
                .sorted(Comparator.comparing(SubTask::getStartTime))
                .toList();
    }

}
