package com.example.photosudoku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.photosudoku.processing.ImageProcessingThread;
import com.example.photosudoku.processing.ProcessingTask;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;

import org.jetbrains.annotations.NotNull;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class CameraPage extends AppCompatActivity implements PropertyChangeListener {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    PreviewView previewView;
    TextView textview2;
    ImageView imageView;
    Snackbar bar;

    ImageCapture imageCapture;
    Handler handler;
    ImageProcessingThread t;


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
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setContentView(R.layout.activity_camera_page);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 100);
        }

        previewView = (PreviewView)findViewById(R.id.previewView);
        textview2 = (TextView)findViewById(R.id.textView2);
        imageView = (ImageView)findViewById(R.id.imageView2);
        bar = Snackbar.make(imageView,"Processing...",Snackbar.LENGTH_INDEFINITE);

        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                ProcessingTask task = (ProcessingTask)msg.obj;
                if(msg.what == ProcessingTask.STATE_COMPLETE){
                    bar.dismiss();
                    imageView.setImageBitmap((Bitmap)task.getObject());
                    t=null;
                }
                else if(msg.what == ProcessingTask.STATE_LOCATING_SUDOKU || msg.what == ProcessingTask.STATE_READING_NUMBERS){
                    bar.setText((String)task.getObject());
                }
                else if(msg.what == ProcessingTask.STATE_ERROR){
                    bar.setText((String)task.getObject());
                    t=null;
                    Log.d(TAG,(String)task.getObject());
                }
            }
        };

        //matches camera process to a single camera process provider
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        //adds a task to the process camera provider
        cameraProviderFuture.addListener(() -> {
            try{
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
            }catch(ExecutionException e){
                e.printStackTrace();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }, getExecutor());
    }

    //return executor that will run enqueued tasks on the main thread associated with the context
    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    private void startCameraX(@NotNull ProcessCameraProvider cameraProvider){
        //selects the camera to use for the use case
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        //creates a preview object and conencts it to the layout element previewView
        //ImageAnalysis analysis = setAnalysis();
        Preview preview = setPreview();

        imageCapture = setImageCapture();
        cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture, preview);
    }

    private Preview setPreview(){
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        return preview;
    }

    private ImageCapture setImageCapture(){
        ImageCapture capture = new ImageCapture.Builder().setTargetRotation(previewView.getDisplay().getRotation()).setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY).build();
        return capture;
    }

    public void TakePicture(View view){
        imageCapture.takePicture(getExecutor(), new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                int rotation = image.getImageInfo().getRotationDegrees();
                try{
                    //ImageProcessingThread t = new ImageProcessingThread(bitmap,rotation,CameraPage.this::propertyChange);
                    if(t==null){
                        Bitmap bitmap = toBitmap(image);
                        t = new ImageProcessingThread(bitmap,rotation,CameraPage.this);
                        bar.setDuration(Snackbar.LENGTH_INDEFINITE);
                        bar.show();
                        t.start();
                    }
//                    while(t.isAlive()){
//                        bar.setText(t.getSnackbarText());
//                        Log.d(TAG,t.getSnackbarText());
//                    }
//                    int[][] sudoku = t.getSudokuResult();
//                    Log.d(TAG,sudoku.toString());
                    //bar.dismiss();
                    //imageView.setImageBitmap(t.getProcessedBitmap());
                }
                catch (Exception e){
                    Snackbar.make(findViewById(R.id.imageView2),e.getMessage(),Snackbar.LENGTH_LONG).show();
                    //Log.d(TAG,e.getMessage());
                }
                image.close();
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                super.onError(exception);
            }
        });
    }


    //https://gist.github.com/moshimore/dfe5cf0216a520a8fef55ebe58a7ebe4
    private Bitmap toBitmap(ImageProxy image) {
        ImageProxy.PlaneProxy planeProxy = image.getPlanes()[0];
        ByteBuffer buffer = planeProxy.getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public void handleProcessingTask(ProcessingTask task, int state){
        Message message = handler.obtainMessage(state,task);
        message.sendToTarget();
    }




    //https://stackoverflow.com/questions/24341114/simple-illumination-correction-in-images-opencv-c
    private void applyCLAHE(Mat srcArry, Mat dstArry) {
        //Function that applies the CLAHE algorithm to "dstArry".

        if (srcArry.channels() >= 3) {
            // READ RGB color image and convert it to Lab
            Mat channel = new Mat();
            Imgproc.cvtColor(srcArry, dstArry, Imgproc.COLOR_RGB2Lab);

            // Extract the L channel
            Core.extractChannel(dstArry, channel, 0);

            // apply the CLAHE algorithm to the L channel
            CLAHE clahe = Imgproc.createCLAHE();
            clahe.setClipLimit(4);
            clahe.apply(channel, channel);

            // Merge the the color planes back into an Lab image
            Core.insertChannel(channel, dstArry, 0);

            // convert back to RGB
            Imgproc.cvtColor(dstArry, dstArry, Imgproc.COLOR_Lab2RGB);

            // Temporary Mat not reused, so release from memory.
            channel.release();
        }

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getNewValue() instanceof int[][]){
            int[][] sudoku = (int[][])evt.getNewValue();
        }
        else if (evt.getNewValue() instanceof Bitmap){
            imageView.setImageBitmap((Bitmap)evt.getNewValue());
        }
    }


    /*
    //https://developer.android.com/training/camerax/analyze
    private ImageAnalysis setAnalysis() {
        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        imageAnalysis.setAnalyzer(getExecutor(), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {
                int rotation = image.getImageInfo().getRotationDegrees();
                //Log.d(TAG,Integer.toString(rotation));
                Bitmap bitmap = toBitmap(image);
                Bitmap processed = processImage(bitmap,rotation);
                imageView.setImageBitmap(processed);
                image.close();
            }
        });
        return imageAnalysis;
    }*/


}