/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters.storage.sql.mysql;

import com.gmail.davideblade99.clashofminecrafters.CoM;
import com.gmail.davideblade99.clashofminecrafters.Currency;
import com.gmail.davideblade99.clashofminecrafters.file.log.ErrorLog;
import com.gmail.davideblade99.clashofminecrafters.Village;
import com.gmail.davideblade99.clashofminecrafters.BuildingType;
import com.gmail.davideblade99.clashofminecrafters.User;
import com.gmail.davideblade99.clashofminecrafters.storage.Columns;
import com.gmail.davideblade99.clashofminecrafters.storage.sql.AbstractSQLDatabase;
import com.gmail.davideblade99.clashofminecrafters.storage.sql.mysql.query.*;
import com.gmail.davideblade99.clashofminecrafters.storage.type.bean.UserDatabaseType;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.BukkitLocationUtil;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import com.gmail.davideblade99.clashofminecrafters.util.geometric.Size2D;
import com.gmail.davideblade99.clashofminecrafters.util.geometric.Vector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

//TODO (spunto): https://github.com/GabrielRStella/Utility/tree/master/src/com/ralitski/util/sql

//TODO: async con thread pool
public final class MySQLDatabase extends AbstractSQLDatabase {

    private final static String TABLE_NAME = "Players";

    private final static String FETCH_ERROR_MESSAGE = "§cAn error occurred while fetching data on the database. Contact an administrator.";
    private final static String SAVE_ERROR_MESSAGE = "§cAn error occurred while saving data to the database. Contact an administrator.";

    private final String databaseName;

