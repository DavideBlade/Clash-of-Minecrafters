/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.util.collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Class that deals with extracting random elements in the collection
 * passed to the constructor avoiding duplicates.
 * More specifically, at each extraction with the {@link #getRandomElement()} method
 * a random element is chosen and removed from the collection so that from the next call
 * it can no longer be selected.
 * The collection can contain duplicate elements or {@code null} values if desired.
 * The collection passed to the constructor is not modified.
 *
 * @param <T> Type of elements in the collection
 */
public final class RandomItemExtractor<T> {

    private final List<T> list;
    private final Random random;

    /**
     * @param array Array from which to extract elements
     *
     * @throws IllegalArgumentException If the array is empty, and therefore there are no elements to be extracted
     */
    public RandomItemExtractor(@Nonnull final T[] array) {
        this(Arrays.asList(array));
    }

    /**
     * @param list List from which to extract elements
     *
     * @throws IllegalArgumentException If the list is empty, and therefore there are no elements to be extracted
     */
    public RandomItemExtractor(@Nonnull final List<T> list) {
        if (list.isEmpty())
            throw new IllegalArgumentException("Cannot extract items from an empty list");

        this.list = new ArrayList<>(list);
        this.random = new Random();
    }

    /**
     * Extracts a random element from the collection and removes it
     *
     * @return The removed element (which can be null)
     *
     * @throws NoSuchElementException If the collection is empty and there are no other elements to extract
     */
    @Nullable
    public T getRandomElement() {
        if (list.isEmpty())
            throw new NoSuchElementException("There are no other elements to be extracted");

        return list.remove(random.nextInt(list.size()));
    }

    /**
     * @return true if there are no other elements to extract, false otherwise
     */
    public boolean isEmpty() {
        return list.isEmpty();
    }
}
