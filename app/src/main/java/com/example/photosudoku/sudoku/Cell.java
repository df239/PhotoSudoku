package com.example.photosudoku.sudoku;

import static com.example.photosudoku.sudoku.SudokuUtils.getBox;

public class Cell {
    private int value;
    private boolean isSolved;
    private final int ROW;
    private final int COL;
    private final int BOX;

    public Cell(int value, int row, int col){
        this.value = value;
        this.ROW = col;
        this.COL = row;
        this.BOX = getBox(row,col);

        this.isSolved = value != 0;
    }
}
