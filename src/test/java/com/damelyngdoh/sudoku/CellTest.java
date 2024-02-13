package com.damelyngdoh.sudoku;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.damelyngdoh.azosudoku.Cell;
import com.damelyngdoh.azosudoku.Grid;
import com.damelyngdoh.azosudoku.exceptions.GridIndexOutOfBoundsException;
import com.damelyngdoh.azosudoku.exceptions.InvalidSizeException;
import com.damelyngdoh.azosudoku.exceptions.ValueOutOfBoundsException;

/**
 * @author Dame Lyngdoh
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class CellTest {
    
    private static final int GRID_ORDER = 9;
    private static final int VALID_COLUMN = 4;
    private static final int VALID_ROW = 5;
    private static final int VALID_VALUE = 3;
    private static final int DIFFERENT_VALID_COLUMN = 5;
    private static final int DIFFERENT_VALID_ROW = 4;
    private static final int DIFFERENT_VALID_VALUE = 1;
    
    Grid grid;
    Cell emptyCell;
    Cell differentEmptyCell;
    Cell cell;
    Cell differentCell;
    
    static Stream<Integer> validIndicesStream() {
        return IntStream.range(0, GRID_ORDER).boxed();
    }

    static Stream<Integer> validValuesStream() {
        return IntStream.rangeClosed(1, GRID_ORDER).boxed();
    }
    
    @BeforeEach
    void initializeGrid() throws InvalidSizeException, GridIndexOutOfBoundsException, ValueOutOfBoundsException {
        grid = new Grid(GRID_ORDER);
        emptyCell = new Cell(grid, VALID_ROW, VALID_COLUMN);
        differentEmptyCell = new Cell(grid, DIFFERENT_VALID_ROW, DIFFERENT_VALID_COLUMN);
        cell = new Cell(grid, VALID_ROW, VALID_COLUMN, VALID_VALUE);
        differentCell = new Cell(grid, DIFFERENT_VALID_ROW, DIFFERENT_VALID_COLUMN, DIFFERENT_VALID_VALUE);
    }

    @Test
    void invalid_grid_argument_constructor_test() {
        assertThrowsExactly(NullPointerException.class, () -> new Cell(null, VALID_ROW, VALID_COLUMN), "Cell constructor without value did not throw NullPointerException with null Grid argument.");
        assertThrowsExactly(NullPointerException.class, () -> new Cell(null, VALID_ROW, VALID_COLUMN), "Cell constructor with value did not throw NullPointerException with null Grid argument.");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, GRID_ORDER, GRID_ORDER + 1})
    void invalid_row_argument_constructor_test(int row) {
        assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> new Cell(grid, row, VALID_COLUMN), "Cell constructor without value did not throw GridIndexOutOfBoundsException with invalid row argument.");
        assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> new Cell(grid, row, VALID_COLUMN, VALID_VALUE), "Cell constructor with value did not throw GridIndexOutOfBoundsException with invalid row argument.");
    }

    @ParameterizedTest
    @MethodSource("validIndicesStream")
    void valid_row_argument_constructor_test(int row) {
        assertDoesNotThrow(() -> new Cell(grid, row, VALID_COLUMN), "Cell constructor without value threw GridIndexOutOfBoundsException with valid row argument.");
        assertDoesNotThrow(() -> new Cell(grid, row, VALID_COLUMN, VALID_VALUE), "Cell constructor with value threw GridIndexOutOfBoundsException with valid row argument.");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, GRID_ORDER, GRID_ORDER + 1})
    void invalid_column_argument_constructor_test(int column) {
        assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> new Cell(grid, VALID_ROW, column), "Cell constructor without value did not throw GridIndexOutOfBoundsException with invalid column argument.");
        assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> new Cell(grid, VALID_ROW, column, VALID_VALUE), "Cell constructor with value did not throw GridIndexOutOfBoundsException with invalid column argument.");
    }

    @ParameterizedTest
    @MethodSource("validIndicesStream")
    void valid_column_argument_constructor_test(int column) {
        assertDoesNotThrow(() -> new Cell(grid, VALID_ROW, column), "Cell constructor without value threw GridIndexOutOfBoundsException with valid column argument.");
        assertDoesNotThrow(() -> new Cell(grid, VALID_ROW, column, VALID_VALUE), "Cell constructor with value threw GridIndexOutOfBoundsException with valid column argument.");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, GRID_ORDER + 1})
    void invalid_value_argument_constructor_test(int value) {
        assertThrowsExactly(ValueOutOfBoundsException.class, () -> new Cell(grid, VALID_ROW, VALID_COLUMN, value), "Cell constructor did not throw ValueOutOfBoundsException with invalid value argument.");
    }

    @ParameterizedTest
    @MethodSource("validValuesStream")
    void valid_value_argument_constructor_test(int value) {
        assertDoesNotThrow(() -> new Cell(grid, VALID_ROW, VALID_COLUMN, value), "Cell constructor threw ValueOutOfBoundsException with valid value argument.");
    }

    @Test
    void valid_constructor_argumentS_test() {
        assertDoesNotThrow(() -> new Cell(grid, VALID_ROW, VALID_COLUMN), "Cell constructor without value threw exception with valid Grid arguments passed.");
        assertDoesNotThrow(() -> new Cell(grid, VALID_ROW, VALID_COLUMN, VALID_VALUE), "Cell constructor with value threw exception with valid Grid arguments passed.");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, GRID_ORDER + 1})
    void invalid_set_value_arguments_test(int value) {
        assertThrowsExactly(ValueOutOfBoundsException.class, () -> cell.setValue(value), "Set value method did not throw ValueOutOfBoundsException with invalid value argument.");
    }

    @ParameterizedTest
    @MethodSource("validValuesStream")
    void valid_set_value_argument_test(int value) {
        assertDoesNotThrow(() -> cell.setValue(value), "Set value method threw ValueOutOfBoundsException with valid value argument.");
    }

    @Test
    void is_empty_test() {
        assertTrue(emptyCell.isEmpty(), "Empty cell returned false for emptiness check.");
        assertFalse(cell.isEmpty(), "Non-empty cell returned true for emptiness check.");
    }

    @Test
    void equality_test() throws GridIndexOutOfBoundsException, ValueOutOfBoundsException {
        assertEquals(emptyCell, emptyCell, "Cell equality check (without value) failed on reflectivity. Supposed to be true.");
        assertEquals(cell, cell, "Cell equality check (with value) failed on reflectivity. Supposed to be true.");
        assertNotEquals(differentEmptyCell, emptyCell, "Cell equality (without value) check failed and returned true when compared to different cell.");
        assertNotEquals(differentCell, cell, "Cell equality (with value) check failed and returned true when compared to different cell.");
    }

    @Test
    void hash_code_test() throws GridIndexOutOfBoundsException, ValueOutOfBoundsException {
        assertEquals(emptyCell.hashCode(), emptyCell.hashCode(), "Cell hashcode check (without value) failed on reflectivity. Supposed to be true.");
        assertEquals(cell.hashCode(), cell.hashCode(), "Cell hashcode check (with value) failed on reflectivity. Supposed to be true.");
        assertNotEquals(differentEmptyCell.hashCode(), emptyCell.hashCode(), "Cell hashcode (without value) check failed and returned true when compared to different cell.");
        assertNotEquals(differentCell.hashCode(), cell.hashCode(), "Cell hashcode (with value) check failed and returned true when compared to different cell.");
    }

    @Test
    void to_string_test() {
        assertEquals(emptyCell.toString(), "[Cell row=5; column=4; house=4; value=0]", "To String returned a different string.");
        assertEquals(cell.toString(), "[Cell row=5; column=4; house=4; value=3]", "To String returned a different string.");
    }

    @Test
    void isRelated_test() {
        final int row = 0;
        final int column = 0;
        final Cell cell = grid.getCell(row, column);
        final Cell sameRowCell = grid.getCell(row, column + 3);
        final Cell sameColumnCell = grid.getCell(row + 3, column);
        final Cell sameNonetCell = grid.getCell( + 1, column + 1);
        final Cell nonRelatedCell = grid.getCell(row + 3, column + 3);
        
        assertTrue(cell.isRelated(cell), "isRelated returned false when compared to itself.");
        assertTrue(cell.isRelated(sameRowCell), "isRelated returned false when compared to cell in the same row.");
        assertTrue(cell.isRelated(sameColumnCell), "isRelated returned false when compared to cell in the same column.");
        assertTrue(cell.isRelated(sameNonetCell), "isRelated returned false when compared to cell in the same nonet.");
        assertFalse(cell.isRelated(nonRelatedCell), "isRelated returned false when compared to non-related cell.");
    }
}