package com.damelyngdoh.azosudoku;

import java.util.NoSuchElementException;
import java.util.Optional;

import com.damelyngdoh.azosudoku.exceptions.GridIndexOutOfBoundsException;
import com.damelyngdoh.azosudoku.exceptions.ValueOutOfBoundsException;

/**
 * Class representing the cell or elements in the Sudoku puzzle which 
 * contains a value. A cell must exist as part of a grid and is identified 
 * by its coordinates, row and column, in the grid.
 *  
 * @author Dame Lyngdoh
 */
public class Cell {

    private static final String CELL_STRING = "[Cell row=%d; column=%d; house=%d; value=%d]";

    /**
     * Parent grid which contains the cell.
     */
    private final Grid grid;
    
    /**
     * Row coordinate.
     */
    private final int row;

    /**
     * Column coordinate.
     */
    private final int column;

    /**
     * Value of the cell wrapped in an {@link java.util.Optional Optional}.
     */
    private Optional<Integer> value = Optional.empty();

    /**
     * Fixed flag to indicate if the cell's value can be updated.
     */
    private boolean fixed = false;

    /**
     * Constructs a cell object with the provided coordinates and parent grid.
     * @param grid parent grid.
     * @param row row coordinate.
     * @param column column coordinate.
     * @throws NullPointerException thrown if the grid argument is null.
     * @throws GridIndexOutOfBoundsException thrown if row or/and column indices are beyond the limits of the parent grid.
     */
    public Cell(Grid grid, int row, int column) throws GridIndexOutOfBoundsException {
        Validator.validateGrid(grid);
        this.grid = grid;
        Validator.validateIndex(grid, column, HouseType.COLUMN);
        Validator.validateIndex(grid, row, HouseType.ROW);
        this.row = row;
        this.column = column;
    }

    /**
     * Constructs a cell object with the provided coordinates, initial value and parent grid.
     * @param grid parent grid.
     * @param row row coordinate.
     * @param column column coordinate.
     * @param value initial value of the cell.
     * @throws NullPointerException thrown if the grid argument is null.
     * @throws GridIndexOutOfBoundsException thrown if row or/and column indices are beyond the limits of the parent grid.
     * @throw ValueOutOfBoundsException thrown if the value argument is beyond the permissible limits for the grid.
     */
    public Cell(Grid grid, int row, int column, int value) throws ValueOutOfBoundsException, GridIndexOutOfBoundsException {
        Validator.validateGrid(grid);
        this.grid = grid;
        Validator.validateIndex(grid, column, HouseType.COLUMN);
        Validator.validateIndex(grid, row, HouseType.ROW);
        this.row = row;
        this.column = column;
        this.setValue(value);
    }

    /**
     * Gets the parent grid of the cell.
     * @return
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Gets the row coordinate of the cell.
     * @return
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the column coordinate of the cell.
     * @return
     */
    public int getColumn() {
        return column;
    }

    /**
     * Gets the index of the nonet which contains the cell.
     * @return
     */
    public int getNonet() {
        return Utils.calculateNonet(grid, row, column);
    }

    /**
     * Gets the value contained in the cell.
     * @return
     * @throws NoSuchElementException thrown if no value was set for the cell.
     */
    public int getValue() {
        return value.get();
    }

    /**
     * Gets the value of the cell wrapped in {@link java.util.Optional Optional}.
     * @return
     */
    public Optional<Integer> getOptionalValue() {
        return this.value;
    }

    /**
     * Sets the value for the cell.
     * @param value the value to set.
     * @throws ValueOutOfBoundsException thrown if the value is not in the range of 1 to grid size.
     */
    public void setValue(int value) throws ValueOutOfBoundsException {
        Validator.validateValue(grid, value);
        this.value = Optional.of(value);
    }

    /**
     * Checks if the cell is emtpy or contains a value.
     * @return
     */
    public boolean isEmpty() {
        return value.isEmpty();
    }

    /**
     * Checks if the cell is marked as fixed or not.
     * @return
     */
    public boolean isFixed() {
        return fixed;
    }

    /**
     * Sets the fixed flag of the cell.
     * @param fixed
     */
    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    /**
     * Removes the value in the cell and returns the value that is removed.
     * @return value that was present in the cell or 0 if the cell was empty.
     */
    public int removeValue() {
        if(value.isPresent()) {
            int previousValue = value.get();
            value = Optional.empty();
            return previousValue;
        }
        return 0;
    }

    /**
     * Checks if a cell is related to this cell or not. Two cells are related if 
     * they share a house.
     * @param cell
     * @return true if related or false otherwise.
     * @throws NullPointerException
     */
    public boolean isRelated(Cell cell) {
        if(cell == null) {
            throw new NullPointerException();
        }
        return row == cell.getRow() || column == cell.getColumn() || getNonet() == cell.getNonet();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(!(obj instanceof Cell)) {
            return false;
        }
        Cell cell = (Cell)obj;
        return row == cell.getRow() && column == cell.getColumn();
    }

    @Override
    public int hashCode() {
        return (grid.getSize() * row) + column;
    }

    @Override
    public String toString() {
        return String.format(CELL_STRING, row, column, getNonet(), value.isEmpty() ? 0 : value.get());
    }
}
