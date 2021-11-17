package com.company;

public class Solver {

    public static Sudoku solveNakedSingles(Sudoku input) {
        Cell[][] grid = input.getCellMatrix();
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid.length; col++) {
                Cell c = grid[row][col];
                if(!c.solved() && c.candidates.size() == 1) {
                    c.setValue(c.candidates.get(0));
                }
            }
        }
        return input;
    }
}
