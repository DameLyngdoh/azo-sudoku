package com.damelyngdoh.azosudoku.solvers;

import java.util.List;
import java.util.Set;

import com.damelyngdoh.azosudoku.Cell;
import com.damelyngdoh.azosudoku.Grid;
import com.damelyngdoh.azosudoku.Validator;
import com.damelyngdoh.azosudoku.exceptions.InvalidSudokuException;
import com.damelyngdoh.azosudoku.exceptions.ValueOutOfBoundsException;

/**
 * This implementation is a brute-force style approach where each permissible 
 * value is used on a cell until, and this is done for all the cells until 
 * there are no emtpy cells in the grid.
 * 
 * @author Dame Lyngdoh
 */
public class SimpleSudokuSolver implements SudokuSolver {

    /**
     * Sets a value to the cell and ignores the ValueOutOfBoundsException 
     * exception as the value has been confirmed by the invoking method.
     * @param cell the cell reference.
     * @param value the value to fill the cell.
     **/
    private void setValue(Cell cell, int value) {
        try {
            cell.setValue(value);
        } catch (ValueOutOfBoundsException e) {}
    }

    /**
     * Solves the sudoku by calling itself recursively and each invocation will proceed to the 
     * next empty cell in the list of empty cells specified by the emptyCellsList argument. The 
     * currentIndex marks the current cell in the list being processed.
     * @param grid the grid context.
     * @param emptyCellsList the list of empty cells of the grid.
     * @param currentIndex the current index of the emtpy cells list which the recursion has reached.
     * @return true if from the perspective of the current index a value was set and no invalid empty cell was encountered or false otherwise.
     */
    boolean solveRecursively(Grid grid, List<Cell> emptyCellsList, int currentIndex) {
        if(currentIndex >= emptyCellsList.size()) {
            return true;
        }
        final Cell currentCell = emptyCellsList.get(currentIndex);
        Set<Integer> permissibleValues = grid.getPermissibleValues(currentCell);
        if(permissibleValues.isEmpty()) {
            return false;
        }
        for(Integer value : permissibleValues) {
            setValue(currentCell, value);
            if(solveRecursively(grid, emptyCellsList, currentIndex + 1)) {
                return true;
            }
            currentCell.removeValue();
        }
        return false;
    }

    @Override
    public void solve(Grid grid) throws InvalidSudokuException {
        Validator.validateGrid(grid);
        if(grid.hasEmptyAndInvalidCells()) {
            throw new InvalidSudokuException();
        }
        final List<Cell> list = List.copyOf(grid.getEmptyCells());
        if(!solveRecursively(grid, list, 0)) {
            throw new InvalidSudokuException();
        }
    }
    
}
