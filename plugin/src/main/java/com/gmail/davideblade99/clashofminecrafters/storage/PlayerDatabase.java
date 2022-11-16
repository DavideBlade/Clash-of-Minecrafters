/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.storage;

import com.gmail.davideblade99.clashofminecrafters.Island;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.storage.type.bean.UserDatabaseType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

//TODO: leggere README Hikari: https://github.com/brettwooldridge/HikariCP

//TODO: guardare https://github.com/AuthMe/AuthMeReloaded/tree/master/src/main/java/fr/xephi/authme/datasource e farlo uguale (per es. non hanno un set per ogni valore ma settano in generale PlayerAuth (cioè il mio User), il che permette di non modificare questa classe astratta ma solo le implementazioni)

//TODO: spunto: https://github.com/aikar/db/tree/master/core/src/main/java/co/aikar/idb + https://github.com/VirtualByte/ByteUtils/blob/master/src/main/java/me/virtualbyte/byteutils/storage/Database.java

//TODO: leggere questo per prendere spunto -> https://www.spigotmc.org/threads/storing-data-in-memory-vs-directly-reading-file.276505/#post-2692411
//TODO: oltre a quello sopra un'alternativa potrebbe essere salvare i dati all'uscita del giocatore, anzichè memorizzarli sul file ogni volta

//TODO (spunto): https://github.com/TechFortress/GriefPrevention/blob/0e3a2e979161327946365e71b97bbcf74f572e6b/src/main/java/me/ryanhamshire/GriefPrevention/DataStore.java#L67

//TODO: come per MySQL, anche con i file può verificarsi un errore -> creare sistema di log anche per loro

//TODO: leggere https://www.spigotmc.org/threads/mysql-async-creating-a-working-class-with-functions.294146/ + https://github.com/RainbowDashLabs/BasicSQLPlugin + https://github.com/SkyWars/bukkit-async-sql + https://www.spigotmc.org/threads/database-what-is-the-most-efficient.190570/#post-1995347
//TODO: async

/**
 * Interface for manipulating {@link User} objects from a database.
 */
public interface PlayerDatabase {

    /**
     * Checks whether the player has already played on the server
     *
     * @param playerUUID UUID of the player to check
     *
     * @return true if the player has already played, otherwise false
     */
    boolean hasPlayedBefore(@Nonnull final UUID playerUUID);

    /**
     * @return an island chosen randomly from all existing islands. Returns {@code null} only in case of an error
     * or if there are no islands available.
     *
     * @since v3.0
     */
    @Nullable
    Island getRandomIsland();

    /**
     * Search for a random island among all the islands of players with a clan and who are not part of the clan
     * passed as a parameter.
     *
     * @param clanName Name of clan whose islands are to be excluded
     *
     * @return Island found or {@code null} if an error occurs or if no islands are available
     *
     * @since v3.0
     */
    @Nullable
    Island getRandomEnemyIsland(@Nonnull final String clanName);

    /**
     * Retrieves all user data from the database
     *
     * @param playerUUID UUID of the player whose data to obtain
     *
     * @return An instance of {@link UserDatabaseType} that contains all the data obtained or {@code null} if the
     * user is not found
     *
     * @see UserDatabaseType
     * @since v3.0.1
     */
    @Nullable
    UserDatabaseType fetchUser(@Nonnull final UUID playerUUID);

    /**
     * Saves all player data to the database
     *
     * @param playerUUID UUID of the player whose data to obtain
     * @param user       User whose data to save
     *
     * @since v3.1
     */
    void storeUser(@Nonnull final UUID playerUUID, @Nonnull final User user);
}
