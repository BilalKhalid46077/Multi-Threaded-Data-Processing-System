package main

import (
	"fmt"
	"log"
	"os"
	"sync"
)

func main() {
	log.Println("****Multithreading And Data Processing System Started****")

	// Queue
	tasks := make(chan string, 10)
	var wg sync.WaitGroup
	var fileMutex sync.Mutex

	// Open the shared output file
	file, err := os.Create("output_go.txt")
	if err != nil {
		log.Fatalf("Failed to initialize output file: %v\n", err)
	}
	defer file.Close() // Graceful cleanup

	// Concurrency Management: Start 3 worker goroutines
	numWorkers := 3
	for i := 1; i <= numWorkers; i++ {
		wg.Add(1)
		go worker(i, tasks, &wg, &fileMutex, file)
	}

	// Add task in queue
	for i := 1; i <= 10; i++ {
		tasks <- fmt.Sprintf("Task_Data_%d", i)
	}

	// Closing the channel signals workers that no more tasks are coming
	close(tasks)

	// Wait for all workers to safely terminate
	wg.Wait()
	log.Println("***Multithreading And Data Processing System Terminated Safely***")
}
