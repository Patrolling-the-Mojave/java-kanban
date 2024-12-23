import java.util.*;

public class TaskManager {

    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, SubTask> subTasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    public static int globalId = 1;

    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    public Map<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllSubtasks() {
        for (Epic epic:epics.values()){
            epic.getSubtaskIds().clear();
        }
        subTasks.clear();

    }

    public void removeAllEpics() {
        epics.clear();
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

    public void createNewTask(Task task) {
        tasks.put(task.getId(), task);
        createNewId();
    }

    public void createNewSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        epics.get(subTask.getEpicId()).getSubtaskIds().add(subTask.getId());
        epicStatusUpdate(epics.get(subTask.getEpicId()));
        createNewId();

    }

    public void createNewEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        createNewId();
    }

    public void updateTask(Task updatedTask) {
        tasks.put(updatedTask.getId(), updatedTask);
    }

    public void updateSubtask(SubTask updatedSubtask) {
        subTasks.put(updatedSubtask.getId(), updatedSubtask);
        epics.get(updatedSubtask.getEpicId()).getSubtaskIds().add(updatedSubtask.getId());
        epicStatusUpdate(epics.get(updatedSubtask.getEpicId()));
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        epicStatusUpdate(epic);
    }

    private void epicStatusUpdate(Epic epic) {
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
        if (isDoneTask) {
            epic.setStatus(Status.DONE);
        } else if (isNewTask) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeSubtaskById(int id) {
        epics.get(subTasks.get(id).getEpicId()).removeSubtask(id);
        epicStatusUpdate(epics.get(subTasks.get(id).getEpicId()));
        subTasks.remove(id);
    }

    public void removeEpicById(int id) {
        epics.remove(id);
    }

    public ArrayList<SubTask> getSubTasksByEpicId(int id) {
        ArrayList<SubTask> subtaskList = new ArrayList<>();
        for(int subTaskId:epics.get(id).getSubtaskIds()){
            subtaskList.add(subTasks.get(subTaskId));
        }
        return subtaskList;
    }

    public static void createNewId() {
        globalId++;
    }


}
