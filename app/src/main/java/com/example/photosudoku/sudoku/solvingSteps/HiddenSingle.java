package com.example.photosudoku.sudoku.solvingSteps;

import com.example.photosudoku.sudoku.Cell;
import com.example.photosudoku.sudoku.SudokuUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HiddenSingle implements ISolvingStep {
    private final int[][] grid = new int[9][9];
    private final HashMap<String, List<Integer>> candidates = new HashMap<String,List<Integer>>();
    private final int value;
    private final int row;
    private final int col;
    private final String house;

    public HiddenSingle(int value, int row, int col, String house, int[][] grid, Cell[][] cellMatrix){
        this.value = value;
        this.row = row;
        this.col = col;
        this.house = house;
        for(int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++){
                this.grid[i][j] = grid[i][j];
                if(grid[i][j] == 0){
                    List<Integer> temp = new ArrayList<>();
                    temp.addAll(cellMatrix[i][j].getCandidates());
                    this.candidates.put(Integer.toString(i)+j,temp);
                }
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
    public HashMap<String, List<Integer>> getCandidates() {
        return this.candidates;
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

        return "R"+(row+1)+"C"+(col+1)+" is the only cell in "+house+" "+(houseNum+1)+", where value "+value+" can be placed.";
    }

    @Override
    public String getTitle(){
        return "Hidden Single ("+value+") at R"+(row+1)+"C"+(col+1);
    }
}
