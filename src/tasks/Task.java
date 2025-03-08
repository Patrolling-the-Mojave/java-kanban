package tasks;

import exceptions.TaskTimeException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Task {

    protected int id;
    protected String taskName;
    protected String description;
    protected Status status;
    protected Duration duration;
    protected LocalDateTime startTime;
    private static final TaskType type = TaskType.TASK;
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy|HH:mm");


    public Task(String taskName, String description, Status status, int duration, String startTime) {
        this.taskName = taskName;
        this.description = description;
        this.status = status;
        this.duration = Duration.ofMinutes(duration);
        this.startTime = LocalDateTime.parse(startTime);
    }

    public Task(String taskName, String description, Status status) {
        this.taskName = taskName;
        this.description = description;
        this.status = status;
    }

    public LocalDateTime getEndTime() {
        LocalDateTime endTime;
        try {
            endTime = startTime.plus(duration);
        } catch (NullPointerException ex) {
            throw new TaskTimeException("время старта не задано");
        }
        return endTime;
    }

    public void setDuration(long durationInMinutes) {
        this.duration = Duration.of(durationInMinutes, ChronoUnit.MINUTES);
    }

    public long getDuration() {
        return duration.toMinutes();
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task task)) return false;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "description='" + description + '\'' +
                ", id=" + id +
                ", taskName='" + taskName + '\'' +
                ", status=" + status +
                ", duration=" + (duration != null ? duration.toMinutes() : "не задано") +
                ", startTime=" + (startTime != null ? startTime.format(FORMATTER) : "не задано") +
                '}';
    }

    public String getTaskName() {
        return taskName;
    }

    public String getDescription() {
        return description;
    }

    public String convertToCSV() {
        if (startTime != null) {
            return String.format("%d,%s,%s,%s,%s,%d,%s", id, type, taskName, status, description, duration.toMinutes(), startTime);
        }
        return String.format("%d,%s,%s,%s,%s", id, type, taskName, status, description);

    }
}
