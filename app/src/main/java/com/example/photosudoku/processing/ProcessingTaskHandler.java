package com.example.photosudoku.processing;

public interface ProcessingTaskHandler {
    //interface that receives the task send from the ImageProcessingThread
    void handleProcessingTask(ProcessingTask task, int state);
}
