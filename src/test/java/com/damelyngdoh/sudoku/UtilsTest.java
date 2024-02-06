package com.damelyngdoh.sudoku;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.damelyngdoh.azosudoku.Grid;
import com.damelyngdoh.azosudoku.Utils;
import com.damelyngdoh.azosudoku.exceptions.DisallowedValueException;
import com.damelyngdoh.azosudoku.exceptions.GridIndexOutOfBoundsException;
import com.damelyngdoh.azosudoku.exceptions.InvalidSizeException;
import com.damelyngdoh.azosudoku.exceptions.ValueOutOfBoundsException;

/**
 * @author Dame Lyngdoh
 */
@TestInstance(Lifecycle.PER_METHOD)
public class UtilsTest {

    @Nested
    class GetRandomElementTest {
        
        @Test
        void invalid_set_argument() {
            assertThrowsExactly(NullPointerException.class, () -> Utils.getRandomElement(null), "getRandomElement did not throw NullPointerException with null set argument.");
        }

        @Test
        void empty_set_argument() {
            Set<Integer> set = new HashSet<>();
            assertAll(
                () -> assertDoesNotThrow(() -> Utils.getRandomElement(set), "getRandomElement threw exception with emtpy set argument."),
                () -> assertTrue(Utils.getRandomElement(set).isEmpty(), "getRandomElement returned non-empty optional with emtpy set argument.")
            );
        }

        @Test
        void single_element_set_argument() {
            final Set<Integer> set = new HashSet<>();
            final int element = 1;
            set.add(element);
            assertAll(
                () -> assertDoesNotThrow(() -> Utils.getRandomElement(set), "getRandomElement threw exception with single element set argument."),
                () -> assertTrue(Utils.getRandomElement(set).isPresent(), "getRandomElement returned empty optional with single element set argument."),
                () -> assertEquals(element, Utils.getRandomElement(set).get(), "getRandomElement returned different value with single element set argument.")
            );
        }

        @Test
        void valid_set_argument() {
            final Set<Integer> set = new HashSet<>();
            set.add(1);
            set.add(2);
            set.add(3);
            set.add(7);
            set.add(11);
            set.add(33);
            assertAll(
                () -> assertDoesNotThrow(() -> Utils.getRandomElement(set), "getRandomElement threw exception with valid set argument."),
                () -> assertTrue(Utils.getRandomElement(set).isPresent(), "getRandomElement returned empty optional with non-empty set argument."),
                () -> assertTrue(set.contains(Utils.getRandomElement(set).get()), "getRandomElement returned different value not contained in input set.")
            );
        }
    }

    @Nested
    class GetRandomElementsTest {
        
        @Test
        void invalid_set_argument() {
            assertThrowsExactly(NullPointerException.class, () -> Utils.getRandomElements(null, 1), "getRandomElements did not throw NullPointerException when null set argument is passed.");
        }

        @Test
        void invalid_count_argument() {
            final Set<Integer> set = IntStream.range(0, 10).boxed().collect(Collectors.toSet());
            final int countGreaterThanSetSize = set.size() + 1;
            assertAll(
                () -> assertThrowsExactly(IllegalArgumentException.class, () -> Utils.getRandomElements(set, -1), "getRandomElements did not throw IllegalArgumentException when negative count argument is passed."),
                () -> assertThrowsExactly(IllegalArgumentException.class, () -> Utils.getRandomElements(set, countGreaterThanSetSize), "getRandomElements did not throw IllegalArgumentException when count argument greater than set size is passed.")
            );
        }

        @Test
        void set_size_equals_count_argument() {
            final Set<Integer> set = IntStream.range(0, 10).boxed().collect(Collectors.toSet());
            assertAll(
                () -> assertDoesNotThrow(() -> Utils.getRandomElements(set, set.size()), "getRandomElements threw an exception when count argument equal to set size is passed."),
                () -> assertIterableEquals(set, Utils.getRandomElements(set, set.size()), "getRandomElements returned a different set when count argument equal to set size is passed.")
            );
        }

        @Test
        void count_less_than_set_size_argument() {
            final Set<Integer> set = IntStream.range(0, 10).boxed().collect(Collectors.toSet());
            final int count = 4;
            assertAll(
                () -> assertDoesNotThrow(() -> Utils.getRandomElements(set, count), "getRandomElements threw an exception when valid count argument is passed."),
                () -> assertEquals(count, Utils.getRandomElements(set, count).size(), "getRandomElements returned a set with different size other than count when vaoid count argument passed."),
                () -> assertTrue(set.containsAll(Utils.getRandomElements(set, count)), "getRandomElements returned a different set when valid count argument is passed.")
            );
        }
    }

