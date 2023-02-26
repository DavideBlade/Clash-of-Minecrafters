/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.storage.type.bean;

import com.gmail.davideblade99.clashofminecrafters.player.Village;
import com.gmail.davideblade99.clashofminecrafters.geometric.Vector;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.player.currency.Balance;
import com.gmail.davideblade99.clashofminecrafters.storage.PlayerDatabase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;

/**
 * Class container of all the data of a {@link User}. It is used to hold together all data received/sent with a
 * single query to the database. This type of class is also called a JavaBean.
 *
 * @see PlayerDatabase
 */
public final class UserDatabaseType {
    public final Balance balance;
    public final int trophies;
    public final String clanName;
    public final int elixirExtractorLevel;
    public final int goldExtractorLevel;
    public final int archerTowerLevel;
    public final Vector archerTowerLoc;
    public final Village island;
    public final LocalDateTime collectionTime;
    public final int townHallLevel;

    public UserDatabaseType(@Nonnull final Balance balance, final int trophies, @Nullable final String clanName, final int elixirExtractorLevel, final int goldExtractorLevel, final int archerTowerLevel, @Nullable final Vector archerTowerPos, @Nullable final Village island, @Nullable final LocalDateTime collectionTime, final int townHallLevel) {
        this.balance = balance;
        this.trophies = trophies;
        this.clanName = clanName;
        this.elixirExtractorLevel = elixirExtractorLevel;
        this.goldExtractorLevel = goldExtractorLevel;
        this.archerTowerLevel = archerTowerLevel;
        this.archerTowerLoc = archerTowerPos;
        this.island = island;
        this.collectionTime = collectionTime;
        this.townHallLevel = townHallLevel;
    }
}
