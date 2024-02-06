package com.damelyngdoh.azosudoku.examples;

import com.damelyngdoh.azosudoku.Grid;
import com.damelyngdoh.azosudoku.Utils;
import com.damelyngdoh.azosudoku.exceptions.DisallowedValueException;
import com.damelyngdoh.azosudoku.exceptions.InvalidSizeException;
import com.damelyngdoh.azosudoku.exceptions.InvalidSudokuException;
import com.damelyngdoh.azosudoku.exceptions.ValueOutOfBoundsException;
import com.damelyngdoh.azosudoku.solvers.SimpleSudokuSolver;
import com.damelyngdoh.azosudoku.solvers.SudokuSolver;

/**
 * Sample program that initialzes a grid from a 2D integer array and 
 * solves the grid using the {@link com.damelyngdoh.azosudoku.solvers.SimpleSudokuSolver SimpleSudokuSolver}.
 * 
 */
public class SampleSudokuSolver {
    
    public static void main(String[] args) throws InvalidSizeException, ValueOutOfBoundsException, DisallowedValueException, InvalidSudokuException {
        final int[][] sudoku = new int[][]
        {
            { 0, 6, 2, 0, 8, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 7, 0, 0, 3, 8 },
            { 0, 3, 0, 0, 9, 0, 0, 0, 0 },
            { 0, 0, 3, 0, 0, 0, 0, 7, 0 },
            { 0, 0, 0, 1, 0, 0, 0, 0, 4 },
            { 0, 9, 0, 7, 0, 8, 0, 0, 0 },
            { 0, 8, 1, 9, 6, 5, 7, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 7, 0, 0, 2, 0, 0, 0 }
        };

        Grid grid = Utils.initializeGrid(sudoku);
        SudokuSolver solver = new SimpleSudokuSolver();

        System.out.println("Unsolved Sudoku");
        System.out.println(grid.asString());

        System.out.println("\nSolved Sudoku");
        solver.solve(grid);
        System.out.println(grid.asString());
    }
}
