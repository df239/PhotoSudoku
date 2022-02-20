package com.example.photosudoku;

import com.example.photosudoku.sudoku.Solver;
import com.example.photosudoku.sudoku.Sudoku;

import org.junit.Test;
import static org.junit.Assert.*;

public class SolverTest {

    @Test
    public void backtracking_test(){
        Sudoku sudoku = new Sudoku(TestingSamples.empty);
        assertArrayEquals(TestingSamples.defaultFull, Solver.solveBacktracking(sudoku));

        Sudoku sudoku2 = new Sudoku(TestingSamples.backtrack1);
        assertArrayEquals(TestingSamples.backtrack1result, Solver.solveBacktracking(sudoku2));
    }

    @Test
    public void naked_single_test(){
        Sudoku sudoku1 = new Sudoku(TestingSamples.nakedSingle1);
        assertTrue(Solver.solveNakedSingles(sudoku1));

        Sudoku sudokuEmpty = new Sudoku(TestingSamples.empty);
        assertFalse(Solver.solveNakedSingles(sudokuEmpty));

        Sudoku sudoku2 = new Sudoku(TestingSamples.backtrack1);
        assertFalse(Solver.solveNakedSingles(sudoku2));
    }

}
