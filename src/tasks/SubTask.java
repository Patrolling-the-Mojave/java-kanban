package tasks;

public class SubTask extends Task {
    private int epicId;
    private static final TaskType type = TaskType.SUBTASK;

    public int getEpicId() {
        return epicId;
    }

    public SubTask(String taskName, String description, Status status, int epicId, int duration, String startTime) {
        super(taskName, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public SubTask(String taskName, String description, Status status, int epicId) {
        super(taskName, description, status);
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "taskName='" + taskName + '\'' +
                ", status=" + status +
                ", startTime=" + (startTime != null ? startTime.format(FORMATTER) : "не задано") +
                ", id=" + id +
                ", duration=" + (duration != null ? duration.toMinutes() : "не задано") +
                ", description='" + description + '\'' +
                ", epicId=" + epicId +
                '}';
    }

    @Override
    public String convertToCSV() {
        if (startTime == null) {
            return String.format("%d,%s,%s,%s,%s,%d", id, type, taskName, status, description, epicId);
        }
        return String.format("%d,%s,%s,%s,%s,%d,%d,%s", id, type, taskName, status, description, epicId, duration.toMinutes(), startTime);
    }

}
