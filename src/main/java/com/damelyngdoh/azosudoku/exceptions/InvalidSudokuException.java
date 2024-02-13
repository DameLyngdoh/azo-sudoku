package com.damelyngdoh.azosudoku.exceptions;

/**
 * Thrown when a {@link #com.damelyngdoh.azosudoku.solvers.SimpleSudokuSolver} is provided 
 * a grid argument which does not have a solution. The grid argument is a partially complete 
 * grid where there exists an empty cell which has an empty set of permissible values.
 * 
 * @author Dame Lyngdoh
 */
public class InvalidSudokuException extends Exception {
    
    private static final String MESSAGE = "Invalid Sudoku. No solution exists.";

    public InvalidSudokuException() {
        super(MESSAGE);
    }

    public InvalidSudokuException(String msg) {
        super(msg);
    }
}
