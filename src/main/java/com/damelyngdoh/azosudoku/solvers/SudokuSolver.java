package com.damelyngdoh.azosudoku.solvers;

import com.damelyngdoh.azosudoku.Grid;
import com.damelyngdoh.azosudoku.exceptions.InvalidSudokuException;

/**
 * A Sudoku puzzle solving instance. The <i>solve</i> method attempts to solve the 
 * provided grid or throws an {@link com.damelyngdoh.azosudoku.exceptions.InvalidSudokuException InvalidSudokuException} 
 * when the puzzle does not have a solution.
 * 
 * @author Dame Lyngdoh
 */
public interface SudokuSolver {
    
    /**
     * Solves the sudoku puzzle .
     * @param grid the sudoku grid.
     * @throws InvalidSudokuException thrown when the puzzle does not have a solution.
     */
    void solve(Grid grid) throws InvalidSudokuException;
}
