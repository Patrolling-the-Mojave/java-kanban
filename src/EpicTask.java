import java.util.ArrayList;

public class EpicTask extends Task {
    ArrayList<Integer> subtasksId = new ArrayList<>();

    public EpicTask(String taskName, String description, Status status) {
        super(taskName, description, status);

    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }


    public void removeSubtask(Integer subtaskId) {
        if (subtasksId.contains(subtaskId)) {
            subtasksId.remove(subtaskId);
        }

    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "Status=" + getStatus()+
                ", id=" + getId() +
                ", subtasksId=" + subtasksId +
                '}';
    }
}
