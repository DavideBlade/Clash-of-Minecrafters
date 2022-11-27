/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters;

import com.gmail.davideblade99.clashofminecrafters.Updater.ResponseHandler;
import com.gmail.davideblade99.clashofminecrafters.command.CommandFramework;
import com.gmail.davideblade99.clashofminecrafters.command.label.*;
import com.gmail.davideblade99.clashofminecrafters.handler.*;
import com.gmail.davideblade99.clashofminecrafters.listener.inventory.ShopClick;
import com.gmail.davideblade99.clashofminecrafters.listener.island.AntiGrief;
import com.gmail.davideblade99.clashofminecrafters.listener.player.*;
import com.gmail.davideblade99.clashofminecrafters.listener.raid.*;
import com.gmail.davideblade99.clashofminecrafters.menu.holder.MenuInventoryHolder;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.schematic.awe.AsyncWEPaster;
import com.gmail.davideblade99.clashofminecrafters.schematic.worldedit.WEPaster;
import com.gmail.davideblade99.clashofminecrafters.setting.Settings;
import com.gmail.davideblade99.clashofminecrafters.storage.DatabaseFactory;
import com.gmail.davideblade99.clashofminecrafters.storage.PlayerDatabase;
import com.gmail.davideblade99.clashofminecrafters.storage.sql.AbstractSQLDatabase;
import com.gmail.davideblade99.clashofminecrafters.util.bukkit.MessageUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.primesoft.asyncworldedit.api.IAsyncWorldEdit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.UUID;

/* TODO list:
 *  Aggiungere supporto ad AsyncWorldEdit e FastAsyncWorldEdit (beta da questo punto in poi) -> aggiornare wiki
 *  Aggiungere comando /com upgrade <NomeStruttura> (permesso: clashofminecrafters.command.?) così che possano essere utilizzati in GUI custom create con altri plugin come ChestCommands -> Grazie a https://www.spigotmc.org/members/fede1132.118978/
 *  Tempi configurabili per l'inizio e la durata della guerra
 *  Supporto per PlaceholderAPI + rendere disabilitabile scoreboard dal config
 *  Tab completer -> vedi FullCloak
 *  Aggiungere tempo massimo al raid
 *  Preview della zona in cui verrebbe incollata la schematic (interfaccia Placeable)
 *  Rank in base ai trofei (come le leghe)
 *  Anche il difensore in un raid vince o perde i trofei
 *  API
 *  Scudo che permette di non ricevere attacchi (viene dato in automatico a chi perde un raid; in più è acquistabile)
 *  Creare video dimostrativo
 *  Aggiungere boost risorse (temporaneo, es. di 1 ora): ogni volta che un giocatore boostato riceve delle risorse, queste vengono moltiplicate (es. anziché ricevere 10 trofei a seguito della vincita di un raid ne riceve 20)
 *  Aggiungere che alcuni livelli sono sbloccabili solo con determinati livelli del municipio (aggiornare wiki con il nuovo config.yml)
 *  Sistemare permessi di default nel plugin.yml (per es. /trophies potrebbe essere eseguito dai giocatori senza problemi)
 *  Supporto per SQLite e MongoDB
 */

//TODO: leggere: https://dev.mysql.com/doc/refman/8.0/en/innodb-transaction-model.html + https://www.spigotmc.org/threads/best-practices-for-async-sql-i-o.578716/page-2#post-4504656

//TODO: https://github.com/Staartvin/Autorank-2/tree/master/src/me/armar/plugins/autorank
//TODO: dare un'occhio a: https://www.google.com/search?q=java+configchecker+builder&rlz=1C1JZAP_itIT969IT971&oq=java+configchecker+builder&aqs=chrome..69i57j33i10i160.4092j1j1&sourceid=chrome&ie=UTF-8
//TODO: dare un'occhiata a: https://helpch.at/
//TODO: dare un'occhiata a: https://docs.eliteminecraftplugins.com/elitepets/ e https://www.gitbook.com/?utm_source=content&utm_medium=trademark&utm_campaign=-MgzZltL9-2FnEMsC3sM
//TODO: dare un'occhio (per gli item, cosa poter settare ecc.) a: https://www.spigotmc.org/wiki/mmoitems-item-stats-options/
//TODO: test SQLite com JUnit: https://www.spigotmc.org/threads/tutorial-plugin-sqlite-junit-testing.293352/

