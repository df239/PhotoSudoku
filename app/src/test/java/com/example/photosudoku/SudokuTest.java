package com.example.photosudoku;

import android.util.Log;

import com.example.photosudoku.sudoku.Cell;
import com.example.photosudoku.sudoku.House;
import com.example.photosudoku.sudoku.Sudoku;
import com.example.photosudoku.sudoku.SudokuUtils;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class SudokuTest {

    @Test
    public void sudoku_init_test(){
        Sudoku sudoku1 = new Sudoku(TestingSamples.empty);
        Random random = new Random();
        for(int i = 0; i < 10; i++){
            int randRow = random.nextInt(9);
            int randCol = random.nextInt(9);
            assertFalse(sudoku1.getCellMatrix()[randRow][randCol].solved());
        }

        Sudoku sudoku2 = new Sudoku(TestingSamples.defaultFull);
        assertTrue(sudoku2.solved());
        for(int i = 0; i < 10; i++){
            int randRow = random.nextInt(9);
            int randCol = random.nextInt(9);
            assertTrue(sudoku2.getCellMatrix()[randRow][randCol].solved());
        }

        Sudoku sudoku3 = new Sudoku(TestingSamples.sample1);
        int[] candidatePlaces = new int[]{0,8,6,5,3,1,4,7,4,4};  //tuples of row and col indices
        for(int i = 0; i < 5; i++){
            assertFalse(sudoku3.getCellMatrix()[candidatePlaces[2*i]][candidatePlaces[2*i+1]].solved());
        }
        int[] solvedPlaces = new int[]{0,0,8,8,6,3,3,2,7,5};   //tupes of row and col indices
        for(int i = 0; i <solvedPlaces.length/2; i++){
            assertTrue(sudoku3.getCellMatrix()[solvedPlaces[2*i]][solvedPlaces[2*i+1]].solved());
        }
    }

    @Test
    public void sudoku_candidates_init_test(){
        Sudoku sudoku1 = new Sudoku(TestingSamples.empty);
        List<Integer> list = Arrays.asList(1,2,3,4,5,6,7,8,9);
        HashSet<Integer> allCandidates = new HashSet<>(list);
        Random random = new Random();
        for(int i = 0; i < 10; i++){
            int randRow = random.nextInt(9);
            int randCol = random.nextInt(9);
            HashSet<Integer> actual = new HashSet<>(sudoku1.getCellMatrix()[randRow][randCol].getCandidates());
            assertEquals(allCandidates,actual);
        }

        Sudoku sudoku2 = new Sudoku(TestingSamples.defaultFull);
        for(int i = 0; i < 10; i++){
            int randRow = random.nextInt(9);
            int randCol = random.nextInt(9);
            HashSet<Integer> actual = new HashSet<>(sudoku2.getCellMatrix()[randRow][randCol].getCandidates());
            assertEquals(new HashSet<Integer>(), actual);
        }

        Sudoku sudoku3 = new Sudoku(TestingSamples.sample1);
        int[] candidateAmounts = new int[]{1,2,3,4,5};
        int[] candidatePlaces = new int[]{0,8,6,5,3,1,4,7,4,4};  //tuples of row and col indices
        for(int i = 0; i < candidateAmounts.length; i++){
            int len = sudoku3.getCellMatrix()[candidatePlaces[2*i]][candidatePlaces[2*i+1]].getCandidates().size();
            assertEquals(candidateAmounts[i],len);
        }
    }

    @Test
    public void update_candidates_test(){
        Sudoku sudoku = new Sudoku(TestingSamples.sample1);
        int rowIdx = 4;
        int colIdx = 4;
        int boxIdx = SudokuUtils.getBox(rowIdx,colIdx);
        int value = 7;
        if(sudoku.getRow(rowIdx).getCandidates().contains(value) || sudoku.getCol(colIdx).getCandidates().contains(value) || sudoku.getBox(boxIdx).getCandidates().contains(value)){
            Cell cell = sudoku.getCellMatrix()[rowIdx][colIdx];
            cell.setValue(value);
            sudoku.updateCellCandidates(cell);
            assertFalse(sudoku.getRow(rowIdx).getCandidates().contains(value));
            assertFalse(sudoku.getCol(colIdx).getCandidates().contains(value));
            assertFalse(sudoku.getBox(boxIdx).getCandidates().contains(value));
        }
    }
}
