public class SubTask extends Task {
    private EpicTask epicTask;

    public EpicTask getEpicTask() {
        return epicTask;
    }

    public int getEpicId() {
        return epicTask.getId();
    }

    public SubTask(String taskName, String description, Status status, Task epicTask) {
        super(taskName, description, status);
        this.epicTask = (EpicTask) epicTask;
    }


}
