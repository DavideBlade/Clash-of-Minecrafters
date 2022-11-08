/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.event;

import org.bukkit.event.Event;

import javax.annotation.Nonnull;

public abstract class ClashEvent extends Event {

    protected ClashEvent() {
        super(false);
    }

    @Nonnull
    @Override
    public final String getEventName() {
        return getClass().getSimpleName();
    }
}