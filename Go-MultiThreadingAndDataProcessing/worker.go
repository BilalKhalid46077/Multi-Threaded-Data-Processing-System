package main

import (
	"fmt"
	"log"
	"os"
	"sync"
)

func worker(id int, tasks <-chan string, wg *sync.WaitGroup, fileMutex *sync.Mutex, file *os.File) {
	// Defer ensures graceful exit and WaitGroup cleanup
	defer wg.Done()
	log.Printf("Worker %d started.\n", id)

	for task := range tasks {
		// Attempt to process the task and capture any returned errors
		resultMsg, err := processTask(task)

		// Exception Case
		if err != nil {
			// Log the error and use 'continue' to move on to the next task in the channel
			// This prevents the goroutine from crashing and dying.
			log.Printf("Worker %d failed to process task: %v\n", id, err)
			continue
		}

		result := fmt.Sprintf("Worker %d %s", id, resultMsg)

		// Synchronize writing to the shared file
		fileMutex.Lock()
		_, fileErr := file.WriteString(result)
		if fileErr != nil {
			log.Printf("Worker %d encountered a file I/O error: %v\n", id, fileErr)
		}
		fileMutex.Unlock()
	}

	log.Printf("Worker %d completed all tasks.\n", id)
}
