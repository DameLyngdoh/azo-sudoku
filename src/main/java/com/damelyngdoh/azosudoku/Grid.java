package com.damelyngdoh.azosudoku;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.damelyngdoh.azosudoku.exceptions.DisallowedValueException;
import com.damelyngdoh.azosudoku.exceptions.GridIndexOutOfBoundsException;
import com.damelyngdoh.azosudoku.exceptions.InvalidSizeException;
import com.damelyngdoh.azosudoku.exceptions.InvalidSudokuException;
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
     * Flag indicating if every value set must be verified actively.
     */
    private boolean activeVerification = true;

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
     * @return map of three type of houses in the grid.
     */
    private Map<HouseType,List<House>> generateHouses() {
        /**
         * Initializing temporary lists to contain the cells.
         */
        final List<List<Cell>> tempRows = new ArrayList<>(matrix.length);
        final List<List<Cell>> tempColumns = new ArrayList<>(matrix.length);
        final List<List<Cell>> tempNonets = new ArrayList<>(matrix.length);
        IntStream.range(0, matrix.length).forEach(i -> {
            tempRows.add(new ArrayList<>(matrix.length));
            tempColumns.add(new ArrayList<>(matrix.length));
            tempNonets.add(new ArrayList<>(matrix.length));
        });

        /**
         * Iterating over all the cells in the grid 
         * and populating each cell into the respective 
         * temporary lists.
         */
        streamCells().forEach(cell -> {
            tempRows.get(cell.getRow()).add(cell);
            tempColumns.get(cell.getColumn()).add(cell);
            tempNonets.get(cell.getNonet()).add(cell);
        });

        /**
         * Initializing the houses and populating the houses 
         * from the temporary lists mentioned above.
         */
        final List<House> rows = new ArrayList<>(matrix.length);
        final List<House> columns = new ArrayList<>(matrix.length);
        final List<House> nonets = new ArrayList<>(matrix.length);
        IntStream.range(0, matrix.length).forEach(i -> {
            rows.add(new House(i, this, HouseType.ROW, tempRows.get(i)));
            columns.add(new House(i, this, HouseType.COLUMN, tempColumns.get(i)));
            nonets.add(new House(i, this, HouseType.NONET, tempNonets.get(i)));
        });

        /**
         * Creating and returning map of the three types of houses.
         */
        return Map.of(HouseType.ROW, List.copyOf(rows), HouseType.COLUMN, List.copyOf(columns), HouseType.NONET, List.copyOf(nonets));
    }

    /**
     * Constructs a new Grid object with the specified size.
     * @param size size of the house.
     * @throws InvalidSizeException thrown if size argument is not a perfect nonet and is less than or equal to 0.
     */
    public Grid(int size) throws InvalidSizeException {
        Validator.validateSize(size);
        this.matrix = new Cell[size][size];
        initializeMatrix(size);
        Map<HouseType,List<House>> houses = generateHouses();
        this.rows = houses.get(HouseType.ROW);
        this.columns = houses.get(HouseType.COLUMN);
        this.nonets = houses.get(HouseType.NONET);
    }

    /**
     * Returns the size of the grid.
     * @return
     */
    public int getSize() {
        return matrix.length;
    }

    /**
     * @return the active verfication state of the grid.
     */
    public boolean getActiveVerification() {
        return activeVerification;
    }

    /**
     * Sets the active verification flag.
     * In case if the active verification is set ot true, a grid validation is performed before setting the flag.
     * @param activeVerification the new state of the active verification flag.
     * @throws InvalidSudokuException thrown when either an empty invalid cell or conflicting values in a house are found.
     */
    public void setActiveVerification(boolean activeVerification) throws InvalidSudokuException {
        if(activeVerification) {
            validateGrid();
        }
        this.activeVerification = activeVerification;
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
        Validator.validateIndex(this, index, HouseType.NONET);
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
     * @return an immutable map of emtpy cell to set of permissible values for the cell.
     */
    public Map<Cell,Set<Integer>> getEmptyCellsWithPermissibleValues() {
        return getEmptyCells()
            .stream()
            .collect(Collectors.toUnmodifiableMap(
                cell -> cell,
                cell -> getPermissibleValues(cell)));
    }

    /**
     * @return a navigable sorted map of emtpy cell to set of permissible values for the cell.
     */
    public NavigableMap<Cell,Set<Integer>> getSortedEmptyCellsWithPermissibleValues() {
        final Map<Cell,Set<Integer>> emptyCells = getEmptyCellsWithPermissibleValues();
        final NavigableMap<Cell,Set<Integer>> sortedMap = new TreeMap<>((key1, key2) -> {
            int size1 = emptyCells.get(key1).size();
            int size2 = emptyCells.get(key2).size();
            if(size1 > size2) {
                return 1;
            }
            if(size1 < size2) {
                return -1;
            }
            int row1 = key1.getRow();
            int row2 = key2.getRow();
            if(row1 > row2) {
                return 1;
            }
            if(row1 < row2) {
                return -1;
            }

            int column1 = key1.getColumn();
            int column2 = key2.getColumn();
            if(column1 > column2) {
                return 1;
            }
            return 0;
        });
        sortedMap.putAll(emptyCells);
        return sortedMap;
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
        if(activeVerification) {
            if(!getPermissibleValues(row, column).contains(value))
                throw new DisallowedValueException(row, column, value);
        }
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
     * Validates the grid by checking that there exists no:
     * <ul>
     *  <li>invalid empty cells</li>
     *  <li>confilcting values in a house</li>
     * </ul>
     * @throws InvalidSudokuException thrown when either an empty invalid cell or conflicting values in a house are found.
     */
    public void validateGrid() throws InvalidSudokuException {
        if(hasEmptyAndInvalidCells()) {
            throw new InvalidSudokuException("Grid contains invalid empty cells.");
        }
        Map<Integer,Set<Cell>> valuesMap = getNonEmptyCells().stream().collect(Collectors.groupingBy(Cell::getValue, Collectors.toSet()));
        for(Map.Entry<Integer,Set<Cell>> entry : valuesMap.entrySet()) {
            Queue<Cell> queue = new ArrayDeque<>(entry.getValue());
            for(Cell currentCell = queue.poll(); !queue.isEmpty(); currentCell = queue.poll()) {
                for(Cell remainingCell : queue) {
                    if(currentCell.isRelated(remainingCell)) {
                        throw new InvalidSudokuException("Grid contains conflicting values.");
                    }
                }
            }
        }
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
