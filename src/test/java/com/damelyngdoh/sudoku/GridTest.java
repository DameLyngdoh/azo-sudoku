package com.damelyngdoh.sudoku;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.damelyngdoh.azosudoku.Cell;
import com.damelyngdoh.azosudoku.Grid;
import com.damelyngdoh.azosudoku.House;
import com.damelyngdoh.azosudoku.exceptions.DisallowedValueException;
import com.damelyngdoh.azosudoku.exceptions.GridIndexOutOfBoundsException;
import com.damelyngdoh.azosudoku.exceptions.InvalidSizeException;
import com.damelyngdoh.azosudoku.exceptions.ValueOutOfBoundsException;

@TestInstance(Lifecycle.PER_METHOD)
public class GridTest {

    static final int VALID_ORDER = 9;
    static final int CELL_COUNT = VALID_ORDER * VALID_ORDER;
    static final int NON_EMPTY_CELL_COUNT_IN_PARTIAL_GRID = 20;
    static final int EMPTY_CELL_COUNT_IN_PARTIAL_GRID = CELL_COUNT - NON_EMPTY_CELL_COUNT_IN_PARTIAL_GRID;
    static final int RANDOM_CELL_ROW = 1;
    static final int RANDOM_CELL_COLUMN = 2;
    static final int RANDOM_VALUE = 2;
    static final String CELL_DELIMITER = ";";
    static final String ROW_DELIMITER = "\n";
    static final String EMPTY_CELL_NOTATION = "*";
    static final int[][] COMPLETE_VALID_MATRIX = {
        {9,3,8,5,6,4,2,1,7},
        {5,6,1,8,2,7,3,9,4},
        {4,2,7,3,1,9,6,5,8},
        {7,4,6,9,8,1,5,2,3},
        {2,8,3,7,5,6,1,4,9},
        {1,9,5,2,4,3,8,7,6},
        {3,7,2,6,9,5,4,8,1},
        {8,1,9,4,3,2,7,6,5},
        {6,5,4,1,7,8,9,3,2}
    };
    static final int[][] PARTIAL_VALID_MATRIX = {
        {0,1,0,9,5,0,7,4,0},
        {0,7,0,0,0,3,9,0,0},
        {0,0,0,0,0,0,8,2,5},
        {0,0,0,0,0,4,0,0,2},
        {6,0,0,0,0,0,0,8,0},
        {0,8,0,5,0,0,3,0,0},
        {0,0,0,0,0,0,1,0,0},
        {0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,3,0}
    };
    static final int[][] EMPTY_VALID_MATRIX = {
        {0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0}
    };
    static final int[][] PARTIALLY_INVALID_MATRIX = {
        {0,2,3,4,5,6,7,8,9},
        {1,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0}
    };

    Grid emptyGrid;
    Grid partialEmptyGrid;
    Grid completeGrid;
    Grid invalidGrid;

    static Stream<Integer> validIndicesStream() {
        return IntStream.range(0, VALID_ORDER).boxed();
    }

    static Stream<Integer> validCValuesStream() {
        return IntStream.rangeClosed(1, VALID_ORDER).boxed();
    }

    static Set<Integer> validValuesSet() {
        return validCValuesStream().collect(Collectors.toSet());
    }

    static void fillGrid(Grid grid, int[][] values) throws ValueOutOfBoundsException, DisallowedValueException {
        for(int row = 0, column = 0; row < values.length; row++) {
            for(column = 0; column < values[row].length; column++) {
                if(values[row][column] != 0)
                    grid.setValue(row, column, values[row][column]);
            }
        }
    }

