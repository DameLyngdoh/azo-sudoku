package com.damelyngdoh.azosudoku.generators;

import java.util.Set;

import com.damelyngdoh.azosudoku.Cell;
import com.damelyngdoh.azosudoku.Grid;
import com.damelyngdoh.azosudoku.Utils;
import com.damelyngdoh.azosudoku.Validator;
import com.damelyngdoh.azosudoku.exceptions.InvalidSizeException;

/**
 * A Sudoku puzzle generator. The generate methods generate puzzles with the specified grid size. 
 * 
 * @author Dame Lyngdoh
 */
public interface SudokuGenerator {

    /**
     * Validates the count of non-empty cells, by comparing it to the size.
     * @param size the size of the grid.
     * @param nonEmptyCellCount the number of non-empty cells in the grid.
     * @throws InvalidSizeException thrown when invalid size is passed.
     * @throws IllegalArgumentException thrown when nonEmptyCellCount is negative or greater than cell count of the grid.
     */
    default void validateNonEmptyCellCount(int size, int nonEmptyCellCount) throws InvalidSizeException {
        Validator.validateSize(size);
        final int cellCount = size * size;
        if(nonEmptyCellCount < 0 || nonEmptyCellCount > cellCount) {
            throw new IllegalArgumentException(String.format("Invalid nonEmptyCellCount %d. Must be in the range 0 to %d inclusively.", nonEmptyCellCount, cellCount));
        }
    }

    /**
     * Checks if the cell is the last cell in the grid, that is, the bottom right most cell.
     * @param cell the cell to check.
     * @param grid the grid which the cell is a part of.
     * @return true if it is the last cell, or false otherwise.
     * @throws NullPointerException when cell or grid argument are null.
     */
    default boolean isLastCell(Cell cell) {
        if(cell == null) {
            throw new NullPointerException();
        }
        final int size = cell.getGrid().getSize();
        return cell.getRow() == size - 1 && cell.getColumn() == size - 1;
    }
    
    /**
     * Checks if a cell is either marked as fixed or not empty.
     * @param cell the cell to check.
     * @return true if cell is marked as fixed or not empty or false otherwise.
     * @throws NullPointerException thrown if cell argument is null.
     */
    default boolean isCellFixedOrNotEmpty(Cell cell) {
        if(cell == null) {
            throw new NullPointerException();
        }
        return cell.isFixed() || !cell.isEmpty();
    }

    /**
     * Generates a complete sudoku grid, where there are no empty cells in the grid.
     * @param size the size of the grid to be generated.
     * @return grid object.
     * @throws InvalidSizeException thrown if size is not a perfect nonet positive integer.
     */
    Grid generate(int size) throws InvalidSizeException;

    /**
     * Generates a partially empty grid with the number of non-empty cells specified as an argument.
     * @param size the size of the grid.
     * @param nonEmptyCellCount the number of non-empty cell count in the range 0 to total number of cells in the grid.
     * @return partially empty grid object.
     * @throws InvalidSizeException thrown if size is not a perfect nonet positive integer.
     * @throws IllegalArgumentException thrown if nonEmptyCellCount is less than 0 or greater than the size of the grid.
     */
    default Grid generate(int size, int nonEmptyCellCount) throws InvalidSizeException {
        Validator.validateSize(size);
        validateNonEmptyCellCount(size, nonEmptyCellCount);
        Grid grid = generate(size);
        final int emptyCellCount = (size * size) - nonEmptyCellCount;
        Set<Cell> randomNonEmptyCells = Utils.getRandomElements(grid.getNonEmptyCells(), emptyCellCount);
        randomNonEmptyCells.forEach(cell -> cell.removeValue());
        return grid;
    }
}
