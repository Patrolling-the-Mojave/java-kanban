package tasks;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Epic extends Task {
    private Set<Integer> subtaskIds = new HashSet<>();
    private static final TaskType type = TaskType.EPIC;
    private LocalDateTime endTime;

    public Epic(String taskName, String description, Status status) {
        super(taskName, description, status, 0, LocalDateTime.now().toString());
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setSubtaskIds(Set<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    public Set<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void removeSubtask(Integer subtaskId) {
        subtaskIds.remove(subtaskId);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "endTime=" + (endTime != null ? endTime.format(FORMATTER) : "не задано") +
                ", subtaskIds=" + subtaskIds +
                ", description='" + description + '\'' +
                ", duration=" + (duration != null ? duration.toMinutes() : "не задано") +
                ", id=" + id +
                ", startTime=" + (startTime != null ? startTime.format(FORMATTER) : "не задано") +
                ", status=" + status +
                ", taskName='" + taskName + '\'' +
                '}';
    }

    @Override
    public String convertToCSV() {
        return (startTime != null ? String.format("%d,%s,%s,%s,%s,%d,%s", id, type, taskName, status, description, duration.toMinutes(), startTime) :
                String.format("%d,%s,%s,%s,%s", id, type, taskName, status, description));
    }
}
