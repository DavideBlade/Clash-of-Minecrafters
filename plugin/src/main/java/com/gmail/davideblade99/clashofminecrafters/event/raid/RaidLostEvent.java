/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.event.raid;

import com.gmail.davideblade99.clashofminecrafters.player.User;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

/**
 * Event called when a player loses a raid
 *
 * @author DavideBlade
 * @since 3.2.2
 */
public final class RaidLostEvent extends RaidEvent {

    private static final HandlerList handlers = new HandlerList();


    public RaidLostEvent(@Nonnull final Player attacker, @Nonnull final User defender) {
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