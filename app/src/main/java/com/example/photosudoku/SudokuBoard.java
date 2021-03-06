package com.example.photosudoku;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.photosudoku.sudoku.Sudoku;
import com.example.photosudoku.sudoku.solvingSteps.Beginning;
import com.example.photosudoku.sudoku.solvingSteps.ISolvingStep;
import com.example.photosudoku.utils.SudokuDisplayHelper;
import com.example.photosudoku.utils.Validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SudokuBoard extends View {
    private final int boardColor;
    private final int letterColor;
    private final int letterNewColor;
    private final int cellHighlightColor;
    private final int cellSelectColor;

    private final Paint boardColorPaint = new Paint();
    private final Paint letterColorPaint= new Paint();
    private final Paint cellHighlightColorPaint = new Paint();
    private final Paint candidatePaint = new Paint();
    private final Paint cellSelectColorPaint = new Paint();

    private final Rect letterPaintBounds = new Rect();
    Rect candidateBounds = new Rect();

    private int cellSize;
    private Canvas canvas;
    private String currentPage;

    private int selectedRow, selectedCol;
    private ArrayList<int[]> handPickedNumbers;

    public SudokuBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.selectedCol = -1;
        this.selectedRow = -1;

        this.handPickedNumbers = new ArrayList<int[]>();

        //load attributes from the .xml file for the SudokuBoard
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SudokuBoard,0,0);
        try{
            boardColor = a.getInteger(R.styleable.SudokuBoard_boardColor,0);
            letterColor = a.getInteger(R.styleable.SudokuBoard_letterColor, 0);
            letterNewColor = a.getInteger(R.styleable.SudokuBoard_letterNewColor, 0);
            cellHighlightColor = a.getInteger(R.styleable.SudokuBoard_cellHighlightColor, 0);
            currentPage = a.getString(R.styleable.SudokuBoard_selectedPage);
            cellSelectColor = a.getInteger(R.styleable.SudokuBoard_cellSelectColor,0);
        }
        finally{
            a.recycle();
        }
    }

    // select/deselect the tapped sudoku square
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        int action = event.getAction();
        if(action == MotionEvent.ACTION_DOWN){
            int tappedRow = (int)Math.ceil(y/cellSize)-1;
            int tappedCol = (int)Math.ceil(x/cellSize)-1;
            if((this.selectedRow != -1 && this.selectedRow == tappedRow) && (this.selectedCol != -1 && this.selectedCol == tappedCol)){
                this.selectedRow = -1;
                this.selectedCol = -1;
            }
            else{
                this.selectedCol = tappedCol;
                this.selectedRow = tappedRow;
            }
            this.invalidate();
            return true;
        }
        else{
            return false;
        }
    }

    //set sudoku board dimenstions to always be square no matter the device
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int dimension = Math.min(this.getMeasuredWidth(), this.getMeasuredHeight());
        cellSize = dimension/9;
        setMeasuredDimension(dimension,dimension);
    }

    //draw the sudoku board
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;

        boardColorPaint.setStyle(Paint.Style.STROKE);
        boardColorPaint.setStrokeWidth(16);
        boardColorPaint.setColor(boardColor);
        boardColorPaint.setAntiAlias(true);

        cellHighlightColorPaint.setStyle(Paint.Style.FILL);
        cellHighlightColorPaint.setColor(cellHighlightColor);
        cellHighlightColorPaint.setAntiAlias(true);

        cellSelectColorPaint.setStyle(Paint.Style.FILL);
        cellSelectColorPaint.setColor(cellSelectColor);
        cellSelectColorPaint.setAntiAlias(true);

        //there is different configuration of the sudoku board depending on the page where it is located
        if(this.currentPage.equals("solvingPage")){
            Sudoku sudoku = SudokuDisplayHelper.currentSudoku;
            ISolvingStep step = sudoku.steps.get(SudokuDisplayHelper.stepIndex);
            int[][] grid = step.getGrid();
            HashMap<String,List<Integer>> candidates = step.getCandidates();
            //Log.d("CameraActivity",candidates.toString());
            //Log.d("CameraActivity",step.getTitle());
            int[] highlightedSquares = step.getAffectedSquares();

            //first highlight the squares that the current solving step has affected
            if(highlightedSquares.length > 0){
                for(int i = 0; i < highlightedSquares.length; i += 2){
                    drawHighlightedCell(canvas,highlightedSquares[i],highlightedSquares[i+1], cellHighlightColorPaint);
                }
            }

            //highlight the square selected by the user
            if(this.selectedRow != -1 && this.selectedCol != -1){
                drawHighlightedCell(canvas,this.selectedRow,this.selectedCol, cellSelectColorPaint);
            }

            //draw the lines on a sudoku board
            canvas.drawRect(0,0,getWidth(),getHeight(),boardColorPaint);
            drawBoard(canvas);
            //draw numbers & candidates for each cell according to the current solving step
            for (int row = 0; row < 9; row++){
                for (int col = 0; col < 9; col++){
                    if (grid[row][col] != 0){
                        drawNumber(grid[row][col],row,col, isToBeHighlighted(row, col, highlightedSquares), sudoku.original[row][col] != 0);
                    }
                    else if(this.handPickedNumbers.size() == 0 && SudokuDisplayHelper.candidatesVisible){
                        String key = Integer.toString(row)+col;
                        drawCandidates(row,col, Objects.requireNonNull(candidates.get(key)));
                    }
                }
            }
            //draw numbers for the cells that have been selected by the user
            for(int[] arr : this.handPickedNumbers){
                drawNumber(arr[2],arr[0],arr[1],false,false);
            }
        }
        else if(this.currentPage.equals("displayPage")){
            Log.d("CameraActivity","drawingOnDisplayPage");
            ArrayList<Integer> invalidSquares = Validator.invalidSquares;

            //highlight invalid cells
            for(int i = 0; i < invalidSquares.size(); i+=2){
                drawHighlightedCell(canvas,invalidSquares.get(i),invalidSquares.get(i+1),cellHighlightColorPaint);
            }
            int[][] grid = SudokuDisplayHelper.original;

            //higlight selected square for editing
            if(this.selectedRow != -1 && this.selectedCol != -1){
                drawHighlightedCell(canvas,this.selectedRow,this.selectedCol, cellSelectColorPaint);
            }

            //draw the lines on a sudoku board
            canvas.drawRect(0,0,getWidth(),getHeight(),boardColorPaint);
            drawBoard(canvas);

            //draw the numbers
            for (int row = 0; row < 9; row++){
                for (int col = 0; col < 9; col++){
                    if (grid[row][col] != 0) {
                        drawNumber(grid[row][col], row, col, false, true);
                    }
                }
            }

        }

    }

    //calculate if a cell is to be highlighted based on the affected squares provided by a solving step
    private boolean isToBeHighlighted(int row, int col, int[]highlighted){
        for(int i = 0; i < highlighted.length; i += 2){
            if(highlighted[i] == row && highlighted[i+1] == col){
                return true;
            }
        }
        return false;
    }

    private void drawBoard(Canvas canvas){
        //draw vertical lines
        for (int col = 0; col < 10; col++){
            if(col%3 == 0){
                boardColorPaint.setStrokeWidth(10);
            }else{
                boardColorPaint.setStrokeWidth(4);
            }
            canvas.drawLine(cellSize * col, 0, cellSize * col, getWidth(), boardColorPaint);
        }

        //draw horizontal lines
        for (int row = 0; row < 10; row++){
            if(row%3 == 0){
                boardColorPaint.setStrokeWidth(10);
            }else{
                boardColorPaint.setStrokeWidth(4);
            }
            canvas.drawLine(0, cellSize * row, getHeight(), cellSize * row, boardColorPaint);
        }
    }

    private void drawHighlightedCell(Canvas canvas, int row, int col, Paint highlightPaint){
        canvas.drawRect(col*cellSize, row*cellSize, (col+1)*cellSize, (row+1)*cellSize, highlightPaint);
    }

    public void drawNumber(int number, int row, int col, boolean highlighted, boolean original){
        String text = Integer.toString(number);
        letterColorPaint.setTextSize((int)(cellSize * 0.7));

        if(highlighted) letterColorPaint.setColor(letterNewColor);
        else letterColorPaint.setColor(letterColor);

        if(original) letterColorPaint.setTypeface(Typeface.DEFAULT_BOLD);
        else letterColorPaint.setTypeface(Typeface.DEFAULT);

        //calculate the position of the number to appear in the middle of the square
        float width, height;
        letterColorPaint.getTextBounds(text, 0, text.length(), letterPaintBounds);
        width = letterColorPaint.measureText(text);
        height = letterPaintBounds.height();
        float x = (col*cellSize) + (cellSize - width)/2;
        float y = (row*cellSize + cellSize) - (cellSize - height)/2;
        this.canvas.drawText(text,x ,y , letterColorPaint);
    }

    public void drawCandidates(int row, int col, List<Integer> candidates){
        int rectDimension = (int)(cellSize / 3.3);
        String sampleText = "0";
        candidatePaint.setTextSize(rectDimension);
        candidatePaint.setColor(letterColor);
        candidatePaint.getTextBounds(sampleText, 0, sampleText.length(), candidateBounds);
        float width, height;
        width = candidatePaint.measureText(sampleText);
        height = candidateBounds.height();

        //calculate the position of the candidate so that it correctly occupies its ninth of the square
        for (int candidate : candidates){
            float x,y;
            if(candidate % 3 == 1){ // column 1: 1,4,7
                x = (col*cellSize)  + (rectDimension - width);
            }
            else if(candidate % 3 == 2){  //column 2: 2,5,8
                x = (col*cellSize) + (rectDimension*2f - width) ;
            }
            else{ //column 3: 3,6,9
                x = (col*cellSize) + (rectDimension*3f - width) ;
            }

            if((candidate-1) / 3 == 0){
                y = (row*cellSize + cellSize) - (rectDimension*3f - height);
            }
            else if((candidate-1) / 3 == 1){
                y = (row*cellSize + cellSize) - (rectDimension*2f - height);
            }
            else{
                y = (row*cellSize + cellSize) - (rectDimension - height);
            }
            this.canvas.drawText(Integer.toString(candidate), x, y, candidatePaint);
        }
    }

    public void addCompletedSquare(int row, int col, int value){
        this.handPickedNumbers.add(new int[]{row, col, value});
        this.invalidate();
    }

    public void clearHandpickedValues(){
        this.handPickedNumbers.clear();
    }

    public int getSelectedRow(){
        return this.selectedRow;
    }

    public int getSelectedCol(){
        return this.selectedCol;
    }
}
