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
 * Class representing the {@link Currencies#GEMS} currency
 *
 * @author DavideBlade
 * @since v3.1.4
 */
public final class Gems implements Currency {

    private int gems;

    /**
     * Initialize to 0 the amount of gems
     */
    public Gems() {
        this.gems = 0;
    }

    /**
     * Initializes gems to the specified value
     *
     * @param initialBalance Amount of gems to be set
     */
    public Gems(final int initialBalance) {
        this.gems = initialBalance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBalance() {
        return this.gems;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int addCurrency(final int amount) {
        final int addableAmount = Currencies.addableAmount(Currencies.GEMS, this.gems, amount);
        this.gems += addableAmount;
        return addableAmount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int removeCurrency(final int amount) {
        final int removableAmount = Currencies.removableAmount(Currencies.GEMS, this.gems, amount);
        this.gems += removableAmount;
        return removableAmount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBalance(final int amount) {
        this.gems = amount;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String getCurrencyTranslation() {
        return this.gems == 1 ? Messages.getMessage(MessageKey.GEM) : Messages.getMessage(MessageKey.GEMS);
    }

    @Override
    public String toString() {
        return "Gems{gems=" + gems + '}';
    }
}