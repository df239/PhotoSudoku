package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Cell {
    private int value;
    private boolean isSolved;

    public final int ROW;
    public final int COL;
    public final int BOX;

    public List<Integer> candidates;
    //private HashSet<Integer> forbiddenValues;

    public Cell(int value, int row, int col){
        this.value = value;
        this.ROW = row;
        this.COL = col;
        this.BOX = SudokuUtils.getBox(row,col);

        this.isSolved = value != 0;

        this.candidates = new ArrayList<Integer>();
        if(!this.isSolved) {
            for(int val = 1; val <=9; val++) {
                this.candidates.add(val);
            }
        }
        //this.forbiddenValues = new HashSet<Integer>();
    }


    public int getValue() {
        return this.value;
    }

    public boolean solved() {
        return this.isSolved;
    }

    public boolean containsCandidate(int candidate) {
        return this.candidates.contains(candidate);
    }

    public boolean canSee(Cell cell) {
        if(this.ROW == cell.ROW || this.COL == cell.COL || this.BOX == cell.BOX) {
            return true;
        }
        return false;
    }

    public void setValue(int value) {
        this.value = value;
        this.isSolved = true;
        this.candidates.clear();
    }

    public void unsolve(){
        this.value = 0;
        this.isSolved = false;
        this.candidates = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
    }
}
