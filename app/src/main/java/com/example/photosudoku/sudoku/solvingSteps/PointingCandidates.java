package com.example.photosudoku.sudoku.solvingSteps;

public class PointingCandidates implements ISolvingStep{


    @Override
    public int[][] getGrid() {
        return new int[0][];
    }

    @Override
    public String getMessage() {
        return "- Pointing Candidates -";
    }

    @Override
    public String getTitle(){
        return "Pointing Candidates";
    }
}