package com.example.photosudoku.sudoku.solvingSteps;

import java.util.HashMap;
import java.util.List;

public interface ISolvingStep {
    //common interface for all of the solving steps

    //returns an int matrix that represents the sudoku to be displayed in the grid
    public int[][] getGrid();

    //tuples (row,col) or index of squares to be highlighted as changed
    public int[] getAffectedSquares();

    //dictionary of cells and their candidates
    //key is the cell location (15) = row index 1 and column index 5
    //value is the list of candidates for that cell
    public HashMap<String, List<Integer>> getCandidates();

    //main title/short message and a thorough message for a solving step
    public String getTitle();
    public String getMessage();
}
