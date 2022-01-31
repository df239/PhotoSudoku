package com.example.photosudoku;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.photosudoku.sudoku.Sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SudokuBoard extends View {
    private final int boardColor;
    private final int letterColor;
    private final int letterNewColor;
    private final int cellHighlightColor;

    private final Paint boardColorPaint = new Paint();
    private final Paint letterColorPaint= new Paint();
    private final Paint letterNewColorPaint = new Paint();
    private final Paint CellHighlightColorPaint = new Paint();
    private final Paint candidatePaint = new Paint();

    private final Rect letterPaintBounds = new Rect();
    Rect candidateBounds = new Rect();

    private int cellSize;
    private Canvas canvas;

    public SudokuBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SudokuBoard,0,0);
        try{
            boardColor = a.getInteger(R.styleable.SudokuBoard_boardColor,0);
            letterColor = a.getInteger(R.styleable.SudokuBoard_letterColor, 0);
            letterNewColor = a.getInteger(R.styleable.SudokuBoard_letterNewColor, 0);
            cellHighlightColor = a.getInteger(R.styleable.SudokuBoard_cellHighlightColor, 0);
        }
        finally{
            a.recycle();
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

        canvas.drawRect(0,0,getWidth(),getHeight(),boardColorPaint);
        drawBoard(canvas);
        int[][] sudoku = Solving_page.original;
        //Sudoku sudoku1 = Solving_page.sudoku;
        for (int row = 0; row < 9; row++){
            for (int col = 0; col < 9; col++){
                if (sudoku[row][col] != 0){
                    drawNumber(sudoku[row][col],row,col);
                }
            }
        }
        drawCandidates(0,0, Arrays.asList(1, 2, 3, 6, 7, 8, 9));
        drawCandidates(5,2, Arrays.asList(1, 3, 5, 7, 9));
        drawCandidates(8,8, Arrays.asList(1, 3, 5, 7, 9));
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

    public void drawNumber(int number, int row, int col){
        String text = Integer.toString(number);
        letterColorPaint.setTextSize((int)(cellSize * 0.8));
        letterColorPaint.setColor(letterColor);
        float width, height;
        letterColorPaint.getTextBounds(text, 0, text.length(), letterPaintBounds);
        width = letterColorPaint.measureText(text);
        height = letterPaintBounds.height();
        this.canvas.drawText(text, (col*cellSize) + (cellSize - width)/2, (row*cellSize + cellSize) - (cellSize - height)/2, letterColorPaint);
    }

    public void drawCandidates(int row, int col, List<Integer> candidates){
        int rectDimension = (int)(cellSize / 3);
        String sampleText = "0";
        candidatePaint.setTextSize(rectDimension);
        candidatePaint.setColor(letterColor);
        candidatePaint.getTextBounds(sampleText, 0, sampleText.length(), candidateBounds);
        float width, height, cellwidth, cellheight;
        width = candidatePaint.measureText(sampleText);
        height = candidateBounds.height();
        cellwidth = letterColorPaint.measureText(sampleText);
        cellheight = letterPaintBounds.height();
        for (int candidate : candidates){
            Log.e("CameraActivity",Integer.toString(candidate));
            float x,y;
            if(candidate % 3 == 1){ // column 1: 1,4,7
                x = (col*cellSize) + (cellSize - cellwidth)/2 - (int)(width*1);
            }
            else if(candidate % 3 == 2){  //column 2: 2,5,8
                x = (col*cellSize) + (cellSize - cellwidth)/2 + width/2;
            }
            else{ //column 3: 3,6,9
                x = (col*cellSize) + (cellSize - cellwidth)/2 + (int)(width*2);
            }

            if((candidate-1) / 3 == 0){
                y = (row*cellSize + cellSize) - (cellSize - cellheight)/2 - (int)(height*2);
            }
            else if((candidate-1) / 3 == 1){
                y = (row*cellSize + cellSize) - (cellSize - cellheight)/2 - height/2;
            }
            else{
                y = (row*cellSize + cellSize) - (cellSize - cellheight)/2 + (int)(height*1);
            }
            this.canvas.drawText(Integer.toString(candidate), x, y, candidatePaint);
        }
    }
}
