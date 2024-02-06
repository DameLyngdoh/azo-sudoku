package com.damelyngdoh.sudoku;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.damelyngdoh.azosudoku.Grid;
import com.damelyngdoh.azosudoku.Utils;
import com.damelyngdoh.azosudoku.exceptions.DisallowedValueException;
import com.damelyngdoh.azosudoku.exceptions.InvalidSizeException;
import com.damelyngdoh.azosudoku.exceptions.InvalidSudokuException;
import com.damelyngdoh.azosudoku.exceptions.ValueOutOfBoundsException;
import com.damelyngdoh.azosudoku.solvers.SimpleSudokuSolver;

public class SimpleSudokuSolverTest {

    static final int[][] PARTIALLY_VALID_MATRIX = {
        {0,0,1,0,4,0,0,0,2},
        {0,5,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,9},
        {0,4,0,0,0,0,2,9,0},
        {0,0,6,0,0,0,0,0,0},
        {0,0,0,0,0,3,0,0,0},
        {5,0,7,0,2,8,0,3,0},
        {4,3,2,0,0,0,0,6,0},
        {0,0,0,0,0,0,5,0,0}
    };
    static final int[][] PARTIALLY_INVALID_MATRIX = {
        {0,2,3,4,5,6,7,8,9},
        {1,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0}
    };
    
    Grid validGrid;
    Grid invalidGrid;
    SimpleSudokuSolver solver;

    @BeforeEach
    void initializeSolver() throws InvalidSizeException, ValueOutOfBoundsException, DisallowedValueException {
        solver = new SimpleSudokuSolver();
        validGrid = Utils.initializeGrid(PARTIALLY_VALID_MATRIX);
        invalidGrid = Utils.initializeGrid(PARTIALLY_INVALID_MATRIX);
    }

    @Test
    void invalid_grid_argument_solve_test() {
        assertThrows(NullPointerException.class, () -> solver.solve(null), "solve did not throw NullPointerException when null grid argument is passed.");
    }

    @Test
    void invalid_sudoku_argument_solve_test() {
        assertThrowsExactly(InvalidSudokuException.class, () -> solver.solve(invalidGrid), "solve did not throw InvalidSudokuException when invalid grid argument is passed.");
    }

    @Test
    void valid_grid_argument_solve_test() {
        assertDoesNotThrow(() -> solver.solve(validGrid), "solve threw an exception when valid grid/sudoku argument is passed.");
        assertTrue(validGrid.getEmptyCells().isEmpty(), "solve did not solve the grid as there are some empty cells left in the grid.");
    }
}
