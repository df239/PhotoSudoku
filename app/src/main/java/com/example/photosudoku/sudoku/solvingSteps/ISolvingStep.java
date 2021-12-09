package com.example.photosudoku.sudoku.solvingSteps;

public interface ISolvingStep {
    public int[][] getGrid();
    public String getTitle();
    public String getMessage();
}
