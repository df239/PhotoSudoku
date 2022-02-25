package com.example.photosudoku;

import com.example.photosudoku.sudoku.Solver;
import com.example.photosudoku.sudoku.Sudoku;
import com.example.photosudoku.sudoku.solvingSteps.Beginning;
import com.example.photosudoku.sudoku.solvingSteps.BruteForce;
import com.example.photosudoku.sudoku.solvingSteps.HiddenSingle;
import com.example.photosudoku.sudoku.solvingSteps.ISolvingStep;
import com.example.photosudoku.sudoku.solvingSteps.NakedSingle;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class SolvingStepsTest {

    @Test
    public void step_beginning_test(){
        Sudoku sudoku = new Sudoku(TestingSamples.sample1);
        List<ISolvingStep> steps = sudoku.steps;
        assertEquals(1,steps.size());
        assertTrue(steps.get(0) instanceof Beginning);
    }

    @Test
    public void step_backtracking_test(){
        Sudoku sudoku = new Sudoku(TestingSamples.backtrack1);
        Solver.solveBacktracking(sudoku);
        List<ISolvingStep> steps = sudoku.steps;
        assertEquals(2, steps.size());
        assertTrue(steps.get(0) instanceof Beginning);
        assertTrue(steps.get(1) instanceof BruteForce);
        assertArrayEquals(TestingSamples.backtrack1result, steps.get(1).getGrid());
    }

    @Test
    public void step_naked_single_test(){
        Sudoku sudoku = new Sudoku(TestingSamples.nakedSingle1);
        Solver.solveNakedSingles(sudoku);
        List<ISolvingStep> steps = sudoku.steps;
        assertEquals(2, steps.size());
        assertTrue(steps.get(0) instanceof Beginning);
        assertTrue(steps.get(1) instanceof NakedSingle);
        assertEquals(2, steps.get(1).getAffectedSquares().length);  //should equal 2 because row and col are separate values
    }

    @Test
    public void step_hidden_single_test(){
        Sudoku sudoku = new Sudoku(TestingSamples.hiddenSingle1);
        Solver.solveHiddenSingles(sudoku);
        List<ISolvingStep> steps = sudoku.steps;
        assertEquals(2, steps.size());
        assertTrue(steps.get(0) instanceof Beginning);
        assertTrue(steps.get(1) instanceof HiddenSingle);
        assertEquals(2, steps.get(1).getAffectedSquares().length);  //should equal 2 because row and col are separate values
    }
}
