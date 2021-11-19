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

    public int getSharedCandidate(){
        HashSet<Integer> candidates = this.getCandidates();
        for (int candidate : candidates){
            boolean allContain = true;
            for (Cell c : this.group){
                if (!c.getCandidates().contains(candidate)){
                    allContain = false;
                    break;
                }
            }
            if(allContain){
                return candidate;
            }
        }
        return 0;
    }

    public HashSet<Cell> getCells(){
        return this.group;
    }

    public int size(){
        return this.group.size();
    }
}
