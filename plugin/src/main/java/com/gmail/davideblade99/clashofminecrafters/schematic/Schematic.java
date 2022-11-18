/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.schematic;

import com.gmail.davideblade99.clashofminecrafters.exception.PastingException;
import com.gmail.davideblade99.clashofminecrafters.geometric.Size3D;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * <p>Interface that generically represents a schematic.</p>
 * <p>Each class that implements this interface should represent a schematic for a different plugin.</p>
 *
 * @since v3.1.2
 */
public interface Schematic {

    /**
     * Paste the schematic from the specified position, facing north-east
     *
     * @param location Origin point where the schematic should be pasted
     *
     * @throws PastingException In case of error during schematic pasting
     */
    void paste(@Nonnull final Location location) throws PastingException;

    /**
     * @return The size of the schematic
     */
    @Nonnull
    Size3D getSize();

    /**
     * @return The origin position where the schematic has been pasted or {@code null} if the schematic has not yet
     * been placed
     */
    @Nullable
    Location getOrigin();
}