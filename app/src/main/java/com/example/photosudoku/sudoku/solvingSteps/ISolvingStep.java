package com.example.photosudoku.sudoku.solvingSteps;

import java.util.HashMap;
import java.util.List;

public interface ISolvingStep {
    public int[][] getGrid();
    public int[] getAffectedSquares();
    public HashMap<String, List<Integer>> getCandidates();
    public String getTitle();
    public String getMessage();
}
