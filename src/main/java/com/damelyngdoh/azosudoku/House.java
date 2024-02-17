package com.damelyngdoh.azosudoku;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class representing a house (row, column or nonet).
 * 
 * @author Dame Lyngdoh
 * @since 1.0.0
 */
public class House implements List<Cell> {
    
    private final int index;

    private final Grid grid;

    private final HouseType houseType;

    private final List<Cell> cells;

    /**
     * Validates the list of cells to be populated in the house.
     * @param cells list of cells.
     * @throws NullPointerException thrown if null is passed as list.
     * @throws IllegalArgumentException thrown if there exists a null element in the list or there exists a cell in the list with a house index not equal to the index of the house.
     */
    private void validateCellsList(List<Cell> cells) {
        if(cells == null) {
            throw new NullPointerException("Null cell list passed to House constructor.");
        }
        if(cells.size() != grid.getSize()) {
            throw new IllegalArgumentException("Cell list size does not match with house size.");
        }
        final String nullCellMsg = "Null cell found at index %d.";
        final String mismatchHouseIndex = "House index of cell does not match for cell at index %d.";
        for(int i = 0; i < cells.size(); i++) {
            Cell cell = cells.get(i);
            if(cell == null) {
                throw new IllegalArgumentException(String.format(nullCellMsg, i));
            }
            int houseIndex = -1;
            switch (houseType) {
                case ROW:
                    houseIndex = cell.getRow();
                    break;
                case COLUMN:
                    houseIndex = cell.getColumn();
                    break;
                case NONET:
                    houseIndex = cell.getNonet();
                    break;
                default:
                    break;
            }
            if(houseIndex != index) {
                throw new IllegalArgumentException(String.format(mismatchHouseIndex, i));
            }
        }
    }

    /**
     * Constructs a new House with the given properties and cells that belong to the house.
     * @param index index of the house in the grid.
     * @param grid the context grid which the house belongs to.
     * @param houseType type of house.
     * @param cells list of cells to populate in the house.
     */
    public House(int index, Grid grid, HouseType houseType, List<Cell> cells) {
        Validator.validateGrid(grid);
        this.grid = grid;
        Validator.validateIndex(grid, index, houseType);
        this.index = index;
        this.houseType = houseType;
        validateCellsList(cells);
        this.cells = List.copyOf(cells);
    }

    /**
     * @return index of the house in the grid.
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return grid which the house belongs to.
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * @return type of house.
     */
    public HouseType getHouseType() {
        return houseType;
    }

    /**
     * @return set of values present in the row.
     */
    public Set<Integer> getPresentValues() {
        return this.stream()
                    .filter(cell -> !cell.isEmpty())
                    .map(cell -> cell.getValue())
                    .collect(Collectors.toSet());
    }

    /**
     * @return set of missing values from the house.
     */
    public Set<Integer> getMissingValues() {
        final Set<Integer> missingValues = grid.getPermissibleValues();
        missingValues.removeAll(getPresentValues());
        return missingValues;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(!(o instanceof House)) {
            return false;
        }
        House house = (House)o;
        return index == house.getIndex() && houseType.equals(house.getHouseType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, houseType);
    }

    @Override
    public int size() {
        return cells.size();
    }

    @Override
    public boolean isEmpty() {
        return cells.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return cells.contains(o);
    }

    @Override
    public Iterator<Cell> iterator() {
        return cells.iterator();
    }

    @Override
    public Object[] toArray() {
        return cells.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return cells.toArray(a);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return cells.containsAll(c);
    }

    @Override
    public Cell get(int index) {
        return cells.get(index);
    }

    @Override
    public int indexOf(Object o) {
        return cells.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return cells.lastIndexOf(o);
    }

    @Override
    public ListIterator<Cell> listIterator() {
        return cells.listIterator();
    }

    @Override
    public ListIterator<Cell> listIterator(int index) {
        return cells.listIterator(index);
    }

    @Override
    public List<Cell> subList(int fromIndex, int toIndex) {
        return cells.subList(fromIndex, toIndex);
    }

    @Override
    public boolean add(Cell e) {
        throw new UnsupportedOperationException("Unsupported method 'add'");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Unsupported method 'remove'");
    }

    @Override
    public boolean addAll(Collection<? extends Cell> c) {
        throw new UnsupportedOperationException("Unsupported method 'addAll'");
    }

    @Override
    public boolean addAll(int index, Collection<? extends Cell> c) {
        throw new UnsupportedOperationException("Unsupported method 'addAll'");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Unsupported method 'removeAll'");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Unsupported method 'retainAll'");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Unsupported method 'clear'");
    }

    @Override
    public Cell set(int index, Cell element) {
        throw new UnsupportedOperationException("Unsupported method 'set'");
    }

    @Override
    public void add(int index, Cell element) {
        throw new UnsupportedOperationException("Unsupported method 'add'");
    }

    @Override
    public Cell remove(int index) {
        throw new UnsupportedOperationException("Unsupported method 'remove'");
    }
}