    @Nested
    static class GetDiagonalNonetsTest {
        static final int ORDER_4 = 4;
        static final int ORDER_9 = 9;
        static final Set<Integer> ORDER_4_DIAGONALS = Set.of(0,3);
        static final Set<Integer> ORDER_9_DIAGONALS = Set.of(0,4,8);

        @Test
        void invalid_grid_argument_test() {
            assertThrowsExactly(NullPointerException.class, () -> Utils.getDiagonalNonets(null), "getDiagonalNonets did not throw NullPointerException when null grid argument is passed.");
        }

        @Test
        void valid_grid_argument_test() throws InvalidSizeException {
            Grid gridSize4 = new Grid(ORDER_4);
            Grid gridSize9 = new Grid(ORDER_9);
            final Set<Integer> actualDiagonalNonetsSize4 = Utils.getDiagonalNonets(gridSize4);
            final Set<Integer> actualDiagonalNonetsSize9 = Utils.getDiagonalNonets(gridSize9);
            assertAll(
                () -> assertTrue(ORDER_4_DIAGONALS.containsAll(actualDiagonalNonetsSize4), "getDiagonalNonets retunred a set that has elements which are not part of the actual diagonal nonet indices."),
                () -> assertTrue(actualDiagonalNonetsSize4.containsAll(ORDER_4_DIAGONALS), "getDiagonalNonets retunred a set that has missing elements from the actual diagonal nonet indices.")
            );

            assertAll(
                () -> assertTrue(ORDER_9_DIAGONALS.containsAll(actualDiagonalNonetsSize9), "getDiagonalNonets retunred a set that has elements which are not part of the actual diagonal nonet indices."),
                () -> assertTrue(actualDiagonalNonetsSize9.containsAll(ORDER_9_DIAGONALS), "getDiagonalNonets retunred a set that has missing elements from the actual diagonal nonet indices.")
            );
        }
    }

    @Nested
    class CalculateNonetTests {
        final static int VALID_ORDER = 9;
        final static int VALID_ROW = 4;
        final static int VALID_COLUMN = 5;

