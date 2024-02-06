package com.damelyngdoh.sudoku;

import java.util.Set;

/**
 * @author Dame Lyngdoh
 */
public final class TestUtils {
    
    /**
     * Util method to check if two sets are equal or not by checking if 
     * both sets are subsets of each other.
     * @param <T> type of elements in the sets.
     * @param setA the first set.
     * @param setB the second set.
     * @return true if the sets are equal or false otherwise.
     */
    public static <T> boolean areSetsEqual(Set<T> setA, Set<T> setB) {
        return setA.containsAll(setB) && setB.containsAll(setA);
    }
}
