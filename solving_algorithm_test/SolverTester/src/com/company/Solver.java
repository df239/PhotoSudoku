package com.company;

import javafx.util.Pair;

import java.util.*;

public class Solver {
    private static Sudoku s;
    private static int[][] matrix;

    public static int[][] solveBacktracking(Sudoku input){
        matrix = input.grid;
        if(backtrack(0,0))
            return matrix;
        return null;
    }

    private static boolean backtrack(int row, int col){
        if (row == 9 - 1 && col == 9)
            return true;

        if (col == 9) {
            row++;
            col = 0;
        }

        if (matrix[row][col] != 0)
            return backtrack(row, col + 1);

        for (int num = 1; num < 10; num++) {
            if (isSafe(matrix, row, col, num)) {
                matrix[row][col] = num;
                if (backtrack(row, col + 1))
                    return true;
            }
            matrix[row][col] = 0;
        }
        return false;
    }

    static boolean isSafe(int[][] grid, int row, int col, int num){
        for (int x = 0; x <= 8; x++)
            if (grid[row][x] == num)
                return false;

        for (int x = 0; x <= 8; x++)
            if (grid[x][col] == num)
                return false;

        int startRow = row - row % 3, startCol
                = col - col % 3;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (grid[i + startRow][j + startCol] == num)
                    return false;

        return true;
    }


//    private static boolean backtrack(int[][] grid, Sudoku sudoku){
//        for (int row = 0; row < 9; row++){
//            for (int col = 0; col < 9; col++){
//                Cell c = sudoku.getCellMatrix()[row][col];
//                if (grid[row][col] != 0){
//                    for (int val = 1; val <= 9; val++){
//                        if(sudoku.isValuePossible(val,c)){
//                            grid[row][col] = val;
//                            return backtrack(grid,sudoku);
//                            grid[row][col] = 0;
//                        }
//                    }
//                    return grid;
//                }
//            }
//        }
//    }

    public static boolean solveNakedSingles(Sudoku input) {
        boolean changeMade = false;
        Cell[][] grid = input.getCellMatrix();
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid.length; col++) {
                Cell c = grid[row][col];
                if(!c.solved() && c.getCandidates().size() == 1) {
                    c.setValue(c.getCandidates().get(0));
                    input.updateCellCandidates(c);
                    changeMade = true;
                }
            }
        }
        return changeMade;
    }

    public static boolean solveHiddenSingles(Sudoku input){
        boolean changeMade = false;
        Cell[][] grid = input.getCellMatrix();
        for (int row = 0; row < grid.length; row++){
            for (int col = 0; col <grid.length; col++){
                Cell c = grid[row][col];
                if (!c.solved()){
                    HashSet<Integer> cellCandidates = new HashSet<Integer>();
                    HashSet<Integer> groupCandidates;

                    cellCandidates.addAll(c.getCandidates());
                    groupCandidates = getGroupCandidates(input.getRow(c.ROW).getGroup(),c);
                    cellCandidates.removeAll(groupCandidates);
                    if(cellCandidates.toArray().length == 1){
                        c.setValue((Integer) cellCandidates.toArray()[0]);
                        input.updateCellCandidates(c);
                        return true;
//                        changeMade = true;
//                        break;
                    }
                    groupCandidates.clear();

                    cellCandidates.addAll(c.getCandidates());
                    groupCandidates = getGroupCandidates(input.getCol(c.COL).getGroup(),c);
                    cellCandidates.removeAll(groupCandidates);
                    if(cellCandidates.toArray().length == 1){
                        c.setValue((Integer) cellCandidates.toArray()[0]);
                        input.updateCellCandidates(c);
                        return true;
//                        changeMade = true;
//                        break;
                    }
                    groupCandidates.clear();

                    cellCandidates.addAll(c.getCandidates());
                    groupCandidates = getGroupCandidates(input.getBox(c.BOX).getGroup(),c);
                    cellCandidates.removeAll(groupCandidates);
                    if(cellCandidates.toArray().length == 1){
                        c.setValue((Integer) cellCandidates.toArray()[0]);
                        input.updateCellCandidates(c);
                        return true;
                        //changeMade = true;
                    }
                }
            }
        }
        return false;
        //return changeMade;
    }

    private static HashSet<Integer> getGroupCandidates(HashSet<Cell> group, Cell cell){
        HashSet<Integer> output = new HashSet<Integer>();
        for (Cell c : group){
            if (!c.equals(cell) && !c.solved()){
                output.addAll(c.getCandidates());
            }
        }
        return output;
    }

    public static boolean solveNakedPair(Sudoku input){
        for (int i = 0; i < 9; i++){
            if (findNakedPairInsideGroup(input.getRow(i))){
                return true;
            }
            if (findNakedPairInsideGroup(input.getCol(i))){
                return true;
            }
            if (findNakedPairInsideGroup(input.getBox(i))){
                return true;
            }
        }
        return false;
    }

    // =-=-=-=-= NAKED PAIRS =-=-=-=-= //
    private static ArrayList<Cell> lastNakedPair = new ArrayList<Cell>(2);
    private static boolean findNakedPairInsideGroup(CellGroup group){
        List<Cell> bivalueCells = new ArrayList<Cell>();
        for (Cell cell : group.getGroup()){
            if (!cell.solved() && cell.biValue()){
                bivalueCells.add(cell);
            }
        }
        if(bivalueCells.size() == 2){
            Cell temp = bivalueCells.get(0);
            if (temp.getCandidates().containsAll(bivalueCells.get(1).getCandidates()) && !lastNakedPair.contains(temp)){
                if(removeCandidatesOutsideOfPair(group.getGroup(),temp.getCandidates(),bivalueCells)){
                    if(lastNakedPair.size() == 0){
                        lastNakedPair.add(temp);
                        lastNakedPair.add(bivalueCells.get(1));
                    }
                    else{
                        lastNakedPair.set(0,temp);
                        lastNakedPair.set(1,bivalueCells.get(1));
                    }
                    return true;
                }
            }
        }
        else if(bivalueCells.size() > 2){
            for(int x = 0; x < bivalueCells.size(); x++){
                Cell tempX = bivalueCells.get(x);
                for (int y = x + 1; y < bivalueCells.size(); y++){
                    Cell tempY = bivalueCells.get(y);
                    if (tempX.getCandidates().containsAll(tempY.getCandidates()) && !lastNakedPair.contains(tempX)){
                        if(removeCandidatesOutsideOfPair(group.getGroup(),tempX.getCandidates(),new ArrayList<Cell>(Arrays.asList(tempX,tempY)))){
                            if(lastNakedPair.size() == 0){
                                lastNakedPair.add(tempX);
                                lastNakedPair.add(tempY);
                            }
                            else{
                                lastNakedPair.set(0,tempX);
                                lastNakedPair.set(0,tempY);
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private static boolean removeCandidatesOutsideOfPair(HashSet<Cell> group, List<Integer> pairCandidates, Collection<Cell> biValueCells){
        HashSet<Integer> candidates = new HashSet<Integer>(pairCandidates);
        for (Cell cell : group){
            //by not being bi-value the cell is inherently not inside the bivalueCell list
            if (!cell.solved() && !biValueCells.contains(cell) &&
                    (cell.getCandidates().contains(pairCandidates.get(0)) || cell.getCandidates().contains(pairCandidates.get(1)))){
                cell.removeCandidates(candidates);
                return true;
            }
        }
        return false;
    }
}
