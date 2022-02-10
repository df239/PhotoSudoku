package com.example.photosudoku.sudoku;

import com.example.photosudoku.sudoku.solvingSteps.Beginning;
import com.example.photosudoku.sudoku.solvingSteps.ISolvingStep;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Sudoku {
    public List<ISolvingStep> steps;

    public int[][] grid;
    public final int[][] original;
    public int[][] solution;
    private Cell[][] sudoku;

    private List<House> rows;
    private List<House> cols;
    private List<House> boxes;

    public Sudoku(int[][] sudokuGrid){
        this.grid = sudokuGrid;
        this.original = new int[sudokuGrid.length][sudokuGrid.length];
        this.rows = new ArrayList<House>();
        this.cols = new ArrayList<House>();
        this.boxes = new ArrayList<House>();
        steps = new ArrayList<>();

        for(int i = 0; i < 9; i++) {
            this.rows.add(new House("row"));
            this.cols.add(new House("column"));
            this.boxes.add(new House("box"));
        }

        this.sudoku = buildSudoku(sudokuGrid);
        this.updateCellCandidates();
        this.steps.add(new Beginning(this.sudoku));
    }

    private Cell[][] buildSudoku(int[][] grid){
        Cell[][] matrix = new Cell[grid.length][grid.length];
        for(int x = 0; x < grid.length; x++){
            for(int y = 0; y < grid.length; y++){
                this.original[x][y] = grid[x][y];
                Cell c = new Cell(grid[x][y],x,y);
                matrix[x][y] = c;
                House temp = this.rows.get(c.ROW);
                temp.add(c);
                temp = this.cols.get(c.COL);
                temp.add(c);
                temp = this.boxes.get(c.BOX);
                temp.add(c);
            }
        }
        return matrix;
    }

    public void updateCellCandidates() {
        for(int row = 0; row < this.grid.length; row++) {
            for(int col = 0; col < this.grid.length; col++) {
                Cell c = this.sudoku[row][col];
                this.grid[row][col] = c.getValue();
                if(!c.solved()) {
                    HashSet<Integer> temp = new HashSet<Integer>();
                    temp.addAll(c.getCandidates());
                    for(Integer candidate : temp) {
                        if(this.rows.get(c.ROW).contains(candidate)){
                            c.removeCandidate(candidate);
                            continue;
                        }
                        if(this.cols.get(c.COL).contains(candidate)){
                            c.removeCandidate(candidate);
                            continue;
                        }
                        if(this.boxes.get(c.BOX).contains(candidate)){
                            c.removeCandidate(candidate);
                        }
                    }
                }
            }
        }
    }

    //updates only candidates of cells that the parameter cell can see
    public void updateCellCandidates(Cell cell){
        if (cell.solved()){
            this.grid[cell.ROW][cell.COL] = cell.getValue();
            for (Cell c : getRow(cell.ROW).getGroup()){
                if (!c.solved()){
                    c.removeCandidate(cell.getValue());
                }
            }

            for (Cell c : getCol(cell.COL).getGroup()){
                if (!c.solved()){
                    c.removeCandidate(cell.getValue());
                }
            }

            for (Cell c : getBox(cell.BOX).getGroup()){
                if (!c.solved()){
                    c.removeCandidate(cell.getValue());
                }
            }
        }
    }

    public Cell[][] getCellMatrix(){
        return this.sudoku;
    }

    public boolean isValuePossible(int value, Cell cell){
        if(this.rows.get(cell.ROW).contains(value)){
            return false;
        }
        else if(this.cols.get(cell.COL).contains(value)){
            return false;
        }
        else if(this.boxes.get(cell.BOX).contains(value)){
            return false;
        }
        return true;
    }

    public House getRow(int row){
        return this.rows.get(row);
    }

    public House getCol(int col){
        return this.cols.get(col);
    }

    public House getBox(int box){
        return this.boxes.get(box);
    }

    public boolean solved(){
        for (int row = 0; row < 9; row++){
            for (int col = 0; col < 9; col++){
                if (this.grid[row][col] == 0)
                    return false;
            }
        }
        this.solution = this.grid;
        return true;
    }
}
