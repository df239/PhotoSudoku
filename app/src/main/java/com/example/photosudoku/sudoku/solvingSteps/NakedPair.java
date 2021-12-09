package com.example.photosudoku.sudoku.solvingSteps;

public class NakedPair implements ISolvingStep{

    @Override
    public int[][] getGrid() {
        return new int[0][];
    }

    @Override
    public String getMessage() {
        return "- Naked Pair -";
    }


    @Override
    public String getTitle(){
        return "Naked Pair";
    }
}
