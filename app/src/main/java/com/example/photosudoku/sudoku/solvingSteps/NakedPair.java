package com.example.photosudoku.sudoku.solvingSteps;

import com.example.photosudoku.sudoku.Cell;
import com.example.photosudoku.sudoku.House;
import com.example.photosudoku.sudoku.SudokuUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class NakedPair implements ISolvingStep{
    private final Cell c1;
    private final Cell c2;
    private final List<Integer> candidates;
    private final House house;
    private final int[][] grid;

    private String title;
    private String message;

    public NakedPair(Cell cell1, Cell cell2, House affectedHouse, int[][] grid){
        this.c1 = cell1;
        this.c2 = cell2;
        this.candidates = cell1.getCandidates();
        this.house = affectedHouse;
        this.grid = new int[9][9];
        for(int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++){
                this.grid[i][j] = grid[i][j];
            }
        }
        buildStrings();
    }

    private void buildStrings(){
        String candidateString = "("+this.candidates.get(0)+","+this.candidates.get(1)+")";
        String cellsString = "R"+(this.c1.ROW+1)+"C"+(this.c1.COL+1)+", R"+(this.c2.ROW+1)+"C"+(this.c2.COL+1);
        int houseNum = 0;
        if(this.house.TYPE.equals("row")){
            houseNum = this.c1.ROW;
        }
        else if (this.house.TYPE.equals("column")){
            houseNum = this.c1.COL;
        }
        else if(this.house.TYPE.equals("box")){
            houseNum = this.c1.BOX;
        }

        this.title = "Naked Pair "+candidateString+" in "+this.house.TYPE+" "+(houseNum+1)+".";
        this.message = "Candidates "+candidateString+" must only appear in cells "+cellsString+" within "+this.house.TYPE+" "+(houseNum+1)+" and can be removed from all other cells in this "+this.house.TYPE+".";
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
    public String getMessage() {
        return this.message;
    }

    @Override
    public String getTitle(){
        return this.title;
    }

    public int[] getCellsLocations(){
        int[] arr = new int[4];
        arr[0] = c1.ROW;
        arr[1] = c1.COL;
        arr[2] = c2.ROW;
        arr[3] = c2.COL;
        return arr;
    }
}
