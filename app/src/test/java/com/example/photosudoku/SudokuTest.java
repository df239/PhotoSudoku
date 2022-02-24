package com.example.photosudoku;

import com.example.photosudoku.sudoku.Cell;
import com.example.photosudoku.sudoku.House;
import com.example.photosudoku.sudoku.Sudoku;
import com.example.photosudoku.sudoku.SudokuUtils;

import org.junit.Test;

import java.util.ArrayList;
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

    @Test
    public void add_cells_to_house_test(){
        House house = new House("row");
        assertEquals(0, house.getGroup().size());
        Cell cell = new Cell(2,3,4);
        Cell cell0 = new Cell(0,1,2);
        house.add(cell);
        assertEquals(1,house.getGroup().size());
        house.add(cell0);
        assertEquals(2,house.getGroup().size());
        assertTrue(house.contains(cell.getValue()));
        assertFalse(house.contains(cell0.getValue()));
    }

    @Test
    public void check_house_candidates(){
        Sudoku sudoku = new Sudoku(TestingSamples.sample1);
        House row4 = sudoku.getRow(4);
        List<Integer> rowCandidates = Arrays.asList(2,3,4,6,7,8,9);
        assertEquals(new HashSet<Integer>(rowCandidates), row4.getCandidates());

        House col6 = sudoku.getCol(6);
        List<Integer> colCandidates = Arrays.asList(1,4,6,7,8,9);
        assertEquals(new HashSet<Integer>(colCandidates),col6.getCandidates());

        House box3 = sudoku.getBox(2);
        List<Integer> boxCandidates = Arrays.asList(1,3,6,8);
        assertEquals(new HashSet<Integer>(boxCandidates),box3.getCandidates());
    }

    @Test
    public void get_house_cross_section_test(){
        Sudoku sudoku = new Sudoku(TestingSamples.sample1);
        House row5 = sudoku.getRow(5);
        House box5 = sudoku.getBox(5);
        House col6 = sudoku.getCol(6);

        assertEquals(2, row5.getCrossSection(box5, true).size());
        assertEquals(0, row5.getCrossSection(col6, true).size());
        assertEquals(2, box5.getCrossSection(row5, true).size());
        assertEquals(2, box5.getCrossSection(col6, true).size());
        assertEquals(0, col6.getCrossSection(row5, true).size());
        assertEquals(2, col6.getCrossSection(box5, true).size());

        assertEquals(3, row5.getCrossSection(box5, false).size());
        assertEquals(1, row5.getCrossSection(col6, false).size());
        assertEquals(3, box5.getCrossSection(row5, false).size());
        assertEquals(3, box5.getCrossSection(col6, false).size());
        assertEquals(1, col6.getCrossSection(row5, false).size());
        assertEquals(3, col6.getCrossSection(box5, false).size());

        House row6 = sudoku.getRow(6);
        House box6 = sudoku.getBox(6);
        House col7 = sudoku.getCol(7);

        assertEquals(0, row5.getCrossSection(row6, true).size());
        assertEquals(0, row6.getCrossSection(row5, true).size());
        assertEquals(0, box5.getCrossSection(box6, true).size());
        assertEquals(0, box6.getCrossSection(box5, true).size());
        assertEquals(0, col6.getCrossSection(col7, true).size());
        assertEquals(0, col7.getCrossSection(col6, true).size());

        assertEquals(0, row5.getCrossSection(row6, false).size());
        assertEquals(0, row6.getCrossSection(row5, false).size());
        assertEquals(0, box5.getCrossSection(box6, false).size());
        assertEquals(0, box6.getCrossSection(box5, false).size());
        assertEquals(0, col6.getCrossSection(col7, false).size());
        assertEquals(0, col7.getCrossSection(col6, false).size());
    }

    @Test
    public void get_cell_diff_between_houses_test(){
        Sudoku sudoku = new Sudoku(TestingSamples.sample1);
        House row0 = sudoku.getRow(0);
        House col0 = sudoku.getCol(0);
        House box0 = sudoku.getBox(0);

        assertEquals(8,row0.getCellDifference(col0.getGroup()).size());
        assertEquals(6,row0.getCellDifference(box0.getGroup()).size());
        assertEquals(8,col0.getCellDifference(row0.getGroup()).size());
        assertEquals(6,col0.getCellDifference(box0.getGroup()).size());
        assertEquals(6,box0.getCellDifference(row0.getGroup()).size());
        assertEquals(6,box0.getCellDifference(col0.getGroup()).size());

        House box4 = sudoku.getBox(4);
        assertEquals(9, row0.getCellDifference(box4.getGroup()).size());
        assertEquals(9, col0.getCellDifference(box4.getGroup()).size());
        assertEquals(9, box0.getCellDifference(box4.getGroup()).size());
        assertEquals(9, box4.getCellDifference(row0.getGroup()).size());
        assertEquals(9, box4.getCellDifference(col0.getGroup()).size());
        assertEquals(9, box4.getCellDifference(box0.getGroup()).size());

        Cell cell00 = sudoku.getCellMatrix()[0][0];
        Cell cell11 = sudoku.getCellMatrix()[1][1];
        assertEquals(8, row0.getCellDifference(cell00,cell11).size());
        assertEquals(8, col0.getCellDifference(cell00,cell11).size());
        assertEquals(7, box0.getCellDifference(cell00,cell11).size());
    }

    @Test
    public void cell_remove_candidates_test(){
        Sudoku sudoku = new Sudoku(TestingSamples.sample1);
        Cell cell = sudoku.getCellMatrix()[4][4];
        List<Integer> initialCandidates = cell.getCandidates();
        cell.removeCandidates(Arrays.asList(2,7,9));
        List<Integer> afterRemoval = cell.getCandidates();
        assertNotEquals(initialCandidates,afterRemoval);
        assertEquals(initialCandidates.size() - 3, afterRemoval.size());
    }

    @Test
    public void cell_shared_candidates_test(){
        Sudoku sudoku = new Sudoku(TestingSamples.sample1);
        Cell cell45 = sudoku.getCellMatrix()[4][5];
        Cell cell16 = sudoku.getCellMatrix()[1][6];
        Cell cell26 = sudoku.getCellMatrix()[2][6];
        Cell cell40 = sudoku.getCellMatrix()[0][4];

        assertEquals(0, cell45.getSharedCandidatesWith(cell16).size());
        assertEquals(2, cell40.getSharedCandidatesWith(cell45).size());
        assertEquals(1, cell16.getSharedCandidatesWith(cell40).size());
        assertEquals(cell26.getCandidates().size(), cell26.getSharedCandidatesWith(cell16).size());
    }
}
