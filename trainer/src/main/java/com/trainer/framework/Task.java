package com.trainer.framework;

public interface Task {
    /**
     * Checks if the task should be executed.
     * @return true if the task should run
     */
    boolean accept();

    /**
     * Executes the task logic.
     * @return The delay in milliseconds before the next loop
     */
    int execute();

    /**
     * @return A short description of the current task for painting.
     */
    String status();
}
