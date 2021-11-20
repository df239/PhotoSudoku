package com.company;

public class Main {

    static int[][] sample = SudokuUtils.EXAMPLE14; //examples 1 - 11; 111 - 116

    static int[][] grid;

    public static void main(String[] args) {
	// write your code here
        System.out.println("Original:");
        printMatrix(sample);
        Sudoku sudoku = new Sudoku(sample);

        grid = sudoku.grid;

        long elapsed;
        long start = System.currentTimeMillis();
        int noChangeCounter = 0;
        while(!sudoku.solved()){
            if (noChangeCounter == 3){
                System.out.println("Could not solve. Using backtracking:");
                int[][] grid = Solver.solveBacktracking(sudoku);
                grid = sudoku.grid;
                printMatrix(grid);
                break;
            }

            if(Solver.solveNakedSingles(sudoku)){
                System.out.println("Naked Singles:");
                grid = sudoku.grid;
                printMatrix(sudoku.grid);
                continue;
            }

            if(Solver.solveHiddenSingles(sudoku)){
                System.out.println("Hidden Singles:");
                grid = sudoku.grid;
                printMatrix(sudoku.grid);
                continue;
            }

            if(Solver.solvePointingCandidates(sudoku)){
                System.out.println("- Pointing Candidates -");
                continue;
            }

            if(Solver.solveNakedPair(sudoku)){
                System.out.println("- Naked Pair -");
                continue;
            }

            if(Solver.solveHiddenPair(sudoku)){
                System.out.println("- Hidden Pair -");
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
