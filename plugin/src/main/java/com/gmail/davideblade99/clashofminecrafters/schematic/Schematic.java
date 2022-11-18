/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.schematic;

import com.gmail.davideblade99.clashofminecrafters.exception.PastingException;
import com.gmail.davideblade99.clashofminecrafters.util.geometric.Size3D;
import com.gmail.davideblade99.clashofminecrafters.util.geometric.Vector;
import org.bukkit.World;

import javax.annotation.Nonnull;

/**
 * <p>Interface that generically represents a schematic.</p>
 * <p>Each class that implements this interface should represent a schematic for a different plugin.</p>
 *
 * @since v3.1.2
 */
//TODO: javadoc metodi
public interface Schematic {

    //TODO: forse è meglio una Location?
    void paste(@Nonnull final World world, @Nonnull final Vector origin) throws PastingException;

    Size3D getSize();

    Vector getOrigin();
}