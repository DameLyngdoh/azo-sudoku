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
    List<Cell> nonet;
    House house;
    House completeHouse;

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
        house = new House(VALID_INDEX, mockGrid, VALID_GROUP_TYPE, row);
        completeHouse = new House(VALID_INDEX, mockGrid, VALID_GROUP_TYPE, completeRow);
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
}
