package com.example.photosudoku.sudoku;

import java.util.ArrayList;
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
                this.rows.get(c.ROW).add(c);
                this.cols.get(c.COL).add(c);
                this.boxes.get(c.BOX).add(c);
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
                    for(int candidate : c.candidates) {
                        if(this.rows.get(c.ROW).contains(candidate)) c.candidates.remove(candidate);
                        if(this.cols.get(c.COL).contains(candidate)) c.candidates.remove(candidate);
                        if(this.boxes.get(c.BOX).contains(candidate)) c.candidates.remove(candidate);
                    }
                }
            }
        }
    }

    public Cell[][] getCellMatrix(){
        return this.sudoku;
    }
}
