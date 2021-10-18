package com.example.photosudoku.processing;

import android.app.Activity;
import android.graphics.Camera;

import androidx.appcompat.app.AppCompatActivity;

import com.example.photosudoku.CameraPage;

//inspiration: https://stuff.mit.edu/afs/sipb/project/android/docs/training/multiple-threads/communicate-ui.html
public class ProcessingTask {
    private final Object taskObject;
    private final CameraPage UIThread;

    public ProcessingTask(CameraPage thread, Object obj){
        UIThread = thread;
        taskObject = obj;
    }

    public void handleDecodeState(ProcessingState state){
        UIThread.handleProcessingTask(this, state);
    }

    public Object getObject(){
        return taskObject;
    }
}
