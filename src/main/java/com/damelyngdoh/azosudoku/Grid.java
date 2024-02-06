package com.damelyngdoh.azosudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.damelyngdoh.azosudoku.exceptions.DisallowedValueException;
import com.damelyngdoh.azosudoku.exceptions.GridIndexOutOfBoundsException;
import com.damelyngdoh.azosudoku.exceptions.InvalidSizeException;
import com.damelyngdoh.azosudoku.exceptions.ValueOutOfBoundsException;

/**
 * @author Dame Lyngdoh
 */
public class Grid {

    private static final String DEFAULT_CELL_DELIMITER = ",";
    private static final String DEFAULT_ROW_DELIMITER = "\n";
    private static final String DEFAULT_EMPTY_CELL_NOTATTION = "0";
    private static final String NULL_CELL_ARGUMENT_MSG = "Null cell argument passed.";

    /**
     * Two-dimensional containing the cells instances and represents the Sudoku.
     */
    private final Cell[][] matrix;

    /**
     * List of rows of the sudoku.
     */
    private List<House> rows;

    /**
     * List of columns of the sudoku.
     */
    private List<House> columns;

    /**
     * List of nonets of the sudoku.
     */
    private List<House> nonets;

    /**
     * Initializes the two-dimensional array with the specified size.
     * @param size
     */
    private void initializeMatrix(int size) {
        for(int row = 0, column = 0; row < size; row++) {
            for(column = 0; column < size; column++) {
                matrix[row][column] = new Cell(this, row, column);
            }
        }
    }

    /**
     * Populates the rows with corresponding cells.
     */
    private void populateRowHouse() {
        for(int row = 0, column = 0; row < matrix.length; row++) {
            House rowHouse = new House(row, this, HouseType.ROW);
            for(column = 0; column < matrix[row].length; column++)
                rowHouse.add(matrix[row][column]);
            this.rows.add(rowHouse);
        }
    }

    /**
     * Populates the columns with corresponding cells.
     */
    private void populateColumnHouse() {
        for(int row = 0, column = 0; column < matrix.length; column++) {
            House columnHouse = new House(column, this, HouseType.COLUMN);
            for(row = 0; row < getSize(); row++)
                columnHouse.add(matrix[row][column]);
            columns.add(columnHouse);
        }
    }

    /**
     * Populates the nonets with corresponding cells.
     */
    private void populateNonetHouse() {
        for(int i = 0; i < getSize(); i++)
            nonets.add(new House(i, this, HouseType.SQUARE));
        streamCells().forEach(cell -> nonets.get(cell.getNonet()).add(cell));
    }

    /**
     * Initializes the rows, columns and nonets.
     */
    private void initializeHouses() {
        this.rows = new ArrayList<>(getSize());
        this.columns = new ArrayList<>(getSize());
        this.nonets = new ArrayList<>(getSize());
        populateRowHouse();
        populateColumnHouse();
        populateNonetHouse();
    }

    private void initializeGrid(int size) {
        initializeMatrix(size);
        initializeHouses();
    }

    /**
     * Constructs a new Grid object with the specified size.
     * @param size size of the house.
     * @throws InvalidSizeException thrown if size argument is not a perfect nonet and is less than or equal to 0.
     */
    public Grid(int size) throws InvalidSizeException {
        Validator.validateSize(size);
        this.matrix = new Cell[size][size];
        initializeGrid(size);
    }

    /**
     * Returns the size of the grid.
     * @return
     */
    public int getSize() {
        return matrix.length;
    }

    /**
     * Gets the cell within the grid specified by the row and column coordinates.
     * @param row the row coordinate of the cell.
     * @param column the column coordinate of the cell.
     * @return cell at row and column coordinate.
     * @throws GridIndexOutOfBoundsException thrown when row or column arguments are out of bounds of the grid context.
     */
    public Cell getCell(int row, int column) {
        Validator.validateIndex(this, row, HouseType.ROW);
        Validator.validateIndex(this, column, HouseType.COLUMN);
        return matrix[row][column];
    }

    /**
     * Returns an Optional wrapped value contained at the cell specified by the row and column coordinates.
     * @param row row coordinate of the cell.
     * @param column column coordinate of the cell.
     * @return Optional wrapped value.
     * @throw GridIndexOutOfBoundsException thrown when row or column arguments are out of bounds of the grid context.
     */
    public Optional<Integer> getValue(int row, int column) {
        return getCell(row, column).getOptionalValue();
    }
    
    /**
     * Returns the row specified by the index.
     * @param index index of the row.
     * @return
     * @throws GridIndexOutOfBoundsException thrown when row argument is beyond the bounds of the grid context.
     */
    public House getRow(int index) {
        Validator.validateIndex(this, index, HouseType.ROW);
        return rows.get(index);
    }
    
    /**
     * Returns the column specified by the index.
     * @param index index of the column.
     * @return
     * @throws GridIndexOutOfBoundsException thrown when column argument is beyond the bounds of the grid context.
     */
    public House getColumn(int index) {
        Validator.validateIndex(this, index, HouseType.COLUMN);
        return columns.get(index);
    }
    
