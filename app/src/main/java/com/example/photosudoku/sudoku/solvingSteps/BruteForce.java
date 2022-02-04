package com.example.photosudoku.sudoku.solvingSteps;

import java.util.HashMap;
import java.util.List;

public class BruteForce implements ISolvingStep{

    private int[][] grid;

    public BruteForce(int[][] grid){
        this.grid = grid;
    }

    @Override
    public int[][] getGrid() {
        return this.grid;
    }

    @Override
    public int[] getAffectedSquares() {
        return new int[0];
    }

    @Override
    public HashMap<String, List<Integer>> getCandidates() {
        return null;
    }

    @Override
    public String getTitle() {
        return "Brute Force";
    }

    @Override
    public String getMessage() {
        return "No logical step could be applied, therefore brute force approach has been used.";
    }
}
