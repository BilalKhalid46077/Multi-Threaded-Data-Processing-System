import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

// 2. Worker Threads
class WorkerClass implements Runnable {
    private static final Logger logger = Logger.getLogger("Worker");
    private final int workerId;
    private final QueueClass<String> sharedQueue;
    private final BufferedWriter writer;
    private final ReentrantLock fileLock;

    public WorkerClass(int workerId, QueueClass<String> sharedQueue, BufferedWriter writer, ReentrantLock fileLock) {
        this.workerId = workerId;
        this.sharedQueue = sharedQueue;
        this.writer = writer;
        this.fileLock = fileLock;
    }

    @Override
    public void run() {
        logger.info("Worker " + workerId + " started.....");

        while (true) {
            String task = sharedQueue.getTask();

            // Graceful exit when the queue is empty
            if (task == null) {
                logger.info("Worker " + workerId + " found empty queue. Terminating "+ "Worker " + workerId);
                break;
            }

            try {
                // Exception Case
                if (task.contains("5")) {
                    throw new IllegalArgumentException("Corrupted data encountered in " + task);
                }

                // Simulate computational delay
                Thread.sleep(500);
                String result = "Worker " + workerId + " processed: " + task + "\n";

                // Safely write to the shared output file
                fileLock.lock();
                try {
                    writer.write(result);
                    writer.flush();
                } finally {
                    fileLock.unlock();
                }

            } catch (IllegalArgumentException e) {
                // Catch task-specific errors so the worker thread DOES NOT die.
                // It logs the error and loops back to get the next task.
                logger.log(Level.WARNING, "Worker " + workerId + " failed to process task due to bad data.", e);
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Worker " + workerId + " was interrupted.", e);
                Thread.currentThread().interrupt(); // Restore interrupted status
                break; // Exit the loop if interrupted
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Worker " + workerId + " encountered a file I/O error.", e);
                // Depending on requirements, you might break here if the file is unrecoverable
            }
        }
        logger.info("Worker " + workerId + " completed all tasks.");
    }
}