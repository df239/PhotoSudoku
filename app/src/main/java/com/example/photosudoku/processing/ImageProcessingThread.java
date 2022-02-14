package com.example.photosudoku.processing;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.appcompat.app.AppCompatActivity;

import com.example.photosudoku.CameraPage;
import com.example.photosudoku.R;
import com.google.android.gms.tasks.Task;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ImageProcessingThread extends Thread{

    //String OCRresult = "";
    Bitmap original;
    int rotation;
    ProcessingTaskHandler originalPage;
    Context context;

    int[][] sudokuResult;
    Bitmap processed;

    private PropertyChangeSupport observableSupport;

//    public ImageProcessingThread(Bitmap originalBitmap, int rotationDegrees, PropertyChangeListener listener){
//        super();
//        original = originalBitmap;
//        rotation = rotationDegrees;
//        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
//        observableSupport = new PropertyChangeSupport(this);
//        observableSupport.addPropertyChangeListener(listener);
//    }

    public ImageProcessingThread(Bitmap originalBitmap, int rotationDegrees, ProcessingTaskHandler originalPage, Context context){
        super();
        original = originalBitmap;
        rotation = rotationDegrees;
        this.originalPage = originalPage;
        this.context = context;
        //recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
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
            new ProcessingTask(originalPage,context.getString(R.string.locating_sudoku)).handleDecodeState(ProcessingTask.STATE_LOCATING_SUDOKU);
            Bitmap rotated = rotateImage(original,rotation);
            Bitmap processed = processImage(rotated);
            this.processed = processed;

//            new ProcessingTask(originalPage,processed).handleDecodeState(ProcessingTask.STATE_COMPLETE);

            new ProcessingTask(originalPage,context.getString(R.string.reading_numbers)).handleDecodeState(ProcessingTask.STATE_READING_NUMBERS);
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

    private Bitmap rotateImage(Bitmap bitmap, int rotation){
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap,mat);
        //image rotation: https://www.geeksforgeeks.org/rotating-images-using-opencv-in-java/
        /*
        Point centre = new Point(mat.width()/2.0,mat.height()/2.0);
        Mat rotationMatrix = Imgproc.getRotationMatrix2D(centre,360-rotation,1);
        Size size = new Size(mat.rows(), mat.cols());
        Bitmap bmp = Bitmap.createBitmap(mat.rows(),mat.cols(), Bitmap.Config.ARGB_8888);
        Mat dsc = new Mat(size,CvType.CV_8UC4);
        Imgproc.warpAffine(mat,dsc,rotationMatrix,size);
        */
        Bitmap bmp = null;
        if (mat.height() > mat.width()){
            bmp = bitmap;
        }
        else{
            if(rotation == 90 || rotation == -270){
                bmp = Bitmap.createBitmap(mat.rows(),mat.cols(), Bitmap.Config.ARGB_8888);
                Core.rotate(mat,mat,Core.ROTATE_90_CLOCKWISE);
            }
            else if (rotation == 180 || rotation == -180){
                bmp = Bitmap.createBitmap(mat.cols(),mat.rows(), Bitmap.Config.ARGB_8888);
                Core.rotate(mat,mat,Core.ROTATE_180);
            }
            else{
                bmp = Bitmap.createBitmap(mat.rows(),mat.cols(), Bitmap.Config.ARGB_8888);
                Core.rotate(mat,mat,Core.ROTATE_90_COUNTERCLOCKWISE);
            }
        }

        Utils.matToBitmap(mat,bmp);

        return bmp;
    }

    private Bitmap processImage(Bitmap bitmap) throws Exception{
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap,mat);

        //Mat original = new Mat();
        //Utils.bitmapToMat(bitmap,original);
        int targetWidth = (int)(bitmap.getWidth()*0.75);
        int targetHeight = (int)(bitmap.getHeight());
        //org.opencv.core.Rect roi = new org.opencv.core.Rect((int)(targetWidth*0.2),0,  targetWidth,targetHeight);
        Bitmap tempBitmap = Bitmap.createBitmap(targetWidth,targetHeight, Bitmap.Config.ARGB_8888);
        //Mat mat = new Mat(original,roi);



        //applyCLAHE(mat,mat);

        //copy of the unprocessed mat, used to cut out the found sudoku from
        Mat copy = mat.clone();

        //image processing
        //https://www.pyimagesearch.com/2020/08/10/opencv-sudoku-solver-and-ocr/
        Imgproc.cvtColor(mat,mat,Imgproc.COLOR_RGB2GRAY);
        Utils.matToBitmap(mat,bitmap);
        Mat temp = new Mat();
        Imgproc.bilateralFilter(mat,temp,7,70,100);
        temp.copyTo(mat);
        Utils.matToBitmap(mat,bitmap);
        //Imgproc.GaussianBlur(mat,mat,new org.opencv.core.Size(7,7),3);
        Imgproc.adaptiveThreshold(mat,mat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY, 11, 2);
        Utils.matToBitmap(mat,bitmap);
        //Imgproc.Canny(mat,mat,150,150);
        Imgproc.dilate(mat,mat,new Mat(3,3,0));
        Utils.matToBitmap(mat,bitmap);
        Imgproc.erode(mat,mat,new Mat(2,2,0));
        Utils.matToBitmap(mat,bitmap);
        Core.bitwise_not(mat,mat);
        Utils.matToBitmap(mat,bitmap);
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

        if(maxCurve.total() != 4) throw new Exception(context.getString(R.string.sudoku_not_located_error));

//        //largestContour = new MatOfPoint2f(contours.get(maxAreaIdx));
        Mat copy2 = copy.clone();
        Mat copy_black = mat.clone();
        Imgproc.drawContours(copy2,contours,maxAreaIdx,new Scalar(255,0,0),20);
        Utils.matToBitmap(copy2,bitmap);
        Imgproc.drawContours(copy2,contours,maxAreaIdx,new Scalar(255,0,0),20);
        Utils.matToBitmap(copy_black,bitmap);
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

        Imgproc.warpPerspective(mat, outputMat, perspectiveTransform, new Size(resultValue, resultValue));
        //Imgproc.erode(mat,mat,new Mat(5,5,0));
        Bitmap output = Bitmap.createBitmap(resultValue,resultValue,Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(outputMat,output);

//        Bitmap output = Bitmap.createBitmap(contourRect.width,contourRect.height,Bitmap.Config.ARGB_8888);
//        Mat outputMat = new Mat(copy,contourRect);

        Imgproc.warpPerspective(copy, outputMat, perspectiveTransform, new Size(resultValue, resultValue));
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
        Mat mat = new Mat(input.rows(), input.cols(), input.type());

        Imgproc.cvtColor(input,input,Imgproc.COLOR_BGRA2GRAY);
        Imgproc.bilateralFilter(input,mat,15,70,100);
        Imgproc.adaptiveThreshold(mat,mat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY, 11, 2);

        //Utils.matToBitmap(mat,bitmap);
        Imgproc.erode(mat,mat,new Mat(9,9,0));
        //Utils.matToBitmap(mat,bitmap);
        Imgproc.dilate(mat,mat,new Mat(7,7,0));
        //Utils.matToBitmap(mat,bitmap);
        Core.bitwise_not(mat,mat);
        Utils.matToBitmap(mat,bitmap);

        Mat output = new Mat();
        Core.bitwise_not(output,output);
        Core.bitwise_and(input,input,input,mat);

        //Utils.matToBitmap(input,bitmap);

        /*
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap,mat);
        Imgproc.dilate(mat,mat,new Mat(9,9,0));
        Imgproc.erode(mat,mat,new Mat(7,7,0));
        Utils.matToBitmap(mat,bitmap);
        */

        int[][] sudokuMatrix = new int[9][9];

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int Wninth = (int)(width / 9);
        int Hninth = (int)(height / 9);

        int row = 0,col = 0;

        double ratio = 0.5;
        int Wactual = (int)(Wninth * ratio);
        int Hactual = (int)(Hninth * ratio);
        int Wdisplacement = (int)((Wninth - Wactual)/2);
        int Hdisplacement = (int)((Hninth - Hactual)/2);

        String[] vals = {"1","2","3","4","5","6","7","8","9"};

        //List<Task<Text>> tasks = new ArrayList<Task<Text>>();
        List<Integer> xcoords = new ArrayList<Integer>();
        List<Integer> ycoords = new ArrayList<Integer>();
        List<String> values = new ArrayList<String>();
        ExecutorService service = Executors.newFixedThreadPool(5);

        boolean valFound = false;
        try{
            for (row = 0; row < 9; row++){
                for (col = 0; col < 9; col++){
                    Rect centerroi = new Rect(Wninth * col + Wdisplacement, Hninth * row + Hdisplacement, Wactual, Hactual);
                    Mat centerMat = new Mat(mat,centerroi);

                    double nonZero = Core.countNonZero(centerMat);
                    int size = centerMat.rows()*centerMat.cols();
                    if (nonZero / size > 0.05){
                        Mat cellMat = new Mat(mat,new Rect(Wninth * col, Hninth * row, Wninth, Hninth));
                        Bitmap cellBmp = Bitmap.createBitmap(Wninth,Hninth,Bitmap.Config.ARGB_8888);

                        Utils.matToBitmap(cellMat,cellBmp);

                        //OCR here

                        InputImage image = InputImage.fromBitmap(cellBmp, 0);
                        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
                        //tasks.add(recognizer.process(image));
                        int finalRow = row;
                        int finalCol = col;

                        service.submit(()->{
                            String str = null;
                            try {
                                str = Tasks.await(recognizer.process(image)).getText().trim();
                            } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                            }
                            values.add(str);
                            xcoords.add(finalRow);
                            ycoords.add(finalCol);
                        });
                    }
                }
            }

            //String[] strings = tasks.get(0).getResult().getText().trim().split("\\W+");
            service.shutdown();
            if(!service.awaitTermination(8, TimeUnit.SECONDS)) service.shutdownNow();
            for (int i = 0; i < values.size(); i++) {
                //Text result = Tasks.await(recognizer.process(image));
                String str = values.get(i);
                if(str.equals("A")){sudokuMatrix[xcoords.get(i)][ycoords.get(i)] = 4; valFound = true;}
                else if (str.equals("I") || str.equals("l")){sudokuMatrix[xcoords.get(i)][ycoords.get(i)] = 1; valFound = true;}
                else if (!str.equals("")){
                    int index = -1;
                    for (String val : vals){
                        if (str.contains(val)){
                            index = str.indexOf(val);
                            break;
                        }
                    }
                    if (index != -1){
                        int number = Integer.parseInt(String.valueOf(str.charAt(index)));
                        sudokuMatrix[xcoords.get(i)][ycoords.get(i)] = number;
                        valFound = true;
                    }
                }

            }

        }
        catch (Exception e){
            throw new Exception(context.getString(R.string.ocr_error)+" R"+(row+1)+"C"+(col+1));
        }

        if (!valFound){
            throw new Exception(context.getString(R.string.ocr_error));
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