    /**
     * Returns the nonet specified by the index.
     * @param index index of the nonet.
     * @return
     * @throws GridIndexOutOfBoundsException thrown when nonet argument is beyond the bounds of the grid context.
     */
    public House getNonet(int index) {
        Validator.validateIndex(this, index, HouseType.SQUARE);
        return nonets.get(index);
    }
    
    /**
     * Gets the set of all cells in the grid which have a value.
     * @return set of non-empty cells.
     */
    public Set<Cell> getNonEmptyCells() {
        return streamCells()
            .filter(cell -> !cell.isEmpty())
            .collect(Collectors.toSet());
    }
    
    /**
     * Gets the set of all empty cells in the grid.
     * @return set of all empty cells.
     */
    public Set<Cell> getEmptyCells() {
        return streamCells()
            .filter(cell -> cell.isEmpty())
            .collect(Collectors.toSet());
    }

    /**
     * Gets a stream object of the cells of the grid.
     * @return stream of cells of the grid.
     */
    public Stream<Cell> streamCells() {
        return Arrays.stream(matrix)
        .flatMap(row -> Arrays.stream(row));
    }

    /**
     * Gets the set of all cells in the grid.
     * @return set of cells.
     */
    public Set<Cell> getAllCells() {
        return streamCells().collect(Collectors.toSet());
    }

    /**
     * Gets a set of invalid empty cells in the grid.
     * @return set of invalid empty cells.
     */
    public Set<Cell> getInvalidEmptyCells() {
        return getEmptyCells()
                .stream()
                .filter(cell -> cell.isEmpty() && this.getPermissibleValues(cell).isEmpty())
                .collect(Collectors.toSet());
    }

