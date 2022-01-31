package com.example.photosudoku;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class SudokuBoard extends View {
    private final int boardColor;
    private final Paint boardColorPaint = new Paint();

    private int cellSize;

    public SudokuBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SudokuBoard,0,0);
        try{
            boardColor = a.getInteger(R.styleable.SudokuBoard_boardColor,0);
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

        boardColorPaint.setStyle(Paint.Style.STROKE);
        boardColorPaint.setStrokeWidth(16);
        boardColorPaint.setColor(boardColor);
        boardColorPaint.setAntiAlias(true);

        canvas.drawRect(0,0,getWidth(),getHeight(),boardColorPaint);
        drawBoard(canvas);
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
}
