/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.listener;

import com.gmail.davideblade99.clashofminecrafters.CoM;

import javax.annotation.Nonnull;

/**
 * Represents village-related event listeners
 *
 * @author DavideBlade
 * @since 3.2.2
 */
public abstract class VillageListener extends CoMListener {

    protected VillageListener(@Nonnull final CoM plugin) {
        super(plugin);
    }
}