        @Test
        void invalid_grid_argument_test() {
            assertThrowsExactly(NullPointerException.class, () -> Utils.calculateNonet(null, VALID_ROW, VALID_COLUMN), "calculateNonet did not throw NullPointerException when null grid is passed.");
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, VALID_ORDER, VALID_ORDER + 1})
        void invalid_row_argument_test(int invalidRow) throws InvalidSizeException {
            final Grid grid = new Grid(VALID_ORDER);
            assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> Utils.calculateNonet(grid, invalidRow, VALID_COLUMN), "calculateNonet did not throw GridIndexOutOfBoundsException when invalid row is passed.");
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, VALID_ORDER, VALID_ORDER + 1})
        void invalid_column_argument_test(int invalidColumn) throws InvalidSizeException {
            final Grid grid = new Grid(VALID_ORDER);
            assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> Utils.calculateNonet(grid, VALID_ROW, invalidColumn), "calculateNonet did not throw GridIndexOutOfBoundsException when invalid column is passed.");
        }

        @Test
        void valid_arguments_test() throws InvalidSizeException {
            final Grid grid = new Grid(VALID_ORDER);
            assertAll(
                () -> assertDoesNotThrow(() -> Utils.calculateNonet(grid, VALID_ROW, VALID_COLUMN), "calculateNonet threw exception with valid arguments."),
                () -> assertEquals(4, Utils.calculateNonet(grid, VALID_ROW, VALID_COLUMN), "calculateNonet returned incorrect nonet index.")
            );
        }
    }
   
    @Nested
    class GetNonetSize {
        @Test
        void invlaid_grid_argument_test() {
            assertThrowsExactly(NullPointerException.class, () -> Utils.getNonetSize(null), "getNonetSize did not thrown NullPointerException with null grid argument.");
        }

        @Test
        void valid_argument_test() throws InvalidSizeException {
            final int size = 16;
            final Grid grid = new Grid(size);
            final int nonetSize = 4;
            assertAll(
                () -> assertDoesNotThrow(() -> Utils.getNonetSize(grid), "getNonetSize threw exception with valid grid argument."),
                () -> assertEquals(nonetSize, Utils.getNonetSize(grid), "getNonetSize returned incorrect nonet size.")
            );
        }
    }

    @Nested
    static class GetPermissibleValuesTest {

        final static Set<Integer> PERMISSIBLE_VALUES_ORDER_4 = Arrays.stream(new int[]{1,2,3,4}).boxed().collect(Collectors.toSet());
        final static Set<Integer> PERMISSIBLE_VALUES_ORDER_9 = Arrays.stream(new int[]{1,2,3,4,5,6,7,8,9}).boxed().collect(Collectors.toSet());

        @ParameterizedTest
        @ValueSource(ints = {-1, 0, 2})
        void invalid_size_test(int invalidSize) {
            assertThrowsExactly(InvalidSizeException.class, () -> Utils.getPermissibleValues(invalidSize), "getPermissibleValues did not throw exception with invalid size argument.");
        }

        void valid_arguments_test() {
            assertAll(
                () -> assertDoesNotThrow(() -> Utils.getPermissibleValues(4), "getPermissibleValues threw exception with valid size argument."),
                () -> assertDoesNotThrow(() -> Utils.getPermissibleValues(9), "getPermissibleValues threw exception with valid size argument."),
                () -> assertIterableEquals(PERMISSIBLE_VALUES_ORDER_4, Utils.getPermissibleValues(4), "getPermissibleValues returned a different set of permissible values for size 4."),
                () -> assertIterableEquals(PERMISSIBLE_VALUES_ORDER_9, Utils.getPermissibleValues(9), "getPermissibleValues returned a different set of permissible values for size 9.")
            );
        }
    }

    @Nested
    static class InitilizeMatrixTest {
        static final int VALID_ORDER = 9;
        static final int[][] VALID_MATRIX = {
            {0,1,0,9,5,0,7,4,0},
            {0,7,0,0,0,3,9,0,0},
            {0,0,0,0,0,0,8,2,5},
            {0,0,0,0,0,4,0,0,2},
            {6,0,0,0,0,0,0,8,0},
            {0,8,0,5,0,0,3,0,0},
            {0,0,0,0,0,0,1,0,0},
            {0,0,0,0,0,0,0,0,0},
            {1,0,0,0,0,0,0,3,0}
        };
        static final int[][] EMPTY_MATRIX = {
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
        
        @Test
        void null_matrix_argument_initialize_grid_grid() {
            assertThrowsExactly(NullPointerException.class, () -> Utils.initializeGrid(null), "initializeGrid did not throw NullPointerException when null argument is passed.");
        }
    
        @Test
        void invalid_size_argument_initialize_grid_grid() {
            final int[][] invalidSizeMatrix = new int[5][5];
            assertThrowsExactly(InvalidSizeException.class, () -> Utils.initializeGrid(invalidSizeMatrix), "initializeGrid did not throw InvalidSizeException when matrix with invalid size is passed.");
        }

        @Test
        void null_row_argument_initialize_grid_grid() {
            final int[][] nullRowMatrix = new int[VALID_ORDER][];
            assertThrowsExactly(NullPointerException.class, () -> Utils.initializeGrid(nullRowMatrix), "initializeGrid did not throw NullPointerException when null row argument is passed.");
        }

        @Test
        void invalid_column_count_initialize_grid_grid() {
            final int[][] invalidColumnCount = new int[VALID_ORDER][VALID_ORDER + 1];
            assertThrowsExactly(InvalidSizeException.class, () -> Utils.initializeGrid(invalidColumnCount), "initializeGrid did not throw InvalidSizeException when matrix with invalid column count is passed.");
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, VALID_ORDER + 1})
        void invalid_value_initialize_grid_grid(int invalidValue) {
            final int[][] invalidValueMatrix = new int[VALID_ORDER][VALID_ORDER];
            for(int row = 0, column = 0; row < VALID_ORDER; row++) {
                for(column = 0; column < VALID_ORDER; column++) {
                    invalidValueMatrix[row][column] = 0;
                }
            }
            invalidValueMatrix[3][3] = invalidValue;
            assertThrowsExactly(ValueOutOfBoundsException.class, () -> Utils.initializeGrid(invalidValueMatrix), "initializeGrid did not throw ValueOutOfBoundsException when matrix with one invalid value is passed.");
        }

        @Test
        void valid_arguments_initialize_grid_grid() {
            assertDoesNotThrow(() -> Utils.initializeGrid(EMPTY_MATRIX), "initializeGrid threw an exception when valid empty matrix is passed.");            
        }

        @Test
        void comparison_initialize_grid_grid_test() throws InvalidSizeException, ValueOutOfBoundsException, DisallowedValueException {
            Grid grid = Utils.initializeGrid(VALID_MATRIX);
            for(int row = 0, column = 0; row < VALID_ORDER; row++) {
                for(column = 0; column < VALID_ORDER; column++) {
                    assertEquals(VALID_MATRIX[row][column], grid.getValue(row, column), "");
                }
            }
        }
    }
}
