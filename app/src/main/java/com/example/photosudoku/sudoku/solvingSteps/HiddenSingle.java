package com.example.photosudoku.sudoku.solvingSteps;

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

    public int getNewValue(){return this.value;}
    public int getAffectedRow(){return this.row;}
    public int getAffectedCol(){return this.col;}

    @Override
    public int[][] getGrid() {
        return this.grid;
    }

    @Override
    public String getMessage() {
        return "- Hidden Single -";
        //return "Hidden Single: Inserted number "+value+" at [row "+(row+1)+" column "+(col+1)+"] as the only square in its "+house+" where it can be placed.";
    }

    @Override
    public String getTitle(){
        return "Hidden Single ("+value+") at R"+(row+1)+"C"+(col+1);
    }
}
