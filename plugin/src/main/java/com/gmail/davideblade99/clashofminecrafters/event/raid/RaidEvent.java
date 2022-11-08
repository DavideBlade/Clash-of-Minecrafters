/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.event.raid;

import com.gmail.davideblade99.clashofminecrafters.event.ClashEvent;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

abstract class RaidEvent extends ClashEvent {

    private final Player attacker;
    private final String defender;

    RaidEvent(@Nonnull final Player attacker, @Nonnull final String defender) {
        super();

        this.attacker = attacker;
        this.defender = defender;
    }

    @Nonnull
    public final Player getAttacker() {
        return attacker;
    }

    @Nonnull
    public final String getDefender() {
        return defender;
    }
}