    /**
     * @param plugin   Plugin instance
     * @param host     Address where database is located
     * @param port     Port on which database is listening
     * @param database Database name
     * @param user     Username for logging into the database
     * @param password Password for logging into the database
     *
     * @throws SQLException If an error occurs while creating the table on the database
     */
    public MySQLDatabase(@Nonnull final CoM plugin, @Nonnull final String host, final int port, @Nonnull final String database, @Nonnull final String user, @Nonnull final String password) throws SQLException {
        super(plugin, "com.mysql.jdbc.Driver", "jdbc:mysql://" + host + ":" + port + "/" + database, user, password);

        this.databaseName = database;

        createTable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPlayedBefore(@Nonnull final UUID playerUUID) {
        final QueryBuilderSelect queryBuilder = QueryBuilder
                .select("*")
                .from(TABLE_NAME)
                .where(Columns.UUID, playerUUID.toString());

        try {
            // #executeQuery == null only if result set is empty, i.e., the player never played
            return queryBuilder.executeQuery(getConnectionPool(), resultSet -> true) != null;
        } catch (final SQLException ex) {
            notifyAndLogError(playerUUID, playerUUID, ex, Operation.FETCH, "attempt to check whether the player is new or has played before");
        }

        return true;
    }

    /**
     * Chooses a player randomly among all those with an island
     *
     * {@inheritDoc}
     *
     * @throws IllegalStateException If there is any missing data regarding the player or island
     */
    @Nullable
    @Override
    public Village getRandomIsland() {
        final QueryBuilderSelect queryBuilder = QueryBuilder
                .select(Columns.UUID, Columns.ISLAND_SPAWN, Columns.ISLAND_ORIGIN, Columns.ISLAND_SIZE, Columns.ISLAND_EXPANSIONS)
                .from(TABLE_NAME)
                .where(new WhereClauseBuilder()
                        .whereColumn(Columns.ISLAND_SPAWN).isNotNull()
                        .orColumn(Columns.ISLAND_ORIGIN).isNotNull()
                        .orColumn(Columns.ISLAND_SIZE).isNotNull()
                        .orColumn(Columns.ISLAND_EXPANSIONS).isNotNull())
                .orderBy(MySQLFunction.RAND)
                .limit(1);

        final UUID[] playerUUID = {null}; // Workaround to allow the use of the variable in the inner anonymous class
        try {
            return queryBuilder.executeQuery(getConnectionPool(), resultSet -> {
                playerUUID[0] = UUID.fromString(resultSet.getString(Columns.UUID));
                final String playerName = plugin.getPlayerHandler().getPlayerName(playerUUID[0]);
                final Location spawn = BukkitLocationUtil.fromString(resultSet.getString(Columns.ISLAND_SPAWN));
                final Vector origin = Vector.fromString(resultSet.getString(Columns.ISLAND_ORIGIN));
                final Size2D size = Size2D.fromString(resultSet.getString(Columns.ISLAND_SIZE));
                final Size2D expansions = Size2D.fromString(resultSet.getString(Columns.ISLAND_EXPANSIONS));

                // Unexpected missing data
                if (playerName == null)
                    throw new IllegalStateException("Player name associated with UUID \"" + playerUUID[0] + "\" missing");
                if (spawn == null || origin == null || size == null || expansions == null)
                    throw new IllegalStateException("Island of \"" + playerUUID[0] + "\" existing with some missing data");
                //TODO: notificare al giocatore target

                return new Village(playerName, spawn, origin, size, expansions);
            });
        } catch (final SQLException ex) {
            //TODO: notificare al giocatore che la sta cercando

            notifyAndLogError((Player) null, playerUUID[0], ex, Operation.FETCH, "attempt to get a random island");
        }

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException If there is any missing data regarding the player or island
     */
    @Nullable
    @Override
    public Village getRandomEnemyIsland(@Nonnull final String clanName) {
        final QueryBuilderSelect queryBuilder = QueryBuilder
                .select(Columns.UUID, Columns.ISLAND_SPAWN, Columns.ISLAND_ORIGIN, Columns.ISLAND_SIZE, Columns.ISLAND_EXPANSIONS)
                .from(TABLE_NAME)
                .where(new WhereClauseBuilder()
                        .openParenthesis()
                        .whereColumn(Columns.ISLAND_SPAWN).isNotNull()
                        .orColumn(Columns.ISLAND_ORIGIN).isNotNull()
                        .orColumn(Columns.ISLAND_SIZE).isNotNull()
                        .orColumn(Columns.ISLAND_EXPANSIONS).isNotNull()
                        .closeParenthesis()
                        .andColumn(Columns.CLAN).isNotNull()
                        .andColumn(Columns.CLAN).differsFrom(clanName))
                .orderBy(MySQLFunction.RAND)
                .limit(1);

        final UUID[] playerUUID = {null}; // Workaround to allow the use of the variable in the inner anonymous class
        try {
            queryBuilder.executeQuery(getConnectionPool(), resultSet -> {
                playerUUID[0] = UUID.fromString(resultSet.getString(Columns.UUID));
                final String playerName = plugin.getPlayerHandler().getPlayerName(playerUUID[0]);
                final Location spawn = BukkitLocationUtil.fromString(resultSet.getString(Columns.ISLAND_SPAWN));
                final Vector origin = Vector.fromString(resultSet.getString(Columns.ISLAND_ORIGIN));
                final Size2D size = Size2D.fromString(resultSet.getString(Columns.ISLAND_SIZE));
                final Size2D expansions = Size2D.fromString(resultSet.getString(Columns.ISLAND_EXPANSIONS));

                // Unexpected missing data
                if (playerName == null)
                    throw new IllegalStateException("Player name associated with UUID \"" + playerUUID[0] + "\" missing");
                if (spawn == null || origin == null || size == null || expansions == null)
                    throw new IllegalStateException("Island of \"" + playerUUID[0] + "\" existing with some missing data");
                //TODO: notificare al giocatore

                return new Village(playerName, spawn, origin, size, expansions);
            });
        } catch (final SQLException ex) {
            //TODO: notificare al giocatore che la sta cercando

            notifyAndLogError((Player) null, playerUUID[0], ex, Operation.FETCH, "attempt to get a random island of an enemy clan");
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public UserDatabaseType fetchUser(@Nonnull final UUID playerUUID) {
        final QueryBuilderSelect queryBuilder = QueryBuilder
                .select("*")
                .from(TABLE_NAME)
                .where(Columns.UUID, playerUUID.toString());

        try {
            return queryBuilder.executeQuery(getConnectionPool(), resultSet -> {
                final String playerName = plugin.getPlayerHandler().getPlayerName(playerUUID);
                if (playerName == null) // Unexpected missing data
                    throw new IllegalStateException("Player name associated with UUID \"" + playerUUID + "\" missing");

                final int gold = resultSet.getInt(Columns.GOLD);
                final int elixir = resultSet.getInt(Columns.ELIXIR);
                final int gems = resultSet.getInt(Columns.GEMS);
                final int trophies = resultSet.getInt(Columns.TROPHIES);
                final String clanName = resultSet.getString(Columns.CLAN);
                final int elixirExtractorLevel = resultSet.getInt(Columns.ELIXIR_EXTRACTOR_LEVEL);
                final int goldExtractorLevel = resultSet.getInt(Columns.GOLD_EXTRACTOR_LEVEL);
                final int archerLevel = resultSet.getInt(Columns.ARCHER_TOWER_LEVEL);
                final Vector archerPos = Vector.fromString(resultSet.getString(Columns.ARCHER_TOWER_LOCATION));

                final Village island;
                final Location islandSpawn = BukkitLocationUtil.fromString(resultSet.getString(Columns.ISLAND_SPAWN));
                final Vector islandOrigin = Vector.fromString(resultSet.getString(Columns.ISLAND_ORIGIN));
                final Size2D islandSize = Size2D.fromString(resultSet.getString(Columns.ISLAND_SIZE));
                final Size2D islandExpansions = Size2D.fromString(resultSet.getString(Columns.ISLAND_EXPANSIONS));

                // If the player has an island
                if (islandSpawn != null || islandOrigin != null || islandSize != null || islandExpansions != null) {
                    // Unexpected missing data
                    if (islandSpawn == null || islandOrigin == null || islandSize == null || islandExpansions == null)
                        throw new IllegalStateException("Island of \"" + playerUUID + "\" existing with some missing data");

                    island = new Village(playerName, islandSpawn, islandOrigin, islandSize, islandExpansions);
                } else
                    island = null;

                final String collectionTimeStr = resultSet.getString(Columns.COLLECTION_TIME);
                final LocalDateTime collectionTime = collectionTimeStr != null ? LocalDateTime.parse(collectionTimeStr, CoM.DATE_FORMAT) : null;

                final int townHallLevel = resultSet.getInt(Columns.TOWN_HALL_LEVEL);

                return new UserDatabaseType(gold, elixir, gems, trophies, clanName, elixirExtractorLevel, goldExtractorLevel, archerLevel, archerPos, island, collectionTime, townHallLevel);
            });

        } catch (final SQLException ex) {
            notifyAndLogError(playerUUID, playerUUID, ex, Operation.FETCH, "attempt to fetch player data");
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void storeUser(@Nonnull final UUID playerUUID, @Nonnull final User user) {
        final int gold = user.getBalance(Currency.GOLD);
        final int elixir = user.getBalance(Currency.ELIXIR);
        final int gems = user.getBalance(Currency.GEMS);
        final int trophies = user.getTrophies();
        final String clan = user.getClanName();
        final int elixirExtractorLevel = user.getBuildingLevel(BuildingType.ELIXIR_EXTRACTOR);
        final int goldExtractorLevel = user.getBuildingLevel(BuildingType.GOLD_EXTRACTOR);
        final int archerLevel = user.getBuildingLevel(BuildingType.ARCHER_TOWER);
        final int townHallLevel = user.getBuildingLevel(BuildingType.TOWN_HALL);
        final String archerPos = user.getTowerPos() == null ? null : user.getTowerPos().toString();
        final Village island = user.getIsland();
        final String islandSpawn = island == null ? null : BukkitLocationUtil.toString(island.spawn);
        final String islandOrigin = island == null ? null : island.origin.toString();
        final String islandSize = island == null ? null : island.size.toString();
        final String islandExpansions = island == null ? null : island.expansions.toString();
        final String collectionTime = user.getCollectionTime() == null ? null : CoM.DATE_FORMAT.format(user.getCollectionTime());

        final QueryBuilderInsert queryBuilder = QueryBuilder
                .insert(TABLE_NAME)
                .value(Columns.UUID, playerUUID.toString())
                .value(Columns.GOLD, gold)
                .value(Columns.ELIXIR, elixir)
                .value(Columns.GEMS, gems)
                .value(Columns.TROPHIES, trophies)
                .safeValue(Columns.CLAN)
                .value(Columns.ELIXIR_EXTRACTOR_LEVEL, elixirExtractorLevel)
                .value(Columns.GOLD_EXTRACTOR_LEVEL, goldExtractorLevel)
                .value(Columns.ARCHER_TOWER_LEVEL, archerLevel)
                .value(Columns.TOWN_HALL_LEVEL, townHallLevel)
                .value(Columns.ISLAND_SPAWN, islandSpawn)
                .value(Columns.ISLAND_ORIGIN, islandOrigin)
                .value(Columns.ISLAND_SIZE, islandSize)
                .value(Columns.ISLAND_EXPANSIONS, islandExpansions)
                .value(Columns.ARCHER_TOWER_LOCATION, archerPos)
                .value(Columns.COLLECTION_TIME, collectionTime)
                .onDuplicateKeyUpdate(Columns.GOLD, gold)
                .onDuplicateKeyUpdate(Columns.ELIXIR, elixir)
                .onDuplicateKeyUpdate(Columns.GEMS, gems)
                .onDuplicateKeyUpdate(Columns.TROPHIES, trophies)
                .onDuplicateKeyUpdateSafe(Columns.CLAN)
                .onDuplicateKeyUpdate(Columns.ELIXIR_EXTRACTOR_LEVEL, elixirExtractorLevel)
                .onDuplicateKeyUpdate(Columns.GOLD_EXTRACTOR_LEVEL, goldExtractorLevel)
                .onDuplicateKeyUpdate(Columns.ARCHER_TOWER_LEVEL, archerLevel)
                .onDuplicateKeyUpdate(Columns.TOWN_HALL_LEVEL, townHallLevel)
                .onDuplicateKeyUpdate(Columns.ISLAND_SPAWN, islandSpawn)
                .onDuplicateKeyUpdate(Columns.ISLAND_ORIGIN, islandOrigin)
                .onDuplicateKeyUpdate(Columns.ISLAND_SIZE, islandSize)
                .onDuplicateKeyUpdate(Columns.ISLAND_EXPANSIONS, islandExpansions)
                .onDuplicateKeyUpdate(Columns.ARCHER_TOWER_LOCATION, archerPos)
                .onDuplicateKeyUpdate(Columns.COLLECTION_TIME, collectionTime);

        queryBuilder.executeUpdate(getConnectionPool(), new QueryResult() {
            @Override
            public <R> void completed(@Nonnull final R ignored) { }

            @Override
            public void failed(@Nonnull final SQLException ex) {
                //TODO: usare i toString() è brutto! se si riesce modificarlo
                notifyAndLogError(playerUUID, playerUUID, ex, Operation.STORE, "attempt to set player data to " + user);
            }
        }, clan, clan);
    }

    /**
     * Creates a string containing the dump of player data currently on the database
     *
     * @param playerUUID UUID of the player whose data is to be fetched
     *
     * @return Player data as a string or "database unreachable" if an error occurs
     */
    @Nonnull
    private String getDataDump(@Nonnull final UUID playerUUID) {
        final QueryBuilderSelect queryBuilder = QueryBuilder
                .select("*")
                .from(TABLE_NAME)
                .where(Columns.UUID, playerUUID.toString());

        //TODO: poi provare a fare il fetch dei dati in User (NB: potrebbero essere già aggiornati a seconda dell'ordine in cui vengono chiamati i metodi!)
        try {
            final String result = queryBuilder.executeQuery(getConnectionPool(), resultSet -> "Gems: '" + resultSet.getInt(Columns.GEMS) + "', " +
                    "Gold: '" + resultSet.getInt(Columns.GOLD) + "', " +
                    "Elixir: '" + resultSet.getInt(Columns.ELIXIR) + "', " +
                    "Trophies: '" + resultSet.getInt(Columns.TROPHIES) + "', " +
                    "Clan: '" + resultSet.getString(Columns.CLAN) + "', " +
                    "Collection timestamp: '" + resultSet.getString(Columns.COLLECTION_TIME) + "', " +
                    "Archer level: '" + resultSet.getInt(Columns.ARCHER_TOWER_LEVEL) + "', " +
                    "Archer position: '" + resultSet.getString(Columns.ARCHER_TOWER_LOCATION) + "', " +
                    "Gold extractor level: '" + resultSet.getInt(Columns.GOLD_EXTRACTOR_LEVEL) + "', " +
                    "Elixir extractor level: '" + resultSet.getInt(Columns.ELIXIR_EXTRACTOR_LEVEL) + "', " +
                    "Island spawn: '" + resultSet.getString(Columns.ISLAND_SPAWN) + "', " +
                    "Island origin: '" + resultSet.getString(Columns.ISLAND_ORIGIN) + "', " +
                    "Island size: '" + resultSet.getString(Columns.ISLAND_SIZE) + "', " +
                    "Island expansions: '" + resultSet.getString(Columns.ISLAND_EXPANSIONS) + "', " +
                    "Town hall level: '" + resultSet.getString(Columns.TOWN_HALL_LEVEL) + "'");

            return result == null ? "no data found" : result;
        } catch (final SQLException ignored) {
            return "database unreachable";
        }
    }

    /**
     * Creates the table or any of its columns if they don't exist
     */
    private void createTable() throws SQLException {
        final QueryBuilderCreateTable queryBuilderCreateTable = QueryBuilder
                .createTable(TABLE_NAME)
                .ifNotExists()
                .withColumn(Columns.UUID, "char(36) NOT NULL")
                .withColumn(Columns.GEMS, "INT UNSIGNED NOT NULL DEFAULT 0")
                .withColumn(Columns.GOLD, "INT UNSIGNED NOT NULL DEFAULT 0")
                .withColumn(Columns.ELIXIR, "INT UNSIGNED NOT NULL DEFAULT 0")
                .withColumn(Columns.TROPHIES, "INT UNSIGNED NOT NULL DEFAULT 0")
                .withColumn(Columns.CLAN, "varchar(30) DEFAULT NULL")
                .withColumn(Columns.COLLECTION_TIME, "varchar(21) DEFAULT NULL")
                .withColumn(Columns.ARCHER_TOWER_LEVEL, "INT NOT NULL DEFAULT 0")
                .withColumn(Columns.ARCHER_TOWER_LOCATION, "varchar(60) DEFAULT NULL")
                .withColumn(Columns.GOLD_EXTRACTOR_LEVEL, "INT NOT NULL DEFAULT 0")
                .withColumn(Columns.ELIXIR_EXTRACTOR_LEVEL, "INT NOT NULL DEFAULT 0")
                .withColumn(Columns.TOWN_HALL_LEVEL, "INT UNSIGNED NOT NULL DEFAULT 1")
                .withColumn(Columns.ISLAND_SPAWN, "varchar(95) DEFAULT NULL")
                .withColumn(Columns.ISLAND_ORIGIN, "varchar(55) DEFAULT NULL")
                .withColumn(Columns.ISLAND_SIZE, "varchar(24) DEFAULT NULL")
                .withColumn(Columns.ISLAND_EXPANSIONS, "varchar(12) DEFAULT NULL")
                .primaryKey(Columns.UUID)
                .setCharset("utf8");

        try {
            queryBuilderCreateTable.execute(getConnectionPool());

            // Check columns if the table already exists
            final DatabaseMetaData md = getConnection().getMetaData();
            final QueryBuilderAlterTable queryBuilderAlterTable = QueryBuilder.alterTable(TABLE_NAME);

            if (isColumnMissing(databaseName, TABLE_NAME, md, Columns.GEMS))
                queryBuilderAlterTable.addColumn(Columns.GEMS, "INT UNSIGNED NOT NULL DEFAULT 0");
            if (isColumnMissing(databaseName, TABLE_NAME, md, Columns.GOLD))
                queryBuilderAlterTable.addColumn(Columns.GOLD, "INT UNSIGNED NOT NULL DEFAULT 0");
            if (isColumnMissing(databaseName, TABLE_NAME, md, Columns.ELIXIR))
                queryBuilderAlterTable.addColumn(Columns.ELIXIR, "INT UNSIGNED NOT NULL DEFAULT 0");
            if (isColumnMissing(databaseName, TABLE_NAME, md, Columns.TROPHIES))
                queryBuilderAlterTable.addColumn(Columns.TROPHIES, "INT UNSIGNED NOT NULL DEFAULT 0");
            if (isColumnMissing(databaseName, TABLE_NAME, md, Columns.CLAN))
                queryBuilderAlterTable.addColumn(Columns.CLAN, "varchar(30) DEFAULT NULL");
            if (isColumnMissing(databaseName, TABLE_NAME, md, Columns.COLLECTION_TIME))
                queryBuilderAlterTable.addColumn(Columns.COLLECTION_TIME, "varchar(21) DEFAULT NULL");
            if (isColumnMissing(databaseName, TABLE_NAME, md, Columns.ARCHER_TOWER_LEVEL))
                queryBuilderAlterTable.addColumn(Columns.ARCHER_TOWER_LEVEL, "INT NOT NULL DEFAULT 0");
            if (isColumnMissing(databaseName, TABLE_NAME, md, Columns.ARCHER_TOWER_LOCATION))
                queryBuilderAlterTable.addColumn(Columns.ARCHER_TOWER_LOCATION, "varchar(60) DEFAULT NULL");
            if (isColumnMissing(databaseName, TABLE_NAME, md, Columns.GOLD_EXTRACTOR_LEVEL))
                queryBuilderAlterTable.addColumn(Columns.GOLD_EXTRACTOR_LEVEL, "INT NOT NULL DEFAULT 0");
            if (isColumnMissing(databaseName, TABLE_NAME, md, Columns.ELIXIR_EXTRACTOR_LEVEL))
                queryBuilderAlterTable.addColumn(Columns.ELIXIR_EXTRACTOR_LEVEL, "INT NOT NULL DEFAULT 0");
            if (isColumnMissing(databaseName, TABLE_NAME, md, Columns.TOWN_HALL_LEVEL))
                queryBuilderAlterTable.addColumn(Columns.TOWN_HALL_LEVEL, "INT UNSIGNED NOT NULL DEFAULT 1");
            if (isColumnMissing(databaseName, TABLE_NAME, md, Columns.ISLAND_SPAWN))
                queryBuilderAlterTable.addColumn(Columns.ISLAND_SPAWN, "varchar(95) DEFAULT NULL");
            if (isColumnMissing(databaseName, TABLE_NAME, md, Columns.ISLAND_ORIGIN))
                queryBuilderAlterTable.addColumn(Columns.ISLAND_ORIGIN, "varchar(55) DEFAULT NULL");
            if (isColumnMissing(databaseName, TABLE_NAME, md, Columns.ISLAND_SIZE))
                queryBuilderAlterTable.addColumn(Columns.ISLAND_SIZE, "varchar(24) DEFAULT NULL");
            if (isColumnMissing(databaseName, TABLE_NAME, md, Columns.ISLAND_EXPANSIONS))
                queryBuilderAlterTable.addColumn(Columns.ISLAND_EXPANSIONS, "varchar(12) DEFAULT NULL");

            queryBuilderAlterTable.execute(getConnectionPool());
        } catch (final SQLException ex) {
            MessageUtil.sendError("The database could not be initialized. Error: " + ex.getMessage() + ".");
            new ErrorLog(plugin, ex, "attempt to create the table '" + TABLE_NAME + "'").writeLog();

            throw ex;
        }
    }

    /**
     * Checks whether the column is present in the table
     *
     * @param databaseName Database name
     * @param tableName    Table name
     * @param metaData     Database metadata
     * @param columnName   Name of column to check
     *
     * @return true if the column is not present in the table, otherwise false
     *
     * @throws SQLException If a database access error occurs
     * @since v3.1
     */
    private boolean isColumnMissing(@Nonnull final String databaseName, @Nonnull final String tableName, @Nonnull final DatabaseMetaData metaData, @Nonnull final String columnName) throws SQLException {
        try (ResultSet rs = metaData.getColumns(databaseName, null, tableName, columnName)) {
            return !rs.next();
        }
    }

    /**
     * Search, if online, for the player with UUID {@code executorUUID} and call the method {@link
     * #notifyAndLogError(Player, UUID, SQLException, Operation, String)}.
     *
     * @see #notifyAndLogError(Player, UUID, SQLException, Operation, String)
     * @since v3.1
     */
    private void notifyAndLogError(@Nonnull final UUID executorUUID, @Nonnull final UUID targetUUID, @Nonnull final SQLException ex, @Nonnull final Operation op, @Nonnull final String action) {
        // If the player is online, notifies the error
        notifyAndLogError(Bukkit.getPlayer(executorUUID), targetUUID, ex, op, action);
    }

    /**
     * Notifies the player that a problem with the database has occurred and logs all exception information in a
     * file.
     *
     * @param executor   Player who triggered the query and needs to be notified or {@code null}  if no one is to
     *                   be notified
     * @param targetUUID UUID of the target player of the query
     * @param ex         Exception that occurred
     * @param op         Query type
     * @param action     Action you tried to execute with the failed query
     *
     * @see ErrorLog
     * @since v3.1
     */
    private void notifyAndLogError(@Nullable final Player executor, @Nonnull final UUID targetUUID, @Nonnull final SQLException ex, @Nonnull final Operation op, @Nonnull final String action) {
        if (executor != null)
            MessageUtil.sendMessage(executor, op == Operation.FETCH ? FETCH_ERROR_MESSAGE : SAVE_ERROR_MESSAGE);

        MessageUtil.sendError("Database error: " + ex.getMessage() + ".");
        new ErrorLog(plugin, ex, "UUID: " + targetUUID, action, getDataDump(targetUUID)).writeLog();
    }

    /**
     * List of database operations
     */
    private enum Operation {
        FETCH, STORE
    }
}