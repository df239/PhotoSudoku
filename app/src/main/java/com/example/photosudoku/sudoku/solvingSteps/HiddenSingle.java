package com.example.photosudoku.sudoku.solvingSteps;

import com.example.photosudoku.sudoku.SudokuUtils;

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
        int houseNum = 0;
        if(this.house.equals("row")){
            houseNum = this.row;
        }
        else if (this.house.equals("column")){
            houseNum = this.col;
        }
        else if(this.house.equals("box")){
            houseNum = SudokuUtils.getBox(this.row, this.col);
        }

        return "Value "+value+" is the only square in "+house+" "+(houseNum+1)+", where it can be placed.";
    }

    @Override
    public String getTitle(){
        return "Hidden Single ("+value+") at R"+(row+1)+"C"+(col+1);
    }
}
