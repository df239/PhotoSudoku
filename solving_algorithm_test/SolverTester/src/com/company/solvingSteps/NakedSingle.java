package com.company.solvingSteps;

public class NakedSingle implements ISolvingStep {
    private final int[][] grid = new int[9][9];
    private final int value;
    private final int row;
    private final int col;

    public NakedSingle(int value, int row, int col, int[][] grid){
        this.value = value;
        this.row = row;
        this.col = col;
        for(int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++){
                this.grid[i][j] = grid[i][j];
            }
        }
    }

    @Override
    public int[][] getGrid() {
        return this.grid;
    }

    @Override
    public String getMessage() {
        return "Naked Single: Inserted number "+value+" at [row "+(row+1)+" column "+(col+1)+"] as the only possible value in that square.";
    }
}
