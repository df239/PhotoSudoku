package com.example.photosudoku;

import com.example.photosudoku.utils.Validator;

import org.junit.Test;
import static org.junit.Assert.*;

public class ValidatorTest {
    @Test
    public void sudoku_is_invalid(){
        assertFalse(Validator.checkBoardValidity(TestingSamples.invalidRow));
        assertFalse(Validator.checkBoardValidity(TestingSamples.invalidCol));
        assertFalse(Validator.checkBoardValidity(TestingSamples.invalidBox));
    }
}
