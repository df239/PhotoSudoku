package com.example.photosudoku.sudoku;

public class SudokuUtils {

    public static int getBox(int row, int col){
        if(row%3 == 0){
            if(col%3 == 0) return 0;
            else if(col%3 == 1) return 1;
            else return 2;
        }
        else if(row%3 == 1){
            if(col%3 == 0) return 3;
            else if(col%3 == 1) return 4;
            else return 5;
        }
        else{
            if(col%3 == 0) return 6;
            else if(col%3 == 1) return 7;
            else return 8;
        }
    }
}
