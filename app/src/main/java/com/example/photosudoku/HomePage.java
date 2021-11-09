package com.example.photosudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import org.opencv.android.OpenCVLoader;

public class HomePage extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
    }

    public void OpenCamera(View view){
        Intent cameraPageIntent = new Intent(this,CameraPage.class);
        startActivity(cameraPageIntent);
    }

    public void OpenSudokuEditor(View view){
        Intent sudokuEditIntent = new Intent(this,SudokuDisplayPage.class);
        int[][] empty = new int[9][9];
        sudokuEditIntent.putExtra(SudokuDisplayPage.SUDOKU_KEY,empty);
        startActivity(sudokuEditIntent);
    }
}