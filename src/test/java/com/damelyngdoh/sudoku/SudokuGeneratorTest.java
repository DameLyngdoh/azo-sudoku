package com.damelyngdoh.sudoku;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.damelyngdoh.azosudoku.Cell;
import com.damelyngdoh.azosudoku.Grid;
import com.damelyngdoh.azosudoku.exceptions.DisallowedValueException;
import com.damelyngdoh.azosudoku.exceptions.InvalidSizeException;
import com.damelyngdoh.azosudoku.exceptions.ValueOutOfBoundsException;
import com.damelyngdoh.azosudoku.generators.SimpleSudokuGenerator;

@TestInstance(Lifecycle.PER_METHOD)
public class SudokuGeneratorTest {
    
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
    @ValueSource(ints = {-1, 0, 2})
    void invalid_size_validate_non_empty_cell_count_test(int invalidSize) {
        assertThrowsExactly(InvalidSizeException.class, () -> sudokuGenerator.validateNonEmptyCellCount(invalidSize, NON_EMPTY_CELL_COUNT), "validateNonEmptyCellCount did not throw InvalidSizeException when invalid size is passed.");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, (VALID_ORDER * VALID_ORDER) + 1})
    void invalid_cell_count_validate_non_empty_cell_count_test(int invalidNonEmptyCellCount) {
        assertThrowsExactly(IllegalArgumentException.class, () -> sudokuGenerator.validateNonEmptyCellCount(VALID_ORDER, invalidNonEmptyCellCount), "validateNonEmptyCellCount did not throw IllegalArgumentException when invalid non-empty cell count is passed.");
    }

    @Test
    void valid_arguments_validate_non_empty_cell_count_test() {
        assertDoesNotThrow(() -> sudokuGenerator.validateNonEmptyCellCount(VALID_ORDER, NON_EMPTY_CELL_COUNT), "validateNonEmptyCellCount threw an exception with valid arguments.");
    }

    @Test
    void invalid_cell_argument_is_last_cell_test() {
        assertThrowsExactly(NullPointerException.class, () -> sudokuGenerator.isLastCell(null), "isLastCell did not throw NullPointerException when null cell is passed.");
    }

    @Test
    void valid_arguments_argument_is_last_cell_test() throws InvalidSizeException {
        final Grid grid = new Grid(VALID_ORDER);
        final Cell firstCell = grid.getCell(0, 0);
        final Cell lastCell = grid.getCell(VALID_ORDER - 1, VALID_ORDER - 1);
        assertAll(
            () -> assertDoesNotThrow(() -> sudokuGenerator.isLastCell(firstCell), "isLastCell threw an exception with valid arguments for first cell."),
            () -> assertDoesNotThrow(() -> sudokuGenerator.isLastCell(lastCell), "isLastCell threw an exception with valid arguments for last cell."),
            () -> assertFalse(sudokuGenerator.isLastCell(firstCell), "isLastCell returned true for first cell of the grid."),
            () -> assertTrue(sudokuGenerator.isLastCell(lastCell), "isLastCell returned false for last cell of the grid.")
        );
    }

    @Test
    void invalid_cell_argument_is_cell_fixed_or_not_empty_test() {
        assertThrowsExactly(NullPointerException.class, () -> sudokuGenerator.isCellFixedOrNotEmpty(null), "isCellFixedOrNotEmpty did not throw NullPointerException when null cell is passed.");
    }

    @Test
    void valid_arguments_argument_is_cell_fixed_or_not_empty_test() throws InvalidSizeException, ValueOutOfBoundsException, DisallowedValueException {
        final Grid grid = new Grid(VALID_ORDER);

        final Cell fixedAndNotEmptyCell = grid.getCell(4, 0);
        fixedAndNotEmptyCell.setFixed(true);
        grid.setValue(fixedAndNotEmptyCell, 2);

        final Cell fixedCell = grid.getCell(4, 1);
        fixedCell.setFixed(true);

        final Cell notEmptyCell = grid.getCell(4, 2);
        grid.setValue(notEmptyCell, 3);

        final Cell notFixedAndEmptyCell = grid.getCell(4, 3);

        assertTrue(sudokuGenerator.isCellFixedOrNotEmpty(fixedAndNotEmptyCell), "isCellFixedOrNotEmpty returned false for fixed and non-empty cell.");
        assertTrue(sudokuGenerator.isCellFixedOrNotEmpty(fixedCell), "isCellFixedOrNotEmpty returned false for fixed only cell.");
        assertTrue(sudokuGenerator.isCellFixedOrNotEmpty(notEmptyCell), "isCellFixedOrNotEmpty returned false for non-empty only cell.");
        assertFalse(sudokuGenerator.isCellFixedOrNotEmpty(notFixedAndEmptyCell), "isCellFixedOrNotEmpty returned true for non-fixed and non-empty cell.");
    }
}
