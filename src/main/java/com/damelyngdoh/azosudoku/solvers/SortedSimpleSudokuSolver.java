package com.damelyngdoh.azosudoku.solvers;

import java.util.List;

import com.damelyngdoh.azosudoku.Cell;
import com.damelyngdoh.azosudoku.Grid;
import com.damelyngdoh.azosudoku.Validator;
import com.damelyngdoh.azosudoku.exceptions.InvalidSudokuException;

/**
 * Same brute-force approach as #SimpleSudokuSolver but it 
 * prioritizes the cell with the least number of permissible values. 
 * This is because the lesser the number of permissible values for a 
 * cell, the greater is the success probability if any one of the value 
 * is used in the trial and error approach.
 * 
 * @author Dame Lyngdoh
 * @since
 */
public class SortedSimpleSudokuSolver extends SimpleSudokuSolver {
    
    @Override
    public void solve(Grid grid) throws InvalidSudokuException {
        Validator.validateGrid(grid);
        if(grid.hasEmptyAndInvalidCells()) {
            throw new InvalidSudokuException();
        }
        final List<Cell> list = List.copyOf(grid.getSortedEmptyCellsWithPermissibleValues().navigableKeySet());
        if(!solveRecursively(grid, list, 0)) {
            throw new InvalidSudokuException();
        }
    }
}
