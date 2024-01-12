/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.schematic;

import com.gmail.davideblade99.clashofminecrafters.exception.PastingException;
import com.gmail.davideblade99.clashofminecrafters.util.geometric.Size3D;
import com.gmail.davideblade99.clashofminecrafters.util.thread.NullableCallback;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * <p>Interface that generically represents a schematic.</p>
 * <p>Each class that implements this interface should represent a schematic for a different plugin.</p>
 *
 * @see SchematicPaster
 * @since 3.1.2
 */
public interface Schematic {

    /**
     * <p>Paste the schematic from the specified position, facing north-east.</p>
     * <p>The operation is delegated to the plugin paster: if it operates asynchronously (e.g., AsyncWorldEdit),
     * the schematic will be pasted asynchronously, otherwise the main thread will be used. In any case, the
     * completion of the operation will correspond to the callback invocation.</p>
     * <p>If exceptions are thrown, they are passed to the callback received as parameter.</p>
     *
     * @param location          Origin point where the schematic should be pasted
     * @param completionHandler Callback that will be invoked when the operation is completed. It will have {@code
     *                          null} as parameter if the operation was completed successfully, otherwise it will
     *                          receive the thrown exception. {@link PastingException} will be thrown in case of
     *                          error during schematic pasting.
     *
     * @since 3.1.4
     */
    void paste(@Nonnull final Location location, @Nonnull final NullableCallback<PastingException> completionHandler);

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