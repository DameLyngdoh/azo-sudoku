package com.damelyngdoh.azosudoku.exceptions;

/**
 * Thrown when an application attempts to provide a value argument which is beyond the 
 * permissible values of the grid. A value is beyond these limits if it is:
 * 
 * <ul>
 * <li>negative number<li>
 * <li>zero<li>
 * <li>greater than the size of the grid</li>
 * </ul>
 * 
 * @author Dame Lyngdoh
 */
public class ValueOutOfBoundsException extends Exception {
    
    private static final String MESSAGE = "Value %d out of bounds. Value must be in the range 1 to %d.";

    public ValueOutOfBoundsException(int value, int gridSize) {
        super(String.format(MESSAGE, value, gridSize));
    }

    public ValueOutOfBoundsException(int value, int gridSize, Throwable throwable) {
        super(String.format(MESSAGE, value, gridSize), throwable);
    }
}
