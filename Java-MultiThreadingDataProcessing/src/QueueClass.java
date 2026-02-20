import java.util.concurrent.locks.ReentrantLock;
import java.util.LinkedList;
import java.util.Queue;

// Shared Resource Queue Class
class QueueClass<String> {
    private final Queue<String> queue = new LinkedList<>();
    private final ReentrantLock lock = new ReentrantLock();

    // Synchronization
    public void addTask(String task) {
        lock.lock();
        try {
            queue.add(task);
        } finally {
            lock.unlock();
        }
    }

    // Synchronization
    public String getTask() {
        lock.lock();
        try {
            // Returns null if the queue is empty
            return queue.poll();
        } finally {
            lock.unlock();
        }
    }
}