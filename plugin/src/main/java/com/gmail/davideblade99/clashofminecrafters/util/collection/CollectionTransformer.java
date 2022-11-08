/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.util.collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Generic class to transform collections from type {@link X} to {@link Y}
 */
abstract class CollectionTransformer<X, Y> {

    CollectionTransformer() {}

    /**
     * Transforms {@link ArrayList} of {@link X} into a {@link ArrayList} of {@link Y}
     *
     * @param list {@link ArrayList} to convert
     * @return {@link ArrayList} obtained by transforming the {@code list} passed with {@link #transform(Object)}
     */
    @Nonnull
    public final ArrayList<Y> transform(@Nonnull final ArrayList<X> list) {
        final ArrayList<Y> newList = new ArrayList<>(list.size());

        for (X from : list)
            newList.add(transform(from));

        return newList;
    }

    /**
     * Transforms {@link HashSet} of {@link X} into a {@link ArrayList} of {@link Y}
     *
     * @param set {@link HashSet} to convert
     * @return {@link ArrayList} obtained by transforming the {@code set} passed with {@link #transform(Object)}
     */
    @Nonnull
    public final ArrayList<Y> transformToList(@Nonnull final HashSet<X> set) {
        final ArrayList<Y> newList = new ArrayList<>(set.size());

        for (X from : set)
            newList.add(transform(from));

        return newList;
    }

    /**
     * Transforms {@link ArrayList} of {@link X} into a {@link HashSet} of {@link Y}
     *
     * @param list {@link ArrayList} to convert
     * @return {@link HashSet} obtained by transforming the {@code list} passed with {@link #transform(Object)}
     */
    @Nonnull
    public final HashSet<Y> transformToSet(@Nonnull final ArrayList<X> list) {
        final HashSet<Y> newSet = new HashSet<>(list.size());

        for (X from : list)
            newSet.add(transform(from));

        return newSet;
    }

    /**
     * Method used when converting from type {@code X} to type {@code Y}.
     * Override this method to specify the transformation to be performed.
     *
     * @param from Element to be converted into the type {@code Y}
     * @return Element converted to type {@code Y}
     */
    @Nullable
    abstract Y transform(@Nullable final X from);
}