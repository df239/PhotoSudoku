package com.example.photosudoku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.photosudoku.processing.ImageProcessingThread;
import com.example.photosudoku.sudoku.Solver;
import com.example.photosudoku.sudoku.Sudoku;

public class Solving_page extends AppCompatActivity {

    TableLayout table;
    ConstraintLayout mainLayout;
    ImageButton nextButton;
    ImageButton prevButton;
    Button solveButton;


    int[][] grid;

    private static String TAG = "CameraActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solving_page);

        table = (TableLayout)findViewById(R.id.sudokuSolvingTable);
        mainLayout = (ConstraintLayout)findViewById(R.id.sudokuSolvingLayout);
        nextButton = (ImageButton)findViewById(R.id.nextButton);
        prevButton = (ImageButton)findViewById(R.id.prevButton);
        solveButton = (Button)findViewById(R.id.solveButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextButtonClick();
            }
        });

        Intent intent = getIntent();

        int[][] sudoku = (int[][])intent.getSerializableExtra(SudokuDisplayPage.SUDOKU_KEY);
        this.grid = sudoku;
        createSudokuUI(sudoku);
    }

    private void createSudokuUI(int[][] sudoku){
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT,0.11f);

        int padding_dp = 7;
        float scale = getResources().getDisplayMetrics().density;
        int padding_px = (int) (padding_dp * scale + 0.5f);

        for (int row = 0; row < 9; row++){
            TableRow tableRow = new TableRow(this);
            table.addView(tableRow);
            for (int col = 0; col < 9; col++){
                TextView cell = new TextView(this);
                //cell.setEms(1);
                //cell.setInputType(InputType.TYPE_CLASS_NUMBER);
                //cell.setHeight(110);
                cell.setLayoutParams(rowParams);
                cell.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                cell.setTextColor(Color.BLACK);
                cell.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                //cell.setMaxEms(1);

                cell.setPadding(0,padding_px,0,padding_px);

                cell.setBackground(getCellBorder(row,col));

                tableRow.addView(cell);

                if (sudoku[row][col] != 0){
                    cell.setText(String.valueOf(sudoku[row][col]));
                    cell.setTypeface(null,Typeface.BOLD);
                }
            }
        }
    }

    private Drawable getCellBorder(int row, int col){
        int rowRem = row % 3;
        int colRem = col % 3;

        if (rowRem == 0){
            if (colRem == 0) return ContextCompat.getDrawable(this,R.drawable.border_top_left);
            else if (colRem == 1) return ContextCompat.getDrawable(this,R.drawable.border_top);
            else if (colRem == 2) return ContextCompat.getDrawable(this,R.drawable.border_top_right);
        }
        else if (rowRem == 1){
            if (colRem == 0)  return ContextCompat.getDrawable(this,R.drawable.border_left);
            else if (colRem == 1)  return ContextCompat.getDrawable(this,R.drawable.border_middle);
            else if (colRem == 2)  return ContextCompat.getDrawable(this,R.drawable.border_right);
        }
        else if (rowRem == 2){
            if (colRem == 0) return ContextCompat.getDrawable(this,R.drawable.border_bottom_left);
            else if (colRem == 1) return ContextCompat.getDrawable(this,R.drawable.border_bottom);
            else if (colRem == 2) return ContextCompat.getDrawable(this,R.drawable.border_bottom_right);
        }

        return ContextCompat.getDrawable(this,R.drawable.thin_cell_border);
    }

    public void nextButtonClick(){
        try{
            Sudoku sudoku = new Sudoku(this.grid);
            sudoku = Solver.solveNakedSingles(sudoku);
            sudoku.updateCellCandidates();
            System.out.print(sudoku.grid);
        }
        catch (Exception e){
            Log.d(TAG,e.getMessage());
        }
    }
}