package com.example.photosudoku.sudoku;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class House {
    //class representing a row, column or a box
    private HashSet<Cell> group;
    private HashSet<Integer> solvedValues;
    private HashSet<Integer> candidates;

    public final String TYPE;  //row, column or box

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

    //get the set of all candidates of this House
    public HashSet<Integer> getCandidates(){
        HashSet<Integer> temp = new HashSet<Integer>();
        for(Cell c: this.group) {
            if (!c.solved()) {
                temp.addAll(c.getCandidates());
            }
        }
        return temp;
    }

    //get a group of cells that are at an intersection with another House
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

    //get a group of cells that belong to this House, but do not belong to the provided collection
    public HashSet<Cell> getCellDifference(Collection<Cell> collection){
        HashSet<Cell> set = new HashSet<Cell>();
        for (Cell c : this.group){
            if (!collection.contains(c)){
                set.add(c);
            }
        }
        return set;
    }

    //get a group of cells that belong to this House, but do not belong to the provided cells
    public HashSet<Cell> getCellDifference(Cell... cells){
        return this.getCellDifference(Arrays.asList(cells));
    }
}
