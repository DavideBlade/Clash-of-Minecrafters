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
import java.util.UUID;

public final class CollectionUtil {

    private CollectionUtil() {
        throw new IllegalAccessError();
    }

    /**
     * Method that transform {@link ArrayList} of {@link Integer} to {@link ArrayList} of {@link String}
     *
     * @param list {@link ArrayList} of {@link Integer} to be converted into {@link String}
     * @return {@link ArrayList} of {@link String} obtained by converting {@link Integer} using {@link CollectionTransformer#transform(Object)}
     * @see CollectionTransformer
     */
    @Nonnull
    public static ArrayList<String> mapIntegerToStringList(@Nonnull final ArrayList<Integer> list) {

        // Override the transform method to specify the transformation
        final CollectionTransformer<Integer, String> transformer = new CollectionTransformer<Integer, String>() {

            @Nullable
            @Override
            String transform(@Nullable final Integer i) {
                return i == null ? null : i.toString();
            }
        };

        return transformer.transform(list);
    }

    /**
     * Method that transform {@link ArrayList} of {@link UUID} to {@link ArrayList} of {@link String}
     *
     * @param list {@link ArrayList} of {@link UUID} to be converted into {@link String}
     * @return {@link ArrayList} of {@link String} obtained by converting {@link UUID} using {@link UUIDToStringTransformer#transform(UUID)}
     * @see UUIDToStringTransformer
     */
    @Nonnull
    public static ArrayList<String> mapUUIDToStringList(@Nonnull final ArrayList<UUID> list) {
        return new UUIDToStringTransformer().transform(list);
    }

    /**
     * Method that transform {@link HashSet} of {@link UUID} to {@link ArrayList} of {@link String}
     *
     * @param set {@link HashSet} of {@link UUID} to be converted into {@link String}
     * @return {@link ArrayList} of {@link String} obtained by converting {@link UUID} using {@link UUIDToStringTransformer#transform(UUID)}
     * @see UUIDToStringTransformer
     */
    @Nonnull
    public static ArrayList<String> mapUUIDToStringList(@Nonnull final HashSet<UUID> set) {
        return new UUIDToStringTransformer().transformToList(set);
    }

    /**
     * Method that transform {@link ArrayList} of {@link UUID} to {@link HashSet} of {@link String}
     *
     * @param list {@link ArrayList} of {@link UUID} to be converted into {@link String}
     * @return {@link HashSet} of {@link String} obtained by converting {@link UUID} using {@link UUIDToStringTransformer#transform(UUID)}
     * @see UUIDToStringTransformer
     */
    @Nonnull
    public static HashSet<String> mapUUIDToStringSet(@Nonnull final ArrayList<UUID> list) {
        return new UUIDToStringTransformer().transformToSet(list);
    }

    /**
     * Method that transform {@link ArrayList} of {@link String} to {@link ArrayList} of {@link UUID}
     *
     * @param list {@link ArrayList} of {@link String} to be converted into {@link UUID}
     * @return {@link ArrayList} of {@link UUID} obtained by converting {@link String} using {@link StringToUUIDTransformer#transform(String)}
     * @throws IllegalArgumentException If the uuid as string does not conform to the string representation as described in {@link UUID#toString()}
     * @see StringToUUIDTransformer
     */
    @Nonnull
    public static ArrayList<UUID> mapStringToUUIDList(@Nonnull final ArrayList<String> list) {
        return new StringToUUIDTransformer().transform(list);
    }

    /**
     * Method that transform {@link HashSet} of {@link String} to {@link ArrayList} of {@link UUID}
     *
     * @param set {@link HashSet} of {@link String} to be converted into {@link UUID}
     * @return {@link ArrayList} of {@link UUID} obtained by converting {@link String} using {@link StringToUUIDTransformer#transform(String)}
     * @throws IllegalArgumentException If the uuid as string does not conform to the string representation as described in {@link UUID#toString()}
     * @see StringToUUIDTransformer
     */
    @Nonnull
    public static ArrayList<UUID> mapStringToUUIDList(@Nonnull final HashSet<String> set) {
        return new StringToUUIDTransformer().transformToList(set);
    }

    /**
     * Method that transform {@link ArrayList} of {@link String} to {@link HashSet} of {@link UUID}
     *
     * @param list {@link ArrayList} of {@link String} to be converted into {@link UUID}
     * @return {@link HashSet} of {@link UUID} obtained by converting {@link String} using {@link StringToUUIDTransformer#transform(String)}
     * @throws IllegalArgumentException If the uuid as string does not conform to the string representation as described in {@link UUID#toString()}
     * @see StringToUUIDTransformer
     */
    @Nonnull
    public static HashSet<UUID> mapStringToUUIDSet(@Nonnull final ArrayList<String> list) {
        return new StringToUUIDTransformer().transformToSet(list);
    }

    /**
     * {@link UUID} to {@link String} converter
     */
    private final static class UUIDToStringTransformer extends CollectionTransformer<UUID, String> {

        /**
         * Converts a {@link UUID} to a {@link String} using {@link UUID#toString()}
         *
         * @param uuid {@link UUID} to convert
         * @return String representation as described in {@link UUID#toString()}
         * @see CollectionTransformer
         * @see CollectionTransformer#transform(Object)
         */
        @Nullable
        @Override
        String transform(@Nullable final UUID uuid) {
            return uuid == null ? null : uuid.toString();
        }
    }

    /**
     * {@link String} to {@link UUID} converter
     */
    private final static class StringToUUIDTransformer extends CollectionTransformer<String, UUID> {

        /**
         * Converts a {@link String} to a {@link UUID} using {@link UUID#fromString(String)}
         *
         * @param str {@link String} to convert
         * @return UUID obtained from {@link UUID#fromString(String)}
         * @see CollectionTransformer
         * @see CollectionTransformer#transform(Object)
         */
        @Nullable
        @Override
        UUID transform(@Nullable final String str) {
            return str == null ? null : UUID.fromString(str);
        }
    }
}
