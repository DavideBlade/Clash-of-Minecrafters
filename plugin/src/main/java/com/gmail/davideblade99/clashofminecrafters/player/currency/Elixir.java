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
 * @since 3.1.4
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
        final int toAdd = Math.min(amount, Currencies.addableAmount(Currencies.ELIXIR, this.elixir));
        this.elixir += toAdd;
        return toAdd;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int removeCurrency(final int amount) {
        final int toRemove = Math.min(amount, Currencies.removableAmount(Currencies.ELIXIR, this.elixir));
        this.elixir -= toRemove;
        return toRemove;
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
