/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.message;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Keys for translatable messages managed by {@link Messages}
 */
public enum MessageKey {

    /* General messages */
    INTEGER_REQUIRED("Integer required"),
    COMMAND_FOR_PLAYER("Command only for player"),
    PLAYER_NOT_ONLINE("Player not online", "%player"),
    NO_PERMISSION("No permission"),
    TELEPORTATION("Teleportation"),
    TELEPORT_TO_SPAWN("Teleport to spawn"),
    TELEPORTATION_CANCELLED("Teleportation cancelled"),
    UPGRADE_COMPLETED("Upgrade completed", "%building", "%level"),
    GOLD_EXTRACTOR("Gold extractor"),
    ELIXIR_EXTRACTOR("Elixir extractor"),
    ARCHER_TOWER("Archer tower"),
    TOWN_HALL("Town hall"),
    MAX_LEVEL_REACHED("Max level reached", "%building"),
    SHOW_TROPHIES("Show trophies", "%amount"),
    ADDED_TROPHIES("Added trophies", "%amount"),
    REMOVED_TROPHIES("Removed trophies", "%amount"),
    MAX_TROPHIES("Max trophies"),
    UNKNOWN_PLAYER("Player never joined", "%player"),
    TOO_BIG_NUMBER("Too big number", "%amount"),
    NO_BUILDINGS("No buildings"),

    /* Usage of commands */
    ADD_COMMAND_USAGE("Add command usage"),
    TAKE_COMMAND_USAGE("Take command usage"),
    POSSIBLE_CLAN_COMMANDS("Possible clan command usage"),
    CLAN_JOIN_USAGE("Clan join usage"),
    CLAN_CREATE_USAGE("Clan create usage"),
    OPEN_COMMAND_USAGE("Open command usage"),
    WAR_COMMAND_USAGE("War command usage"),

    /* Clan section */
    CLAN_NOT_FOUND("Clan not found"),
    JOINED_CLAN("Joined the clan", "%clan"),
    ALREADY_HAVE_CLAN("Already have clan"),
    CLAN_REQUIRED("Must have a clan"),
    LEFT_CLAN("Left clan", "%clan"),
    CREATED_CLAN("Created clan", "%clan"),
    MAX_LENGTH("Max length"),
    CANNOT_CONTAINS_DOT("Cannot contains dot"),
    CLAN_ALREADY_EXISTS("Already exists"),
    FULL_CLAN("Full clan"),
    SAVING_CLAN_ERROR("Impossible save properly clan"), //TODO: unused
    INCREASED_LEVEL("Increased level", "%level"),

    /* Extractor section */
    EXTRACTOR_PLACED("Extractor placed"),
    EXTRACTOR_NOT_PLACED("Extractor not placed"),
    NO_EXTRACTOR("No extractor"),
    COLLECTED_RESOURCES("Collected resources"),
    EXTRACTORS_INFO("Extractors info", "%building", "%level", "%amount", "%resources", "%capacity"),
    DISABLED_EXTRACTORS("Disabled extractors"),

    /* War section */
    WAR_STARTED("War started"),
    WAR_FINISHED("War finished"),
    STARTING_WAR("Starting war", "%time", "%type"),
    ENDING_WAR("Ending war", "%time", "%type"),
    SECONDS("Seconds"),
    SECOND("Second"),
    SEARCHING("Searching"),
    ISLAND_NOT_AVAILABLE("Island not available"),
    WAR_REQUIRED("Must be in war"),
    CANNOT_HIT_GUARDIAN("Cannot hit guardian"),
    CANNOT_HIT_ARCHER("Cannot hit archer"),
    ALREADY_ATTACKING("Already attacking"),
    RAID_SUCCESSFUL("Raid successful", "%player"),
    RAID_FAILUIRE("Raid failure", "%player"),
    RAID_CANCELLED("Raid cancelled", "%reason"),
    RELOAD("Reload"),
    WAR_ENDED("War ended"),

    /* Island section */
    ISLAND_CREATION("Island creation"),
    ISLAND_SPAWN_NOT_SAFE("Island spawn not safe"),
    ISLAND_CREATION_ERROR("Impossible create properly island"),
    ISLAND_WORLD_LIMIT_REACHED("Island world limit reached"),
    ISLAND_REQUIRED("Must have an island"),
    ISLAND_UNDER_ATTACK("Island under attack"),
    LEFT_ISLAND("Left island"),
    ENTERED_IN_ISLAND("Entered into the island"),
    CANNOT_SET_SPAWN("Cannot set spawn"),
    LOCATION_NOT_SAFE("Location not safe"),
    ISLAND_SPAWN_SET("Island spawn set"),

    /* Economy messages */
    SHOW_BALANCE("Show balance", "%amount", "%currency"),
    NOT_ENOUGH_MONEY("Not enough money", "%currency"),
    ADDED_TO_BALANCE("Added to balance", "%amount", "%currency"),
    ADDED_TO_OTHERS_BALANCE("Added to others balance", "%amount", "%currency", "%player"),
    REMOVED_FROM_BALANCE("Removed from balance", "%amount", "%currency"),
    REMVOED_FROM_OTHERS_BALANCE("Removed from others balance", "%amount", "%currency", "%player"),
    MAX_MONEY("Max money"),
    GOLD("Gold"),
    ELIXIR("Elixir"),
    GEMS("Gems"),
    GEM("Gem"),

    /* Menu messages */
    MENU_NOT_FOUND("Menu not found"),
    ITEM_NOT_FOUND("Item not found"),

    /* Archer tower messages */
    TOWER_PLACED("Tower placed"),
    TOWER_NOT_PLACED("Tower not placed"),

    /* Schematic messages */
    LOAD_ERROR("Load error", "%schem");

    private final String path;
    private final String[] tags;

    MessageKey(@Nonnull final String path, @Nullable final String... tags) {
        this.path = path;
        this.tags = tags;
    }

    /**
     * @return The path in the messages file
     */
    @Nonnull
    public String getPath() {
        return path;
    }

    /**
     * @return List of tags to replace in the message
     */
    @Nullable
    public String[] getTags() {
        return tags;
    }

    @Nonnull
    @Override
    public String toString() {
        return path;
    }
}
