package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds = new ArrayList<>();
    private static final TaskType type = TaskType.EPIC;

    public Epic(String taskName, String description, Status status) {
        super(taskName, description, status);

    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    @Override
    public TaskType getType() {
        return type;
    }

    public void removeSubtask(Integer subtaskId) {
        subtaskIds.remove(subtaskId);
    }

    @Override
    public String toString() {
        return "EpicTask{" + "tasks.Status=" + getStatus() + ", id=" + getId() + ", subtaskIds=" + subtaskIds + '}';
    }

    @Override
    public String convertToCSV() {
        return String.format("%d,%s,%s,%s,%s", id, type, taskName, status, description);
    }
}
