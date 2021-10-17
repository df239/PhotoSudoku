package com.example.photosudoku.processing;

import android.graphics.Bitmap;

import com.google.android.gms.tasks.Tasks;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ImageProcessingThread extends Thread{

    TextRecognizer recognizer;
    //String OCRresult = "";
    Bitmap original;
    int rotation;

    int[][] sudokuResult;
    Bitmap processed;

    String snackbarText = "";

    private PropertyChangeSupport observableSupport;

    public ImageProcessingThread(Bitmap originalBitmap, int rotationDegrees, PropertyChangeListener listener){
        super();
        original = originalBitmap;
        rotation = rotationDegrees;
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        observableSupport = new PropertyChangeSupport(this);
        observableSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void run() {
        try{
            notifyListeners("snackbarText",snackbarText,"Locating Sudoku...");
            snackbarText = "Locating Sudoku...";
            Bitmap processed = processImage(original,rotation);
            this.processed = processed;
            notifyListeners("snackbarText",snackbarText,"Reading numbers...");
            snackbarText = "Reading numbers...";
            int[][] sudokuMatrix = getMatrixFromBitmap(processed);
            notifyListeners("sudokuResult",sudokuResult,sudokuMatrix);
            notifyListeners("processed",this.processed,processed);
            sudokuResult = sudokuMatrix;
        }
        catch (Exception e){
            snackbarText = e.getMessage();
        }
    }

    public Bitmap getProcessedBitmap(){
        return  processed;
    }

    public int[][] getSudokuResult(){
        return sudokuResult;
    }

    public String getSnackbarText(){
        return snackbarText;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener){
        observableSupport.addPropertyChangeListener(listener);
    }

    private void notifyListeners(String varName, Object oldVal, Object newVal){
        observableSupport.firePropertyChange(varName,oldVal,newVal);
    }

    private Bitmap processImage(Bitmap bitmap, int rotation) throws Exception{
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
        //https://www.pyimagesearch.com/2020/08/10/opencv-sudoku-solver-and-ocr/
        Imgproc.cvtColor(mat,mat,Imgproc.COLOR_RGB2GRAY);
        Imgproc.GaussianBlur(mat,mat,new org.opencv.core.Size(7,7),3);
        Imgproc.adaptiveThreshold(mat,mat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY, 11, 2);
        //Imgproc.Canny(mat,mat,150,150);
        Imgproc.dilate(mat,mat,new Mat(3,3,0));
        Imgproc.erode(mat,mat,new Mat(2,2,0));
        Core.bitwise_not(mat,mat);
        //Imgproc.dilate(mat,mat,new Mat(2,2,0));

        //contour detection
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mat,contours,hierarchy,Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_NONE);

        //https://stackoverflow.com/questions/17637730/android-opencv-getperspectivetransform-and-warpperspective
        double maxArea = -1;
        int maxAreaIdx = -1;
        MatOfPoint temp_contour = contours.get(0); //the largest is at the index 0 for starting point
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        MatOfPoint2f maxCurve = new MatOfPoint2f();

        for (int idx = 0; idx < contours.size(); idx++) {
            temp_contour = contours.get(idx);
            double contourarea = Imgproc.contourArea(temp_contour);
            //compare this contour to the previous largest contour found
            if (contourarea > maxArea && contourarea > 4000) {
                //check if this contour is a square
                MatOfPoint2f new_mat = new MatOfPoint2f( temp_contour.toArray() );
                int contourSize = (int)temp_contour.total();
                Imgproc.approxPolyDP(new_mat, approxCurve, contourSize*0.05, true);
                if (approxCurve.total() == 4) {
                    maxCurve = approxCurve;
                    maxArea = contourarea;
                    maxAreaIdx = idx;
                }
            }
        }

        if(maxCurve.total() != 4) throw new Exception("Sorry, I could not locate any sudoku. Try again.");

//        //largestContour = new MatOfPoint2f(contours.get(maxAreaIdx));
        //Imgproc.drawContours(copy,contours,maxAreaIdx,new Scalar(255,0,0),20);
//        //https://stackoverflow.com/questions/17361693/cant-get-opencvs-warpperspective-to-work-on-android
//        //https://stackoverflow.com/questions/17637730/android-opencv-getperspectivetransform-and-warpperspective
//        //https://www.pyimagesearch.com/2014/08/25/4-point-opencv-getperspective-transform-example/
        List<Point> inputPoints = orderPoints(maxCurve.toList());
        //Log.d(TAG,inputPoints.toString());
        int resultWidth = (int)(inputPoints.get(0).x - inputPoints.get(1).x);
        int bottomWidth = (int)(inputPoints.get(3).x - inputPoints.get(2).x);
        if(bottomWidth > resultWidth)
            resultWidth = bottomWidth;

        int resultValue = resultWidth;

        int resultHeight = (int)(inputPoints.get(3).y - inputPoints.get(0).y);
        int bottomHeight = (int)(inputPoints.get(2).y - inputPoints.get(1).y);
        if(bottomHeight > resultHeight)
            resultHeight = bottomHeight;

        if (resultHeight > resultValue)
            resultValue = resultHeight;

        List<Point> outputPoints = new ArrayList<Point>();
        outputPoints.add(new Point(resultValue,0));
        outputPoints.add(new Point(0,0));
        outputPoints.add(new Point(0,resultValue));
        outputPoints.add(new Point(resultValue,resultValue));
        Mat startM = Converters.vector_Point2f_to_Mat(inputPoints);
        Mat endM = Converters.vector_Point2f_to_Mat(outputPoints);
        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(startM, endM);
        Mat outputMat = new Mat(resultValue,resultValue, CvType.CV_8UC4);
        Imgproc.warpPerspective(copy, outputMat, perspectiveTransform, new Size(resultValue, resultValue));
        Bitmap output = Bitmap.createBitmap(resultValue,resultValue,Bitmap.Config.ARGB_8888);


//        Bitmap output = Bitmap.createBitmap(contourRect.width,contourRect.height,Bitmap.Config.ARGB_8888);
//        Mat outputMat = new Mat(copy,contourRect);

        Utils.matToBitmap(outputMat,output);
        return output;


//        Utils.matToBitmap(mat,bitmap);
//        return bitmap;
    }

    private List<Point> orderPoints(List<Point> points){
        List<Point> ordered = new ArrayList<Point>();
        for(int i = 0; i < 4; i++){ordered.add(new Point());}
        //Log.d(TAG,points.toString());
        //Log.d(TAG,ordered.toString());
        double xavg = 0;
        double yavg = 0;

        for (Point p : points){
            xavg += p.x / 4;
            yavg += p.y / 4;
        }

        for (Point p : points){
            if (p.x >= xavg && p.y < yavg) {ordered.set(0,p);}
            else if (p.x < xavg && p.y < yavg) {ordered.set(1,p);}
            else if (p.x < xavg && p.y >= yavg) {ordered.set(2,p);}
            else {ordered.set(3,p);}
        }

        return ordered;
    }

    private int[][] getMatrixFromBitmap(Bitmap bitmap) throws ExecutionException, InterruptedException {
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap,mat);

        int[][] sudokuMatrix = new int[9][9];

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int Wninth = (int)(width / 9);
        int Hninth = (int)(height / 9);

        for (int row = 0; row < 9; row++){
            for (int col = 0; col < 9; col++){
                Rect cellroi = new Rect(Wninth * col, Hninth * row, Wninth, Hninth);
                Mat cellMat = new Mat(mat,cellroi);
                Bitmap cellBmp = Bitmap.createBitmap(Wninth,Hninth,Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(cellMat,cellBmp);

                //OCR here
                InputImage image = InputImage.fromBitmap(cellBmp, 0);

                Text result = Tasks.await(recognizer.process(image));
                String str = result.getText();

                if (!str.equals("")){
                    int number = Integer.parseInt(str);
                    sudokuMatrix[row][col] = number;
                }
                //Log.d(TAG,str);
            }
        }

        return sudokuMatrix;
    }
}
