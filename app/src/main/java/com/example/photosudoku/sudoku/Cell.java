package com.example.photosudoku.sudoku;
import java.util.*;

public class Cell {
    //class that represents one square in a sudoku
    private int value;
    private boolean isSolved;
    private boolean isBiValue;

    //indexes of the cell's row, column and box
    public final int ROW;
    public final int COL;
    public final int BOX;

    private List<Integer> candidates;

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
    }


    public int getValue() {
        return this.value;
    }

    public boolean solved() {
        return this.isSolved;
    }

    public boolean biValue(){
        return this.isBiValue;
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

    //solves this cell with a value
    public void setValue(int value) {
        this.value = value;
        this.isSolved = true;
        this.candidates.clear();
    }

    public boolean removeCandidate(int candidate){
        boolean status = this.candidates.remove((Integer)candidate);
        if(this.candidates.size() == 2){
            this.isBiValue = true;
        }
        return status;
    }

    public void removeCandidates(Collection<Integer> candidates){
        this.candidates.removeAll(candidates);
        if(this.candidates.size() == 2){
            this.isBiValue = true;
        }
    }

    public List<Integer> getCandidates(){
        return new ArrayList<>(this.candidates);
    }

    //returns a set of candidates that this cell shares with the provided cell
    public HashSet<Integer> getSharedCandidatesWith(Cell cell){
        HashSet<Integer> shared = new HashSet<>();
        for (int candidate : cell.getCandidates()){
            if (this.getCandidates().contains(candidate)){
                shared.add(candidate);
            }
        }
        return shared;
    }
}
