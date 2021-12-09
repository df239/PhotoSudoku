package com.example.photosudoku.sudoku.solvingSteps;

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

    public int getNewValue(){return this.value;}
    public int getAffectedRow(){return this.row;}
    public int getAffectedCol(){return this.col;}

    @Override
    public int[][] getGrid() {
        return this.grid;
    }

    @Override
    public String getMessage() {
        //return "- Naked Single -";
        return "Value "+value+" is the only possible value in square R"+(row+1)+"C"+(col+1);
    }

    @Override
    public String getTitle(){
        return "Naked Single ("+value+") at R"+(row+1)+"C"+(col+1);
    }
}
