package com.company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class CellGroup {
    private HashSet<Cell> group;
    private HashSet<Integer> solvedValues;
    private HashSet<Integer> candidates;

    public CellGroup() {
        this.group = new HashSet<Cell>();
        this.solvedValues = new HashSet<Integer>();
        this.candidates = new HashSet<Integer>();
    }

    public void add(Cell cell) {
        this.group.add(cell);
        if (cell.solved()) {
            this.solvedValues.add(cell.getValue());
        }
    }

    public boolean contains(int value) {
        return this.solvedValues.contains(value);
    }

    public HashSet<Cell> getGroup(){
        return this.group;
    }

//    public void updateCandidates(){
//        this.candidates.clear();
//        for(Cell c : this.group){
//            if(!c.solved()){
//                this.candidates.addAll(c.candidates);
//            }
//        }
//    }

    public HashSet<Integer> getCandidates(){
        HashSet<Integer> temp = new HashSet<Integer>();
        temp.addAll(this.candidates);
        return temp;
    }
}
