package com.gmail.davideblade99.clashofminecrafters.setting;

import javax.annotation.Nullable;

/**
 * Represents a level of a clan, configured in the config.yml
 *
 * @author DavideBlade
 * @since 3.1.4
 */
public final class ClanLevel {

    public final int expRequired;
    public final String command;

    public ClanLevel(final int expRequired, @Nullable final String command) {
        this.expRequired = expRequired;
        this.command = command;
    }
}
