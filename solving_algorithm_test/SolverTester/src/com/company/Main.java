package com.company;

public class Main {

    static int[][] sample = SudokuUtils.EXAMPLE6; //examples 1 - 8

    public static void main(String[] args) {
	// write your code here
        System.out.println("Original:");
        printMatrix(sample);
        Sudoku sudoku = new Sudoku(sample);

        long elapsed;
        long start = System.currentTimeMillis();
        int noChangeCounter = 0;
        while(!sudoku.solved()){
            if (noChangeCounter == 3){
                System.out.println("Could not solve");
                break;
            }

            if(Solver.solveNakedSingles(sudoku)){
                System.out.println("Naked Singles:");
                printMatrix(sudoku.grid);
                continue;
            }

            if(Solver.solveHiddenSingles(sudoku)){
                System.out.println("Hidden Singles:");
                printMatrix(sudoku.grid);
                continue;
            }

            noChangeCounter++;
        }
        elapsed = System.currentTimeMillis() - start;
        System.out.println("Time: "+elapsed+"ms");

//        long elapsed = 0l;
//        long start = System.currentTimeMillis();
//        int[][] grid = Solver.solveBacktracking(sudoku);
//        printMatrix(grid);
//        elapsed = System.currentTimeMillis() - start;
//        System.out.println("Time: "+elapsed+"ms");
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
