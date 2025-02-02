package managers;

import tasks.Task;

public class Node<T> {
    private Task task;
    private Node prev;
    private Node next;

    public Node(Task task){
        this.task = task;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Task getTask() {
        return task;
    }
}
