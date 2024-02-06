package com.damelyngdoh.azosudoku.examples;

import com.damelyngdoh.azosudoku.Grid;
import com.damelyngdoh.azosudoku.exceptions.InvalidSizeException;
import com.damelyngdoh.azosudoku.generators.SimpleSudokuGenerator;
import com.damelyngdoh.azosudoku.generators.SudokuGenerator;

/**
 * Sample program that generates a completely filled sudoku puzzle.
 */
public class SampleCompleteSudokuGenerator {
    
    public static void main(String[] args) throws InvalidSizeException {
        
        final int size = 9;
        SudokuGenerator generator = new SimpleSudokuGenerator();
        Grid grid = generator.generate(size);
        System.out.println("Complete Sudoku");
        System.out.println(grid.asString(", ", "\n"));
    }
}
