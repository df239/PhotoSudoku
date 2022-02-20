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


}
