package com.example.photosudoku.sudoku.solvingSteps;

import com.example.photosudoku.sudoku.Cell;
import com.example.photosudoku.sudoku.House;
import com.example.photosudoku.sudoku.SudokuUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PointingCandidates implements ISolvingStep{
    private final int candidate;
    private final House affected;
    private final House comparative;
    private Cell sample;
    private List<Cell> cells;
    private final int[][] grid;

    String message="";
    String title = "";

    public PointingCandidates(int candidate, House affected_house, House comparative_house, Collection<Cell> cells, int[][] grid){
        this.candidate = candidate;
        this.affected = affected_house;
        this.comparative = comparative_house;
        this.grid = new int[9][9];
        for(int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++){
                this.grid[i][j] = grid[i][j];
            }
        }
        this.cells = new ArrayList<>(cells);
        this.sample = this.cells.get(0);
        buildStrings();
    }

    private void buildStrings(){
        String cells_string = "R"+(this.sample.ROW+1)+"C"+(this.sample.COL+1)+", "+"R"+(this.cells.get(1).ROW+1)+"C"+(this.cells.get(1).COL+1);
        int affected_num = getHouseNum(this.affected);
        int comparative_num = getHouseNum(this.comparative);

        this.title = "Pointing candidate "+candidate+" in cells "+cells_string+".";
        this.message = "Cells ("+cells_string+") are the only cells in "+comparative.TYPE+" "+(comparative_num+1)+" where candidate "+candidate+" is possible.";
        this.message += " It can be, therefore, removed from all other cells in "+affected.TYPE+" "+(affected_num+1)+".";
    }

    private int getHouseNum(House h){
        int num = 0;

        if(h.TYPE.equals("row")){
            num = this.sample.ROW;
        }
        else if (h.TYPE.equals("column")){
            num = this.sample.COL;
        }
        else if(h.TYPE.equals("box")){
            num = SudokuUtils.getBox(this.sample.ROW, this.sample.COL);
        }
        return num;
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
