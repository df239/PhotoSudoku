package com.example.photosudoku;

import com.example.photosudoku.sudoku.Solver;
import com.example.photosudoku.sudoku.Sudoku;
import com.example.photosudoku.sudoku.solvingSteps.Beginning;
import com.example.photosudoku.sudoku.solvingSteps.BruteForce;
import com.example.photosudoku.sudoku.solvingSteps.HiddenPair;
import com.example.photosudoku.sudoku.solvingSteps.HiddenSingle;
import com.example.photosudoku.sudoku.solvingSteps.ISolvingStep;
import com.example.photosudoku.sudoku.solvingSteps.NakedPair;
import com.example.photosudoku.sudoku.solvingSteps.NakedSingle;
import com.example.photosudoku.sudoku.solvingSteps.PointingCandidates;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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
        int[] affectedSquares = steps.get(1).getAffectedSquares();
        assertEquals(2, affectedSquares.length);  //should equal 2 because row and col are separate values
        assertEquals(0, affectedSquares[0]);
        assertEquals(7, affectedSquares[1]);
    }

    @Test
    public void step_hidden_single_test(){
        Sudoku sudoku = new Sudoku(TestingSamples.hiddenSingle1);
        Solver.solveHiddenSingles(sudoku);
        List<ISolvingStep> steps = sudoku.steps;
        assertEquals(2, steps.size());
        assertTrue(steps.get(0) instanceof Beginning);
        assertTrue(steps.get(1) instanceof HiddenSingle);
        int[] affectedSquares = steps.get(1).getAffectedSquares();
        assertEquals(2, affectedSquares.length);  //should equal 2 because row and col are separate values
        assertEquals(1, affectedSquares[0]);
        assertEquals(1, affectedSquares[1]);
    }

    @Test
    public void step_pointing_candidates_test(){
        Sudoku sudoku = new Sudoku(TestingSamples.pointingCandidates3);
        Solver.solvePointingCandidates(sudoku);
        List<ISolvingStep> steps = sudoku.steps;
        assertEquals(2, steps.size());
        assertTrue(steps.get(0) instanceof Beginning);
        assertTrue(steps.get(1) instanceof PointingCandidates);
        int[] affectedSquares = steps.get(1).getAffectedSquares();
        assertEquals(4, affectedSquares.length);
        List<Integer> answer = Arrays.asList(2,1,2,0);
        List<Integer> affected = Arrays.stream(affectedSquares).boxed().collect(Collectors.toList());
        assertTrue(answer.containsAll(affected));

        Sudoku sudoku2 = new Sudoku(TestingSamples.pointingCandidates1);
        Solver.solvePointingCandidates(sudoku2);
        List<ISolvingStep> steps2 = sudoku2.steps;
        assertEquals(2, steps2.size());
        assertTrue(steps2.get(0) instanceof Beginning);
        assertTrue(steps2.get(1) instanceof PointingCandidates);
        int[] affectedSquares2 = steps2.get(1).getAffectedSquares();
        assertEquals(6, affectedSquares2.length);
        List<Integer> answer2 = Arrays.asList(0,7,1,7,2,7);
        List<Integer> affected2 = Arrays.stream(affectedSquares2).boxed().collect(Collectors.toList());
        assertTrue(answer2.containsAll(affected2));
    }

    @Test
    public void step_naked_pair_test(){
        Sudoku sudoku = new Sudoku(TestingSamples.nakedPair1);
        Solver.solveNakedPair(sudoku);
        List<ISolvingStep> steps = sudoku.steps;
        assertEquals(2, steps.size());
        assertTrue(steps.get(0) instanceof Beginning);
        assertTrue(steps.get(1) instanceof NakedPair);
        int[] affectedSquares = steps.get(1).getAffectedSquares();
        assertEquals(4, affectedSquares.length);
        List<Integer> answer = Arrays.asList(0,3,0,7);
        List<Integer> affected = Arrays.stream(affectedSquares).boxed().collect(Collectors.toList());
        assertTrue(answer.containsAll(affected));
    }

    @Test
    public void step_hidden_pair_test(){
        Sudoku sudoku = new Sudoku(TestingSamples.hiddenPair1);
        Solver.solveHiddenPair(sudoku);
        List<ISolvingStep> steps = sudoku.steps;
        assertEquals(2, steps.size());
        assertTrue(steps.get(0) instanceof Beginning);
        assertTrue(steps.get(1) instanceof HiddenPair);
        int[] affectedSquares = steps.get(1).getAffectedSquares();
        assertEquals(4, affectedSquares.length);
        List<Integer> answer = Arrays.asList(0,1,0,7);
        List<Integer> affected = Arrays.stream(affectedSquares).boxed().collect(Collectors.toList());
        assertTrue(answer.containsAll(affected));
    }
}
