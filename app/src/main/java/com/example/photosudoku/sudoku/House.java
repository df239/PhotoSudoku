package com.example.photosudoku.sudoku;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class House {
    private HashSet<Cell> group;
    private HashSet<Integer> solvedValues;
    private HashSet<Integer> candidates;

    public final String TYPE;

    public House(String houseType) {
        this.TYPE = houseType;
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
        for(Cell c: this.group) {
            if (!c.solved()) {
                temp.addAll(c.getCandidates());
            }
        }
        return temp;
    }

    public HashSet<Cell> getCrossSection(House house, boolean countUnsolvedOnly){
        HashSet<Cell> set = new HashSet<Cell>();
        for (Cell c : house.getGroup()){
            if(countUnsolvedOnly){
                if (!c.solved() && this.group.contains(c)){
                    set.add(c);
                }
            }
            else if(this.group.contains(c)){
                set.add(c);
            }
        }
        return set;
    }

    public HashSet<Cell> getCrossSection(House house){
        return this.getCrossSection(house, true);
    }

    public HashSet<Cell> getCellDifference(Collection<Cell> collection){
        HashSet<Cell> set = new HashSet<Cell>();
        for (Cell c : this.group){
            if (!collection.contains(c)){
                set.add(c);
            }
        }
        return set;
    }

    public HashSet<Cell> getCellDifference(Cell... cells){
        return this.getCellDifference(Arrays.asList(cells));
    }
}