//TODO: info sulla skin delle teste
// https://www.spigotmc.org/threads/dynamic-player-head-textures.482194/#post-4047576
// https://www.spigotmc.org/threads/solved-custom-head-texture.74387/
// https://www.spigotmc.org/threads/tutorial-get-player-heads-without-lag.396186/
// https://www.google.com/search?q=store+player+head+skin+spigot&rlz=1C1JZAP_itIT969IT971&oq=store+player+head+skin+spigot&aqs=chrome..69i57.4125j0j9&sourceid=chrome&ie=UTF-8

//TODO: 1.18 Clash of Clans buildings: https://www.planetminecraft.com/project/clash-of-clans-every-building-with-every-upgrade-th-1-to-th12/

//TODO: https://www.spigotmc.org/resources/1-13-1-18-craftofclans.22966/

//TODO: catch errori non gestiti da nessuno: https://stackoverflow.com/a/13507137 + https://docs.oracle.com/javase/6/docs/api/java/lang/Thread.html#setDefaultUncaughtExceptionHandler%28java.lang.Thread.UncaughtExceptionHandler%29 + https://www.javatpoint.com/java-thread-setdefaultuncaughtexceptionhandler-method + https://stackoverflow.com/a/46591885
public final class CoM extends JavaPlugin {

    /**
     * Formatter used by the whole plugin which formats dates to something like "[17-03-2001 19:18:35]" for any
     * given {@link LocalDateTime}.
     */
    public static final DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder().appendLiteral('[').appendPattern("dd-MM-yyyy HH:mm:ss").appendLiteral(']').toFormatter();

    /** List of Minecraft versions on which the plugin has been tested */
    private final static String[] SUPPORTED_VERSIONS = {"1.14", "1.15", "1.16", "1.17", "1.18", "1.19"};

    private static CoM instance;

    private Settings settings;
    private PlayerDatabase database;
    private PlayerHandler playerHandler;
    private ClanHandler clanHandler;
    private VillageHandler villageHandler;
    private SchematicHandler schematicHandler;
    private WarHandler warHandler;
    private ArcherHandler archerHandler;
    private GuardianHandler guardianHandler;

    public CoM() {
        super();
    }

