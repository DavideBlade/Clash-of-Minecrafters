package com.gmail.davideblade99.clashofminecrafters.setting.bean;

import javax.annotation.Nullable;

/**
 * JavaBean that contains clan settings retrieved from config.yml.
 *
 * @author DavideBlade
 * @since v3.1.2
 */
public final class ClanSettings {

    public final int expRequired;
    public final String command;

    public ClanSettings(final int expRequired, @Nullable final String command) {
        this.expRequired = expRequired;
        this.command = command;
    }
}
