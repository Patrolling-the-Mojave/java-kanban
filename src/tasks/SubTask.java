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

    @Override
    public String toString() {
        return "SubTask{" +
                "taskName='" + taskName + '\'' +
                ", status=" + status +
                ", startTime=" + startTime.format(FORMATTER) +
                ", id=" + id +
                ", duration=" + duration.toMinutes() +
                ", description='" + description + '\'' +
                ", epicId=" + epicId +
                '}';
    }

    @Override
    public String convertToCSV() {
        return String.format("%d,%s,%s,%s,%s,%d,%d,%s", id, type, taskName, status, description, epicId, duration.toMinutes(), startTime);
    }

}
