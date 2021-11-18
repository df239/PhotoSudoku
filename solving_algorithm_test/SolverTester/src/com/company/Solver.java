package com.company;

import java.util.HashSet;

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
}
