/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.listener;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public abstract class CoMListener implements Listener {

    protected final CoM plugin;

    protected CoMListener(@Nonnull final CoM plugin) {
        super();

        this.plugin = plugin;
    }
}