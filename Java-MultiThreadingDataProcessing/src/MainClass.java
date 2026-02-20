import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MainClass {
    // Using for logs
    private static final Logger Log = Logger.getLogger("Main");
    private static final int TOTAL_WORKER = 3;
    private static final int TIME_OUT = 10;
    public static void main(String[] args) {
        Log.info("****Multithreading And Data Processing System Started****");

        QueueClass<String> queue = new QueueClass<>();
        ReentrantLock reentrantLock = new ReentrantLock();

        // Add tasks in queue
        for (int d= 1; d <= 10; d++) {
            queue.addTask("Task_Data_" + d);
        }

        // Concurrency Management
        ExecutorService serviceExecutor = Executors.newFixedThreadPool(3);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output_java.txt"))) {
            // Send workers to the serviceExecutor
            for (int i = 1; i <= TOTAL_WORKER; i++) {
                serviceExecutor.submit(new WorkerClass(i, queue, writer, reentrantLock));
            }

            // Shutdown serviceExecutor and wait for tasks to finish
            serviceExecutor.shutdown();
            if (!serviceExecutor.awaitTermination(TIME_OUT, TimeUnit.SECONDS)) {
                // Add Logs
                Log.warning("Forcing shutdown. Tasks did not finish in time.");
                serviceExecutor.shutdownNow();
            }
        } catch (IOException e) {
            // Show exception in logs
            Log.log(Level.SEVERE, "Failed to initialize or close the output file.", e);
        } catch (InterruptedException e) {
            // Show exception in logs
            Log.log(Level.SEVERE, "Main thread interrupted while waiting for workers.", e);
            serviceExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // End of the all tasks
        Log.info("***Multithreading And Data Processing System Terminated Safely***");
    }
}