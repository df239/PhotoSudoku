package com.example.photosudoku.utils;

import androidx.lifecycle.ViewModel;

import com.example.photosudoku.sudoku.Sudoku;

public class SudokuDisplayHelper extends ViewModel {
    public static Sudoku currentSudoku;
    public static int stepIndex;
    public static boolean candidatesVisible = true;
    public static int[][] original;

    public void setOriginal(int[][] newOriginal){
        original = newOriginal;
    }

    public int[][] getOriginal(){
        return original;
    }
}
