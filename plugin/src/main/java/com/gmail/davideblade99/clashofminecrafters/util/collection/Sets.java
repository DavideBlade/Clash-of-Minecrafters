/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.util.collection;

import java.util.Collections;
import java.util.HashSet;

public final class Sets {

    private Sets() {
        throw new IllegalAccessError();
    }

    /**
     * Creates an {@link HashSet} instance initially containing the given elements
     *
     * @param elements The elements that the set should contain
     * @param <E>      Type of elements to insert into the {@link HashSet}
     * @return Return a new hash set with enough capacity to hold {@code elements} without resizing
     */
    public static <E> HashSet<E> newHashSet(final E... elements) {
        final HashSet<E> set = new HashSet<>(elements.length);
        Collections.addAll(set, elements);
        return set;
    }
}
