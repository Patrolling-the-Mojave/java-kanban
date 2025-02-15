package tasks;

public class SubTask extends Task {
    private int epicId;
    private final static TaskType type = TaskType.SUBTASK;

    public int getEpicId() {
        return epicId;
    }

    public SubTask(String taskName, String description, Status status, int epicId) {
        super(taskName, description, status);
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + getId() +
                ", epicId=" + epicId +
                '}';
    }

    @Override
    public TaskType getType() {
        return type;
    }

    @Override
    public String convertToCSV() {
        return String.format("%d,%s,%s,%s,%s,%d", id, type, taskName, status, description, epicId);
    }


}
