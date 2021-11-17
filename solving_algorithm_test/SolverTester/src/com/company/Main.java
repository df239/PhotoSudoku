package com.company;

public class Main {

    static int[][] sample = SudokuUtils.EXAMPLE2;

    public static void main(String[] args) {
	// write your code here
        try{
            printMatrix(sample);
            Sudoku sudoku = new Sudoku(sample);
            for (int i = 0; i < 2; i++){
                sudoku = Solver.solveNakedSingles(sudoku);
                sudoku.updateCellCandidates();
                printMatrix(sudoku.grid);
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }

    }

    static void printMatrix(int[][] mat){
        for (int i = 0; i < mat.length; i++){
            for (int j = 0; j < mat[i].length; j++){
                if (mat[i][j] != 0) System.out.print(mat[i][j] + " ");
                else System.out.print(". ");
            }
            System.out.println("");
        }
        System.out.println("");
    }
}
