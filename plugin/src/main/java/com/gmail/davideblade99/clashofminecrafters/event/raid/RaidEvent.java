/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.event.raid;

import com.gmail.davideblade99.clashofminecrafters.event.ClashEvent;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * Represents a raid-related event
 *
 * @author DavideBlade
 * @since 3.2.2
 */
abstract class RaidEvent extends ClashEvent {

    private final Player attacker;
    private final User defender;

    RaidEvent(@Nonnull final Player attacker, @Nonnull final User defender) {
        super();

        this.attacker = attacker;
        this.defender = defender;
    }

    @Nonnull
    public final Player getAttacker() {
        return attacker;
    }

    @Nonnull
    public final User getDefender() {
        return defender;
    }
}