    protected CoM(@Nonnull final JavaPluginLoader loader, @Nonnull final PluginDescriptionFile description, @Nonnull final File dataFolder, @Nonnull final File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void onEnable() {
        //TODO: suggerimenti: https://www.spigotmc.org/conversations/clash-of-minecrafters.171775/

        //TODO: Aggiungere al guardiano teletrasporto casuale dietro il giocatore (cosicché possa sempre raggiungerlo nel caso sia bloccato da dei blocchi)

        //TODO: Consigliare di utilizzare HealthBar (configurarlo in modo che funzioni solo nel mondo delle isole)

        try {
            if (!checkVersion()) {
                MessageUtil.sendError("This version is only compatible with the following versions:" + String.join(", ", SUPPORTED_VERSIONS));
                disablePlugin();
                return;
            }

            instance = this;
            settings = new Settings(this);
            try {
                database = DatabaseFactory.getInstance(this);
            } catch (final Exception ignored) {
                disablePlugin();
                return;
            }
            playerHandler = new PlayerHandler(this);
            clanHandler = new ClanHandler(this);
            villageHandler = new VillageHandler(this);
            schematicHandler = new SchematicHandler(this);
            warHandler = new WarHandler(this);
            archerHandler = new ArcherHandler(this);
            guardianHandler = new GuardianHandler(this);

            registerListeners();
            registerCommands();

            final String pluginVersion = getDescription().getVersion();

            if (settings.isCheckForUpdate()) {
                new Updater(this).checkForUpdate(new ResponseHandler() {
                    @Override
                    public void onUpdateFound(@Nonnull final String newVersion) {
                        final String currentVersion = pluginVersion.contains(" ") ? pluginVersion.split(" ")[0] : pluginVersion;

                        MessageUtil.sendWarning("Found a new version: " + newVersion + " (Yours: v" + currentVersion + ")");
                        MessageUtil.sendWarning("Download it on spigot:");
                        MessageUtil.sendWarning("spigotmc.org/resources/31180");
                    }
                });
            }

            playerHandler.loadUUIDMap();

            // Check for all dependencies
            checkDependencies();


            new Messages(this); // Setup messages files

            // Create and save village world
            setupVillageWorld();


            // Saves in cache players already online (possible in case of reload)
            for (Player player : Bukkit.getOnlinePlayers())
                getUser(player);


            if (pluginVersion.contains("alpha"))
                MessageUtil.sendWarning("WARNING! This is an alpha version and backward compatibility of future versions is not guaranteed. Do not use on active servers.");
            MessageUtil.sendWarning("Clash of minecrafters has been enabled. (Version: " + pluginVersion + ")");
        } catch (final Throwable throwable) {
            // Any unexpected issues not handled
            throwable.printStackTrace();

            disablePlugin();
        }
    }

    @Override
    public void onDisable() {
        // If the plugin has not been completely enabled, warHandler may be null
        if (warHandler != null)
            warHandler.removeAllIslandsUnderAttack(Messages.getMessage(MessageKey.RELOAD)); // Stop all raids

        // Close all plugin menus
        for (Player player : Bukkit.getOnlinePlayers())
            if (player.getOpenInventory().getTopInventory().getHolder() instanceof MenuInventoryHolder)
                player.closeInventory();

        Bukkit.getScheduler().cancelTasks(this);
        HandlerList.unregisterAll(this);

        if (database instanceof AbstractSQLDatabase)
            ((AbstractSQLDatabase) database).shutdown();

        instance = null;
        settings = null;
        database = null;
        playerHandler = null;
        clanHandler = null;
        villageHandler = null;
        schematicHandler = null;
        warHandler = null;
        archerHandler = null;
        guardianHandler = null;

        MessageUtil.sendWarning("Clash of minecrafters has been disabled. (Version: " + getDescription().getVersion() + ")");
    }

    @Override
    @Nonnull
    public Settings getConfig() {
        return settings;
    }

    @Nonnull
    public PlayerDatabase getDatabase() {
        return database;
    }

    @Nonnull
    public PlayerHandler getPlayerHandler() {
        return playerHandler;
    }

    @Nonnull
    public ClanHandler getClanHandler() {
        return clanHandler;
    }

    @Nonnull
    public VillageHandler getVillageHandler() {
        return villageHandler;
    }

    @Nonnull
    public SchematicHandler getSchematicHandler() {
        return schematicHandler;
    }

    @Nonnull
    public WarHandler getWarHandler() {
        return warHandler;
    }

    @Nonnull
    public ArcherHandler getArcherHandler() {
        return archerHandler;
    }

    @Nonnull
    public GuardianHandler getGuardianHandler() {
        return guardianHandler;
    }

    // This will create a new user if there is not a match
    @Nonnull
    public User getUser(@Nonnull final Player player) {
        User user = playerHandler.getPlayer(player.getUniqueId());

        if (user == null)
            /*
             * If the player has never entered the server a new User will be created
             * but it will not be cached (it will be done only at the next get)
             */
            //TODO: inserire in cache fin da subito
            user = new User(this, player);
        else
            /*
             * Each time a search is made for a player, who has already entered the server at least once,
             * his corresponding User will be cached. If the player is not online at that time (or is trying to enter,
             * for example at the AsyncPlayerPreLoginEvent, and thus is not yet in the list of online players),
             * the User will have an OfflinePlayer instead of a Player as a reference (player base).
             * As a result, it will be considered as an offline player and therefore
             * will not receive messages, be teleported, etc.
             * If I do not update the reference with User#setBasePlayer(Player), even though
             * the player has logged in and therefore now online, he will still be considered offline
             * and therefore will continue to be treated as such (no messages, teleports, etc.).
             */
            user.setBasePlayer(player);

        return user;
    }

    // This will return null if there is not a match
    @Nullable
    public User getUser(@Nonnull final UUID uuid) {
        //TODO: viene inserito in cache fin da subito?
        return playerHandler.getPlayer(uuid);
    }

    // This will return null if there is not a match
    @Nullable
    public User getUser(@Nonnull final String playerName) {
        //TODO: viene inserito in cache fin da subito?
        return playerHandler.getPlayer(playerName);
    }

    public void disablePlugin() {
        if (isEnabled()) {
            // Notify that the plugin will be disabled
            Bukkit.getPluginManager().callEvent(new PluginDisableEvent(this));

            // Disable the plugin
            setEnabled(false);
        }
    }

    private void setupVillageWorld() {
        /*
         * "v1_14_R1" -> Server runs from 1.14.x
         * "v1_15_R1" -> Server runs from 1.15.x
         * "v1_16_R1" -> Server is running 1.16 or 1.16.1
         * "v1_16_R2" -> Server is running 1.16.2 or 1.16.3
         * "v1_16_R3" -> Server is running 1.16.4 or 1.16.5
         * "v1_17_R1" -> Server runs from 1.17 to 1.17.1
         * "v1_18_R1" -> Server runs from 1.18 to 1.18.1
         * "v1_18_R2" -> Server runs from 1.18.2
         * "v1_19_R1" -> Server runs from 1.19 to 1.19.2 (currently)
         */

        final ChunkGenerator chunkGenerator;
        try {
            final String packageName = CoM.class.getPackage().getName();
            final String internalsName = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            chunkGenerator = (ChunkGenerator) Class.forName(packageName + ".world." + internalsName + ".EmptyWorldGenerator").getDeclaredConstructor().newInstance();
        } catch (final Exception unknownVersion) {
            throw new RuntimeException("Unknown server version: " + Bukkit.getServer().getClass().getPackage().getName());
        }

        final World world = getServer().createWorld(new WorldCreator(VillageHandler.VILLAGE_WORLD_NAME).generator(chunkGenerator)); // Load or create world

        // Setup world
        world.setDifficulty(Difficulty.EASY);
        world.setPVP(false);
        world.setSpawnFlags(true, false);
        world.setAutoSave(true);
        world.setKeepSpawnInMemory(false);

        world.setAnimalSpawnLimit(0);
        world.setMonsterSpawnLimit(5);
        world.setWaterAnimalSpawnLimit(0);

        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false); // Block natural spawn
        world.setGameRule(GameRule.KEEP_INVENTORY, false);
        world.setGameRule(GameRule.MOB_GRIEFING, false);
        world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);


