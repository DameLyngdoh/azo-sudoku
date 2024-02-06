package com.damelyngdoh.azosudoku.generators;

import java.util.Set;

import com.damelyngdoh.azosudoku.Cell;
import com.damelyngdoh.azosudoku.Grid;
import com.damelyngdoh.azosudoku.Utils;
import com.damelyngdoh.azosudoku.exceptions.InvalidSizeException;
import com.damelyngdoh.azosudoku.exceptions.ValueOutOfBoundsException;

/**
 * A brute-force style approach of populating the sudoku grid. 
 * This implementation starts from the first cell (top-left) and 
 * using trial and error, recursively populates the cells with every 
 * possible value and the recursion stops when there are no empty cells 
 * or no invalid empty cell present in the grid.
 * 
 * @author Dame Lyngdoh
 */
public class SimpleSudokuGenerator implements SudokuGenerator {

    /**
     * Sets a value to the cell and ignores the ValueOutOfBoundsException 
     * exception as the value has been confirmed by the invoking method.
     * @param cell the cell reference.
     * @param value the value to fill the cell.
     **/
    private void setValueForced(Cell cell, int value) {
        try {
            cell.setValue(value);
        } catch (ValueOutOfBoundsException e) {}
    }

    /**
     * Gets the next cell with respect to the specified cell. 
     * The next cell is the cell just to the right of the specified cell or 
     * if the specified cell is the last cell in the row, then the next cell 
     * is the first cell of the next row.
     * @param cell the cell to get the next cell to.
     * @return next cell.
     */
    private Cell getNextCell(Cell cell) {
        final Grid grid = cell.getGrid();
        final int size = grid.getSize();
        final int column = cell.getColumn();
        final int row = cell.getRow();
        return grid.getCell(column == size - 1 ? row + 1 : row, (column + 1) % size);
    }

    /**
     * Gets a random value from the specified set of permissible values.
     * @param permissibleValues set of values to choose from.
     * @return random element from the input set.
     */
    private int getRandomValueFromPermissibleValues(Set<Integer> permissibleValues) {
        return Utils.getRandomElement(permissibleValues).get();
    }

    /**
     * Processes the last cell of the grid and returns the result of whether 
     * the last cell can be successfully populated by one of its permissible 
     * value or it is an invalid emtpy cell.
     * @param lastCell the last cell of the grid.
     * @param permissibleValues the permissible values of the cell.
     * @return true if the last cell can be populated by a permissible value or has already been populated or false otherwise.
     */
    private boolean processLastCell(Cell lastCell, Set<Integer> permissibleValues) {
        if(isCellFixedOrNotEmpty(lastCell)) {
            return true;
        }
        if(permissibleValues.isEmpty()) {
            return false;
        }
        setValueForced(lastCell, getRandomValueFromPermissibleValues(permissibleValues));
        return true;
    }

    /**
     * Recursively populates the cells of the grid with a permissible value from 
     * the set of permissible values of the cell and then moves to the next cell 
     * in the grid.
     * @param grid the grid context.
     * @param currentCell the current cell to populate.
     * @return true if all cells after the current cell could be successfulyl populated or false otherwise.
     */
    private boolean recrusivePopulate(Grid grid, Cell currentCell) {
        Set<Integer> permissibleValues = grid.getPermissibleValues(currentCell);
        if(isLastCell(currentCell)) {
            return processLastCell(currentCell, permissibleValues);
        }

        final Cell nextCell = getNextCell(currentCell);
        if(isCellFixedOrNotEmpty(currentCell)) {
            return recrusivePopulate(grid, nextCell);
        }
        while(!permissibleValues.isEmpty()) {
            int randomValue = getRandomValueFromPermissibleValues(permissibleValues);
            setValueForced(currentCell, randomValue);
            if(recrusivePopulate(grid, nextCell)) {
                return true;
            }
            currentCell.removeValue();
            permissibleValues.remove(randomValue);
        }
        return false;
    }

    @Override
    public Grid generate(int size) throws InvalidSizeException {
        Grid grid = new Grid(size);
        recrusivePopulate(grid, grid.getCell(0, 0));
        return grid;
    }
    
}
