package com.company;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CellGroup {
    private HashSet<Cell> group;
    private HashSet<Integer> solvedValues;

    public CellGroup() {
        this.group = new HashSet<Cell>();
        this.solvedValues = new HashSet<Integer>();
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
}
