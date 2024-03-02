package com.damelyngdoh.sudoku;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.damelyngdoh.azosudoku.Cell;
import com.damelyngdoh.azosudoku.Grid;
import com.damelyngdoh.azosudoku.House;
import com.damelyngdoh.azosudoku.HouseType;
import com.damelyngdoh.azosudoku.exceptions.GridIndexOutOfBoundsException;
import com.damelyngdoh.azosudoku.exceptions.InvalidSizeException;
import com.damelyngdoh.azosudoku.exceptions.ValueOutOfBoundsException;

@TestInstance(Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
public class HouseTest {
    
    static final int GRID_ORDER = 9;
    static final int VALID_INDEX = 3;
    static final HouseType VALID_GROUP_TYPE = HouseType.ROW;
    static final Set<Integer> PERMISSIBLE_VALUES = Set.of(1,2,3,4,5,6,7,8,9);

    @Mock Grid mockGrid;
    List<Cell> row;
    List<Cell> completeRow;
    List<Cell> column;
    List<Cell> completeColumn;
    List<Cell> nonet;
    List<Cell> completeNonet;
    House house;
    House completeHouse;
    House columnHouse;
    House nonetHouse;
    Map<HouseType,House> HOUSES;
    Map<HouseType,House> COMPLETE_HOUSES;

    static Stream<Integer> validIndicesStream() {
        return IntStream.range(0, GRID_ORDER).sorted().boxed();
    }

    static Set<Integer> permissibleValuesSet() {
        return IntStream.rangeClosed(1, GRID_ORDER).boxed().collect(Collectors.toSet());
    }

    @BeforeEach
    void initializeGrid() throws InvalidSizeException, GridIndexOutOfBoundsException, ValueOutOfBoundsException {
        when(mockGrid.getSize()).thenReturn(GRID_ORDER);

        row = List.of(
            new Cell(mockGrid,VALID_INDEX,0),
            new Cell(mockGrid,VALID_INDEX,1),
            new Cell(mockGrid,VALID_INDEX,2),
            new Cell(mockGrid,VALID_INDEX,3),
            new Cell(mockGrid,VALID_INDEX,4),
            new Cell(mockGrid,VALID_INDEX,5),
            new Cell(mockGrid,VALID_INDEX,6),
            new Cell(mockGrid,VALID_INDEX,7),
            new Cell(mockGrid,VALID_INDEX,8));
        
        completeRow = List.of(
            new Cell(mockGrid,VALID_INDEX,0,1),
            new Cell(mockGrid,VALID_INDEX,1,2),
            new Cell(mockGrid,VALID_INDEX,2,3),
            new Cell(mockGrid,VALID_INDEX,3,4),
            new Cell(mockGrid,VALID_INDEX,4,5),
            new Cell(mockGrid,VALID_INDEX,5,6),
            new Cell(mockGrid,VALID_INDEX,6,7),
            new Cell(mockGrid,VALID_INDEX,7,8),
            new Cell(mockGrid,VALID_INDEX,8,9));
        
        column = List.of(
            new Cell(mockGrid,0,VALID_INDEX),
            new Cell(mockGrid,1,VALID_INDEX),
            new Cell(mockGrid,2,VALID_INDEX),
            new Cell(mockGrid,3,VALID_INDEX),
            new Cell(mockGrid,4,VALID_INDEX),
            new Cell(mockGrid,5,VALID_INDEX),
            new Cell(mockGrid,6,VALID_INDEX),
            new Cell(mockGrid,7,VALID_INDEX),
            new Cell(mockGrid,8,VALID_INDEX));
        completeColumn = List.of(
            new Cell(mockGrid,0,VALID_INDEX,1),
            new Cell(mockGrid,1,VALID_INDEX,2),
            new Cell(mockGrid,2,VALID_INDEX,3),
            new Cell(mockGrid,3,VALID_INDEX,4),
            new Cell(mockGrid,4,VALID_INDEX,5),
            new Cell(mockGrid,5,VALID_INDEX,6),
            new Cell(mockGrid,6,VALID_INDEX,7),
            new Cell(mockGrid,7,VALID_INDEX,8),
            new Cell(mockGrid,8,VALID_INDEX,9));
        nonet = List.of(
            new Cell(mockGrid,3,0),
            new Cell(mockGrid,3,1),
            new Cell(mockGrid,3,2),
            new Cell(mockGrid,4,0),
            new Cell(mockGrid,4,1),
            new Cell(mockGrid,4,2),
            new Cell(mockGrid,5,0),
            new Cell(mockGrid,5,1),
            new Cell(mockGrid,5,2));
        completeNonet = List.of(
            new Cell(mockGrid,3,0,1),
            new Cell(mockGrid,3,1,2),
            new Cell(mockGrid,3,2,3),
            new Cell(mockGrid,4,0,4),
            new Cell(mockGrid,4,1,5),
            new Cell(mockGrid,4,2,6),
            new Cell(mockGrid,5,0,7),
            new Cell(mockGrid,5,1,8),
            new Cell(mockGrid,5,2,9));
        house = new House(VALID_INDEX, mockGrid, VALID_GROUP_TYPE, row);
        completeHouse = new House(VALID_INDEX, mockGrid, VALID_GROUP_TYPE, completeRow);
        HOUSES = Map.of(
            HouseType.ROW, house,
            HouseType.COLUMN, new House(VALID_INDEX, mockGrid, HouseType.COLUMN, column),
            HouseType.NONET, new House(VALID_INDEX, mockGrid, HouseType.NONET, nonet)
        );

        COMPLETE_HOUSES = Map.of(
            HouseType.ROW, new House(VALID_INDEX, mockGrid, VALID_GROUP_TYPE, completeRow),
            HouseType.COLUMN, new House(VALID_INDEX, mockGrid, HouseType.COLUMN, completeColumn),
            HouseType.NONET, new House(VALID_INDEX, mockGrid, HouseType.NONET, completeNonet)
        );
    }

    @Test
    void invalid_grid_constructor_argument_test() {
        assertThrowsExactly(NullPointerException.class, () -> new House(VALID_INDEX, null, VALID_GROUP_TYPE, row), "House constructor did not throw NullPointerException when null grid is passed as argument.");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, GRID_ORDER})
    void invalid_index_constructor_argument_test(int index) {
        assertThrowsExactly(GridIndexOutOfBoundsException.class, () -> new House(index, mockGrid, VALID_GROUP_TYPE, row), "House constructor did not throw GridIndexOutOfBoundsException when invalid index is passed as argument.");
    }

    @Test
    void invalid_house_type_constructor_argument_test() {
        assertThrowsExactly(NullPointerException.class, () -> new House(VALID_INDEX, mockGrid, null, row), "House constructor did not throw NullPointerException when null HouseType is passed as argument.");
    }

    @Test
    void valid_house_type_constructor_argument_test() {
        assertDoesNotThrow(() -> new House(VALID_INDEX, mockGrid, HouseType.ROW, row), "House constructor threw exception when valid row house type is passed as argument.");
        assertDoesNotThrow(() -> new House(VALID_INDEX, mockGrid, HouseType.COLUMN, column), "House constructor threw exception when valid column house type is passed as argument.");
        assertDoesNotThrow(() -> new House(VALID_INDEX, mockGrid, HouseType.NONET, nonet), "House constructor threw exception when valid nonet house type is passed as argument.");
    }

    @Test
    void constructor_null_cellList_test() {
        assertThrowsExactly(NullPointerException.class, () -> new House(VALID_INDEX, mockGrid, VALID_GROUP_TYPE, null), "House constructor did not throw NullPointerException when null cellList argument is passed.");
    }

    @Test
    void constructor_invalid_size_cellList_test() {
        final List<Cell> cellList = List.of(
            new Cell(mockGrid,VALID_INDEX,0),
            new Cell(mockGrid,VALID_INDEX,1),
            new Cell(mockGrid,VALID_INDEX,2));
            assertThrowsExactly(IllegalArgumentException.class, () -> new House(VALID_INDEX, mockGrid, VALID_GROUP_TYPE, cellList), "House constructor did not throw IllegalArgumentException when cellList with size not equal to grid size argument is passed.");
    }

    @Test
    void constructor_null_element_cellList_test() {
        final List<Cell> cellList = new ArrayList<>();
        cellList.add(null);
        cellList.add(null);
        cellList.add(null);
        cellList.add(null);
        cellList.add(null);
        cellList.add(null);
        cellList.add(null);
        cellList.add(null);
        cellList.add(null);
        cellList.add(null);
        assertThrowsExactly(IllegalArgumentException.class, () -> new House(VALID_INDEX, mockGrid, VALID_GROUP_TYPE, cellList), "House constructor did not throw IllegalArgumentException when cellList with null elements argument is passed.");
    }

    @Test
    void constructor_mismatch_index_cellList_test() {
        final List<Cell> cellList = List.of(
            new Cell(mockGrid,VALID_INDEX,0),
            new Cell(mockGrid,VALID_INDEX,1),
            new Cell(mockGrid,VALID_INDEX,2),
            new Cell(mockGrid,VALID_INDEX,3),
            new Cell(mockGrid,VALID_INDEX,4),
            new Cell(mockGrid,VALID_INDEX,5),
            new Cell(mockGrid,VALID_INDEX,6),
            new Cell(mockGrid,5,7),
            new Cell(mockGrid,VALID_INDEX,8));
            assertThrowsExactly(IllegalArgumentException.class, () -> new House(VALID_INDEX, mockGrid, VALID_GROUP_TYPE, cellList), "House constructor did not throw IllegalArgumentException when cellList with element(s) of different house index argument is passed.");
    }

    @Test
    void valid_constructor_arguments_test() {
        assertDoesNotThrow(() -> new House(VALID_INDEX, mockGrid, VALID_GROUP_TYPE, row), "House constructor threw exception when valid arguments are passed.");
    }

    @Test
    void getPresentValues_test() throws GridIndexOutOfBoundsException, ValueOutOfBoundsException {
        final int addedValue = 1;
        assertTrue(house.getPresentValues().isEmpty(), "getPresentValues is not empty with all empty cells in a row.");
        assertEquals(completeRow.size(), completeHouse.getPresentValues().size(), "getPresentValues returned set with different size for complete row.");
        house.get(0).setValue(addedValue);
        assertFalse(house.getPresentValues().isEmpty(), "getPresentValues returned empty set after setting the value of one of the cells of the house.");
        assertTrue(house.getPresentValues().contains(addedValue), "getPresentValues does not contain the value which was set to one of the cells.");
        assertEquals(1, house.getPresentValues().size(), "getPresentValues returned incorrect set size.");
    }

    @Test
    void getMissingValues_test() throws ValueOutOfBoundsException {
        final int addedValue = 1;
        when(mockGrid.getPermissibleValues()).thenReturn(permissibleValuesSet());
        assertFalse(house.getMissingValues().isEmpty(), "getMissingValues returned true for emptiness check on empty row.");
        assertTrue(completeHouse.getMissingValues().isEmpty(), "getMissingValues returned true for emptiness check on complete row.");
        
        when(mockGrid.getPermissibleValues()).thenReturn(permissibleValuesSet());
        assertEquals(permissibleValuesSet(), house.getMissingValues(), "getMissingValues returned different missing values set on empty row.");

        when(mockGrid.getPermissibleValues()).thenReturn(permissibleValuesSet());
        house.get(0).setValue(addedValue);
        Set<Integer> oneLessElementSet = permissibleValuesSet();
        oneLessElementSet.remove(addedValue);
        assertEquals(oneLessElementSet, house.getMissingValues(), "getMisingValues returned incorrect missing values set.");
    }

    @Test
    void equality_test() {
        assertFalse(house.equals(null), "Equality check with null returns true when it should not.");
        assertTrue(house.equals(house), "Equality check with same object returns false when it shoul dbe reflective.");
        List<Cell> differentRow = List.of(
            new Cell(mockGrid,4,0),
            new Cell(mockGrid,4,1),
            new Cell(mockGrid,4,2),
            new Cell(mockGrid,4,3),
            new Cell(mockGrid,4,4),
            new Cell(mockGrid,4,5),
            new Cell(mockGrid,4,6),
            new Cell(mockGrid,4,7),
            new Cell(mockGrid,4,8));
        House houseWithDifferentIndex = new House(4, mockGrid, VALID_GROUP_TYPE, differentRow);
        assertFalse(house.equals(houseWithDifferentIndex), "Equality check with house with different index returned true.");
        House houseWithDifferentHouseType = new House(VALID_INDEX, mockGrid, HouseType.NONET, nonet);
        assertFalse(house.equals(houseWithDifferentHouseType), "Equality check with house with different house type returned true.");
    }

    @Test
    void hash_code_test() {
        House sameHouse = new House(VALID_INDEX, mockGrid, VALID_GROUP_TYPE, row);
        assertEquals(house.hashCode(), sameHouse.hashCode(),"Hash code of house with same identifiers is not equal.");
        List<Cell> differentRow = List.of(
            new Cell(mockGrid,4,0),
            new Cell(mockGrid,4,1),
            new Cell(mockGrid,4,2),
            new Cell(mockGrid,4,3),
            new Cell(mockGrid,4,4),
            new Cell(mockGrid,4,5),
            new Cell(mockGrid,4,6),
            new Cell(mockGrid,4,7),
            new Cell(mockGrid,4,8));
        House houseWithDifferentIndex = new House(4, mockGrid, VALID_GROUP_TYPE, differentRow);
        assertNotEquals(house.hashCode(), houseWithDifferentIndex.hashCode(),"Hash code of house with different index is equal.");
        assertNotEquals(house.hashCode(), nonet.hashCode(),"Hash code of house with different house type is equal.");
    }

    @ParameterizedTest
    @EnumSource(HouseType.class)
    void getFixedCells_contains_no_fixed_cell_test(final HouseType houseType) {
        assertTrue(HOUSES.get(houseType).getFixedCells().isEmpty(), "getFixedCells returned a non-empty set with house that does not contain fixed cells.");
    }

    @ParameterizedTest
    @EnumSource(HouseType.class)
    void getFixedCells_contains_fixed_cell_test(final HouseType houseType) {
        final int fixedCellIndex = 4;
        final House house = HOUSES.get(houseType);
        final Cell fixedCell = house.get(fixedCellIndex);
        fixedCell.setFixed(true);
        assertTrue(house.getFixedCells().contains(fixedCell), "getFixedCells returned list which does not contain fixed cell.");
        assertEquals(1, house.getFixedCells().size(), "getFixedCells returned list which has incorrect number of elements.");
    }

    @ParameterizedTest
    @EnumSource(HouseType.class)
    void getNonFixedCells_contains_no_fixed_cell_test(final HouseType houseType) {
        House house = HOUSES.get(houseType);
        assertEquals(house.size(), house.getNonFixedCells().size(), "getNonFixedCells returned a list with incorrect number of elements.");
    }

    @ParameterizedTest
    @EnumSource(HouseType.class)
    void getNonFixedCells_contains_fixed_cell_test(final HouseType houseType) {
        final int fixedCellIndex = 4;
        final House house = HOUSES.get(houseType);
        final Cell fixedCell = house.get(fixedCellIndex);
        fixedCell.setFixed(true);
        assertFalse(house.getNonFixedCells().contains(fixedCell), "getNonFixedCells returned list which contains fixed cell.");
        assertEquals(house.size() - 1, house.getNonFixedCells().size(), "getNonFixedCells returned list which has incorrect number of elements.");
    }

    @ParameterizedTest
    @EnumSource(HouseType.class)
    void clearValues_contains_no_values_test(final HouseType houseType) {
        final House house = HOUSES.get(houseType);
        assertFalse(house.clearValues(), "clearValues returned true for house with no values set.");
    }

    @ParameterizedTest
    @EnumSource(HouseType.class)
    void clearValues_contains_values_test(final HouseType houseType) {
        final House house = COMPLETE_HOUSES.get(houseType);
        assertTrue(house.clearValues(), "clearValues returned false for house with all values set.");
        for(Cell cell : house) {
            assertTrue(cell.isEmpty(), "clearValues did not clear the value of a cell.");
        }
    }

    @ParameterizedTest
    @EnumSource(HouseType.class)
    void setFixed_contains_no_fixed_cells_test(final HouseType houseType) {
        final House house = HOUSES.get(houseType);
        assertTrue(house.setFixed(), "setFixed returned false for house with no fixed cells.");
        for(Cell cell : house) {
            assertTrue(cell.isFixed(), "setFixed did not set the fixed flag of a cell.");
        }
    }

    @ParameterizedTest
    @EnumSource(HouseType.class)
    void setFixed_contains_fixed_cells_test(final HouseType houseType) {
        final House house = HOUSES.get(houseType);
        house.forEach(cell -> cell.setFixed(true));
        assertFalse(house.setFixed(), "setFixed returned true for house with fixed cells.");
        for(Cell cell : house) {
            assertTrue(cell.isFixed(), "setFixed changed the fixed flag of an already fixed cell.");
        }
    }

    @ParameterizedTest
    @EnumSource(HouseType.class)
    void asString_test(final HouseType houseType) {
        final House emptyHouse = HOUSES.get(houseType);
        final House completeHouse = COMPLETE_HOUSES.get(houseType);
        final String emptyHouseString = "0,0,0,0,0,0,0,0,0";
        final String completeHouseString = "1,2,3,4,5,6,7,8,9";

        assertEquals(emptyHouseString, emptyHouse.asString(), "asString returned incorrect string with empty house.");
        assertEquals(completeHouseString, completeHouse.asString(), "asString returned incorrect string with complete house.");
    }

    @ParameterizedTest
    @EnumSource(HouseType.class)
    void asString_with_params_test(final HouseType houseType) {
        final House emptyHouse = HOUSES.get(houseType);
        final House completeHouse = COMPLETE_HOUSES.get(houseType);
        final String emptyCellNotation = "X";
        final String delimiter = "#";
        final String emptyHouseString = "X#X#X#X#X#X#X#X#X";
        final String completeHouseString = "1#2#3#4#5#6#7#8#9";

        assertEquals(emptyHouseString, emptyHouse.asString(delimiter,emptyCellNotation), "asString with parameters returned incorrect string with empty house.");
        assertEquals(completeHouseString, completeHouse.asString(delimiter,emptyCellNotation), "asString with parameters returned incorrect string with complete house.");
    }
}
