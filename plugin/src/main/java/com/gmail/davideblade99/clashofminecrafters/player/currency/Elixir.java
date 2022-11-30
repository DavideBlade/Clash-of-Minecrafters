/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.player.currency;

import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;

import javax.annotation.Nonnull;

/**
 * Class representing the {@link Currencies#ELIXIR} currency
 *
 * @author DavideBlade
 * @since v3.1.4
 */
public final class Elixir implements Currency {

    private int elixir;

    /**
     * Initialize to 0 the amount of elixir
     */
    public Elixir() {
        this.elixir = 0;
    }

    /**
     * Initializes elixir to the specified value
     *
     * @param initialBalance Amount of elixir to be set
     */
    public Elixir(final int initialBalance) {
        this.elixir = initialBalance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBalance() {
        return this.elixir;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int addCurrency(final int amount) {
        final int addableAmount = Currencies.addableAmount(Currencies.ELIXIR, this.elixir, amount);
        this.elixir += addableAmount;
        return addableAmount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int removeCurrency(final int amount) {
        final int removableAmount = Currencies.removableAmount(Currencies.ELIXIR, this.elixir, amount);
        this.elixir += removableAmount;
        return removableAmount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBalance(final int amount) {
        this.elixir = amount;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String getCurrencyTranslation() {
        return Messages.getMessage(MessageKey.ELIXIR);
    }

    @Override
    public String toString() {
        return "Elixir{elixir=" + elixir + '}';
    }
}