    /**
     * Checks if the grid has any invalid cells.
     * @return true if the grid has invalid empty cells or false otherwise.
     */
    public boolean hasEmptyAndInvalidCells() {
        for(Cell cell : getEmptyCells()) {
            if(cell.isEmpty() && this.getPermissibleValues(cell).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets all the fixed flag of the non-empty cells to true.
     * @return true if there was at least one non-empty cell whose fixed flag was initially false and was set to true and returns false otherwise.
     */
    public boolean setNonEmptyAsFixed() {
        boolean result = false;
        for(Cell cell : getNonEmptyCells()) {
            if(!cell.isEmpty() && !cell.isFixed()) {
                cell.setFixed(true);
                result = true;
            }
        }
        return result;
    }

    /**
     * Gets the set of numbers allowed for the sudoku.
     * @return
     */
    public Set<Integer> getPermissibleValues() {
        Set<Integer> permissibleValues = null;
        try {
            permissibleValues = Utils.getPermissibleValues(getSize());
        } catch (InvalidSizeException e) {}
        return permissibleValues;
    }

    /**
     * Gets a set of values that are permissible for the cell specified by the coordinates row and column. 
     * This is calculated by taking the union of the set of all values that are present in the cells in the 
     * same row, column or nonet as the cell specified by the coordinates.
     * @param row row index or coordinate of the cell.
     * @param column column index or coordinate of the cell.
     * @return set of values permissible for the cell.
     * @throws GridIndexOutOfBoundsException thrown when row or column argument are out of bounds of grid context.
     */
    public Set<Integer> getPermissibleValues(int row, int column) {
        Validator.validateIndex(this, column, HouseType.COLUMN);
        Validator.validateIndex(this, row, HouseType.ROW);

        Set<Integer> presentValues = Stream.concat(
            Stream.concat(rows.get(row).getPresentValues().stream(), columns.get(column).getPresentValues().stream()),
            nonets.get(Utils.calculateNonet(this, row, column)).getPresentValues().stream())
            .collect(Collectors.toSet());
        Set<Integer> allPermissibleValues = getPermissibleValues();
        allPermissibleValues.removeAll(presentValues);
        return allPermissibleValues;
    }

    /**
     * Gets a set of values that are permissible for the cell. This is calculated by 
     * taking the union of the set of all values that are present in the cells in the 
     * same row, column or nonet as the cell.
     * @param cell cell whose permissible values are to be calculated.
     * @return set of values permissible for the cell.
     * @throws NullPointerException when cell argument is null.
     */
    public Set<Integer> getPermissibleValues(Cell cell) {
        if(cell == null) {
            throw new NullPointerException(NULL_CELL_ARGUMENT_MSG);
        }
        return getPermissibleValues(cell.getRow(), cell.getColumn());
    }

    /**
     * Sets the value for the specified cell.
     * @param cell the cell to the set the value for.
     * @param value the value to set.
     * @throws NullPointerException thrown when cell argument is null.
     * @throws ValueOutOfBoundsException thrown when value is beyond the range of permissible values for the grid.
     * @throws DisallowedValueException thrown when the value is not a permissible value for the cell, although it is a permissble value for the grid.
     */
    public void setValue(Cell cell, int value) throws ValueOutOfBoundsException, DisallowedValueException {
        if(cell == null) {
            throw new NullPointerException(NULL_CELL_ARGUMENT_MSG);
        }
        setValue(cell.getRow(), cell.getColumn(), value);
    }

    /**
     * Sets the value for the cell specified by the row and column arguments.
     * @param row the row coordinate of the cell.
     * @param column the column coordinate of the cell.
     * @param value the value to set.
     * @throws GridIndexOutOfBoundsException thrown when row or column argument are out of bounds of grid context.
     * @throws ValueOutOfBoundsException thrown when value is beyond the range of permissible values for the grid.
     * @throws DisallowedValueException thrown when the value is not a permissible value for the cell, although it is a permissble value for the grid.
     */
    public void setValue(int row, int column, int value) throws ValueOutOfBoundsException, DisallowedValueException {
        Validator.validateIndex(this, column, HouseType.COLUMN);
        Validator.validateIndex(this, row, HouseType.ROW);
        Validator.validateValue(this, value);
        if(!getPermissibleValues(row, column).contains(value))
            throw new DisallowedValueException(row, column, value);
        matrix[row][column].setValue(value);
    }

    /**
     * Removes the value contained in a cell specified by the row and column coordinates and returns the value removed or 0 if the cell was empty.
     * @param row the row coordinate of the cell.
     * @param column the column coordinate of the cell.
     * @return the value of the cell before removing the value or 0 if the cell was empty.
     * @throws GridIndexOutOfBoundsException thrown when row or column arguments are out of bounds of grid context.
     */
    public int removeValue(int row, int column) {
        Validator.validateIndex(this, column, HouseType.COLUMN);
        Validator.validateIndex(this, row, HouseType.ROW);
        return matrix[row][column].removeValue();
    }

    /**
     * Removes the value contained in a cell and returns the value removed or 0 if the cell was empty.
     * @param cell the cell whose value is to be removed.
     * @return the value of the cell before removing the value or 0 if the cell was empty.
     * @throws NullPointerException thrown when cell argument is null.
     */
    public int removeValue(Cell cell) {
        if(cell == null) {
            throw new NullPointerException(NULL_CELL_ARGUMENT_MSG);
        }
        return removeValue(cell.getRow(), cell.getColumn());
    }

    /**
     * Returns the grid as a two-dimensional integer array where the cell coordinates map to the coordinates in the array. 
     * The values of cells are populated in the appropriate cells of the array and empty cells are populated with 0.
     * @return
     */
    public int[][] asArray() {
        int[][] array = new int[getSize()][getSize()];
        for(int row = 0, column = 0; row < array.length; row++) {
            for(column = 0; column < array[row].length; column++) {
                array[row][column] = matrix[row][column].isEmpty() ? 0 : matrix[row][column].getValue();
            }
        }
        return array;
    }

    /**
     * Generates a string representing the Sudoku.
     * @return the sudoku as string with comma as the cell delimiter and new-line as the row delimiter and 0 as the empty cell notation.
     */
    public String asString() {
        return asString(DEFAULT_CELL_DELIMITER, DEFAULT_ROW_DELIMITER, DEFAULT_EMPTY_CELL_NOTATTION);
    }

    /**
     * Generates a string representing the Sudoku.
     * @param cellDelimiter string to separate the cells in a row.
     * @param rowDelimiter string to separate rows in the grid.
     * @return the sudoku as string with the appropriate separators and 0 as the empty cell notation.
     * @throws NullPointerException thrown if cellDelimiter or rowDelimiter arguments are null.
     */
    public String asString(String cellDelimiter, String rowDelimiter) {
        return asString(cellDelimiter, rowDelimiter, DEFAULT_EMPTY_CELL_NOTATTION);
    }

    /**
     * Generates a string representing the Sudoku.
     * @param cellDelimiter string to separate the cells in a row.
     * @param rowDelimiter string to separate rows in the grid.
     * @param emptyCellNotation string to represent empty cells.
     * @return the sudoku as string with the appropriate separators and empty cell notation.
     * @throws NullPointerException thrown if cellDelimiter or rowDelimiter or emptyCellNotation arguments are null.
     */
     public String asString(String cellDelimiter, String rowDelimiter, String emptyCellNotation) {
        if(cellDelimiter == null) {
            throw new NullPointerException("Null cell delimiter passed.");
        }
        if(rowDelimiter == null) {
            throw new NullPointerException("Null row delimiter passed.");
        }
        if(emptyCellNotation == null) {
            throw new NullPointerException("Null empty cell notation passed.");
        }
        StringBuilder stringBuilder = new StringBuilder();
        for(int row = 0, column = 0; row < matrix.length; row++) {
            for(column = 0; column < matrix[row].length; column++) {
                stringBuilder.append(matrix[row][column].isEmpty() ? emptyCellNotation : matrix[row][column].getValue());
                if(column < matrix[row].length - 1) {
                    stringBuilder.append(cellDelimiter);
                }
            }
            stringBuilder.append(rowDelimiter);
        }
        return stringBuilder.toString();
    }
}