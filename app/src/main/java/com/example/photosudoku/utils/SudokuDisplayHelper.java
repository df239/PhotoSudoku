package com.example.photosudoku.utils;

import androidx.lifecycle.ViewModel;

import com.example.photosudoku.sudoku.Sudoku;

public class SudokuDisplayHelper extends ViewModel {
    //an intermediary class containing variables that can be seen by both the Activities and the SudokuBoard
    //arguably not the best programming practice
    public static Sudoku currentSudoku;  //a sudoku that is currently being solved/edited
    public static int stepIndex;        //index of a solving step currently displayed
    public static boolean candidatesVisible = true;
    public static int[][] original;     //original sudoku as came from the Sudoku Display / Edit page

    public void setOriginal(int[][] newOriginal){
        original = newOriginal;
    }

    public int[][] getOriginal(){
        return original;
    }
}
