/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters;

import com.gmail.davideblade99.clashofminecrafters.util.EnumUtil;

import javax.annotation.Nullable;
import java.util.Locale;

public enum Currency {
    GEMS, GOLD, ELIXIR;

    @Override
    public String toString() {
        final String name = super.name();

        return name.charAt(0) + name.substring(1).toLowerCase(Locale.ENGLISH);
    }

    public static boolean isCorrectCurrency(@Nullable final String currency) {
        return EnumUtil.isInEnumIgnoreCase(currency, Currency.class);
    }

    @Nullable
    public static Currency matchCurrency(@Nullable final String currency) {
        return matchOrDefault(currency, null);
    }

    @Nullable
    private static Currency matchOrDefault(@Nullable final String currency, @Nullable final Currency def) {
        return EnumUtil.getEnumIgnoreCase(currency, Currency.class, def);
    }
}
