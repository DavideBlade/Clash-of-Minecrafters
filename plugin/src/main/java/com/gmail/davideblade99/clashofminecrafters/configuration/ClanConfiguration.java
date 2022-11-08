/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.configuration;

import com.gmail.davideblade99.clashofminecrafters.util.collection.CollectionUtil;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

public final class ClanConfiguration extends CoMYamlConfiguration {

    private final static String OWNER = "Owner";
    private final static String MEMBERS = "Members";
    private final static String LEVEL = "Level";
    private final static String EXP = "Exp";

    /**
     * {@inheritDoc}
     */
    public ClanConfiguration(@Nonnull final File file, final boolean autoSave) {
        super(file, autoSave);
    }

    public ClanConfiguration(@Nonnull final File file) {
        super(file);
    }

    @Nonnull
    public UUID getOwner() {
        return UUID.fromString(super.getString(OWNER));
    }

    /**
     * Sets the clan owner to the respective path
     *
     * @param owner Clan owner
     */
    public void setOwner(@Nonnull final UUID owner) {
        super.set(OWNER, owner.toString());
    }

    /**
     * Returns the list of members (including owner)
     *
     * @return {@link HashSet} containing the members of the clan (including the owner)
     */
    @Nonnull
    public HashSet<UUID> getMembers() {
        return CollectionUtil.mapStringToUUIDSet((ArrayList<String>) super.getStringList(MEMBERS));
    }

    /**
     * Sets the clan members to the respective path
     *
     * @param members List of members in the clan (including the owner)
     */
    public void setMembers(@Nonnull final HashSet<UUID> members) {
        super.set(MEMBERS, CollectionUtil.mapUUIDToStringList(members));
    }

    public int getLevel() {
        return super.getInt(LEVEL);
    }

    /**
     * Sets the clan level to the respective path
     *
     * @param level Clan level
     */
    public void setLevel(final int level) {
        super.set(LEVEL, level);
    }

    public int getExp() {
        return super.getInt(EXP);
    }

    /**
     * Sets the clan exp to the respective path
     *
     * @param exp Clan exp
     */
    public void setExp(final int exp) {
        super.set(EXP, exp);
    }
}