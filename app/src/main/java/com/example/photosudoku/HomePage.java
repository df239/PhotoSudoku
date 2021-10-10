package com.example.photosudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.opencv.android.OpenCVLoader;

public class HomePage extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
    }

    public void OpenCamera(View view){
        Intent cameraPageIntent = new Intent(this,CameraPage.class);
        startActivity(cameraPageIntent);
    }
}