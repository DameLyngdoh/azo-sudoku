package com.damelyngdoh.azosudoku.exceptions;

/**
 * Thrown when an application attempts to access a cell with coordinates, 
 * row and/or column, which are beyond the boundaries of the grid. Negative valued 
 * coordinates are examples of such values.
 * 
 * @author Dame Lyngdoh
 */
public class GridIndexOutOfBoundsException extends RuntimeException {
    
    private static final String MESSAGE = "Invalid index %d. Grid index (row/column) must be in the range 0 to %d";

    /**
     * Constructs a new GridIndexOutOfBoundsException.
     * @param index the invalid index.
     * @param gridSize the size of the grid.
     */
    public GridIndexOutOfBoundsException(int index, int gridSize) {
        super(String.format(MESSAGE, index, gridSize - 1));
    }
}
