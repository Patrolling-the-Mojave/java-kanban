package servers;

import com.sun.net.httpserver.HttpServer;
import managers.Managers;
import managers.TaskManager;
import servers.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer server;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler(taskManager));
        server.createContext("/subtasks", new SubTaskHandler(taskManager));
        server.createContext("/epics", new EpicHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedTasksHandler(taskManager));
        server.createContext("/history", new HistoryHandler(taskManager));
    }

    public void start() {
        System.out.println("server has started");
        server.start();
    }

    public void stop() {
        System.out.println("server has stopped");
        server.stop(0);
    }

    public static void main(String[] args) {
        try {
            HttpTaskServer server = new HttpTaskServer(Managers.getDefault(Managers.getDefaultHistory()));
            server.start();
        } catch (IOException e) {
            System.out.println("что-то пошло не так");
        }
    }
}
