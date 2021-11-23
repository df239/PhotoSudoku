package com.company.solvingSteps;

public class HiddenSingle implements ISolvingStep {
    private final int[][] grid = new int[9][9];
    private final int value;
    private final int row;
    private final int col;
    private final String house;

    public HiddenSingle(int value, int row, int col, String house, int[][] grid){
        this.value = value;
        this.row = row;
        this.col = col;
        this.house = house;
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
        return "Hidden Single: Inserted number "+value+" at [row "+(row+1)+" column "+(col+1)+"] as the only square in its "+house+" where it can be placed.";
    }
}
