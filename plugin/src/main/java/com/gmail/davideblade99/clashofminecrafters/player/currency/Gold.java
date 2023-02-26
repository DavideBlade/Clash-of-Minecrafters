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
 * Class representing the {@link Currencies#GOLD} currency
 *
 * @author DavideBlade
 * @since v3.1.4
 */
public final class Gold implements Currency {

    private int gold;

    /**
     * Initialize to 0 the amount of gold
     */
    public Gold() {
        this.gold = 0;
    }

    /**
     * Initializes gold to the specified value
     *
     * @param initialBalance Amount of gold to be set
     */
    public Gold(final int initialBalance) {
        this.gold = initialBalance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBalance() {
        return this.gold;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int addCurrency(final int amount) {
        final int toAdd = Math.min(amount, Currencies.addableAmount(Currencies.GOLD, this.gold));
        this.gold += toAdd;
        return toAdd;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int removeCurrency(final int amount) {
        final int toRemove = Math.min(amount, Currencies.removableAmount(Currencies.GOLD, this.gold));
        this.gold -= toRemove;
        return toRemove;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBalance(final int amount) {
        this.gold = amount;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String getCurrencyTranslation() {
        return Messages.getMessage(MessageKey.GOLD);
    }

    @Override
    public String toString() {
        return "Gold{gold=" + gold + '}';
    }
}