/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.player.currency;

import javax.annotation.Nonnull;
import java.util.EnumMap;

/**
 * Class representing a player's balance
 *
 * @author DavideBlade
 * @see Currencies
 * @since 3.1.4
 */
public final class Balance {

    private final EnumMap<Currencies, Currency> currencies = new EnumMap<>(Currencies.class);

    /**
     * Initializes the player's balance to 0
     */
    public Balance() {
        currencies.put(Currencies.GOLD, new Gold());
        currencies.put(Currencies.ELIXIR, new Elixir());
        currencies.put(Currencies.GEMS, new Gems());
    }

    /**
     * Initializes the player balance with the specified values
     *
     * @param gold   Gold to be set
     * @param elixir Elixir to be set
     * @param gems   Gems to be set
     */
    public Balance(final int gold, final int elixir, final int gems) {
        currencies.put(Currencies.GOLD, new Gold(gold));
        currencies.put(Currencies.ELIXIR, new Elixir(elixir));
        currencies.put(Currencies.GEMS, new Gems(gems));
    }

    /**
     * @param currency Currency to be obtained
     *
     * @return The amount of a currency that the player owns
     */
    public int getBalance(@Nonnull final Currencies currency) {
        return currencies.get(currency).getBalance();
    }

    /**
     * @return The amount of elixir the player owns
     */
    public int getElixir() {
        return currencies.get(Currencies.ELIXIR).getBalance();
    }

    /**
     * @return The amount of gold the player owns
     */
    public int getGold() {
        return currencies.get(Currencies.GOLD).getBalance();
    }

    /**
     * @return The amount of gems the player owns
     */
    public int getGems() {
        return currencies.get(Currencies.GEMS).getBalance();
    }

    /**
     * Adds the specified amount in the specified currency. If with the amount to be added you exceed limit of a currency, if
     * any, only the amount to reach the maximum will be added. In other words, no overflow can occur.
     *
     * @param currency Currency of the {@code amount}
     * @param amount   Amount to add
     *
     * @return The amount effectively added
     */
    public int addCurrency(@Nonnull final Currencies currency, final int amount) {
        return currencies.get(currency).addCurrency(amount);
    }

    /**
     * Removed the specified amount in the specified currency. If with the amount to be removed goes below 0, only the amount
     * to get to 0 will be removed.
     *
     * @param currency Currency of the {@code amount}
     * @param amount   Amount to remove
     *
     * @return The amount effectively removed
     */
    public int removeCurrency(@Nonnull final Currencies currency, final int amount) {
        return currencies.get(currency).removeCurrency(amount);
    }

    /**
     * Sets the specified amount in the specified currency
     *
     * @param currency Currency of the {@code amount}
     * @param amount   Amount to set
     */
    public void setBalance(@Nonnull final Currencies currency, final int amount) {
        currencies.get(currency).setBalance(amount);
    }

    /**
     * @param currency Currency to translate
     *
     * @return Currency translation as specified in the message files
     */
    @Nonnull
    public String getCurrencyTranslation(@Nonnull final Currencies currency) {
        return currencies.get(currency).getCurrencyTranslation();
    }
}
