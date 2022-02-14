package com.example.photosudoku;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.example.photosudoku.processing.ImageProcessingThread;
import com.example.photosudoku.processing.ProcessingTask;
import com.example.photosudoku.processing.ProcessingTaskHandler;
import com.google.android.material.snackbar.Snackbar;

import org.opencv.android.OpenCVLoader;

import java.io.IOException;

public class HomePage extends AppCompatActivity implements ProcessingTaskHandler {
    private boolean isProcessingImage;
    Handler handler;
    ImageProcessingThread t;
    Snackbar bar;
    ConstraintLayout mainLaout;

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
        new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                processLoadedImage(uri);
            }
        }
    );

    private static String TAG = "CameraActivity";
    static{
        if(OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV is connected or configured successfully.");
        }
        else{
            Log.d(TAG, "OpenCV not working or loaded.");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        this.isProcessingImage = false;

        mainLaout = findViewById(R.id.main_page_layout);

        bar = Snackbar.make(mainLaout,getString(R.string.processing),Snackbar.LENGTH_INDEFINITE);

        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                ProcessingTask task = (ProcessingTask)msg.obj;
                if(msg.what == ProcessingTask.STATE_COMPLETE){
                    bar.dismiss();
                    isProcessingImage = false;
                    Intent sudokuDisplayIntent = new Intent(HomePage.this,SudokuDisplayPage.class);
                    int[][] sudoku = (int[][])task.getObject();
                    sudokuDisplayIntent.putExtra(SudokuDisplayPage.SUDOKU_KEY,sudoku);
                    startActivity(sudokuDisplayIntent);
                    t=null;
                }
                else if(msg.what == ProcessingTask.STATE_LOCATING_SUDOKU || msg.what == ProcessingTask.STATE_READING_NUMBERS){
                    bar.setText((String)task.getObject());
                }
                else if(msg.what == ProcessingTask.STATE_ERROR){
                    Snackbar.make(mainLaout,(String)task.getObject(),Snackbar.LENGTH_LONG).show();
                    isProcessingImage = false;
                    bar.dismiss();
                    t=null;
                }
            }
        };
    }

    public void OpenCamera(View view){
        if(!this.isProcessingImage){
            Intent cameraPageIntent = new Intent(this,CameraPage.class);
            startActivity(cameraPageIntent);
        }
    }

    public void OpenSudokuEditor(View view){
        if(!this.isProcessingImage){
            Intent sudokuEditIntent = new Intent(this,SudokuDisplayPage.class);
            int[][] empty = new int[9][9];
            sudokuEditIntent.putExtra(SudokuDisplayPage.SUDOKU_KEY,empty);
            //sudokuEditIntent.putExtra(SudokuDisplayPage.SUDOKU_KEY, SudokuUtils.EXAMPLE1);
            startActivity(sudokuEditIntent);
        }
    }

    public void LoadSudokuFromGallery(View view){
        if(!this.isProcessingImage){
            mGetContent.launch("image/*");
        }
    }

    private void processLoadedImage(Uri uri){
        this.isProcessingImage = true;
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (IOException e) {
            bar.setText(e.getMessage());
            bar.show();
        }
        t = new ImageProcessingThread(bitmap,90,HomePage.this, getApplicationContext());
        bar.setDuration(Snackbar.LENGTH_INDEFINITE);
        bar.show();
        t.start();

    }

    @Override
    public void handleProcessingTask(ProcessingTask task, int state) {
        Message message = handler.obtainMessage(state,task);
        message.sendToTarget();
    }
}