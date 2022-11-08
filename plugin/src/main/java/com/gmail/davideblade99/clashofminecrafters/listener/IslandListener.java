/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.listener;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import org.bukkit.World;

import javax.annotation.Nonnull;

public abstract class IslandListener extends CoMListener {

    protected IslandListener(@Nonnull final CoM plugin) {
        super(plugin);
    }

    public final boolean isIslandWorld(@Nonnull final World world) {
        return world.getName().equals("Islands");
    }
}
