package com.example.photosudoku;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class SudokuBoard extends View {
    private final int boardColor;
    private final int letterColor;
    private final int letterNewColor;
    private final int cellHighlightColor;

    private final Paint boardColorPaint = new Paint();
    private final Paint letterColorPaint= new Paint();
    private final Paint letterNewColorPaint = new Paint();
    private final Paint CellHighlightColorPaint = new Paint();

    private final Rect letterPaintBounds = new Rect();

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
        for (int row = 0; row < 9; row++){
            for (int col = 0; col < 9; col++){
                if (sudoku[row][col] != 0){
                    drawNumber(sudoku[row][col],row,col);
                }
            }
        }
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
}
