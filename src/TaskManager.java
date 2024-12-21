import java.util.*;

public class TaskManager {
    private Map<Integer, Task> taskMap = new HashMap<>();
    private Map<Integer, SubTask> subTaskMap = new HashMap<>();

    public ArrayList<Task> getTasks() {
        ArrayList<Task> taskList = new ArrayList<>();
        for (Task task : taskMap.values()) {
            taskList.add(task);
        }
        for (Task task : subTaskMap.values()) {
            taskList.add(task);
        }
        return taskList;
    }

    public void removeAllTasks() {
        taskMap.clear();
        subTaskMap.clear();
    }

    public Task getTaskById(int id) {
        if (taskMap.get(id) == null) {
            return subTaskMap.get(id);
        }
        return taskMap.get(id);
    }

    public void createNewTask(Task task) {
        taskMap.put(task.getId(), task);
        Task.createNewId();
    }

    public void createNewSubTask(SubTask subTask) {
        if (taskMap.get(subTask.getEpicId()) instanceof EpicTask) {
            ((EpicTask) taskMap.get(subTask.getEpicId())).getSubtasksId().add(subTask.getId());
            subTaskMap.put(subTask.getId(), subTask);
            epicStatusUpdate(taskMap.get(subTask.getEpicId()));
            Task.createNewId();

        }
    }

    public void updateTask(Task updatedTask) {
        if (updatedTask instanceof SubTask) {
            subTaskMap.put(updatedTask.getId(), (SubTask) updatedTask);
            epicStatusUpdate(taskMap.get(((SubTask) updatedTask).getEpicId()));
        } else if (updatedTask instanceof EpicTask) {
            taskMap.put(updatedTask.getId(), updatedTask);
            epicStatusUpdate(updatedTask);
        } else {
            taskMap.put(updatedTask.getId(), updatedTask);
        }
    }

    private void epicStatusUpdate(Task task) {
        boolean isNewTask = true;
        boolean isDoneTask = true;
        for (int subtaskId : ((EpicTask) task).getSubtasksId()) {
            if (subTaskMap.get(subtaskId).getStatus() != Status.DONE) {
                isDoneTask = false;
            }
            if (subTaskMap.get(subtaskId).getStatus() != Status.NEW) {
                isNewTask = false;
            }
        }
        if (isNewTask) {
            task.setStatus(Status.NEW);
        } else if (isDoneTask) {
            task.setStatus(Status.DONE);
        } else {
            task.setStatus(Status.IN_PROGRESS);
        }
    }

    public void removeById(int id) {
        if (taskMap.get(id) == null) {
            for (Task task : taskMap.values()) {
                if (task instanceof EpicTask) {
                    ((EpicTask) task).removeSubtask(id);
                }
            }
            subTaskMap.remove(id);
        } else {
            taskMap.remove(id);
        }
    }

    public ArrayList<SubTask> getSubTasksByEpicId(int id) {
        ArrayList<SubTask> epicSubtasks = new ArrayList<>();
        if (taskMap.get(id) instanceof EpicTask) {
            for (int subtaskId : ((EpicTask) taskMap.get(id)).subtasksId) {
                epicSubtasks.add(subTaskMap.get(subtaskId));
            }
        }
        return epicSubtasks;
    }


}
