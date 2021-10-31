package com.example.photosudoku.processing;

import android.graphics.Bitmap;

import com.example.photosudoku.CameraPage;
import com.example.photosudoku.R;
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
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class ImageProcessingThread extends Thread{

    TextRecognizer recognizer;
    //String OCRresult = "";
    Bitmap original;
    int rotation;
    CameraPage originalPage;

    int[][] sudokuResult;
    Bitmap processed;

    private PropertyChangeSupport observableSupport;

    public ImageProcessingThread(Bitmap originalBitmap, int rotationDegrees, PropertyChangeListener listener){
        super();
        original = originalBitmap;
        rotation = rotationDegrees;
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        observableSupport = new PropertyChangeSupport(this);
        observableSupport.addPropertyChangeListener(listener);
    }

    public ImageProcessingThread(Bitmap originalBitmap, int rotationDegrees, CameraPage cameraPage){
        super();
        original = originalBitmap;
        rotation = rotationDegrees;
        originalPage = cameraPage;
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    }

//    public ImageProcessingThread(List<Bitmap> bitmaps, int rotationDegrees, CameraPage cameraPage){
//        super();
//        original = meanBmp(bitmaps);
//        rotation = rotationDegrees;
//        originalPage = cameraPage;
//        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
//    }
//
//    private Bitmap meanBmp(List<Bitmap> bmps){
//        int type = CvType.CV_8UC3;
//        Mat[] mats = new Mat[bmps.size()];
//        for (int i = 0; i < bmps.size(); i++){
//            mats[i] = new Mat(bmps.get(0).getWidth(),bmps.get(0).getHeight(),type);
//            Utils.bitmapToMat(bmps.get(i),mats[i]);
//        }
//
//        Mat mean = mats[0];
//        for (int i = 1; i < mats.length; i++){
//            Imgproc.accumulate(mats[i],mean);
//        }
//
//        Core.divide(mean,new Scalar(bmps.size(),bmps.size(),bmps.size(),bmps.size()),mean);
//        Bitmap output = Bitmap.createBitmap(mean.width(),mean.height(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(mean,output);
//        return output;
//    }


    @Override
    public void run() {
        try{
            new ProcessingTask(originalPage,originalPage.getString(R.string.locating_sudoku)).handleDecodeState(ProcessingTask.STATE_LOCATING_SUDOKU);
            Bitmap processed = processImage(original,rotation);
            this.processed = processed;

//            new ProcessingTask(originalPage,processed).handleDecodeState(ProcessingTask.STATE_COMPLETE);

            new ProcessingTask(originalPage,originalPage.getString(R.string.reading_numbers)).handleDecodeState(ProcessingTask.STATE_READING_NUMBERS);
            int[][] sudokuMatrix = getMatrixFromBitmap(processed);
            new ProcessingTask(originalPage,sudokuMatrix).handleDecodeState(ProcessingTask.STATE_COMPLETE);
            sudokuResult = sudokuMatrix;
        }
        catch (Exception e){
            new ProcessingTask(originalPage,e.getMessage()).handleDecodeState(ProcessingTask.STATE_ERROR);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener){
        observableSupport.addPropertyChangeListener(listener);
    }

    private void notifyListeners(String varName, Object oldVal, Object newVal){
        observableSupport.firePropertyChange(varName,oldVal,newVal);
    }

    private Bitmap processImage(Bitmap bitmap, int rotation) throws Exception{
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap,mat);

        //Mat original = new Mat();
        //Utils.bitmapToMat(bitmap,original);
        int targetWidth = (int)(bitmap.getWidth()*0.75);
        int targetHeight = (int)(bitmap.getHeight());
        //org.opencv.core.Rect roi = new org.opencv.core.Rect((int)(targetWidth*0.2),0,  targetWidth,targetHeight);
        Bitmap tempBitmap = Bitmap.createBitmap(targetWidth,targetHeight, Bitmap.Config.ARGB_8888);
        //Mat mat = new Mat(original,roi);

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

        if(maxCurve.total() != 4) throw new Exception(originalPage.getString(R.string.sudoku_not_located_error));

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
        //Imgproc.erode(mat,mat,new Mat(5,5,0));
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

    private int[][] getMatrixFromBitmap(Bitmap bitmap) throws Exception {
        Mat input = new Mat();
        Utils.bitmapToMat(bitmap,input);
        Imgproc.cvtColor(input,input,Imgproc.COLOR_BGRA2GRAY);
        Mat mat = new Mat(input.rows(), input.cols(), input.type());
        Imgproc.bilateralFilter(input,mat,15,70,100);
        Imgproc.adaptiveThreshold(mat,mat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY, 11, 2);

        Imgproc.erode(mat,mat,new Mat(5,5,0));
        Imgproc.dilate(mat,mat,new Mat(3,3,0));
        Core.bitwise_not(mat,mat);

        Utils.matToBitmap(mat,bitmap);


//        Bitmap temp = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.ARGB_8888);
//        Utils.matToBitmap(mat,temp);

//        Imgproc.cvtColor(mat,mat,Imgproc.COLOR_BGR2GRAY);
//        Imgproc.adaptiveThreshold(mat,mat,255,Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,Imgproc.THRESH_BINARY,101,1);
//        Core.bitwise_not(mat,mat);

        int[][] sudokuMatrix = new int[9][9];

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int Wninth = (int)(width / 9);
        int Hninth = (int)(height / 9);

        int row = 0,col = 0;

        double ratio = 0.85;
        int Wactual = (int)(Wninth * ratio);
        int Hactual = (int)(Hninth * ratio);
        int Wdisplacement = (int)((Wninth - Wactual)/2);
        int Hdisplacement = (int)((Hninth - Hactual)/2);

        try{
            for (row = 0; row < 9; row++){
                for (col = 0; col < 9; col++){
                    Rect cellroi = new Rect(Wninth * col + Wdisplacement, Hninth * row + Hdisplacement, Wactual, Hactual);
                    Mat cellMat = new Mat(mat,cellroi);
                    double nonZero = Core.countNonZero(cellMat);
                    int size = cellMat.rows()*cellMat.cols();
                    if (nonZero / size > 0.04){
                        Bitmap cellBmp = Bitmap.createBitmap(Wactual,Hactual,Bitmap.Config.ARGB_8888);

                        Utils.matToBitmap(cellMat,cellBmp);

                        //OCR here
                        InputImage image = InputImage.fromBitmap(cellBmp, 0);

                        Text result = Tasks.await(recognizer.process(image));
                        String str = result.getText().trim();

                        if (tryParseSudokuDigit(str)){
                            int number = Integer.parseInt(str);
                            sudokuMatrix[row][col] = number;
                        }
                    }
                }
            }
        }
        catch (Exception e){
            throw new Exception(originalPage.getString(R.string.ocr_error)+" R"+(row+1)+"C"+(col+1));
        }


        return sudokuMatrix;
    }

    private boolean tryParseSudokuDigit(String string){
        try{
            int n = Integer.parseInt(string);
            return n - 10 < 0;
        }
        catch (NumberFormatException e){
            return false;
        }
    }
}
