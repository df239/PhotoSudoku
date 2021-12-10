package com.example.photosudoku.sudoku.solvingSteps;

import com.example.photosudoku.sudoku.Cell;
import com.example.photosudoku.sudoku.House;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HiddenPair implements ISolvingStep{
    private final Cell c1;
    private final Cell c2;
    private final List<Integer> candidates;
    private final House house;
    private final int[][] grid;

    private String title;
    private String message;

    public HiddenPair(Cell cell1, Cell cell2, Collection<Integer> candidatePair, House affectedHouse, int[][] grid){
        this.c1 = cell1;
        this.c2 = cell2;
        this.candidates = new ArrayList<>(candidatePair);
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
        String candidateString = "(";
        for (int i : this.candidates){
            candidateString += (i+",");
        }
        candidateString = candidateString.replaceAll(",$",")");
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

        this.title = "Hidden Pair "+candidateString+" in "+this.house.TYPE+" "+(houseNum+1)+".";
        this.message = "Cells "+cellsString+" are the only cells in "+this.house.TYPE+" "+(houseNum+1)+", where candidates "+candidateString+" are possible. All other candidates can be removed from these cells.";
    }

    @Override
    public int[][] getGrid() {
        return this.grid;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String getTitle(){
        return this.title;
    }
}
