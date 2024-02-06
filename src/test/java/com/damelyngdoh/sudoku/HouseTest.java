package com.damelyngdoh.sudoku;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.damelyngdoh.azosudoku.Cell;
import com.damelyngdoh.azosudoku.Grid;
import com.damelyngdoh.azosudoku.House;
import com.damelyngdoh.azosudoku.HouseType;
import com.damelyngdoh.azosudoku.exceptions.GridIndexOutOfBoundsException;
import com.damelyngdoh.azosudoku.exceptions.InvalidSizeException;
import com.damelyngdoh.azosudoku.exceptions.ValueOutOfBoundsException;

@TestInstance(Lifecycle.PER_METHOD)
public class HouseTest {
    
    static final int GRID_ORDER = 9;
    static final int VALID_INDEX = 3;
    static final HouseType VALID_GROUP_TYPE = HouseType.COLUMN;

    static Grid grid;
    static House house;

    static Stream<Integer> validIndicesStream() {
        return IntStream.range(0, GRID_ORDER).boxed();
    }

    static Set<Integer> permissibleValuesSet() {
        return IntStream.rangeClosed(1, GRID_ORDER).boxed().collect(Collectors.toSet());
    }

    @BeforeEach
    void initializeGrid() throws InvalidSizeException, GridIndexOutOfBoundsException, ValueOutOfBoundsException {
        grid = new Grid(GRID_ORDER);
        house = new House(VALID_INDEX, grid, VALID_GROUP_TYPE);
    }

    @Test
    void invalid_grid_constructor_argument_test() {
        assertThrowsExactly(NullPointerException.class, () -> new House(VALID_INDEX, null, VALID_GROUP_TYPE), "House constructor did not throw NullPointerException when null grid is passed as argument.");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, GRID_ORDER})
    void invalid_index_constructor_argument_test(int index) {
        assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> new House(index, grid, VALID_GROUP_TYPE), "House constructor did not throw GridIndexOutOfBoundsException when invalid index is passed as argument.");
    }

    @Test
    void invalid_house_type_constructor_argument_test() {
        assertThrowsExactly(NullPointerException.class, () -> new House(VALID_INDEX, grid, null), "House constructor did not throw NullPointerException when null HouseType is passed as argument.");
    }

    @ParameterizedTest
    @MethodSource("validIndicesStream")
    void valid_index_constructor_argument_test(int index) {
        assertDoesNotThrow(() -> new House(index, grid, VALID_GROUP_TYPE), "House constructor threw GridIndexOutOfBoundsException when valid index is passed as argument.");
    }

    @ParameterizedTest
    @EnumSource(HouseType.class)
    void valid_house_type_constructor_argument_test(HouseType houseType) {
        assertDoesNotThrow(() -> new House(VALID_INDEX, grid, houseType), "House constructor threw exception when valid house type is passed as argument.");
    }

    @Test
    void valid_constructor_arguments_test() {
        assertDoesNotThrow(() -> new House(VALID_INDEX, grid, VALID_GROUP_TYPE), "House constructor threw exception when valid arguments are passed.");
    }

    @Test
    void present_and_missing_values_test() throws GridIndexOutOfBoundsException, ValueOutOfBoundsException {
        Map<Integer,Integer> valuesMap = new HashMap<>(GRID_ORDER);
        valuesMap.put(0, 0);
        valuesMap.put(1, 3);
        valuesMap.put(2, 0);
        valuesMap.put(3, 0);
        valuesMap.put(4, 5);
        valuesMap.put(5, 0);
        valuesMap.put(6, 0);
        valuesMap.put(7, 7);
        valuesMap.put(8, 0);

        Set<Integer> presentValues = valuesMap.values().stream().filter(value -> value != 0).collect(Collectors.toSet());
        Set<Integer> missingValues = new HashSet<>(permissibleValuesSet());
        missingValues.removeAll(presentValues);

        for(Map.Entry<Integer,Integer> entry : valuesMap.entrySet()) {
            if(entry.getValue() == 0)
                house.add(new Cell(grid, entry.getKey(), VALID_INDEX));
            else
                house.add(new Cell(grid, entry.getKey(), VALID_INDEX, entry.getValue()));
        }

        assertIterableEquals(presentValues, house.getPresentValues(), "Present values in the house is not calculated accurately.");
        assertIterableEquals(missingValues, house.getMissingValues(), "Missing values in the house is not calculated accurately.");
    }

    @Test
    void equality_test() {
        assertFalse(house.equals(null), "Equality check with null returns true when it should not.");
        assertTrue(house.equals(house), "Equality check with same object returns false when it shoul dbe reflective.");
        House houseWithDifferentIndex = new House(VALID_INDEX + 1, grid, VALID_GROUP_TYPE);
        assertFalse(house.equals(houseWithDifferentIndex), "Equality check with house with different index returned true.");
        House houseWithDifferentHouseType = new House(VALID_INDEX, grid, HouseType.SQUARE);
        assertFalse(house.equals(houseWithDifferentHouseType), "Equality check with house with different house type returned true.");
    }

    @Test
    void hash_code_test() {
        House sameHouse = new House(VALID_INDEX, grid, VALID_GROUP_TYPE);
        assertEquals(house.hashCode(), sameHouse.hashCode(),"Hash code of house with same identifiers is not equal.");
        House houseWithDifferentIndex = new House(VALID_INDEX + 1, grid, VALID_GROUP_TYPE);
        assertNotEquals(house.hashCode(), houseWithDifferentIndex.hashCode(),"Hash code of house with different index is equal.");
        House houseWithDifferentHouseType = new House(VALID_INDEX, grid, HouseType.SQUARE);
        assertNotEquals(house.hashCode(), houseWithDifferentHouseType.hashCode(),"Hash code of house with different house type is equal.");
        House differentHouseType = new House(VALID_INDEX + 1, grid, HouseType.SQUARE);
        assertNotEquals(house.hashCode(), differentHouseType.hashCode(),"Hash code of house with different identifiers is equal.");
    }
}
