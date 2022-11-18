/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters;

public final class Permissions {

    public final static String COMMAND_BASE = "com.command.";
    public final static String CLAN_COMMAND_BASE = COMMAND_BASE + "clan.";
    public final static String WAR_COMMAND_BASE = COMMAND_BASE + "war.";
    public final static String ISLAND_COMMAND_BASE = COMMAND_BASE + "island.";
    public final static String ISLAND_BASE = "com.island.";

    private Permissions() {
        throw new IllegalAccessError();
    }
}
