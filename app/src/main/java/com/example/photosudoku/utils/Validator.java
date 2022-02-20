package com.example.photosudoku.utils;

import java.util.ArrayList;
import java.util.HashSet;

public class Validator {
    public static ArrayList<Integer> invalidSquares = new ArrayList<>();

    public static boolean checkBoardValidity(int[][] grid){
        //check rows
        for (int row = 0; row < grid.length; row++) {
            HashSet<Integer> temp = new HashSet<>();
            for (int col = 0; col < grid.length; col++) {
                if (temp.contains(grid[row][col])) {
                    invalidSquares = findInvalidSquares(grid,grid[row][col],row,col,"row");
                    return false;
                }
                if (grid[row][col] != 0) {
                    temp.add(grid[row][col]);
                }
            }
        }

        //check columns
        for(int col = 0; col < grid.length; col++){
            HashSet<Integer> temp = new HashSet<>();
            for (int row = 0; row < grid.length; row++) {
                if (temp.contains(grid[row][col])) {
                    invalidSquares = findInvalidSquares(grid,grid[row][col],row,col,"col");
                    return false;
                }
                if (grid[row][col] != 0) {
                    temp.add(grid[row][col]);
                }
            }
        }

        //check boxes
        for(int row = 0; row < 9; row+=3){
            for(int col = 0; col < 9; col+=3){
                HashSet<Integer> temp = new HashSet<>();
                for(int subrow = row; subrow < row+3; subrow++){
                    for(int subcol = col; subcol < col+3; subcol++){
                        if(temp.contains(grid[subrow][subcol])){
                            invalidSquares = findInvalidSquares(grid,grid[subrow][subcol],row,col,"box");
                            return false;
                        }
                        if(grid[subrow][subcol] != 0){
                            temp.add(grid[subrow][subcol]);
                        }
                    }
                }
            }
        }
        invalidSquares.clear();
        return true;
    }

    private static ArrayList<Integer> findInvalidSquares(int[][] grid, int value, int row, int col, String type){
        ArrayList<Integer> invalidSquares = new ArrayList<>();
        switch(type){
            case "row":{
                for(int c = 0; c < 9; c++){
                    if(grid[row][c] == value){
                        invalidSquares.add(row);
                        invalidSquares.add(c);
                    }
                }
                break;
            }
            case "col":{
                for(int r = 0; r < 9; r++){
                    if(grid[r][col] == value){
                        invalidSquares.add(r);
                        invalidSquares.add(col);
                    }
                }
                break;
            }
            default:{
                for(int subrow = row; subrow < row+3; subrow++){
                    for(int subcol = col; subcol < col+3; subcol++){
                        if(grid[subrow][subcol] == value){
                            invalidSquares.add(subrow);
                            invalidSquares.add(subcol);
                        }
                    }
                }
                break;
            }
        }
        return invalidSquares;
    }
}
