package main

import (
	"fmt"
	"strings"
	"time"
)

// NEW: Helper function demonstrating Go's idiomatic error handling
func processTask(task string) (string, error) {
	// Simulate bad/corrupted data
	if strings.Contains(task, "5") {
		return "", fmt.Errorf("corrupted data encountered in %s", task)
	}

	// Simulate computational delay
	time.Sleep(500 * time.Millisecond)
	return fmt.Sprintf("processed: %s\n", task), nil
}
