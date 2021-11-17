package com.example.photosudoku.sudoku;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CellGroup {
    private List<Cell> group;
    private HashSet<Integer> solvedValues;

    public CellGroup() {
        this.group = new ArrayList<Cell>(9);
        this.solvedValues = new HashSet<Integer>();
    }

    public void add(Cell cell) {
        this.group.add(cell.getValue() - 1, cell);
        if (!cell.solved()) {
            this.solvedValues.add(cell.getValue());
        }
    }

    public boolean contains(int value) {
        return this.solvedValues.contains(value);
    }
}
