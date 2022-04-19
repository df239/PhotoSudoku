package com.example.photosudoku.processing;

import android.app.Activity;
import android.graphics.Camera;

import androidx.appcompat.app.AppCompatActivity;

import com.example.photosudoku.CameraPage;

//class that carries the information such as state and data about a currently undergoing processing task in the ImageProcessingThread
//inspiration: https://stuff.mit.edu/afs/sipb/project/android/docs/training/multiple-threads/communicate-ui.html
public class ProcessingTask {
    private final Object taskObject;
    private final ProcessingTaskHandler handler;

    public static final int STATE_COMPLETE = 0;
    public static final int STATE_LOCATING_SUDOKU = 1;
    public static final int STATE_READING_NUMBERS = 2;
    public static final int STATE_ERROR = -1;

    public ProcessingTask(ProcessingTaskHandler taskHandler, Object obj){
        handler = taskHandler;
        taskObject = obj;
    }

    //by calling this method from the class implementing ProcessingTaksHanler, the handler receives the task content
    public void handleDecodeState(int state){
        handler.handleProcessingTask(this, state);
    }

    public Object getObject(){
        return taskObject;
    }
}