        MessageUtil.sendInfo("World of villages has been loaded.");
    }

    private void checkDependencies() {
        final Plugin worldEdit = Bukkit.getPluginManager().getPlugin("WorldEdit");
        if (worldEdit == null || !worldEdit.isEnabled()) {
            MessageUtil.sendError("WorldEdit should handle the schematic(s) but it isn't enabled.");
            MessageUtil.sendError("Clash of minecrafters " + getDescription().getVersion() + " was disabled.");
            disablePlugin();
            return;
        }

        // If installed, use AsyncWorldEdit for schematic
        final Plugin awe = Bukkit.getPluginManager().getPlugin("AsyncWorldEdit");
        if (awe != null && awe.isEnabled()) {
            schematicHandler.setSchematicPaster(new AsyncWEPaster((IAsyncWorldEdit) awe));
            return;
        }

        // Use WorldEdit if there are no other alternative plugins
        schematicHandler.setSchematicPaster(new WEPaster());
    }

    private void registerCommands() {
        CommandFramework.register(this, new ClanCommand(this));
        CommandFramework.register(this, new IslandCommand(this));
        CommandFramework.register(this, new TakeCommand(this));
        CommandFramework.register(this, new AddCommand(this));
        CommandFramework.register(this, new RaidCommand(this));
        CommandFramework.register(this, new BalanceCommand(this));
        CommandFramework.register(this, new OpenCommand(this));
        CommandFramework.register(this, new UpgradeCommand(this));
        CommandFramework.register(this, new ComCommand(this));
        CommandFramework.register(this, new WarCommand(this));
        CommandFramework.register(this, new TrophiesCommand(this));
        CommandFramework.register(this, new ExtractorsCommand(this));
    }

    private void registerListeners() {
        final PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerJoin(this), this);
        pm.registerEvents(new ShopClick(this), this);
        pm.registerEvents(new PlayerMove(this), this);
        pm.registerEvents(new EntityDamageByEntity(this), this);
        pm.registerEvents(new EntityDeath(this), this);
        pm.registerEvents(new PlayerDeath(this), this);
        pm.registerEvents(new PlayerQuit(this), this);
        pm.registerEvents(new EntityCombust(this), this);
        pm.registerEvents(new AsyncPlayerPreLogin(this), this);
        pm.registerEvents(new RaidWin(this), this);
        pm.registerEvents(new RaidLost(this), this);
        pm.registerEvents(new AntiGrief(this), this);
        pm.registerEvents(new EntityChangeTarget(this), this);
    }

    /**
     * @return true if the Minecraft server version is supported, otherwise false
     */
    private boolean checkVersion() {
        final String serverVersion = Bukkit.getVersion();
        for (String version : SUPPORTED_VERSIONS)
            if (serverVersion.contains(version))
                return true;

        return false;
    }


    //TODO: fare sito per le traduzioni come quello di essentials
    //TODO aggiungere ai costruttori che non posso essere aggiunti valori nulli


    /*TODO fare sistema di upgrade (/upgrade) con varie schematic per livello (es. livello 1 c'� la schematic ArcherTower1, livello 2 c'� ArcherTower2 e cos� via)
    ed ologrammi che indicano quanto produce, a che livello � ecc. -> non devono pi� poter distruggere le costruzioni
    Scriverlo/aggiornarlo sulla wiki.
    */
    //TODO aggiungere % all'attacco in base ai cuori del guardiano. Se supera il 50% lo vince senn� lo perde. Se arriva al 100% vince tutti i trofei e tutte le monete senn� (se oltre il 50% ma sotto il 100 n� vince la met�)
    /*
    # trophies received if completed the attack at 100%
    # if the attack is 50%, this value will be divided by two
    Trophies win: 25
    # trophies removed if the attack is completed less than 50%
    Trophies lost: 15
     */
    //TODO in base alla % della vittoria fare anche l'exp data al clan

    //TODO aggiornare completamente l'architettura delle isole:
    // Non posso distruggere niente e le espansioni le devono fare tramite comando (es. /island expand) verso dove guardano.
    // Aggiornare anche sulla wiki


    //TODO alle SQLException fare aggiungere throws SQLException al metodo in modo che possa gestirlo il chiamante in maniera migliore (es. hasArcher(UUID playerUUID) throws SQLException)

    //TODO mettere le rule dei vari mondi impostabili dal config
    //TODO aggiungere vari livelli delle costruzioni (es. livello 1 dell'estrattore hai una cosa. livello 2 ne hai un'altra ecc.)---> abilitabile del config. se disabilitato usa la schematic del livello 1
    //TODO clan chat
    //TODO controllare che la torre degli arcieri e gli estrattori stiano totalmente dentro l'isola e non solo il punto in cui clicca il player
    // Creare meteore: https://bukkit.org/threads/tutorial-custom-entities-meteor.93899/ (potrei usarlo per creare la palla del mortaio)

    //TODO: https://www.spigotmc.org/resources/essentials-mysql-storage-extension.25673/ -> Estensione di Essentials per salvare su MySQL

    //TODO: i proprietari del clan possono impostare un minimo di trofei necessario affinché i giocatori possano unirsi al clan

    //TODO: rimpire per il 50% la cache con gli ultimi giocatori che sono entrati nel server (o meno, se non ne sono mai entrati così tanti)


    //TODO: quando possibile non usare questo metodo statico ma passare l'istanza col costruttore
    public static CoM getInstance() {
        return instance;
    }
}