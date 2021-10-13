package com.example.photosudoku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.impl.CameraCaptureMetaData;
import androidx.camera.core.impl.ImageCaptureConfig;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.util.concurrent.ListenableFuture;

import org.jetbrains.annotations.NotNull;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class CameraPage extends AppCompatActivity {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    PreviewView previewView;
    TextView textview2;
    ImageView imageView;

    ImageCapture imageCapture;

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
                Log.d(TAG,Integer.toString(rotation));
                Bitmap bitmap = toBitmap(image);
                Bitmap processed = processImage(bitmap,rotation);
                imageView.setImageBitmap(processed);
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

    private Bitmap processImage(Bitmap bitmap, int rotation){
//        Mat mat = new Mat();
//        Utils.bitmapToMat(bitmap,mat);

        Mat original = new Mat();
        Utils.bitmapToMat(bitmap,original);
        int targetWidth = (int)(bitmap.getWidth()*0.75);
        int targetHeight = (int)(bitmap.getHeight());
        org.opencv.core.Rect roi = new org.opencv.core.Rect((int)(targetWidth*0.2),0,  targetWidth,targetHeight);
        Bitmap tempBitmap = Bitmap.createBitmap(targetWidth,targetHeight, Bitmap.Config.ARGB_8888);
        Mat mat = new Mat(original,roi);

        //image rotation: https://www.geeksforgeeks.org/rotating-images-using-opencv-in-java/
        Point centre = new Point(mat.rows()/2.0,mat.cols()/2.0);
        Mat rotationMatrix = Imgproc.getRotationMatrix2D(centre,360-rotation,1);
        Imgproc.warpAffine(mat,mat,rotationMatrix,mat.size());

        //applyCLAHE(mat,mat);

        Mat copy = mat.clone();

        //image processing
        Imgproc.cvtColor(mat,mat,Imgproc.COLOR_RGB2GRAY);
        Imgproc.GaussianBlur(mat,mat,new org.opencv.core.Size(7,7),1);
        Imgproc.Canny(mat,mat,100,150);

        Imgproc.dilate(mat,mat,new Mat(10,10,0));
        Imgproc.erode(mat,mat,new Mat(5,5,0));

        //contour detection
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mat,contours,hierarchy,Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_NONE);
        double maxArea = -1;
        int maxAreaIdx = -1;
        org.opencv.core.Rect contourRect = new Rect();
        MatOfPoint2f largestContour = new MatOfPoint2f();
        for (int idx = 0; idx < contours.size(); idx++) {
            Mat contour = contours.get(idx);
            double contourarea = Imgproc.contourArea(contour);
            if (contourarea > maxArea) {
                MatOfPoint2f curve = new MatOfPoint2f(contours.get(idx).toArray());
                Imgproc.approxPolyDP(curve, largestContour, 0.02 * Imgproc.arcLength(curve, true), true);
                int numberVertices = (int) largestContour.total();
                if(numberVertices >= 4 && numberVertices <= 6){
                    contourRect = Imgproc.boundingRect(largestContour);
                    maxArea = contourarea;
                    maxAreaIdx = idx;
                }
            }
        }
        Imgproc.drawContours(copy,contours,maxAreaIdx,new Scalar(255,0,0),20);

//        List<Point> inputPoints = largestContour.toList();
//        int resultWidth = (int)(inputPoints.get(0).x - inputPoints.get(1).x);
//        int bottomWidth = (int)(inputPoints.get(2).x - inputPoints.get(3).x);
//        if(bottomWidth > resultWidth)
//            resultWidth = bottomWidth;
//
//        int resultHeight = (int)(inputPoints.get(2).y - inputPoints.get(0).y);
//        int bottomHeight = (int)(inputPoints.get(3).y - inputPoints.get(1).y);
//        if(bottomHeight > resultHeight)
//            resultHeight = bottomHeight;
//
//        List<Point> outputPoints = new ArrayList<Point>();
//        outputPoints.add(new Point(0,0));
//        outputPoints.add(new Point(targetWidth,0));
//        outputPoints.add(new Point(0,targetHeight));
//        outputPoints.add(new Point(targetWidth,targetHeight));
//        Mat startM = Converters.vector_Point2f_to_Mat(inputPoints);
//        Mat endM = Converters.vector_Point2f_to_Mat(outputPoints);
//        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(startM, endM);
//        Mat outputMat = new Mat(resultWidth,resultHeight, CvType.CV_8UC4);
//        Imgproc.warpPerspective(copy, outputMat, perspectiveTransform, new Size(resultWidth, resultHeight));
//        Bitmap output = Bitmap.createBitmap(resultWidth,resultHeight,Bitmap.Config.ARGB_8888);


        Bitmap output = Bitmap.createBitmap(contourRect.width,contourRect.height,Bitmap.Config.ARGB_8888);
        Mat outputMat = new Mat(copy,contourRect);

        Utils.matToBitmap(outputMat,output);
        return output;

//        Utils.matToBitmap(mat,bitmap);
//        return bitmap;
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