package com.example.photosudoku;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.photosudoku.sudoku.Sudoku;
import com.example.photosudoku.sudoku.solvingSteps.Beginning;
import com.example.photosudoku.sudoku.solvingSteps.ISolvingStep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SudokuBoard extends View {
    private final int boardColor;
    private final int letterColor;
    private final int letterNewColor;
    private final int cellHighlightColor;

    private final Paint boardColorPaint = new Paint();
    private final Paint letterColorPaint= new Paint();
    private final Paint cellHighlightColorPaint = new Paint();
    private final Paint candidatePaint = new Paint();

    private final Rect letterPaintBounds = new Rect();
    Rect candidateBounds = new Rect();

    private int cellSize;
    private Canvas canvas;
    private String currentPage;

    private int selectedRow, selectedCol;

    public SudokuBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.selectedCol = -1;
        this.selectedRow = -1;

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SudokuBoard,0,0);
        try{
            boardColor = a.getInteger(R.styleable.SudokuBoard_boardColor,0);
            letterColor = a.getInteger(R.styleable.SudokuBoard_letterColor, 0);
            letterNewColor = a.getInteger(R.styleable.SudokuBoard_letterNewColor, 0);
            cellHighlightColor = a.getInteger(R.styleable.SudokuBoard_cellHighlightColor, 0);
            currentPage = a.getString(R.styleable.SudokuBoard_selectedPage);
        }
        finally{
            a.recycle();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        int action = event.getAction();
        if(action == MotionEvent.ACTION_DOWN){
            this.selectedRow = (int)Math.ceil(y/cellSize)-1;
            this.selectedCol = (int)Math.ceil(x/cellSize)-1;
            this.invalidate();
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int dimension = Math.min(this.getMeasuredWidth(), this.getMeasuredHeight());
        cellSize = dimension/9;
        setMeasuredDimension(dimension,dimension);
    }

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


        if(this.currentPage.equals("solvingPage")){
            Sudoku sudoku = Solving_page.sudoku;
            ISolvingStep step = sudoku.steps.get(Solving_page.stepIndex);
            int[][] grid = step.getGrid();
            HashMap<String,List<Integer>> candidates = step.getCandidates();
            Log.d("CameraActivity",candidates.toString());
            Log.d("CameraActivity",step.getTitle());
            int[] highlightedSquares = step.getAffectedSquares();
            if(highlightedSquares.length > 0){
                for(int i = 0; i < highlightedSquares.length; i += 2){
                    drawHighlightedCell(canvas,highlightedSquares[i],highlightedSquares[i+1]);
                }
            }
            canvas.drawRect(0,0,getWidth(),getHeight(),boardColorPaint);
            drawBoard(canvas);
            for (int row = 0; row < 9; row++){
                for (int col = 0; col < 9; col++){
                    if (grid[row][col] != 0){
                        drawNumber(grid[row][col],row,col, isToBeHighlighted(row, col, highlightedSquares));
                    }
                    else{
                        String key = Integer.toString(row)+col;
                        drawCandidates(row,col, candidates.get(key));
                    }
                }
            }
        }
        else if(this.currentPage.equals("displayPage")){
            Log.d("CameraActivity","drawingOnDisplayPage");
            int[][] grid = SudokuDisplayPage.sudoku;
            canvas.drawRect(0,0,getWidth(),getHeight(),boardColorPaint);
            if(this.selectedRow != -1 && this.selectedCol != -1){
                drawHighlightedCell(canvas,this.selectedRow,this.selectedCol);
            }
            drawBoard(canvas);
            for (int row = 0; row < 9; row++){
                for (int col = 0; col < 9; col++){
                    if (grid[row][col] != 0) {
                        drawNumber(grid[row][col], row, col, false);
                    }
                }
            }

        }

    }

    private boolean isToBeHighlighted(int row, int col, int[]highlighted){
        for(int i = 0; i < highlighted.length; i += 2){
            if(highlighted[i] == row && highlighted[i+1] == col){
                return true;
            }
        }
        return false;
    }

    private void drawBoard(Canvas canvas){
        for (int col = 0; col < 10; col++){
            if(col%3 == 0){
                boardColorPaint.setStrokeWidth(10);
            }else{
                boardColorPaint.setStrokeWidth(4);
            }
            canvas.drawLine(cellSize * col, 0, cellSize * col, getWidth(), boardColorPaint);
        }

        for (int row = 0; row < 10; row++){
            if(row%3 == 0){
                boardColorPaint.setStrokeWidth(10);
            }else{
                boardColorPaint.setStrokeWidth(4);
            }
            canvas.drawLine(0, cellSize * row, getHeight(), cellSize * row, boardColorPaint);
        }
    }

    private void drawHighlightedCell(Canvas canvas, int row, int col){
        canvas.drawRect(col*cellSize, row*cellSize, (col+1)*cellSize, (row+1)*cellSize, cellHighlightColorPaint);
    }

    public void drawNumber(int number, int row, int col, boolean highlighted){
        String text = Integer.toString(number);
        letterColorPaint.setTextSize((int)(cellSize * 0.8));

        if(highlighted) letterColorPaint.setColor(letterNewColor);
        else letterColorPaint.setColor(letterColor);

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

    public void setSelectedRow(int row){
        this.selectedRow = row;
    }

    public void setSelectedCol(int col){
        this.selectedCol = col;
    }

    public int getSelectedRow(){
        return this.selectedRow;
    }

    public int getSelectedCol(){
        return this.selectedCol;
    }
}
