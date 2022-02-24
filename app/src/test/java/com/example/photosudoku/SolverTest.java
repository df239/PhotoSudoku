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
        Sudoku sudokuF2 = new Sudoku(TestingSamples.backtrack1);
        assertFalse(Solver.solveNakedSingles(sudokuF2));
        Sudoku sudokuF3 = new Sudoku(TestingSamples.hiddenSingle1);
        assertFalse(Solver.solveNakedSingles(sudokuF3));
        Sudoku sudokuF4 = new Sudoku(TestingSamples.pointingCandidates1);
        assertFalse(Solver.solveNakedSingles(sudokuF4));
    }

    @Test
    public void hidden_single_test(){
        Sudoku sudoku1 = new Sudoku(TestingSamples.hiddenSingle1);
        assertTrue(Solver.solveHiddenSingles(sudoku1));

        Sudoku sudokuEmpty = new Sudoku(TestingSamples.empty);
        assertFalse(Solver.solveHiddenSingles(sudokuEmpty));
        Sudoku sudokuF2 = new Sudoku(TestingSamples.backtrack1);
        assertFalse(Solver.solveHiddenSingles(sudokuF2));
        Sudoku sudokuF3 = new Sudoku(TestingSamples.pointingCandidates1);
        assertFalse(Solver.solveHiddenSingles(sudokuF3));
    }

    @Test
    public void pointing_candidates_test(){
        Sudoku sudoku1 = new Sudoku(TestingSamples.pointingCandidates1);
        assertTrue(Solver.solvePointingCandidates(sudoku1));
        Sudoku sudoku2 = new Sudoku(TestingSamples.pointingCandidates2);
        assertTrue(Solver.solvePointingCandidates(sudoku2));

        Sudoku sudokuEmpty = new Sudoku(TestingSamples.empty);
        assertFalse(Solver.solvePointingCandidates(sudokuEmpty));
        Sudoku sudokuF2 = new Sudoku(TestingSamples.backtrack1);
        assertFalse(Solver.solvePointingCandidates(sudokuF2));
    }

    @Test
    public void naked_pairs_test(){
        Sudoku sudoku1 = new Sudoku(TestingSamples.nakedPair1);
        assertTrue(Solver.solveNakedPair(sudoku1));
        Sudoku sudoku2 = new Sudoku(TestingSamples.nakedPair2);
        assertTrue(Solver.solveNakedPair(sudoku2));
        Sudoku sudoku3 = new Sudoku(TestingSamples.nakedPair3);  //contains two naked pairs, therefore it should assert true twice
        assertTrue(Solver.solveNakedPair(sudoku3));
        assertTrue(Solver.solveNakedPair(sudoku3));
        Sudoku sudoku4 = new Sudoku(TestingSamples.nakedPair4);
        assertTrue(Solver.solveNakedPair(sudoku4));

        Sudoku sudokuEmpty = new Sudoku(TestingSamples.empty);
        assertFalse(Solver.solveNakedPair(sudokuEmpty));
        Sudoku sudokuF1 = new Sudoku(TestingSamples.backtrack1);
        assertFalse(Solver.solveNakedPair(sudokuF1));
        Sudoku sudokuF2 = new Sudoku(TestingSamples.nakedPair5F);
        assertFalse(Solver.solveNakedPair(sudokuF2));
        Sudoku sudokuF3 = new Sudoku(TestingSamples.nakedPair6F);
        assertFalse(Solver.solveNakedPair(sudokuF3));
    }

}
