package com.example.photosudoku.sudoku;

public class Sudoku {
//    public static final int[][] sample =
//            {{0,0,8,0,1,0,0,0,9},
//             {6,0,1,0,9,0,3,2,0},
//             {}};


    private int[][] grid;
    private int[][] solution;
    private Cell[][] sudoku;

    public Sudoku(int[][] sudokuGrid){
        grid = sudokuGrid;
        this.sudoku = buildSudoku(sudokuGrid);
    }

    private Cell[][] buildSudoku(int[][] grid){
        Cell[][] matrix = new Cell[grid.length][grid.length];
        for(int x = 0; x < grid.length; x++){
            for(int y = 0; y < grid.length; y++){
                matrix[x][y] = new Cell(grid[x][y],x,y);
            }
        }
        return matrix;
    }

}
