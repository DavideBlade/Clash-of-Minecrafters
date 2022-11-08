/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.util.bukkit;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;
import java.util.Locale;

public final class ColorUtil {

    private final static char COLOR_CHAR = '&';

    private ColorUtil() {
        throw new IllegalAccessError();
    }

    @Contract("null -> null; !null -> !null")
    @Nullable
    public static String colour(@Nullable final String toTranslate) {
        if (toTranslate == null || toTranslate.isEmpty())
            return toTranslate;

        return ChatColor.translateAlternateColorCodes(COLOR_CHAR, toTranslate);
    }

    @Nullable
    public static Color matchColor(@Nullable final String rgb) {
        if (rgb == null || rgb.isEmpty())
            return null;

        final String[] split = rgb.split(",");
        if (split.length != 3) // Non RGB format
            return null;

        final int r, g, b;
        try {
            r = Integer.parseInt(split[0].trim());
            g = Integer.parseInt(split[1].trim());
            b = Integer.parseInt(split[2].trim());
        } catch (final NumberFormatException ignored) {
            return null; // Not integer
        }

        // Non RGB format
        if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255)
            return null;

        return Color.fromRGB(r, g, b);
    }

    @Nullable
    public static DyeColor matchDyeColor(@Nullable final String color) {
        if (color == null || color.isEmpty())
            return null;

        try {
            return DyeColor.valueOf(color.trim().toUpperCase(Locale.ENGLISH).replace(" ", "_"));
        } catch (final IllegalArgumentException ignored) {
            return null; // Wrong color
        }
    }
}
