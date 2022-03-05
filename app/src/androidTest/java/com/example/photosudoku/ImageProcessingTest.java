package com.example.photosudoku;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.photosudoku.processing.ImageProcessingThread;
import com.example.photosudoku.processing.ProcessingTask;
import com.example.photosudoku.processing.ProcessingTaskHandler;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

public class ImageProcessingTest implements ProcessingTaskHandler {

    @Test
    public void scanning_from_photo_test(){
        //Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        //Bitmap bmp = convertStringToBitmap(context.getString(R.string.sudokuString1));
        //ImageProcessingThread t = new ImageProcessingThread(bmp,0,ImageProcessingTest.this,context);
        //t.start();
    }

    @Override
    public void handleProcessingTask(ProcessingTask task, int state) {
        if(state == ProcessingTask.STATE_COMPLETE){
            Assert.assertArrayEquals(scanning1,(int[][])task.getObject());
        }
    }

    //http://www.java2s.com/example/android/graphics/convert-string-to-bitmap.html
    //https://stackoverflow.com/questions/22532136/android-how-to-create-a-bitmap-from-a-string
    public static Bitmap convertStringToBitmap(String string) {
        byte[] byteArray1;
        byteArray1 = Base64.decode(string, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray1, 0,
                byteArray1.length);/* w  w  w.ja va 2 s  .  c om*/
        return bmp;
    }

    public static int[][] scanning1 = {
            {8,0,0,0,1,0,0,0,9},
            {0,5,0,8,0,7,0,1,0},
            {0,0,4,0,9,0,7,0,0},
            {0,6,0,7,0,1,0,2,0},
            {5,0,8,0,6,0,1,0,7},
            {0,1,0,5,0,2,0,9,0},
            {0,0,7,0,4,0,6,0,0},
            {0,8,0,3,0,9,0,4,0},
            {3,0,0,0,5,0,0,0,8}
    };
}
