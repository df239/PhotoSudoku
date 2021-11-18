package com.company;

public class Main {

    static int[][] sample = SudokuUtils.EXAMPLE1;

    public static void main(String[] args) {
	// write your code here
        printMatrix(sample);
        Sudoku sudoku = new Sudoku(sample);

        Solver.solveNakedSingles(sudoku);
        sudoku.updateCellCandidates();
        System.out.println("Naked Singles:");
        printMatrix(sudoku.grid);

        Solver.solveHiddenSingles(sudoku);
        sudoku.updateCellCandidates();
        System.out.println("Hidden Singles:");
        printMatrix(sudoku.grid);

        Solver.solveNakedSingles(sudoku);
        sudoku.updateCellCandidates();
        System.out.println("Naked Singles:");
        printMatrix(sudoku.grid);

        Solver.solveHiddenSingles(sudoku);
        sudoku.updateCellCandidates();
        System.out.println("Hidden Singles:");
        printMatrix(sudoku.grid);


//        int[][] grid = Solver.solveBacktracking(sudoku);
//        printMatrix(grid);

    }

    static void printMatrix(int[][] mat){
        System.out.println("+ - - - + - - - + - - - +");
        int rowcount = 0;
        for (int i = 0; i < mat.length; i++){
            int colcount = 0;
            System.out.print("| ");
            for (int j = 0; j < mat[i].length; j++){
                if (mat[i][j] != 0) System.out.print(mat[i][j] + " ");
                else System.out.print(". ");

                colcount++;
                if(colcount == 3){
                    System.out.print("| ");
                    colcount = 0;
                }
            }
            System.out.println("");
            rowcount++;
            if(rowcount == 3){
                System.out.println("+ - - - + - - - + - - - +");
                rowcount = 0;
            }
        }
        System.out.println("");
    }
}
