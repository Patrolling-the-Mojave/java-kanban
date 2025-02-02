package managers;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Node tail;
    private Node head;
    private int size = 0;
    private final Map<Integer, Node<Task>> nodes = new HashMap<>();

    @Override
    public void add(Task task) {
        if (nodes.containsKey(task.getId())) {
            removeNode(nodes.get(task.getId()));
            addLastNode(nodes.get(task.getId()));
        } else {
            Node node = new Node(task);
            addLastNode(node);
            nodes.put(task.getId(), node);
        }
    }

    @Override
    public void remove(int id) {
        if (!nodes.containsKey(id)) return;
        removeNode(nodes.get(id));
        nodes.remove(id);
        size--;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasks = new ArrayList<>();
        Node currentNode = head;
        for (int i = 0; i < nodes.size(); i++) {
            tasks.add(currentNode.getTask());
            currentNode = currentNode.getNext();
        }
        return tasks;
    }

    private void removeNode(Node node) {
        if (node.getPrev() == null) {
            head = node.getNext();
            if (head != null) {
                head.setPrev(null);
            } else {
                tail = null;
            }

        } else if (node.getNext() == null) {
            tail = node.getPrev();
            if (tail != null) {
                tail.setNext(null);
            } else {
                head = null;
            }
        } else {
            node.getPrev().setNext(node.getNext());
            node.getNext().setPrev(node.getPrev());
        }
        node.setNext(null);
        node.setPrev(null);

    }

    public void addLastNode(Node node) {
        if (head == null && tail == null) { //если в списке нет элементов, то первый элемент будет и первым и последним
            head = node;
            tail = node;
        } else if (head == tail) { //если в списке один элемент, нужно изменить tail и указатель next у head
            node.setPrev(tail);
            head.setNext(node);
            tail = node;
        } else {
            tail.setNext(node); // в остальных случаях необходимо лишь поменять tail на новый узел(с заменой указателей)
            node.setPrev(tail);
            tail = node;
        }
        size++;

    }

    @Override
    public int getSize() {
        return size;
    }
}
