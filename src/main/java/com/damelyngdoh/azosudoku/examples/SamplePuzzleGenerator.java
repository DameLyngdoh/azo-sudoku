package com.damelyngdoh.azosudoku.examples;

import com.damelyngdoh.azosudoku.Grid;
import com.damelyngdoh.azosudoku.exceptions.InvalidSizeException;
import com.damelyngdoh.azosudoku.generators.SimpleSudokuGenerator;
import com.damelyngdoh.azosudoku.generators.SudokuGenerator;

/**
 * Sample program that generates a sudoku puzzle with some specific number of filled cells.
 */
public class SamplePuzzleGenerator {
    
    public static void main(String[] args) throws InvalidSizeException {
        
        final int size = 9;
        final int filledCellsCOunt = 23;
        SudokuGenerator generator = new SimpleSudokuGenerator();
        Grid grid = generator.generate(size, filledCellsCOunt);
        System.out.println(grid.asString(", ", "\n", "*"));
    }
}
