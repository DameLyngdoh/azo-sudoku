package com.damelyngdoh.sudoku;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.damelyngdoh.azosudoku.Grid;
import com.damelyngdoh.azosudoku.exceptions.InvalidSizeException;
import com.damelyngdoh.azosudoku.generators.SimpleSudokuGenerator;

@TestInstance(Lifecycle.PER_METHOD)
public class SimpleSudokuGeneratorTest {
    
    final static int VALID_ORDER = 9;
    final static int CELL_COUNT = VALID_ORDER * VALID_ORDER;
    final static int NON_EMPTY_CELL_COUNT = 5;
    final static int EMPTY_CELL_COUNT = CELL_COUNT - NON_EMPTY_CELL_COUNT;

    static SimpleSudokuGenerator sudokuGenerator;
    
    @BeforeEach
    void initializeGenerator() {
        sudokuGenerator = new SimpleSudokuGenerator();
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 3})
    void invalid_size_argument_generate_complete_test(int invalidSize) {
        assertThrowsExactly(InvalidSizeException.class, () -> sudokuGenerator.generate(invalidSize), "generate did not throw InvalidSizeException when invalid size is passed.");
    }

    @Test
    void valid_argument_generate_complete_test() throws InvalidSizeException {
        final int cellCount = VALID_ORDER * VALID_ORDER;
        final Grid grid = sudokuGenerator.generate(VALID_ORDER);
        assertTrue(grid.getEmptyCells().isEmpty(), "generate did not return a gird with no empty cells.");
        assertEquals(cellCount, grid.getNonEmptyCells().size(), "generate returned a grid with some empty cells.");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 3})
    void invalid_size_argument_generate_test(int invalidSize) {
        assertThrowsExactly(InvalidSizeException.class, () -> sudokuGenerator.generate(invalidSize, NON_EMPTY_CELL_COUNT), "generate did not throw InvalidSizeException when invalid size is passed.");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, (VALID_ORDER * VALID_ORDER) + 1})
    void invalid_empty_cell_count_argument_generate_test(int invalidEmptyCellCount) {
        assertThrowsExactly(IllegalArgumentException.class, () -> sudokuGenerator.generate(VALID_ORDER, invalidEmptyCellCount), "generate did not throw IllegalArgumentException when invalid empty cell count is passed.");
    }

    @Test
    void valid_arguments_generate_test() throws InvalidSizeException {
        final Grid grid = sudokuGenerator.generate(VALID_ORDER, NON_EMPTY_CELL_COUNT);
        assertEquals(EMPTY_CELL_COUNT, grid.getEmptyCells().size(), "generate with empty cells returned a grid with incorrect number of empty cell.");
        assertEquals(NON_EMPTY_CELL_COUNT, grid.getNonEmptyCells().size(), "generate with empty cells returned a grid with incorrect number of non-empty cell.");
    }
}
