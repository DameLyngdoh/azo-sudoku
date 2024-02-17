package com.damelyngdoh.azosudoku;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.damelyngdoh.azosudoku.exceptions.DisallowedValueException;
import com.damelyngdoh.azosudoku.exceptions.GridIndexOutOfBoundsException;
import com.damelyngdoh.azosudoku.exceptions.InvalidSizeException;
import com.damelyngdoh.azosudoku.exceptions.ValueOutOfBoundsException;

/**
 * Utilities that are used across the package.
 * 
 * @author Dame Lyngdoh
 * @since 1.0.0
 */
public final class Utils {

    /**
     * Gets the set of indices of the nonets which are diagonal in the grid.
     * @param grid the input grid for context.
     * @return set of indices (non-negative integers).
     * @throws NullPointerException when grid argument is null.
     */
    public static Set<Integer> getDiagonalNonets(Grid grid) {
        Validator.validateGrid(grid);
        final int size = grid.getSize();
        final int nonetRootOfSize = Utils.getNonetSize(grid);
        Set<Integer> diagonalNonets = new HashSet<>(nonetRootOfSize);
        diagonalNonets.add(0);
        for(int nonetIndex = nonetRootOfSize + 1; nonetIndex < size; nonetIndex += nonetRootOfSize + 1) {
            diagonalNonets.add(nonetIndex);
        }
        return Set.copyOf(diagonalNonets);
    }
    
    /**
     * Calculates the nonet index a cell specified by the row and column in the context of the grid.
     * @param grid the grid context.
     * @param row row index of the cell.
     * @param column column index of the cell.
     * @return nonet index of the cell (in the range 0 to grid size, exclusively).
     * @throws NullPointerException thrown if grid argument is null.
     * @throws GridIndexOutOfBoundsException thrown if row or column arguments are beyond the range of the grid context.
     */
    public static int calculateNonet(Grid grid, int row, int column) {
        Validator.validateGrid(grid);
        Validator.validateIndex(grid, row, HouseType.ROW);
        Validator.validateIndex(grid, column, HouseType.COLUMN);
        final int rootSize = (int)Math.sqrt(grid.getSize());
        return (rootSize * (row / rootSize)) + (column / rootSize);
    }

    /**
     * Returns the size of a nonet for the grid. The nonet size is the size of each row of a nonet.
     * @param grid the grid to get context.
     * @return
     * @throws NullPointerException thrown if grid argument is null.
     */
    public static int getNonetSize(Grid grid) {
        Validator.validateGrid(grid);
        return (int)Math.sqrt(grid.getSize());
    }

    /**
     * Returns a set of all values permissible for the specified sudoku size.
     * @param size the size of the sudoku
     * @return set of all integers in the range of 1 to size (inclusively).
     * @throws InvalidSizeException thrown when invalid size is passed.
     */
    public static Set<Integer> getPermissibleValues(int size) throws InvalidSizeException {
        Validator.validateSize(size);
        return IntStream.rangeClosed(1, size).boxed().collect(Collectors.toSet());
    }

    /**
     * Gets a random element from the collection. Uses #ThreadLocalRandom.
     * @param collection Collection to pick the element from.
     * @return Random element.
     */
    public static <T> Optional<T> getRandomElement(Collection<T> collection) {
        if(collection == null) {
            throw new NullPointerException("Null collection passed as argument.");
        }
        if(collection.isEmpty()) {
            return Optional.empty();
        }
        int randomIndex = ThreadLocalRandom.current().nextInt(collection.size());
        return collection.stream()
                    .skip(randomIndex)
                    .findFirst();
    }

    /**
     * Gets a subset of elements from the given set where the elements in the subset are chosen at random.
     * @param <T> Type of elements contained in the set.
     * @param set set of elements to create subset from.
     * @param count required size of the subset.
     * @return subset of elements from the provided set.
     * @throws NullPointerException
     * @throws IllegalArgumentException
     */
    public static <T> Set<T> getRandomElements(Set<T> set, int count) {
        if(set == null) {
            throw new NullPointerException("Null set argument passed.");
        }
        if(count < 0 || count > set.size()) {
            throw new IllegalArgumentException("");
        }
        if(set.size() == count) {
            return new HashSet<>(set);
        }

        Set<T> copy = new HashSet<>(set);
        Set<T> result = new HashSet<>(count);
        while(count > 0) {
            Optional<T> element = getRandomElement(copy);
            if(element.isPresent()) {
                result.add(element.get());
                copy.remove(element.get());
                count--;
            }
        }
        return result;
    }

    /**
     * Initializes a grid from the two-dimensional array. 
     * Each cell of the array represents a cell in the grid 
     * and the value in the cell will be populated into the grid cell. 
     * If an array cell contains 0, then an empty cell will be initialized.
     * @param matrix the two-dimensional array.
     * @return a grid with an size equal to the size of the matrix.
     * @throws InvalidSizeException thrown when the size of the input matrix is not a perfect nonet or if the matrix is not nonet.
     * @throws ValueOutOfBoundsException thrown if any of the value in the matrix is beyond the context of the grid to be intialized.
     * @throws DisallowedValueException thrown when a value was set for a cell when it was not permissible. Inidicates that the input matrix is invalid and cannot form a Sudoku.
     */
    public static Grid initializeGrid(int[][] matrix) throws InvalidSizeException, ValueOutOfBoundsException, DisallowedValueException {
        Validator.validateMatrix(matrix);
        final int size = matrix.length;
        Grid grid = new Grid(size);
        for(int row = 0, column = 0; row < size; row++) {
            for(column = 0; column < size; column++) {
                if(matrix[row][column] != 0)
                    grid.setValue(row, column, matrix[row][column]);
            }
        }
        return grid;
    }
}
