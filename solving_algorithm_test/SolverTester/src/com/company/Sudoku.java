package com.company;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Sudoku {

    public int[][] grid;
    private int[][] solution;
    private Cell[][] sudoku;

    private List<CellGroup> rows;
    private List<CellGroup> cols;
    private List<CellGroup> boxes;

    public Sudoku(int[][] sudokuGrid){
        this.grid = sudokuGrid;
        this.rows = new ArrayList<CellGroup>();
        this.cols = new ArrayList<CellGroup>();
        this.boxes = new ArrayList<CellGroup>();

        for(int i = 0; i < 9; i++) {
            this.rows.add(new CellGroup());
            this.cols.add(new CellGroup());
            this.boxes.add(new CellGroup());
        }

        this.sudoku = buildSudoku(sudokuGrid);
        this.updateCellCandidates();
    }

    private Cell[][] buildSudoku(int[][] grid){
        Cell[][] matrix = new Cell[grid.length][grid.length];
        for(int x = 0; x < grid.length; x++){
            for(int y = 0; y < grid.length; y++){
                Cell c = new Cell(grid[x][y],x,y);
                matrix[x][y] = c;
                CellGroup temp = this.rows.get(c.ROW);
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
                    temp.addAll(c.candidates);
                    for(Integer candidate : temp) {
                        if(this.rows.get(c.ROW).contains(candidate)){
                            c.candidates.remove(candidate);
                            continue;
                        }
                        if(this.cols.get(c.COL).contains(candidate)){
                            c.candidates.remove(candidate);
                            continue;
                        }
                        if(this.boxes.get(c.BOX).contains(candidate)){
                            c.candidates.remove(candidate);
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
            for (Cell c : getRow(cell.ROW)){
                if (!c.solved()){
                    c.candidates.remove((Integer)cell.getValue());
                }
            }

            for (Cell c : getCol(cell.COL)){
                if (!c.solved()){
                    c.candidates.remove((Integer)cell.getValue());
                }
            }

            for (Cell c : getBox(cell.BOX)){
                if (!c.solved()){
                    c.candidates.remove((Integer)cell.getValue());
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

    public HashSet<Cell> getRow(int row){
        return this.rows.get(row).getGroup();
    }

    public HashSet<Cell> getCol(int col){
        return this.cols.get(col).getGroup();
    }

    public HashSet<Cell> getBox(int box){
        return this.boxes.get(box).getGroup();
    }
}
