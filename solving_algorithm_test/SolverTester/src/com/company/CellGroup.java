package com.company;

import java.util.Collection;
import java.util.HashSet;

public class CellGroup {
    private HashSet<Cell> group;

    public CellGroup(Collection<Cell> collection){
        this.group = new HashSet<Cell>(collection);
    }

    public HashSet<Integer> getCandidates(){
        HashSet<Integer> set = new HashSet<Integer>();
        for (Cell c : this.group){
            set.addAll(c.getCandidates());
        }
        return set;
    }

    public HashSet<Integer> getSharedCandidates(){
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

    public int size(){
        return this.group.size();
    }
}
