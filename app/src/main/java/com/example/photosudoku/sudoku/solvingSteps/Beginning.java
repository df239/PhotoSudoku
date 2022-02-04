package com.example.photosudoku.sudoku.solvingSteps;

import android.util.Log;

import com.example.photosudoku.sudoku.Cell;
import com.example.photosudoku.sudoku.Sudoku;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Beginning implements ISolvingStep{

    private int[][] grid;
    private HashMap<String, List<Integer>> candidates;

    public Beginning(Cell[][] matrix){
        this.grid = new int[9][9];
        this.candidates = new HashMap<String, List<Integer>>();
        for(int row = 0; row < 9; row++){
            for(int col = 0; col < 9; col++){
                Cell c = matrix[row][col];
                if(!c.solved()){
                    String key = Integer.toString(row)+col;
                    Log.d("CameraActivity",key);
                    Log.d("CameraActivity",c.getCandidates().toString());
                    List<Integer> temp = new ArrayList<>();
                    temp.addAll(c.getCandidates());
                    this.candidates.put(key,temp);
                }
                else{
                    this.grid[row][col] = c.getValue();
                }
            }
        }
    }

    public HashMap<String,List<Integer>> getCandidates(){
        return this.candidates;
    }

    @Override
    public int[][] getGrid() {
        return this.grid;
    }

    @Override
    public int[] getAffectedSquares() {
        return new int[0];
    }

    @Override
    public String getTitle() {
        return "beginning";
    }

    @Override
    public String getMessage() {
        return "";
    }
}
