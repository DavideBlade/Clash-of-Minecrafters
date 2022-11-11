/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class EnumUtil {

    private EnumUtil() {
        throw new IllegalAccessError();
    }

    /**
     * Gets the enum for the class
     *
     * @param <E>         the type of the enumeration
     * @param value       the enum name
     * @param enumClass   the class of the enum to query
     * @param defaultEnum the default enum
     *
     * @return the enum, {@code defaultEnum} if not found
     */
    @Nullable
    public static <E extends Enum<E>> E getEnum(@Nullable final String value, @Nonnull final Class<E> enumClass, @Nullable final E defaultEnum) {
        try {
            return Enum.valueOf(enumClass, value);
        } catch (final Exception ignored) {
            return defaultEnum;
        }
    }

    /**
     * Gets the enum for the class, ignoring uppercase and lowercase letters
     *
     * @param <E>         the type of the enumeration
     * @param value       the enum name
     * @param enumClass   the class of the enum to query
     * @param defaultEnum the default enum
     *
     * @return the enum, {@code defaultEnum} if not found
     */
    @Nullable
    public static <E extends Enum<E>> E getEnumIgnoreCase(@Nullable final String value, @Nonnull final Class<E> enumClass, @Nullable final E defaultEnum) {
        if (value == null)
            return defaultEnum;

        for (E e : enumClass.getEnumConstants()) {
            if (e.name().equalsIgnoreCase(value))
                return e;
        }

        return defaultEnum;
    }

    public static <E extends Enum<E>> boolean isInEnum(@Nullable final String value, @Nonnull final Class<E> enumClass) {
        return getEnum(value, enumClass, null) != null;
    }

    public static <E extends Enum<E>> boolean isInEnumIgnoreCase(@Nullable final String value, @Nonnull final Class<E> enumClass) {
        return getEnumIgnoreCase(value, enumClass, null) != null;
    }
}
