package com.damelyngdoh.azosudoku.exceptions;

/**
 * Thrown when an application attempts to provide a grid size argument which is not a
 * <ul>
 * <li>perfect nonet<li>
 * <li>positive integer</li>
 * </ul>
 * 
 * @author Dame Lyngdoh
 */
public class InvalidSizeException extends Exception {
    
    private static final String MESSAGE = "Invalid size %d. Must be a perfect nonet integer.";

    public InvalidSizeException(int size) {
        super(String.format(MESSAGE, size));
    }
}
