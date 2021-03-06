package com.example.photosudoku.sudoku;

import com.example.photosudoku.sudoku.solvingSteps.*;

import java.util.*;

public class Solver {
    private static int[][] matrix;

    // =-=-=-=-= BACKTRACKING =-=-=-=-= //
    public static int[][] solveBacktracking(Sudoku input){
        matrix = input.grid;
        if(backtrack(0,0)){
            input.steps.add(new BruteForce(matrix));
            return matrix;
        }
        return null;
    }

    private static boolean backtrack(int row, int col){
        if (row == 9 - 1 && col == 9) return true;

        if (col == 9){
            row++;
            col = 0;
        }

        if (matrix[row][col] != 0) return backtrack(row, col + 1);

        for (int num = 1; num < 10; num++){
            if (isSafe(matrix, row, col, num)){
                matrix[row][col] = num;
                if (backtrack(row, col + 1)) return true;
            }
            matrix[row][col] = 0;
        }
        return false;
    }

    //check if a number can be placed in a cell
    static boolean isSafe(int[][] grid, int row, int col, int num){
        for (int x = 0; x <= 8; x++){
            if (grid[row][x] == num) return false;
        }

        for (int x = 0; x <= 8; x++){
            if (grid[x][col] == num) return false;
        }

        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++){
                if (grid[i + startRow][j + startCol] == num) return false;
            }
        }
        return true;
    }


    // =-=-=-=-= NAKED SINGLES =-=-=-=-= //
    public static boolean solveNakedSingles(Sudoku input) {
        Cell[][] grid = input.getCellMatrix();
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid.length; col++) {
                Cell c = grid[row][col];
                //if there is only one candidate in a cell, then it must be the correct solution for that cell
                if(!c.solved() && c.getCandidates().size() == 1) {
                    int candidate = c.getCandidates().get(0);
                    c.setValue(candidate);
                    input.updateCellCandidates(c);
                    input.steps.add(new NakedSingle(candidate,row,col,input.grid,input.getCellMatrix()));

                    return true;
                }
            }
        }
        return false;
    }

    // =-=-=-=-= HIDDEN SINGLES =-=-=-=-= //
    public static boolean solveHiddenSingles(Sudoku input){
        Cell[][] grid = input.getCellMatrix();
        for (int row = 0; row < grid.length; row++){
            for (int col = 0; col <grid.length; col++){
                Cell c = grid[row][col];
                if (!c.solved()){
                    if(findHiddenSingle(input,c,input.getRow(c.ROW), row, col, input.grid)){
                        return true;
                    }
                    if(findHiddenSingle(input,c,input.getCol(c.COL), row, col, input.grid)){
                        return true;
                    }
                    if(findHiddenSingle(input,c,input.getBox(c.BOX), row, col, input.grid)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean findHiddenSingle(Sudoku input, Cell currentCell, House house, int row, int col, int[][] grid){
        HashSet<Integer> groupCandidates;
        HashSet<Integer> cellCandidates = new HashSet<Integer>(currentCell.getCandidates());

        groupCandidates = getGroupCandidates(house.getGroup(),currentCell);
        cellCandidates.removeAll(groupCandidates);
        //if one candidate cannot be anywhere else in the same House, then it must be the solution for this cell
        if(cellCandidates.size() == 1){
            currentCell.setValue((Integer) cellCandidates.toArray()[0]);
            input.updateCellCandidates(currentCell);
            input.steps.add(new HiddenSingle((int)cellCandidates.toArray()[0], row, col, house.TYPE, grid, input.getCellMatrix()));
            return true;
        }
        return false;
    }

    //get the collection of candidates of all cells in the group except for the provided cell
    private static HashSet<Integer> getGroupCandidates(HashSet<Cell> group, Cell cell){
        HashSet<Integer> output = new HashSet<Integer>();
        for (Cell c : group){
            if (!c.equals(cell) && !c.solved()){
                output.addAll(c.getCandidates());
            }
        }
        return output;
    }

    // =-=-=-=-= POINTING CANDIDATES =-=-=-=-= //
    public static boolean solvePointingCandidates(Sudoku input){
        for (int boxIndex = 0; boxIndex < 9; boxIndex++){
            House box = input.getBox(boxIndex);
            int rowStart = (boxIndex / 3) * 3;

            //iterate over boxes and rows/columns separately, because we always compare the intersection between a box and a row/column
            for (int rowIndex = rowStart; rowIndex < rowStart + 3; rowIndex++){
                if (removePointingCandidates(box, input.getRow(rowIndex), input)){
                    return true;
                }

            }
            int colStart = (boxIndex % 3) * 3;
            for (int colIndex = colStart; colIndex < colStart + 3; colIndex++){
                if (removePointingCandidates(box, input.getCol(colIndex), input)){
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean removePointingCandidates(House box, House group, Sudoku input){
        //get the intersection of a house and a row/column
        CellGroup crossSection = new CellGroup(box.getCrossSection(group));
        HashSet<Integer> sharedCandidates = crossSection.getSharedCandidatesBetweenAny();
        if (crossSection.size() > 1 && sharedCandidates.size() != 0){
            //create groups of cells that are exclusive for row/column and box to see if a candidate appears elsewehere than the intersection
            CellGroup groupExclusive = new CellGroup(group.getCellDifference(crossSection.getCells()));
            CellGroup boxExclusive = new CellGroup(box.getCellDifference(crossSection.getCells()));
            for (int sharedCandidate : sharedCandidates){
                if (groupExclusive.getCandidates().contains(sharedCandidate) && !boxExclusive.getCandidates().contains(sharedCandidate)){
                    //if a candidate only appears in the intersection and the row/column, then it can be removed from all candidates of cells in that row/column
                    //because it must be in one of the intersecting cells within the box
                    for (Cell c : groupExclusive.getCells()){
                        c.removeCandidate(sharedCandidate);
                    }
                    input.steps.add(new PointingCandidates(sharedCandidate,group,box,crossSection.getCellsWithCandidate(sharedCandidate),input.grid,input.getCellMatrix()));
                    return true;
                }
                else if (!groupExclusive.getCandidates().contains(sharedCandidate) && boxExclusive.getCandidates().contains(sharedCandidate)){
                    //if a candidate only appears in the intersection and the box, then it can be removed from all candidates of cells in that box
                    //because it must be in one of the intersecting cells within the row/column
                    for (Cell c : boxExclusive.getCells()){
                        c.removeCandidate(sharedCandidate);
                    }
                    input.steps.add(new PointingCandidates(sharedCandidate,box,group,crossSection.getCellsWithCandidate(sharedCandidate),input.grid,input.getCellMatrix()));
                    return true;
                }
            }
        }
        return false;
    }

    // =-=-=-=-= NAKED PAIRS =-=-=-=-= //
    public static boolean solveNakedPair(Sudoku input){
        for (int i = 0; i < 9; i++){
            if (findNakedPairInsideGroup(input.getRow(i), input)){
                return true;
            }
            if (findNakedPairInsideGroup(input.getCol(i), input)){
                return true;
            }
            if (findNakedPairInsideGroup(input.getBox(i), input)){
                return true;
            }
        }
        return false;
    }

    private static boolean findNakedPairInsideGroup(House group, Sudoku input){
        //find all cells that have exactly two candidates
        List<Cell> bivalueCells = new ArrayList<Cell>();
        for (Cell cell : group.getGroup()){
            if (!cell.solved() && cell.biValue()){
                bivalueCells.add(cell);
            }
        }
        if(bivalueCells.size() == 2){
            Cell temp = bivalueCells.get(0);
            if (temp.getCandidates().containsAll(bivalueCells.get(1).getCandidates())){
                //if there are two cells with exactly two candidates in a House AND those candidates are the same,
                //attempt to remove those candidates from all other cells in the House
                if(removeCandidatesOutsideOfPair(group.getGroup(),temp.getCandidates(),bivalueCells)){
                    input.steps.add(new NakedPair(temp,bivalueCells.get(1),group, input.grid, input.getCellMatrix()));
                    return true;
                }
            }
        }
        else if(bivalueCells.size() > 2){
            //first find a pair of bi-value cells with the exact same candidates and
            //attempt to remove those candidates from all other cells in the House
            for(int x = 0; x < bivalueCells.size(); x++){
                Cell tempX = bivalueCells.get(x);
                for (int y = x + 1; y < bivalueCells.size(); y++){
                    Cell tempY = bivalueCells.get(y);
                    if (tempX.getCandidates().containsAll(tempY.getCandidates())){
                        if(removeCandidatesOutsideOfPair(group.getGroup(),tempX.getCandidates(),new ArrayList<Cell>(Arrays.asList(tempX,tempY)))){
                            input.steps.add(new NakedPair(tempX,tempY,group,input.grid,input.getCellMatrix()));
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
        boolean changeMade = false;
        for (Cell cell : group){
            //by not being bi-value the cell is inherently not inside the bivalueCell list
            if (!cell.solved() && !biValueCells.contains(cell) &&
                    (cell.getCandidates().contains(pairCandidates.get(0)) || cell.getCandidates().contains(pairCandidates.get(1)))){
                cell.removeCandidates(candidates);
                changeMade = true;
            }
        }
        return changeMade;
    }

    // =-=-=-=-= HIDDEN PAIRS =-=-=-=-= //
    public static boolean solveHiddenPair(Sudoku input){
        for (int i = 0; i < 9; i++){
            if (findHiddenPairInsideGroup(input.getRow(i), input)){
                return true;
            }
            if (findHiddenPairInsideGroup(input.getCol(i), input)){
                return true;
            }
            if (findHiddenPairInsideGroup(input.getBox(i), input)){
                return true;
            }
        }
        return false;
    }

    private static boolean findHiddenPairInsideGroup(House group, Sudoku input){
        ArrayList<Cell> cells = new ArrayList<Cell>(group.getGroup());
        //for each unsolved pair of cells in the House, check if they contain a pair of candidates that do not appear in any other cell in the House
        for(int i = 0; i < 8; i++){
            Cell c1 = cells.get(i);
            if (!c1.solved()){
                for (int j = i + 1; j < 9; j++){
                    Cell c2 = cells.get(j);
                    if (!c2.solved()){
                        ArrayList<Integer> shared = new ArrayList<Integer>(c1.getSharedCandidatesWith(c2));
                        if(shared.size() >= 2){
                            CellGroup othersInGroup = new CellGroup(group.getCellDifference(c1,c2));
                            for (int x = 0; x < shared.size() - 1; x++){
                                for (int y = x + 1; y < shared.size(); y++){
                                    List<Integer> candidatePair = Arrays.asList(shared.get(x), shared.get(y));
                                    if (!othersInGroup.sharesAnyCandidateWith(candidatePair)){
                                        //if two cells share a pair of candidates AND those candidates do not appear in any other cell in the House
                                        //AND are not a naked pair, then remove all other candidates from those two cells except the said pair of candidates
                                        if (c1.getCandidates().size() > 2 || c2.getCandidates().size() > 2){
                                            removeCandidatesFromCellExcept(candidatePair,c1);
                                            removeCandidatesFromCellExcept(candidatePair,c2);
                                            input.steps.add(new HiddenPair(c1,c2,candidatePair,group,input.grid, input.getCellMatrix()));
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private static void removeCandidatesFromCellExcept(Collection<Integer> candidates, Cell cell){
        HashSet<Integer> cellCandidates = new HashSet<Integer>(cell.getCandidates());
        cellCandidates.removeAll(candidates);
        cell.removeCandidates(cellCandidates);
    }
}
