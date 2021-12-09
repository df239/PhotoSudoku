package com.example.photosudoku.sudoku.solvingSteps;

public class HiddenPair implements ISolvingStep{

    @Override
    public int[][] getGrid() {
        return new int[0][];
    }

    @Override
    public String getMessage() {
        return "- Hidden Pair -";
    }

    @Override
    public String getTitle(){
        return "Hidden Pair";
    }
}
