package com.example.photosudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SudokuDisplayPage extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku_display_page);

        textView = (TextView)findViewById(R.id.sudokuTextView);

        Intent intent = getIntent();
        int[][] sudoku = (int[][])intent.getSerializableExtra("sudoku");

        String str = arrayToString(sudoku);
        textView.setText(str);
    }

    private String arrayToString(int[][] array){
        StringBuilder sb = new StringBuilder();
        for (int[] row : array){
            for (int col : row){
                if (col == 0){
                    sb.append("_");
                }
                else{
                    sb.append(col);
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}