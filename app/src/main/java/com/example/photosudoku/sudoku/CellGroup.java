package com.example.photosudoku.sudoku;

import java.util.Collection;
import java.util.HashSet;

public class CellGroup {
    //a helper class that represents a generic group of cells other than a House or a Sudoku
    private HashSet<Cell> group;

    public CellGroup(Collection<Cell> collection){
        this.group = new HashSet<Cell>(collection);
    }

    //get the set of all candidates of all cells in this group
    public HashSet<Integer> getCandidates(){
        HashSet<Integer> set = new HashSet<Integer>();
        for (Cell c : this.group){
            set.addAll(c.getCandidates());
        }
        return set;
    }

    //get the set of candidates that are shared between two or more cells in this group
    public HashSet<Integer> getSharedCandidatesBetweenAny(){
        HashSet<Integer> set = new HashSet<>();
        HashSet<Integer> candidates = this.getCandidates();
        for (int candidate : candidates){
            boolean oneContains = false;
            for (Cell c : this.group){
                if (c.getCandidates().contains(candidate)){
                    if (oneContains){
                        set.add(candidate);
                    }
                    else{
                        oneContains = true;
                    }
                }
            }
        }
        return set;
    }

    //return in any cell in this group shares a candidate with the provided collection
    public boolean sharesAnyCandidateWith(Collection<Integer> candidates){
        for (int candidate : candidates){
            if (this.getCandidates().contains(candidate)){
                return true;
            }
        }
        return false;
    }

    public HashSet<Cell> getCells(){
        return this.group;
    }

    //return only cells that have a specific candidate
    public HashSet<Cell> getCellsWithCandidate(int candidate){
        HashSet<Cell> set = new HashSet<>();
        for(Cell c : this.getCells()){
            if(c.containsCandidate(candidate)){
                set.add(c);
            }
        }
        return set;
    }

    public int size(){
        return this.group.size();
    }
}
