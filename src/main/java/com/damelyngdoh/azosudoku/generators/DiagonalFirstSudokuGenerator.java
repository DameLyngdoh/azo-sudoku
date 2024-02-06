package com.damelyngdoh.azosudoku.generators;

import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import com.damelyngdoh.azosudoku.Cell;
import com.damelyngdoh.azosudoku.Grid;
import com.damelyngdoh.azosudoku.House;
import com.damelyngdoh.azosudoku.Utils;
import com.damelyngdoh.azosudoku.Validator;
import com.damelyngdoh.azosudoku.exceptions.InvalidSizeException;
import com.damelyngdoh.azosudoku.exceptions.ValueOutOfBoundsException;

/**
 * Implementation of the SudokuGenerator where the diagonal nonets of the 
 * grid are populated first as the state of any one of the nonets does not 
 * affect the state of the other squres in the diagonal. The rest of the nonets 
 * are populated this step, and the approach is the same as that of the SimpleSudokuGenerator 
 * as the cells are populated one after the other row wise.
 * The recursion in this approach begins after the diagonal nonets are populated and the 
 * values in these nonets are not affected during the recursion process.
 * 
 * @author Dame Lyngdoh
 */
public class DiagonalFirstSudokuGenerator implements SudokuGenerator {

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
     * Gets a shuffeled queue of permissible values.
     * @param permissibleValues set of permissible values to shuffle and return as list/queue.
     * @return queue of permissible in a random size.
     */
    private Queue<Integer> getShuffledPermissibleValues(Set<Integer> permissibleValues) {
        LinkedList<Integer> valuesQueue = new LinkedList<>(permissibleValues);
        Random random = new Random((long)LocalTime.now().get(ChronoField.MILLI_OF_SECOND));
        Collections.shuffle(valuesQueue, random);
        return valuesQueue;
    }

    /**
     * Populates the diagonal nonets with the provided set of permissible values.
     * @param grid the grid context.
     * @param diagonalNonets the set of indices of the nonets that are diagonal nonets.
     * @param permissibleValues the set of permissible values for the grid.
     * @throws ValueOutOfBoundsException thrwon when 
     */
    private void populateDiagonalNonets(Grid grid, Set<Integer> diagonalNonets, Set<Integer> permissibleValues) {
        for(Integer nonetIndex : diagonalNonets) {
            House nonet = grid.getNonet(nonetIndex);
            Queue<Integer> valuesQueue = getShuffledPermissibleValues(permissibleValues);
            for(Cell cell : nonet) {
                setValueForced(cell, valuesQueue.poll());
                cell.setFixed(true);
            }
        }
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
     * Checks if a cell is the last cell of the nonet just to the left or before 
     * the last nonet of the grid.
     * @param cell the cell to check.
     * @return true if the cell is the last cell or false otherwise.
     */
    private boolean isLastCellOfLastNonDiagonalNonet(Cell cell) {
        final int lastIndex = cell.getGrid().getSize() - 1;
        return cell.getRow() == lastIndex && cell.getColumn() == lastIndex - Utils.getNonetSize(cell.getGrid());
    }

    /**
     * Populates the remaining non-diagonal nonets with values. This method calls itself recursively.
     * @param grid the grid context.
     * @param currentCell current cell to populate, taken from the set of cells of the non-diagonal nonets.
     * @return true if with respect to the current cell, the value set did not encounter an invalid empty cell or false otherwise.
     */
    private boolean populateRemainingNonets(Grid grid, Cell currentCell) {
        if(isCellFixedOrNotEmpty(currentCell)) {
            if(isLastCell(currentCell)) {
                return true;
            }
            return populateRemainingNonets(grid, getNextCell(currentCell));
        }
        Set<Integer> permissibleValues = grid.getPermissibleValues(currentCell);
        if(permissibleValues.isEmpty()) {
            return false;
        }
        if(isLastCellOfLastNonDiagonalNonet(currentCell)) {
            setValueForced(currentCell, getRandomValueFromPermissibleValues(permissibleValues));
            return true;
        }

        while(!permissibleValues.isEmpty()) {
            int randomValue = getRandomValueFromPermissibleValues(permissibleValues);
            setValueForced(currentCell, randomValue);
            if(populateRemainingNonets(grid, getNextCell(currentCell))) {
                return true;
            }
            currentCell.removeValue();
            permissibleValues.remove(randomValue);
        }
        return false;
    }

    /**
     * Sets all the fixed flags of the cells of diagonal nonets to false.
     * @param grid the grid context.
     * @param diagonalNonets the indices of the diagonal nonets.
     */
    private void resetFixedStatus(Grid grid, Set<Integer> diagonalNonets) {
        for(Integer nonetIndex : diagonalNonets) {
            for(Cell cell : grid.getNonet(nonetIndex)) {
                cell.setFixed(false);
            }
        }
    }

    @Override
    public Grid generate(int size) throws InvalidSizeException {
        Validator.validateSize(size);
        final Grid grid = new Grid(size);
        final Set<Integer> diagonalNonets = Utils.getDiagonalNonets(grid);
        populateDiagonalNonets(grid, diagonalNonets, Set.copyOf(grid.getPermissibleValues()));
            if(grid.getSize() > 1)
                populateRemainingNonets(grid, grid.getCell(0, Utils.getNonetSize(grid)));
        resetFixedStatus(grid, diagonalNonets);
        return grid;
    }

}
