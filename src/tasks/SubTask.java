package tasks;

public class SubTask extends Task {
    private int epicId;

    public int getEpicId() {
        return epicId;
    }

    public SubTask(String taskName, String description, Status status, int epicId) {
        super(taskName, description, status);
        this.epicId = epicId;
    }
}
