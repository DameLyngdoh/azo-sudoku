package com.damelyngdoh.azosudoku.exceptions;

/**
 * Thrown when an application attempts to set the value of a cell with a value which 
 * is not permissible for the cell, even though the value is within the limits of the 
 * grid context. For example, if a cell can only accomodate the values 7 or 4, but an attempt 
 * was made to set the value of the cell to 3.
 * 
 * @author Dame Lyngdoh
 */
public class DisallowedValueException extends Exception {
    
    private static final String MESSAGE = "Value %d, not allowed for cell at (%d,%d)";

    /**
     * Constructs a new DisallowedValueException object.
     * @param row the row of the cell.
     * @param column the column of the cell.
     * @param value the disallowed value.
     */
    public DisallowedValueException(int row, int column, int value) {
        super(String.format(MESSAGE, value, row, column));
    }
}