    @BeforeEach
    void initializeGrid() throws InvalidSizeException, ValueOutOfBoundsException, DisallowedValueException {
        emptyGrid = new Grid(VALID_ORDER);
        completeGrid = new Grid(VALID_ORDER);
        partialEmptyGrid = new Grid(VALID_ORDER);
        invalidGrid = new Grid(VALID_ORDER);
        fillGrid(completeGrid, COMPLETE_VALID_MATRIX);
        fillGrid(partialEmptyGrid, PARTIAL_VALID_MATRIX);
        fillGrid(invalidGrid, PARTIALLY_INVALID_MATRIX);
    }
    
    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 3})
    void invalid_size_constructor_test(int invalidSize) {
        assertThrowsExactly(InvalidSizeException.class, () -> new Grid(invalidSize), "Grid constructor did not throw InvalidSizeException when invalid size argument is passed.");
    }

    @Test
    void valid_constructor_test() {
        assertDoesNotThrow(() -> new Grid(VALID_ORDER), "Grid constructor threw an exception with invalid size argument passed.");
    }

    @Test
    void get_size_test() throws InvalidSizeException {
        final int size4 = 4;
        final int size9 = 9;
        final Grid gridSize4 = new Grid(size4);
        final Grid gridSize9 = new Grid(size9);
        assertEquals(size4, gridSize4.getSize(), "getSize returned a wrong size for grid of size 4.");
        assertEquals(size9, gridSize9.getSize(), "getSize returned a wrong size for grid of size 9.");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, VALID_ORDER, VALID_ORDER + 1})
    void invalid_row_argument_get_row_test(int invalidRow) {
        assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> completeGrid.getRow(invalidRow), "getRow did not throw GridIndexOutOfBoundsException when invalid row index is passed.");
    }

    @Test
    void valid_arguments_get_row_test() {
        final int GROUP_INDEX = 5;
        final int[] VALUES_AT_GROUP = {1,9,5,2,4,3,8,7,6};
        assertDoesNotThrow(() -> completeGrid.getRow(GROUP_INDEX), "getRow threw an exception with valid arguments.");
        final House row = completeGrid.getRow(GROUP_INDEX);
        assertEquals(VALUES_AT_GROUP.length, row.size(), "getRow returned a house (list) with wrong number of elements.");
        for(int i = 0; i < row.size(); i++) {
            assertEquals(VALUES_AT_GROUP[i], row.get(i).getValue(), "getRow returned wrong value at index " + i + ".");
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, VALID_ORDER, VALID_ORDER + 1})
    void invalid_column_argument_get_column_test(int invalidColumn) {
        assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> completeGrid.getColumn(invalidColumn), "getColumn did not throw GridIndexOutOfBoundsException when invalid column index is passed.");
    }

    @Test
    void valid_arguments_get_column_test() {
        final int GROUP_INDEX = 2;
        final int[] VALUES_AT_GROUP = {8,1,7,6,3,5,2,9,4};
        assertDoesNotThrow(() -> completeGrid.getColumn(GROUP_INDEX), "getColumn threw an exception with valid arguments.");
        final House column = completeGrid.getColumn(GROUP_INDEX);
        assertEquals(VALUES_AT_GROUP.length, column.size(), "getColumn returned a house (list) with wrong number of elements.");
        for(int i = 0; i < column.size(); i++) {
            assertEquals(VALUES_AT_GROUP[i], column.get(i).getValue(), "getColumn returned wrong value at index " + i + ".");
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, VALID_ORDER, VALID_ORDER + 1})
    void invalid_nonet_argument_get_nonet_test(int invalidNonet) {
        assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> completeGrid.getNonet(invalidNonet), "getNonet did not throw GridIndexOutOfBoundsException when invalid nonet index is passed.");
    }

    @Test
    void valid_arguments_get_nonet_test() {
        final int GROUP_INDEX = 3;
        final int[] VALUES_AT_GROUP = {7,4,6,2,8,3,1,9,5};
        assertDoesNotThrow(() -> completeGrid.getNonet(GROUP_INDEX), "getNonet threw an exception with valid arguments.");
        final House nonet = completeGrid.getNonet(GROUP_INDEX);
        assertEquals(VALUES_AT_GROUP.length, nonet.size(), "getNonet returned a house (list) with wrong number of elements.");
        for(int i = 0; i < nonet.size(); i++) {
            assertEquals(VALUES_AT_GROUP[i], nonet.get(i).getValue(), "getNonet returned wrong value at index " + i + ".");
        }
    }

    @Test
    void get_permissible_values_test() throws InvalidSizeException {
        final Set<Integer> permissibleValuesForSize4 = Set.of(1,2,3,4);
        final Set<Integer> permissibleValuesForSize9 = Set.of(1,2,3,4,5,6,7,8,9);
        Grid gridSize4 = new Grid(4);
        assertTrue(TestUtils.areSetsEqual(permissibleValuesForSize4, gridSize4.getPermissibleValues()), "getPermissibleValues returned an incorrect set of integers for size 4.");
        assertTrue(TestUtils.areSetsEqual(permissibleValuesForSize9, completeGrid.getPermissibleValues()), "getPermissibleValues returned an incorrect set of integers for size 9.");
    }

    @Test
    void get_non_empty_cells_test() {
        assertAll(
            () -> assertEquals(0, emptyGrid.getNonEmptyCells().size(), "getNonEmptyCells returned a non-empty set for a grid with all cells empty."),
            () -> assertEquals(CELL_COUNT, completeGrid.getNonEmptyCells().size(), "getNonEmptyCells returned a set with size less than cell count for a grid with no cells empty."),
            () -> assertEquals(NON_EMPTY_CELL_COUNT_IN_PARTIAL_GRID, partialEmptyGrid.getNonEmptyCells().size(), "getNonEmptyCells returned a wrong set size for partially empty grid.")
        );
    }

    @Test
    void get_empty_cells_test() {
        assertAll(
            () -> assertEquals(CELL_COUNT, emptyGrid.getEmptyCells().size(), "getEmptyCells returned a set with size not equal to total cell count for empty grid."),
            () -> assertEquals(0, completeGrid.getEmptyCells().size(), "getEmptyCells returned a set with size not equal to 0 for complete grid."),
            () -> assertEquals(EMPTY_CELL_COUNT_IN_PARTIAL_GRID, partialEmptyGrid.getEmptyCells().size(), "getNonEmptyCells returned a wrong set size for partially empty grid.")
        );
    }

    @Test
    void get_invalid_empty_cells_test() {
        assertTrue(emptyGrid.getInvalidEmptyCells().isEmpty(), "getInvalidEmptyCells returned a non-empty set of invalid cells with empty grid.");
        assertTrue(completeGrid.getInvalidEmptyCells().isEmpty(), "getInvalidEmptyCells returned a non-empty set of invalid cells with complete grid.");
        assertTrue(partialEmptyGrid.getInvalidEmptyCells().isEmpty(), "getInvalidEmptyCells returned a non-empty set of invalid cells with partially-empty grid.");
        assertEquals(1, invalidGrid.getInvalidEmptyCells().size(), "getInvalidEmptyCells returned incorrect number of elements.");
    }

    @Test
    void has_invalid_empty_cells_test() {
        assertFalse(emptyGrid.hasEmptyAndInvalidCells(), "hasEmptyAndInvalidCells returned true with empty grid.");
        assertFalse(completeGrid.hasEmptyAndInvalidCells(), "hasEmptyAndInvalidCells returned true with complete grid.");
        assertFalse(partialEmptyGrid.hasEmptyAndInvalidCells(), "hasEmptyAndInvalidCells returned true with partially-empty grid.");
        assertTrue(invalidGrid.hasEmptyAndInvalidCells(), "hasEmptyAndInvalidCells returned false with invalid grid.");
    }

    @Test
    void set_non_empty_as_fixed_test() {
        assertDoesNotThrow(() -> emptyGrid.setNonEmptyAsFixed(), "setNonEmptyAsFixed threw an exception when it should not.");
        assertFalse(emptyGrid.setNonEmptyAsFixed(), "setNonEmptyAsFixed returned true for emtpy grid which does not contain any non-empty cells.");
        assertAll(
            () -> assertTrue(partialEmptyGrid.setNonEmptyAsFixed(), "setNonEmptyAsFixed returned false for partially emtpy grid contains some non-empty cells."),
            () -> assertFalse(partialEmptyGrid.setNonEmptyAsFixed(), "setNonEmptyAsFixed returned true for partially emtpy grid after all non-empty cells were set to fixed.")
        );
        assertAll(
            () -> assertTrue(completeGrid.setNonEmptyAsFixed(), "setNonEmptyAsFixed returned false for complete grid which contains all non-empty cells."),
            () -> assertFalse(completeGrid.setNonEmptyAsFixed(), "setNonEmptyAsFixed returned true for complete grid after all non-empty cells were set to fixed.")
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, VALID_ORDER, VALID_ORDER + 1})
    void invalid_row_argument_get_cell_test(int invalidRow) {
        assertAll(
            () -> assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> emptyGrid.getCell(invalidRow, RANDOM_CELL_COLUMN), "getCell did not throw GridIndexOutOfBoundsException for empty grid when invalid row argument is passed."),
            () -> assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> completeGrid.getCell(invalidRow, RANDOM_CELL_COLUMN), "getCell did not throw GridIndexOutOfBoundsException for complete grid when invalid row argument is passed."),
            () -> assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> partialEmptyGrid.getCell(invalidRow, RANDOM_CELL_COLUMN), "getCell did not throw GridIndexOutOfBoundsException for partially empty grid when invalid row argument is passed.")
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, VALID_ORDER, VALID_ORDER + 1})
    void invalid_column_argument_get_cell_test(int invalidColumn) {
        assertAll(
            () -> assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> emptyGrid.getCell(RANDOM_CELL_ROW, invalidColumn), "getCell did not throw GridIndexOutOfBoundsException for empty grid when invalid row argument is passed."),
            () -> assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> completeGrid.getCell(RANDOM_CELL_ROW, invalidColumn), "getCell did not throw GridIndexOutOfBoundsException for complete grid when invalid row argument is passed."),
            () -> assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> partialEmptyGrid.getCell(RANDOM_CELL_ROW, invalidColumn), "getCell did not throw GridIndexOutOfBoundsException for partially empty grid when invalid row argument is passed.")
        );
    }

    @Test
    void valid_arguments_get_cell_test() {
        assertAll(
            () -> assertDoesNotThrow(() -> emptyGrid.getCell(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN), "getCell threw an exception for valid row and column arguments for an empty grid."),
            () -> assertDoesNotThrow(() -> completeGrid.getCell(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN), "getCell threw an exception for valid row and column arguments for an complete grid."),
            () -> assertDoesNotThrow(() -> partialEmptyGrid.getCell(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN), "getCell threw an exception for valid row and column arguments for an partially empty grid."),
            () -> assertEquals(RANDOM_CELL_ROW, emptyGrid.getCell(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN).getRow(), "getCell returned a cell with a wrong row index."),
            () -> assertTrue(emptyGrid.getCell(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN).isEmpty(), "getCell returned a cell which is not empty for an empty grid."),
            () -> assertEquals(RANDOM_CELL_COLUMN, partialEmptyGrid.getCell(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN).getColumn(), "getCell returned a cell with a wrong column index."),
            () -> assertFalse(completeGrid.getCell(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN).isEmpty(), "getCell returned a cell which is empty for a cell in a complete grid.")
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, VALID_ORDER, VALID_ORDER + 1})
    void invalid_row_argument_get_value_test(int invalidRow) {
        assertAll(
            () -> assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> emptyGrid.getValue(invalidRow, RANDOM_CELL_COLUMN), "getValue did not throw GridIndexOutOfBoundsException for empty grid when invalid row argument is passed."),
            () -> assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> completeGrid.getValue(invalidRow, RANDOM_CELL_COLUMN), "getValue did not throw GridIndexOutOfBoundsException for complete grid when invalid row argument is passed."),
            () -> assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> partialEmptyGrid.getValue(invalidRow, RANDOM_CELL_COLUMN), "getValue did not throw GridIndexOutOfBoundsException for partially empty grid when invalid row argument is passed.")
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, VALID_ORDER, VALID_ORDER + 1})
    void invalid_column_argument_get_value_test(int invalidColumn) {
        assertAll(
            () -> assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> emptyGrid.getValue(RANDOM_CELL_ROW, invalidColumn), "getValue did not throw GridIndexOutOfBoundsException for empty grid when invalid row argument is passed."),
            () -> assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> completeGrid.getValue(RANDOM_CELL_ROW, invalidColumn), "getValue did not throw GridIndexOutOfBoundsException for complete grid when invalid row argument is passed."),
            () -> assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> partialEmptyGrid.getValue(RANDOM_CELL_ROW, invalidColumn), "getValue did not throw GridIndexOutOfBoundsException for partially empty grid when invalid row argument is passed.")
        );
    }

    @Test
    void valid_arguments_get_value_test() {
        for(int row = 0, column = 0; row < VALID_ORDER; row++) {
            for(column = 0; column < VALID_ORDER; column++) {
                final int rowIndex = row;
                final int columnIndex = column;
                assertEquals(Optional.of(COMPLETE_VALID_MATRIX[row][column]), completeGrid.getValue(row, column), "getValue returned wrong value for complete grid.");
                assertThrowsExactly(NoSuchElementException.class, () -> emptyGrid.getValue(rowIndex, columnIndex).get(), "getValue did not throw NoSuchElementException on empty Optional.");
            }
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, VALID_ORDER, VALID_ORDER + 1})
    void invalid_row_argument_get_permissible_values_for_coordinates(int invalidRow) {
        assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> emptyGrid.getPermissibleValues(invalidRow, RANDOM_CELL_COLUMN), "getPermissibleValues for cell did not throw GridIndexOutOfBoundsException when invalid row argument is passed.");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, VALID_ORDER, VALID_ORDER + 1})
    void invalid_column_argument_get_permissible_values_for_coordinates(int invalidColumn) {
        assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> emptyGrid.getPermissibleValues(RANDOM_CELL_ROW, invalidColumn), "getPermissibleValues for coordinates did not throw GridIndexOutOfBoundsException when invalid column argument is passed.");
    }

    @Test
    void get_permissible_values_for_coordinates() {
        final Set<Integer> permissibleValuesForRandomCell = Arrays.stream(new int[]{2,4,5,6,8}).boxed().collect(Collectors.toSet());
        assertAll(
            () -> assertDoesNotThrow(() -> emptyGrid.getPermissibleValues(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN), "getPermissibleValues threw an exception for empty grid with valid arguments."),
            () -> assertEquals(9, emptyGrid.getPermissibleValues(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN).size(), "getPermissibleValues returned a set with size less than set of all possible values for an empty grid."),
            () -> assertDoesNotThrow(() -> completeGrid.getPermissibleValues(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN), "getPermissibleValues threw an exception for complete grid with valid arguments."),
            () -> assertTrue(completeGrid.getPermissibleValues(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN).isEmpty(), "getPermissibleValues did not return an empty set for a random cell on a complete grid."),
            () -> assertDoesNotThrow(() -> partialEmptyGrid.getPermissibleValues(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN), "getPermissibleValues threw an exception for partially empty grid with valid arguments."),
            () -> assertTrue(TestUtils.areSetsEqual(permissibleValuesForRandomCell, partialEmptyGrid.getPermissibleValues(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN)), "getPermissibleValues returned a wrong set of permissible values for partially empty grid.")
        );
    }

    @Test
    void invalid_cell_argument_get_permissible_values_for_cell() {
        assertThrowsExactly(NullPointerException.class, () -> emptyGrid.getPermissibleValues(null), "getPermissibleValues for cell did not throw NullPointerException when null cell argument is passed.");
    }

    @Test
    void get_permissible_values_for_cell() {
        final Set<Integer> permissibleValuesForRandomCell = Arrays.stream(new int[]{2,4,5,6,8}).boxed().collect(Collectors.toSet());
        final Cell randomCellFromEmptyGrid = emptyGrid.getCell(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN);
        final Cell randomCellFromCompleteGrid = completeGrid.getCell(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN);
        final Cell randomCellFromPartiallyEmptyGrid = partialEmptyGrid.getCell(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN);

        assertAll(
            () -> assertDoesNotThrow(() -> emptyGrid.getPermissibleValues(randomCellFromEmptyGrid), "getPermissibleValues threw an exception for empty grid with valid arguments."),
            () -> assertEquals(9, emptyGrid.getPermissibleValues(randomCellFromEmptyGrid).size(), "getPermissibleValues returned a set with size less than set of all possible values for an empty grid."),
            () -> assertDoesNotThrow(() -> completeGrid.getPermissibleValues(randomCellFromCompleteGrid), "getPermissibleValues threw an exception for complete grid with valid arguments."),
            () -> assertTrue(completeGrid.getPermissibleValues(randomCellFromCompleteGrid).isEmpty(), "getPermissibleValues did not return an empty set for a random cell on a complete grid."),
            () -> assertDoesNotThrow(() -> partialEmptyGrid.getPermissibleValues(randomCellFromPartiallyEmptyGrid), "getPermissibleValues threw an exception for partially empty grid with valid arguments."),
            () -> assertTrue(TestUtils.areSetsEqual(permissibleValuesForRandomCell, partialEmptyGrid.getPermissibleValues(randomCellFromPartiallyEmptyGrid)), "getPermissibleValues returned a wrong set of permissible values for partially empty grid.")
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, VALID_ORDER, VALID_ORDER + 1})
    void invalid_row_argument_set_value_by_coordinate_test(int invalidRow) {
        assertAll(
            () -> assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> emptyGrid.setValue(invalidRow, RANDOM_CELL_COLUMN, RANDOM_VALUE), "setValue did not throw GridIndexOutOfBoundsException when invalid row argument is passed."),
            () -> assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> completeGrid.setValue(invalidRow, RANDOM_CELL_COLUMN, RANDOM_VALUE), "setValue did not throw GridIndexOutOfBoundsException when invalid row argument is passed."),
            () -> assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> partialEmptyGrid.setValue(invalidRow, RANDOM_CELL_COLUMN, RANDOM_VALUE), "setValue did not throw GridIndexOutOfBoundsException when invalid row argument is passed.")
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, VALID_ORDER, VALID_ORDER + 1})
    void invalid_column_argument_set_value_by_coordinate_test(int invalidColumn) {
        assertAll(
            () -> assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> emptyGrid.setValue(RANDOM_CELL_ROW, invalidColumn, RANDOM_VALUE), "setValue did not throw GridIndexOutOfBoundsException when invalid column argument is passed."),
            () -> assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> completeGrid.setValue(RANDOM_CELL_ROW, invalidColumn, RANDOM_VALUE), "setValue did not throw GridIndexOutOfBoundsException when invalid column argument is passed."),
            () -> assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> partialEmptyGrid.setValue(RANDOM_CELL_ROW, invalidColumn, RANDOM_VALUE), "setValue did not throw GridIndexOutOfBoundsException when invalid column argument is passed.")
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, VALID_ORDER + 1})
    void invalid_value_argument_set_value_by_coordinate_test(int invalidValue) {
        assertAll(
            () -> assertThrowsExactly(ValueOutOfBoundsException.class, () -> emptyGrid.setValue(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN, invalidValue), "setValue did not throw ValueOutOfBoundsException when invalid value argument is passed."),
            () -> assertThrowsExactly(ValueOutOfBoundsException.class, () -> completeGrid.setValue(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN, invalidValue), "setValue did not throw ValueOutOfBoundsException when invalid value argument is passed."),
            () -> assertThrowsExactly(ValueOutOfBoundsException.class, () -> partialEmptyGrid.setValue(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN, invalidValue), "setValue did not throw ValueOutOfBoundsException when invalid value argument is passed.")
        );
    }

    @Test
    void non_permissible_value_argument_set_value_by_coordinate_test() {
        assertAll(
            () -> assertThrowsExactly(DisallowedValueException.class, () -> completeGrid.setValue(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN, 3), "setValue did not throw DisallowedValueException when non-permissible value argument is passed on complete grid."),
            () -> assertThrowsExactly(DisallowedValueException.class, () -> partialEmptyGrid.setValue(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN, 1), "setValue did not throw DisallowedValueException when non-permissible value argument is passed on partially empty.")
        );
    }
    
    @Test
    void valid_arguments_set_value_by_coordinate_test() {
        assertAll(
            () -> assertDoesNotThrow(() -> emptyGrid.setValue(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN, RANDOM_VALUE), "setValue threw an exception when valid arguments are passed on empty grid."),
            () -> assertFalse(emptyGrid.getCell(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN).isEmpty(), "setValue did not correctly set value of cell and cell is found to be empty on emtpy grid."),
            () -> assertEquals(RANDOM_VALUE, emptyGrid.getCell(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN).getValue(), "setValue did not correctly set value of cell and value in cell is found to be different on emtpy grid.")
        );

        assertAll(
            () -> assertDoesNotThrow(() -> partialEmptyGrid.setValue(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN, RANDOM_VALUE), "setValue threw an exception when valid arguments are passed on partial empty grid."),
            () -> assertFalse(partialEmptyGrid.getCell(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN).isEmpty(), "setValue did not correctly set value of cell and cell is found to be empty on partially emtpy grid."),
            () -> assertEquals(RANDOM_VALUE, partialEmptyGrid.getCell(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN).getValue(), "setValue did not correctly set value of cell and value in cell is found to be different on partially emtpy grid.")
        );
    }

    @Test
    void invalid_cell_argument_set_value_by_cell_test() {
        assertAll(
            () -> assertThrowsExactly(NullPointerException.class, () -> emptyGrid.setValue(null, RANDOM_VALUE), "setValue did not throw NullPointerException when null cell argument is passed on empty grid."),
            () -> assertThrowsExactly(NullPointerException.class, () -> partialEmptyGrid.setValue(null, RANDOM_VALUE), "setValue did not throw NullPointerException when null cell argument is passed on partially empty grid.")
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, VALID_ORDER + 1})
    void invalid_value_argument_set_value_by_cell_test(int invalidValue) {
        final Cell randomCellFromEmptyGrid = emptyGrid.getCell(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN);
        final Cell randomCellFromPartiallyEmptyGrid = partialEmptyGrid.getCell(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN);
        assertAll(
            () -> assertThrowsExactly(ValueOutOfBoundsException.class, () -> emptyGrid.setValue(randomCellFromEmptyGrid, invalidValue), "setValue did not throw ValueOutOfBoundsException when invalid value argument is passed on empty grid."),
            () -> assertThrowsExactly(ValueOutOfBoundsException.class, () -> partialEmptyGrid.setValue(randomCellFromPartiallyEmptyGrid, invalidValue), "setValue did not throw ValueOutOfBoundsException when invalid value argument is passed on partially empty grid.")
        );
    }

    @Test
    void non_permissible_value_argument_set_value_by_cell_test() {
        final Cell randomCellFromCompleteGrid = completeGrid.getCell(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN);
        final Cell randomCellFromPartiallyEmptyGrid = partialEmptyGrid.getCell(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN);
        final int nonPermissibleValue = 1;
        assertAll(
            () -> assertThrowsExactly(DisallowedValueException.class, () -> completeGrid.setValue(randomCellFromCompleteGrid, nonPermissibleValue), "setValue did not throw DisallowedValueException when invalid value argument is passed on complete grid."),
            () -> assertThrowsExactly(DisallowedValueException.class, () -> partialEmptyGrid.setValue(randomCellFromPartiallyEmptyGrid, nonPermissibleValue), "setValue did not throw DisallowedValueException when invalid value argument is passed on partially empty grid.")
        );
    }

    @Test
    void valid_arguments_set_value_by_cell_test() {
        final Cell randomCellFromEmptyGrid = emptyGrid.getCell(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN);
        final Cell randomCellFromPartiallyEmptyGrid = partialEmptyGrid.getCell(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN);
        assertAll(
            () -> assertDoesNotThrow(() -> emptyGrid.setValue(randomCellFromEmptyGrid, RANDOM_VALUE), "setValue threw an exception when valid arguments are passed on empty grid."),
            () -> assertFalse(randomCellFromEmptyGrid.isEmpty(), "setValue did not correctly set value of cell and cell is found to be empty on emtpy grid."),
            () -> assertEquals(RANDOM_VALUE, randomCellFromEmptyGrid.getValue(), "setValue did not correctly set value of cell and value in cell is found to be different on emtpy grid.")
        );

        assertAll(
            () -> assertDoesNotThrow(() -> partialEmptyGrid.setValue(randomCellFromPartiallyEmptyGrid, RANDOM_VALUE), "setValue threw an exception when valid arguments are passed on partial empty grid."),
            () -> assertFalse(randomCellFromPartiallyEmptyGrid.isEmpty(), "setValue did not correctly set value of cell and cell is found to be empty on partially emtpy grid."),
            () -> assertEquals(RANDOM_VALUE, randomCellFromPartiallyEmptyGrid.getValue(), "setValue did not correctly set value of cell and value in cell is found to be different on partially emtpy grid.")
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, VALID_ORDER, VALID_ORDER + 1})
    void invalid_row_argument_remove_valueby_coordinate_test(int invalidRow) {
        assertAll(
            () -> assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> emptyGrid.removeValue(invalidRow, RANDOM_CELL_COLUMN), "removeValue did not throw GridIndexOutOfBoundsException when invalid row argument is passed."),
            () -> assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> completeGrid.removeValue(invalidRow, RANDOM_CELL_COLUMN), "removeValue did not throw GridIndexOutOfBoundsException when invalid row argument is passed."),
            () -> assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> partialEmptyGrid.removeValue(invalidRow, RANDOM_CELL_COLUMN), "removeValue did not throw GridIndexOutOfBoundsException when invalid row argument is passed.")
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, VALID_ORDER, VALID_ORDER + 1})
    void invalid_column_argument_remove_valueby_coordinate_test(int invalidColumn) {
        assertAll(
            () -> assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> emptyGrid.removeValue(RANDOM_CELL_ROW, invalidColumn), "removeValue did not throw GridIndexOutOfBoundsException when invalid column argument is passed."),
            () -> assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> completeGrid.removeValue(RANDOM_CELL_ROW, invalidColumn), "removeValue did not throw GridIndexOutOfBoundsException when invalid column argument is passed."),
            () -> assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> partialEmptyGrid.removeValue(RANDOM_CELL_ROW, invalidColumn), "removeValue did not throw GridIndexOutOfBoundsException when invalid column argument is passed.")
        );
    }

    @Test
    void valid_arguments_remove_valueby_coordinate_test() {
        final Cell nonEmtpyCellFromCompleteGrid = completeGrid.getCell(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN);
        final Cell nonEmtpyCellFromPartiallyEmptyGrid = partialEmptyGrid.getCell(5, 3);
        final int nonEmtpyCellFromCompleteGridValue = nonEmtpyCellFromCompleteGrid.getValue();
        final int nonEmtpyCellFromPartiallyEmptyGridValue = nonEmtpyCellFromPartiallyEmptyGrid.getValue();
        assertAll(
            () -> assertDoesNotThrow(() -> emptyGrid.removeValue(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN), "removeValue threw an exception with valid arguments on emtpy grid."),
            () -> assertEquals(0, emptyGrid.removeValue(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN), "removeValue did not return 0 on empty cell in empty grid.")
        );
        assertAll(
            () -> assertEquals(nonEmtpyCellFromCompleteGridValue, completeGrid.removeValue(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN), "removeValue did not return value of cell in complete grid with valid arguments."),
            () -> assertTrue(nonEmtpyCellFromCompleteGrid.isEmpty(), "removeValue did not make the cell empty on valid arguments on complete grid.")
        );
        assertAll(
            () -> assertEquals(nonEmtpyCellFromPartiallyEmptyGridValue, partialEmptyGrid.removeValue(5, 3), "removeValue did not return value of cell in partially empty grid with valid arguments."),
            () -> assertTrue(nonEmtpyCellFromPartiallyEmptyGrid.isEmpty(), "removeValue did not make the cell empty on valid arguments on partially empty grid.")
        );
    }

    @Test
    void invalid_cell_argument_remove_value_by_cell_test() {
        assertAll(
            () -> assertThrowsExactly(NullPointerException.class, () -> emptyGrid.removeValue(null), "removeValue did not throw NullPointerException when null cell argument is passed on empty grid."),
            () -> assertThrowsExactly(NullPointerException.class, () -> partialEmptyGrid.removeValue(null), "removeValue did not throw NullPointerException when null cell argument is passed on partially empty grid."),
            () -> assertThrowsExactly(NullPointerException.class, () -> completeGrid.removeValue(null), "removeValue did not throw NullPointerException when null cell argument is passed on complete empty grid.")
        );
    }

    @Test
    void valid_arguments_remove_value_by_cell_test() {
        final Cell cellFromEmptyGrid = emptyGrid.getCell(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN);
        final Cell nonEmtpyCellFromCompleteGrid = completeGrid.getCell(RANDOM_CELL_ROW, RANDOM_CELL_COLUMN);
        final Cell nonEmtpyCellFromPartiallyEmptyGrid = partialEmptyGrid.getCell(5, 3);
        final int nonEmtpyCellFromCompleteGridValue = nonEmtpyCellFromCompleteGrid.getValue();
        final int nonEmtpyCellFromPartiallyEmptyGridValue = nonEmtpyCellFromPartiallyEmptyGrid.getValue();
        assertAll(
            () -> assertDoesNotThrow(() -> emptyGrid.removeValue(cellFromEmptyGrid), "removeValue threw an exception with valid arguments on emtpy grid."),
            () -> assertEquals(0, emptyGrid.removeValue(cellFromEmptyGrid), "removeValue did not return 0 on empty cell in empty grid.")
        );
        assertAll(
            () -> assertEquals(nonEmtpyCellFromCompleteGridValue, completeGrid.removeValue(nonEmtpyCellFromCompleteGrid), "removeValue did not return value of cell in complete grid with valid arguments."),
            () -> assertTrue(nonEmtpyCellFromCompleteGrid.isEmpty(), "removeValue did not make the cell empty on valid arguments on complete grid.")
        );
        assertAll(
            () -> assertEquals(nonEmtpyCellFromPartiallyEmptyGridValue, partialEmptyGrid.removeValue(nonEmtpyCellFromPartiallyEmptyGrid), "removeValue did not return value of cell in partially empty grid with valid arguments."),
            () -> assertTrue(nonEmtpyCellFromPartiallyEmptyGrid.isEmpty(), "removeValue did not make the cell empty on valid arguments on partially empty grid.")
        );
    }

    @Test
    void as_int_array_test() {
        final int[][] emptyGridAsArray = emptyGrid.asArray();
        final int[][] partiallyEmptyGridAsArray = partialEmptyGrid.asArray();
        final int[][] completeGridAsArray = completeGrid.asArray();

        for(int row = 0; row < VALID_ORDER; row++) {
            assertArrayEquals(EMPTY_VALID_MATRIX[row], emptyGridAsArray[row], "asArray returned an incorrect row for empty grid.");
            assertArrayEquals(PARTIAL_VALID_MATRIX[row], partiallyEmptyGridAsArray[row], "asArray returned an incorrect row for partially empty grid.");
            assertArrayEquals(COMPLETE_VALID_MATRIX[row], completeGridAsArray[row], "asArray returned an incorrect row for complete grid.");
        }
    }

    @Test
    void invalid_cell_delimiter_argument_as_string_test() {
        assertAll(
            () -> assertThrowsExactly(NullPointerException.class, () -> emptyGrid.asString(null, ROW_DELIMITER, EMPTY_CELL_NOTATION), "asString did not throw NullPointerException when null cell delimiter is passed."),
            () -> assertThrowsExactly(NullPointerException.class, () -> emptyGrid.asString(null, ROW_DELIMITER), "asString did not throw NullPointerException when null cell delimiter is passed.")
        );
        
    }

    @Test
    void invalid_row_delimiter_argument_as_string_test() {
        assertAll(
            () -> assertThrowsExactly(NullPointerException.class, () -> emptyGrid.asString(CELL_DELIMITER, null, EMPTY_CELL_NOTATION), "asString did not throw NullPointerException when null row delimiter is passed."),
            () -> assertThrowsExactly(NullPointerException.class, () -> emptyGrid.asString(CELL_DELIMITER, null), "asString did not throw NullPointerException when null row delimiter is passed.")
        );
    }

    @Test
    void invalid_empty_cell_notation_argument_as_string_test() {
        assertThrowsExactly(NullPointerException.class, () -> emptyGrid.asString(CELL_DELIMITER, ROW_DELIMITER, null), "asString did not throw NullPointerException when null empty cell notation is passed.");
    }

    @Test
    void blank_cell_delimiter_as_string_test() {
        final String output = 
        "010950740" + "\n" +
        "070003900" + "\n" +
        "000000825" + "\n" +
        "000004002" + "\n" +
        "600000080" + "\n" +
        "080500300" + "\n" +
        "000000100" + "\n" +
        "000000000" + "\n" +
        "000000030" + "\n";
        assertEquals(output, partialEmptyGrid.asString("", ROW_DELIMITER), "asString returned wrong string when blank cell delimiter is passed.");
    }

    @Test
    void blank_row_delimiter_as_string_test() {
        final String output = 
        "0;1;0;9;5;0;7;4;0" +
        "0;7;0;0;0;3;9;0;0" +
        "0;0;0;0;0;0;8;2;5" +
        "0;0;0;0;0;4;0;0;2" +
        "6;0;0;0;0;0;0;8;0" +
        "0;8;0;5;0;0;3;0;0" +
        "0;0;0;0;0;0;1;0;0" +
        "0;0;0;0;0;0;0;0;0" +
        "0;0;0;0;0;0;0;3;0";
        assertEquals(output, partialEmptyGrid.asString(CELL_DELIMITER, ""), "asString returned wrong string when blank row delimiter is passed.");
    }

    @Test
    void blank_empty_cell_notation_arguments_as_string_test() {
        final String output = 
        ";1;;9;5;;7;4;" + "\n" +
        ";7;;;;3;9;;" + "\n" +
        ";;;;;;8;2;5" + "\n" +
        ";;;;;4;;;2" + "\n" +
        "6;;;;;;;8;" + "\n" +
        ";8;;5;;;3;;" + "\n" +
        ";;;;;;1;;" + "\n" +
        ";;;;;;;;" + "\n" +
        ";;;;;;;3;" + "\n";
        assertEquals(output, partialEmptyGrid.asString(CELL_DELIMITER, ROW_DELIMITER, ""), "asString returned wrong string when valid arguments are passed.");
    }

    @Test
    void valid_arguments_as_string_test() {
        final String outputWithDefaultArguments = 
        "0,1,0,9,5,0,7,4,0" + "\n" +
        "0,7,0,0,0,3,9,0,0" + "\n" +
        "0,0,0,0,0,0,8,2,5" + "\n" +
        "0,0,0,0,0,4,0,0,2" + "\n" +
        "6,0,0,0,0,0,0,8,0" + "\n" +
        "0,8,0,5,0,0,3,0,0" + "\n" +
        "0,0,0,0,0,0,1,0,0" + "\n" +
        "0,0,0,0,0,0,0,0,0" + "\n" +
        "0,0,0,0,0,0,0,3,0" + "\n";
        final String outputWithDefaultEmptyCellNotation = 
        "0;1;0;9;5;0;7;4;0" + "\n" +
        "0;7;0;0;0;3;9;0;0" + "\n" +
        "0;0;0;0;0;0;8;2;5" + "\n" +
        "0;0;0;0;0;4;0;0;2" + "\n" +
        "6;0;0;0;0;0;0;8;0" + "\n" +
        "0;8;0;5;0;0;3;0;0" + "\n" +
        "0;0;0;0;0;0;1;0;0" + "\n" +
        "0;0;0;0;0;0;0;0;0" + "\n" +
        "0;0;0;0;0;0;0;3;0" + "\n";
        final String outputWithCustomArguments = 
        "*;1;*;9;5;*;7;4;*" + "\n" +
        "*;7;*;*;*;3;9;*;*" + "\n" +
        "*;*;*;*;*;*;8;2;5" + "\n" +
        "*;*;*;*;*;4;*;*;2" + "\n" +
        "6;*;*;*;*;*;*;8;*" + "\n" +
        "*;8;*;5;*;*;3;*;*" + "\n" +
        "*;*;*;*;*;*;1;*;*" + "\n" +
        "*;*;*;*;*;*;*;*;*" + "\n" +
        "*;*;*;*;*;*;*;3;*" + "\n";
        assertAll(
            () -> assertEquals(outputWithDefaultArguments, partialEmptyGrid.asString(), "asString with default separators returned wrong string."),
            () -> assertEquals(outputWithDefaultEmptyCellNotation, partialEmptyGrid.asString(CELL_DELIMITER, ROW_DELIMITER), "asString with default empty cell notation returned wrong string when valid arguments are passed."),
            () -> assertEquals(outputWithCustomArguments, partialEmptyGrid.asString(CELL_DELIMITER, ROW_DELIMITER, EMPTY_CELL_NOTATION), "asString returned wrong string when valid arguments are passed.")
        );
        
    }
}
