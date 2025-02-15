package tasks;

import java.util.Objects;

public class Task {

    protected int id;
    protected String taskName;
    protected String description;
    protected Status status;
    private static final TaskType type = TaskType.TASK;

    public Task(String taskName, String description, Status status) {
        this.taskName = taskName;
        this.description = description;
        this.status = status;
    }

    public TaskType getType() {
        return type;
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
        return "tasks.Task{" +
                "id=" + id +
                ", taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    public String getTaskName() {
        return taskName;
    }

    public String getDescription() {
        return description;
    }

    public String convertToCSV() {
        return String.format("%d,%s,%s,%s,%s", id, type, taskName, status, description);
    }
}
