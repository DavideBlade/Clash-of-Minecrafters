/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.player.currency;

import javax.annotation.Nonnull;

/**
 * Each class that implements this interface should represent a type of currency
 *
 * @author DavideBlade
 * @see Currencies
 * @since 3.1.4
 */
public interface Currency {

    /**
     * @return The amount of the currency that the player owns
     */
    int getBalance();

    /**
     * Adds the specified amount in the currency. If with the amount to be added the upper limit of a currency
     * ({@link Currencies#max}) is exceeded, only the amount to reach the maximum will be added.
     *
     * @param amount Amount you would like to add
     *
     * @return The amount effectively added
     *
     * @see Currencies#addableAmount(Currencies, int)
     */
    int addCurrency(final int amount);

    /**
     * Removed the specified amount in the currency. If with the amount to be removed falls below the lower limit
     * of a currency ({@link Currencies#min}), only the amount to reach the minimum will be removed.
     *
     * @param amount Amount you would like to remove
     *
     * @return The amount effectively removed
     *
     * @see Currencies#removableAmount(Currencies, int)
     */
    int removeCurrency(final int amount);

    /**
     * Sets the currency amount to the specified value
     *
     * @param amount Amount to set
     */
    void setBalance(final int amount);

    /**
     * @return Currency translation as specified in the message files. If it exists, the singular or plural
     * translation will be returned.
     */
    @Nonnull
    String getCurrencyTranslation();
}
