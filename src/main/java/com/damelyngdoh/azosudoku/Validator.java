package com.damelyngdoh.azosudoku;

import com.damelyngdoh.azosudoku.exceptions.GridIndexOutOfBoundsException;
import com.damelyngdoh.azosudoku.exceptions.InvalidSizeException;
import com.damelyngdoh.azosudoku.exceptions.ValueOutOfBoundsException;

/**
 * Utility class specifically for validation purposes. Validates the different parameters of a sudoku. 
 * Including, but not limited to, size, value, null checks and other validations.
 * 
 * @author Dame Lyngdoh
 */
public class Validator {

    /**
     * Validate size of Sudoku. Size must be a positive integer and a perfect nonet.
     * @param size size value to validate.
     * @throws InvalidSizeException thrown when size is not a positive integer and a perfect nonet.
     */
    public static void validateSize(int size) throws InvalidSizeException {
        if(size <= 0) {
            throw new InvalidSizeException(size);
        }
        double realSqrt = Math.sqrt(size);
        if(Math.ceil(realSqrt) != Math.floor(realSqrt)) {
            throw new InvalidSizeException(size);
        }
    }

    /**
     * Validates if the given one-dimensional array can be represented as a row in the sudoku.
     * @param row the array to validate.
     * @param size the size of the two-dimensional array of which the row is a part of.
     * @throws NUllPointerException thrown when the row or array reference points to null.
     * @throws InvalidSizeException thrown when the number of columns in the row is not the same as size argument.
     */
    private static void validateRow(int[] row, int size) throws InvalidSizeException {
        if(row == null) {
            throw new NullPointerException(String.format("Null row at %d found in matrix argument.", row));
        }
        if(row.length != size) {
            throw new InvalidSizeException(size);
        }
    }

    /**
     * Validates if all the values in a specific row is valid.
     * @param row the row of as a one-dimensional array containing the values.
     * @param size the size of the two-dimensional array of which the row is a part of.
     * @throws ValueOutOfBoundsException thrown when any value within the row is not in the range of 0 to size argument (inclusively).
     */
    private static void validateValues(int[] row, int size) throws ValueOutOfBoundsException {
        for(int column = 0; column < size; column++) {
            if(row[column] < 0 || row[column] > size) {
                throw new ValueOutOfBoundsException(row[column], row.length);
            }
        }
    }
    
    /**
     * Validates if the given matrix has the necessary conditions to be represented as a sudoku. 
     * The necessary conditions are:
     * <ul>
     *  <li>matrix size must be a perfect nonet (row count)</li>
     *  <li>each row must not be null and must have column count equals to size</li>
     *  <li>each value of the matrix must be in the range 0 to size of the matrix (inclusizely)</li>
     * </ul>
     * @param matrix as two-dimensional int array.
     * @throws NullPointerException thrown if matrix argument is null or if any one of the rows is null.
     * @throws InvalidSizeException thrown if the row count or column count of any of the rows is not a perfect nonet.
     * @throws ValueOutOfBoundsException thrown if any of the value in the matrix is not in the range of 0 to size (inclusively)
     */
    public static void validateMatrix(int[][] matrix) throws InvalidSizeException, ValueOutOfBoundsException {
        if(matrix == null) {
            throw new NullPointerException("Null two-dimensional array passed.");
        }
        int size = matrix.length;
        validateSize(size);
        for(int row = 0; row < size; row++) {
            validateRow(matrix[row], size);
            validateValues(matrix[row], size);
        }
    }

    /**
     * Validates if a grid reference points to null or not.
     * @param grid reference to validate.
     * @throws NullPointerException when references points to null.
     */
    public static void validateGrid(Grid grid) {
        if(grid == null) {
            throw new NullPointerException();
        }
    }

    /**
     * Validates if the index and house type are valid for the specific grid argument. Index must be in the range 0 to size of the grid.
     * @param grid the grid context to refer from.
     * @param index index of the house to validate.
     * @param houseType houseType of the goroup to validate.
     * @throws GridIndexOutOfBoundsException thrown if index is out of bounds of the grid context.
     * @throws NullPointerException thrown if null is passed to grid argument or to houseType argument.
     */
    public static void validateIndex(Grid grid, int index, HouseType houseType) {
        validateGrid(grid);
        if(index < 0 || index >= grid.getSize()) {
            throw new GridIndexOutOfBoundsException(index, grid.getSize());
        }
        if(houseType == null) {
            throw new NullPointerException("Null HouseType argument passed.");
        }
    }

    /**
     * Validates if a value is valid in the context of a grid. Value must be in the range of 1 to grid size (inclusively).
     * @param grid the grid context to refer from.
     * @param value the value to validate.
     * @throws NullPointerException thrown when null is passed to grid argument.
     * @throws ValueOutOfBoundsException thrown when value is out of bounds of the grid context.
     */
    public static void validateValue(Grid grid, int value) throws ValueOutOfBoundsException {
        validateGrid(grid);
        if(value <= 0 || value > grid.getSize()) {
            throw new ValueOutOfBoundsException(value, grid.getSize());
        }
    }

}
