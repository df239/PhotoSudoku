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
        System.out.println("  _ _ _   _ _ _   _ _ _");
        int rowcount = 0;
        for (int i = 0; i < mat.length; i++){
            int colcount = 0;
            System.out.print("| ");
            for (int j = 0; j < mat[i].length; j++){
                if (mat[i][j] != 0) System.out.print(mat[i][j] + " ");
                else System.out.print("  ");

                colcount++;
                if(colcount == 3){
                    System.out.print("| ");
                    colcount = 0;
                }
            }
            System.out.println("");
            rowcount++;
            if(rowcount == 3){
                System.out.println("  _ _ _   _ _ _   _ _ _");
                rowcount = 0;
            }
        }
        System.out.println("");
    }
}
