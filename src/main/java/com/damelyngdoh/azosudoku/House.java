package com.damelyngdoh.azosudoku;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Dame Lyngdoh
 */
public class House extends ArrayList<Cell> {
    
    private final int index;

    private final Grid grid;

    private final HouseType houseType;

    public House(int index, Grid grid, HouseType houseType) {
        Validator.validateGrid(grid);
        this.grid = grid;
        Validator.validateIndex(grid, index, houseType);
        this.index = index;
        this.houseType = houseType;
    }

    public int getIndex() {
        return index;
    }

    public Grid getGrid() {
        return grid;
    }

    public HouseType getHouseType() {
        return houseType;
    }

    public Set<Integer> getPresentValues() {
        return this.stream()
                    .filter(cell -> !cell.isEmpty())
                    .map(cell -> cell.getValue())
                    .collect(Collectors.toSet());
    }

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
}
