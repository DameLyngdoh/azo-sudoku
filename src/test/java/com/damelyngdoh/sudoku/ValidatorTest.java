package com.damelyngdoh.sudoku;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.damelyngdoh.azosudoku.Grid;
import com.damelyngdoh.azosudoku.HouseType;
import com.damelyngdoh.azosudoku.Validator;
import com.damelyngdoh.azosudoku.exceptions.GridIndexOutOfBoundsException;
import com.damelyngdoh.azosudoku.exceptions.InvalidSizeException;
import com.damelyngdoh.azosudoku.exceptions.ValueOutOfBoundsException;

@TestInstance(Lifecycle.PER_METHOD)
public class ValidatorTest {
    
    static final int VALID_ORDER = 9;
    static final int VALID_INDEX = 5;
    static final HouseType VALID_GROUP_TYPE = HouseType.ROW;
    static final int VALID_VALUE = 3;
    static Grid grid;

    static Stream<Integer> validIndicesStream() {
        return IntStream.range(0, VALID_ORDER).boxed();
    }

    static Stream<Integer> validValuesStream() {
        return IntStream.rangeClosed(1, VALID_ORDER).boxed();
    }

    @BeforeEach
    void initializeGrid() throws InvalidSizeException {
        grid = new Grid(VALID_ORDER);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 2})
    void invalid_size_validate_size_test(int size) {
        assertThrowsExactly(InvalidSizeException.class, () -> Validator.validateSize(size), "validateSize did not throw InvalidSizeException with invalid size argument.");
    }

    @Test
    void valid_size_validate_size_test() {
        assertDoesNotThrow(() -> Validator.validateSize(VALID_ORDER), "validateSize threw exception with valid size argument.");
    }

    @Test
    void validate_grid_test() {
        assertThrowsExactly(NullPointerException.class, () -> Validator.validateGrid(null), "validateGrid did not throw NullPointerException when null is passed as argument.");
        assertDoesNotThrow(() -> Validator.validateGrid(grid), "validateGrid threw exception when valid grid is passed as argument.");
    }

    @Test
    void invalid_grid_argument_validate_index() {
        assertThrowsExactly(NullPointerException.class, () -> Validator.validateIndex(null, VALID_INDEX, VALID_GROUP_TYPE), "validateIndex did not throw NullPointerException when null is passed as argument.");
    }

    @Test
    void valid_grid_argument_validate_index() {
        assertDoesNotThrow(() -> Validator.validateIndex(grid, VALID_INDEX, VALID_GROUP_TYPE), "validateIndex threw exception when valid grid argument is passed as argument.");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, VALID_ORDER, VALID_ORDER + 1})
    void invalid_index_argument_validate_index(int invalidIndex) {
        assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> Validator.validateIndex(grid, invalidIndex, VALID_GROUP_TYPE), "validateIndex did not throw GridIndexOutOfBoundsException when invalid index is passed as argument.");
    }

    @ParameterizedTest
    @MethodSource("validIndicesStream")
    void valid_index_arguments_validate_index(int index) {
        assertDoesNotThrow(() -> Validator.validateIndex(grid, index, VALID_GROUP_TYPE), "validateIndex threw exception when valid index argument is passed as argument.");
    }

    @Test
    void invalid_house_type_argument_validate_index() {
        assertThrowsExactly(NullPointerException.class, () -> Validator.validateIndex(grid, VALID_INDEX, null), "validateIndex did not throw NullPointerException when null houseType is passed as argument.");
    }

    @ParameterizedTest
    @EnumSource(HouseType.class)
    void valid_house_type_arguments_validate_index(HouseType houseType) {
        assertDoesNotThrow(() -> Validator.validateIndex(grid, VALID_INDEX, houseType), "validateIndex threw exception when valid houseType argument is passed as argument.");
    }

    @Test
    void invalid_grid_argument_validate_value() {
        assertThrowsExactly(NullPointerException.class, () -> Validator.validateValue(null, VALID_VALUE), "validateValue did not throw NullPointerException when null is passed as argument.");
    }

    @Test
    void valid_grid_argument_validate_value() {
        assertDoesNotThrow(() -> Validator.validateValue(grid, VALID_VALUE), "validateValue threw exception when valid grid argument is passed as argument.");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, VALID_ORDER + 1})
    void invalid_value_argument_validate_value(int invalidValue) {
        assertThrowsExactly(ValueOutOfBoundsException.class, () -> Validator.validateValue(grid, invalidValue), "validateValue did not throw ValueOutOfBoundsException when invalid value is passed as argument.");
    }

    @ParameterizedTest
    @MethodSource("validValuesStream")
    void valid_value_arguments_validate_value(int value) {
        assertDoesNotThrow(() -> Validator.validateValue(grid, value), "validateValue threw exception when valid value argument is passed as argument.");
    }

    @Nested
    class ValidateMatrixTest {
        
        @Test
        void null_matrix_argument_validate_matrix() {
            assertThrowsExactly(NullPointerException.class, () -> Validator.validateMatrix(null), "validateMatrix did not throw NullPointerException when null argument is passed.");
        }
    
        @Test
        void invalid_size_argument_validate_matrix() {
            final int[][] invalidSizeMatrix = new int[5][5];
            assertThrowsExactly(InvalidSizeException.class, () -> Validator.validateMatrix(invalidSizeMatrix), "validateMatrix did not throw InvalidSizeException when matrix with invalid size is passed.");
        }

        @Test
        void null_row_argument_validate_matrix() {
            final int[][] nullRowMatrix = new int[VALID_ORDER][];
            assertThrowsExactly(NullPointerException.class, () -> Validator.validateMatrix(nullRowMatrix), "validateMatrix did not throw NullPointerException when null row argument is passed.");
        }

        @Test
        void invalid_column_count_validate_matrix() {
            final int[][] invalidColumnCount = new int[VALID_ORDER][VALID_ORDER + 1];
            assertThrowsExactly(InvalidSizeException.class, () -> Validator.validateMatrix(invalidColumnCount), "validateMatrix did not throw InvalidSizeException when matrix with invalid column count is passed.");
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, VALID_ORDER + 1})
        void invalid_value_validate_matrix(int invalidValue) {
            final int[][] invalidValueMatrix = new int[VALID_ORDER][VALID_ORDER];
            for(int row = 0, column = 0; row < VALID_ORDER; row++) {
                for(column = 0; column < VALID_ORDER; column++) {
                    invalidValueMatrix[row][column] = 0;
                }
            }
            invalidValueMatrix[3][3] = invalidValue;
            assertThrowsExactly(ValueOutOfBoundsException.class, () -> Validator.validateMatrix(invalidValueMatrix), "validateMatrix did not throw ValueOutOfBoundsException when matrix with one invalid value is passed.");
        }

        @Test
        void valid_empty_matrix_validate_matrix() {
            final int[][] validEmptyMatrix = new int[VALID_ORDER][VALID_ORDER];
            for(int row = 0, column = 0; row < VALID_ORDER; row++) {
                for(column = 0; column < VALID_ORDER; column++) {
                    validEmptyMatrix[row][column] = 0;
                }
            }
            assertDoesNotThrow(() -> Validator.validateMatrix(validEmptyMatrix), "validateMatrix threw an exception when valid empty matrix is passed.");
        }
    }

}
