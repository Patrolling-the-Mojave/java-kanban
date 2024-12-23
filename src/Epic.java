import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String taskName, String description, Status status) {
        super(taskName, description, status);

    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }


    public void removeSubtask(Integer subtaskId) {
        if (subtaskIds.contains(subtaskId)) {
            subtaskIds.remove(subtaskId);
        }

    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "Status=" + getStatus()+
                ", id=" + getId() +
                ", subtaskIds=" + subtaskIds +
                '}';
    }
}