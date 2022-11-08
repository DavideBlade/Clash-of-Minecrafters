/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.event.raid;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

/**
 * Event called when a player loses a raid
 */
public final class RaidLostEvent extends RaidEvent {

    private static final HandlerList handlers = new HandlerList();


    public RaidLostEvent(@Nonnull final Player attacker, @Nonnull final String defender) {
        super(attacker, defender);
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Nonnull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}