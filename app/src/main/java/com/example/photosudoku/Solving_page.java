package com.example.photosudoku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.photosudoku.sudoku.Solver;
import com.example.photosudoku.sudoku.Sudoku;
import com.example.photosudoku.sudoku.solvingSteps.Beginning;
import com.example.photosudoku.sudoku.solvingSteps.HiddenPair;
import com.example.photosudoku.sudoku.solvingSteps.HiddenSingle;
import com.example.photosudoku.sudoku.solvingSteps.ISolvingStep;
import com.example.photosudoku.sudoku.solvingSteps.NakedPair;
import com.example.photosudoku.sudoku.solvingSteps.NakedSingle;
import com.example.photosudoku.sudoku.solvingSteps.PointingCandidates;
import com.example.photosudoku.utils.SudokuDisplayHelper;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Solving_page extends AppCompatActivity {

    TableLayout table;
    ConstraintLayout mainLayout;
    ImageButton nextButton;
    ImageButton prevButton;
    Button solveButton;
    TextView messageView;
    TextView textView;
    Button toggleMessageButton;
    ToggleButton toggleCandidatesButton;

    SudokuBoard sudokuBoard;

    private int[][] original;
    //public static Sudoku sudoku;
    int[][] solution;
    List<ISolvingStep> steps;
    //public static int stepIndex = 0;
    //public static boolean candidatesVisible = true;

    private static String TAG = "CameraActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solving_page);

        //table = (TableLayout)findViewById(R.id.sudokuSolvingTable);
        mainLayout = (ConstraintLayout)findViewById(R.id.sudokuSolvingLayout);
        nextButton = (ImageButton)findViewById(R.id.nextButton);
        prevButton = (ImageButton)findViewById(R.id.prevButton);
        solveButton = (Button)findViewById(R.id.solveButton);
        messageView = (TextView)findViewById(R.id.messageView);
        //textView = (TextView)findViewById(R.id.tempView);
        toggleMessageButton = (Button)findViewById(R.id.toggleMessageButton);
        toggleCandidatesButton = (ToggleButton)findViewById(R.id.toggleCandidatesButton);

        //process the 2D array of digits provided by the Edit / Sudoku display page
        Intent intent = getIntent();
        int[][] sudoku = (int[][])intent.getSerializableExtra(SudokuDisplayPage.SUDOKU_KEY);
        original = new int[9][9];
        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                original[i][j] = sudoku[i][j];
            }
        }

        nextButton.setOnClickListener(v -> nextButtonClick());

        prevButton.setOnClickListener(v -> prevButtonClick());

        solveButton.setOnClickListener(v -> solveButtonClick());

        //createSudokuUI(sudoku);
        //long startTime = System.nanoTime();
        solveSudoku(sudoku);
        //long endTime = System.nanoTime();
        //String output = TimeUnit.NANOSECONDS.toMicros(endTime - startTime) +" μs";
        //textView.setText(output);

        sudokuBoard = findViewById(R.id.sudokuBoard);

        SudokuDisplayHelper.stepIndex = 0;
        String message = (SudokuDisplayHelper.stepIndex + 1) + " - "  + this.steps.get(SudokuDisplayHelper.stepIndex).getTitle() + "\n" + this.steps.get(SudokuDisplayHelper.stepIndex).getMessage();
        messageView.setText(message);

        toggleCandidatesButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SudokuDisplayHelper.candidatesVisible = isChecked;
            sudokuBoard.invalidate();
        });
    }

    private void solveSudoku(int[][] matrix){
        SudokuDisplayHelper.currentSudoku = new Sudoku(matrix);

        int noChangeCounter = 0;
        //iterate over all solving techniques, if all fail use backtracking to solve
        while(!SudokuDisplayHelper.currentSudoku.solved()){
            if (noChangeCounter == 1){
                //System.out.println("Could not solve. Using backtracking:");
                SudokuDisplayHelper.currentSudoku.solution = Solver.solveBacktracking(SudokuDisplayHelper.currentSudoku);
                break;
            }
            if(Solver.solveNakedSingles(SudokuDisplayHelper.currentSudoku)){
                continue;
            }
            if(Solver.solveHiddenSingles(SudokuDisplayHelper.currentSudoku)){
                continue;
            }
            if(Solver.solvePointingCandidates(SudokuDisplayHelper.currentSudoku)){
                continue;
            }
            if(Solver.solveNakedPair(SudokuDisplayHelper.currentSudoku)){
                continue;
            }
            if(Solver.solveHiddenPair(SudokuDisplayHelper.currentSudoku)){
                continue;
            }
            noChangeCounter++;
        }

        this.solution = SudokuDisplayHelper.currentSudoku.grid;
        this.steps = SudokuDisplayHelper.currentSudoku.steps;
    }

    //show complete solution / the last solving step
    public void solveButtonClick(){
        SudokuDisplayHelper.stepIndex = steps.size() - 1;
        String message = (SudokuDisplayHelper.stepIndex + 1) + " - "  + this.steps.get(SudokuDisplayHelper.stepIndex).getTitle() + "\n" + this.steps.get(SudokuDisplayHelper.stepIndex).getMessage();
        messageView.setText(message);
        this.sudokuBoard.clearHandpickedValues();
        sudokuBoard.invalidate();
        this.prevButton.setEnabled(true);
        this.nextButton.setEnabled(true);
    }

    //show the next solving step
    public void nextButtonClick(){
        if(SudokuDisplayHelper.stepIndex < steps.size() - 1){
            SudokuDisplayHelper.stepIndex ++;
            String message = (SudokuDisplayHelper.stepIndex + 1) + " - "  + this.steps.get(SudokuDisplayHelper.stepIndex).getTitle() + "\n" + this.steps.get(SudokuDisplayHelper.stepIndex).getMessage();
            messageView.setText(message);
            //rewriteSudokuBoard(this.steps.get(stepIndex));
            //rewriteGrid(this.steps.get(stepIndex));
            sudokuBoard.invalidate();
        }
        else{
            SudokuDisplayHelper.stepIndex = steps.size() - 1;
        }
    }

    //show the previous solving step
    public void prevButtonClick(){
        if(SudokuDisplayHelper.stepIndex > 0){
            SudokuDisplayHelper.stepIndex --;
            String message = (SudokuDisplayHelper.stepIndex + 1) + " - "  + this.steps.get(SudokuDisplayHelper.stepIndex).getTitle() + "\n" + this.steps.get(SudokuDisplayHelper.stepIndex).getMessage();
            messageView.setText(message);
            //rewriteSudokuBoard(this.steps.get(stepIndex));
            //rewriteGrid(this.steps.get(stepIndex));
            sudokuBoard.invalidate();
        }
        else{
            SudokuDisplayHelper.stepIndex = 0;
        }
    }

    public void toggleMessageVisible(View view){
        if(this.messageView.getVisibility() == View.VISIBLE) this.messageView.setVisibility(View.INVISIBLE);
        else this.messageView.setVisibility(View.VISIBLE);
    }

    //show the digit in the selected square and disable next/previous step buttons
    public void solveParticularSquare(View view){
        int row = this.sudokuBoard.getSelectedRow();
        int col = this.sudokuBoard.getSelectedCol();

        if (row != -1 && col != -1){
            sudokuBoard.addCompletedSquare(row,col,SudokuDisplayHelper.currentSudoku.solution[row][col]);

            this.prevButton.setEnabled(false);
            this.nextButton.setEnabled(false);
        }
    }

    /*
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

    private void rewriteGrid(int[][] grid){
        for (int row = 0; row < 9; row++){
            TableRow tableRow = (TableRow)table.getChildAt(row);
            for (int col = 0; col < 9; col++){
                if (this.original[row][col] == 0){
                    TextView view = (TextView)tableRow.getChildAt(col);
                    view.setTextColor(Color.BLACK);
                    view.setBackground(getCellBorder(row,col));
                    if(grid[row][col] == 0){
                        view.setText("");
                    }
                    else{
                        view.setText(String.valueOf(grid[row][col]));
                    }
                }
            }
        }
    }

    private void rewriteGrid(ISolvingStep step){
        rewriteGrid(step.getGrid());
        if (step instanceof  NakedSingle){
            int row = ((NakedSingle) step).getAffectedRow();
            int col = ((NakedSingle) step).getAffectedCol();
            TextView view = (TextView)((TableRow)table.getChildAt(row)).getChildAt(col);
            view.setTextColor(ContextCompat.getColor(this,R.color.red));
            view.setText(String.valueOf(((NakedSingle) step).getNewValue()));
            view.setBackgroundColor(ContextCompat.getColor(this,R.color.light_red));
        }
        else if (step instanceof HiddenSingle){
            int row = ((HiddenSingle) step).getAffectedRow();
            int col = ((HiddenSingle) step).getAffectedCol();
            TextView view = (TextView)((TableRow)table.getChildAt(row)).getChildAt(col);
            view.setTextColor(ContextCompat.getColor(this,R.color.red));
            view.setText(String.valueOf(((HiddenSingle) step).getNewValue()));
            view.setBackgroundColor(ContextCompat.getColor(this,R.color.light_red));
        }
        else if (step instanceof PointingCandidates){
            int[] positions = ((PointingCandidates) step).getCellsLocations();
            for(int i = 0; i < positions.length/2; i++){
                int row = positions[2*i];
                int col = positions[2*i + 1];
                TextView view = (TextView)((TableRow)table.getChildAt(row)).getChildAt(col);
                view.setBackgroundColor(ContextCompat.getColor(this,R.color.light_red));
            }
        }
        else if (step instanceof NakedPair){
            int[] positions = ((NakedPair)step).getCellsLocations();
            for(int i = 0; i < positions.length/2; i++){
                int row = positions[2*i];
                int col = positions[2*i + 1];
                TextView view = (TextView)((TableRow)table.getChildAt(row)).getChildAt(col);
                view.setBackgroundColor(ContextCompat.getColor(this,R.color.light_red));
            }
        }
        else if (step instanceof HiddenPair){
            int[] positions = ((HiddenPair)step).getCellsLocations();
            for(int i = 0; i < positions.length/2; i++){
                int row = positions[2*i];
                int col = positions[2*i + 1];
                TextView view = (TextView)((TableRow)table.getChildAt(row)).getChildAt(col);
                view.setBackgroundColor(ContextCompat.getColor(this,R.color.light_red));
            }
        }
    }

    */
}