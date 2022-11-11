/*
 * Copyright (c) DavideBlade.
 *
 * All Rights Reserved.
 */

package com.gmail.davideblade99.clashofminecrafters;

import com.gmail.davideblade99.clashofminecrafters.Updater.ResponseHandler;
import com.gmail.davideblade99.clashofminecrafters.clan.ClanHandler;
import com.gmail.davideblade99.clashofminecrafters.clan.WarHandler;
import com.gmail.davideblade99.clashofminecrafters.command.CommandFramework;
import com.gmail.davideblade99.clashofminecrafters.command.label.*;
import com.gmail.davideblade99.clashofminecrafters.configuration.Config;
import com.gmail.davideblade99.clashofminecrafters.island.IslandHandler;
import com.gmail.davideblade99.clashofminecrafters.island.creature.ArcherHandler;
import com.gmail.davideblade99.clashofminecrafters.island.creature.GuardianHandler;
import com.gmail.davideblade99.clashofminecrafters.listener.inventory.ShopClick;
import com.gmail.davideblade99.clashofminecrafters.listener.island.AntiGrief;
import com.gmail.davideblade99.clashofminecrafters.listener.player.*;
import com.gmail.davideblade99.clashofminecrafters.listener.raid.*;
import com.gmail.davideblade99.clashofminecrafters.menu.holder.MenuInventoryHolder;
import com.gmail.davideblade99.clashofminecrafters.message.MessageKey;
import com.gmail.davideblade99.clashofminecrafters.message.Messages;
import com.gmail.davideblade99.clashofminecrafters.player.PlayerHandler;
import com.gmail.davideblade99.clashofminecrafters.player.User;
import com.gmail.davideblade99.clashofminecrafters.schematic.SchematicHandler;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.UUID;

/* TODO list:
 *  Aggiungere supporto ad AsyncWorldEdit e FastAsyncWorldEdit + rimuovere il mio formato interno (beta da questo punto in poi) -> aggiornare wiki
 *  Supporto per PaperSpigot
 *  Tab completer -> vedi FullCloak
 *  Splittare in 2 estrattori di elisir e di oro
 *  Aggiungere tempo massimo al raid
 *  Rank in base ai trofei (come le leghe)
 *  Anche il difensore in un raid vince o perde i trofei
 *  API
 *  Supporto per SQLite
 */

//TODO: https://github.com/Staartvin/Autorank-2/tree/master/src/me/armar/plugins/autorank

//TODO: dare un'occhio a: https://www.google.com/search?q=spigot+entity+nbttag+tutorial&rlz=1C1JZAP_itIT969IT971&sxsrf=APq-WBsJNMU_6XwGq7Bo1M2-1ln43D9ThQ%3A1645052927738&ei=_4MNYrzLLJGAkwWolLeYAg&ved=0ahUKEwj8xpPbq4X2AhURwKQKHSjKDSMQ4dUDCA8&uact=5&oq=spigot+entity+nbttag+tutorial&gs_lcp=Cgdnd3Mtd2l6EAM6BwgAEEcQsANKBAhBGABKBAhGGABQyARYxwhg0wloAXABeACAAbQBiAHDBpIBAzIuNZgBAKABAcgBCMABAQ&sclient=gws-wiz

//TODO: dare un'occhio a: https://www.google.com/search?q=java+configchecker+builder&rlz=1C1JZAP_itIT969IT971&oq=java+configchecker+builder&aqs=chrome..69i57j33i10i160.4092j1j1&sourceid=chrome&ie=UTF-8

//TODO: dare un'occhiata a: https://helpch.at/

//TODO: dare un'occhiata a: https://docs.eliteminecraftplugins.com/elitepets/ e https://www.gitbook.com/?utm_source=content&utm_medium=trademark&utm_campaign=-MgzZltL9-2FnEMsC3sM

//TODO: dare un'occhiata a: https://www.google.com/search?q=cache+inventory+spigot&rlz=1C1JZAP_itIT969IT971&oq=cache+inventory+spigot&aqs=chrome..69i57j35i19i39j35i39j0i131i433j0i512j69i60l3.1835j0j1&sourceid=chrome&ie=UTF-8

//TODO: dare un'occhio (per gli item, cosa poter settare ecc.) a: https://www.spigotmc.org/wiki/mmoitems-item-stats-options/

//TODO: dare un'occhio a: https://github.com/lorddusk/Bagginses/tree/master/src/main/java/nl/lang2619/bagginses

//TODO: to test SQLite with JUnit: https://www.spigotmc.org/threads/tutorial-plugin-sqlite-junit-testing.293352/

//TODO: info sulla skin delle teste
// https://www.spigotmc.org/threads/dynamic-player-head-textures.482194/#post-4047576
// https://www.spigotmc.org/threads/solved-custom-head-texture.74387/
// https://www.spigotmc.org/threads/tutorial-get-player-heads-without-lag.396186/
// https://www.google.com/search?q=store+player+head+skin+spigot&rlz=1C1JZAP_itIT969IT971&oq=store+player+head+skin+spigot&aqs=chrome..69i57.4125j0j9&sourceid=chrome&ie=UTF-8

//TODO: 1.18 Clash of Clans buildings: https://www.planetminecraft.com/project/clash-of-clans-every-building-with-every-upgrade-th-1-to-th12/

//TODO: altre idee da https://www.spigotmc.org/resources/1-13-1-18-craftofclans.22966/

//TODO: per le valute anziché usare gli int perché non uso un BigInt?

//TODO: ottimizzare codice e migliorarlo (aggiungere alle features No lag)

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

    private Config settings;
    private PlayerDatabase database;
    private PlayerHandler playerHandler;
    private ClanHandler clanHandler;
    private IslandHandler islandHandler;
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
        // Added: Controllo sulle colonne sul MySQL (adesso verranno aggiunte in automatico le nuove colonne delle future versioni dopo che un database è già stato creato)
        // Added: Statistiche (configurabili) del guardiano che difende i villaggi in base al livello del municipio
        // Added: Adesso è possibile rimuovere la sezione dei town hall, degli estrattori, delle torri dell'arciere e dei clan dal config.yml per rimuovere la funzionalità (nei primi 3 casi, non verranno più mostrate nel menù /upgrade) //TODO: scriverlo sulla wiki
        // Added: Separate le configurazioni dell'estrattore di elisir e d'oro //TODO: aggiornare wiki

        // Removed: Rimossi i valori di default in caso di dati mancanti dal config (dalle sezioni town hall, estrattori e torri dell'arciere) per una migliore manutenibilità del codice: adesso se mancheranno i valori obbligatori la funzionalità verrà disattivata (non verrà più mostrata nel menù /upgrade) con un alert //TODO: scrivere sulla wiki (tra cui sulle FAQ: se si disattiva la funzionalità del town hall/estrattori/... significa che c'è un errore nella configurazione. Controlla i log per risolverlo. Nessun dato andrà perso, solo verrà disattivata la funzionalità di incremento livello tramite il menù /upgrade così come (fino alla risoluzione del problema) gli estrattori non produrranno più, i guardiani non avranno più le statistiche ecc.)

        // Fixed: Adesso se vengono eliminati dei livelli dal config.yml dopo che qualche giocatore ne ha già acquistato qualcosa non viene più lanciato errore in console.
        // Fixed: Corretto il messaggio di errore in caso di comando sconosciuto.

        // Other: Pubblicato codice sorgente
        // Other: Aggiunto messaggio di warning in caso manchi una delle seguenti sezioni: town hall, estrattori e torri dell'arciere
        // Other: Modificato il metodo di salvataggio su file o MySQL dei dati dei giocatori
        // Other: Migliorato il codice
        // Other: Modificata la comunicazione con il MySQL
        // Other: Modificato esteticamente il file di log degli errori sul database MySQL
        // Other: Divisa l'etichetta "Extractors" con "Gold extractors" e "Elixir extractors" nel confg.yml
        // Other: Modificata l'etichetta "TownHall" con "Town halls" nel config.yml
        // Other: Modificata l'etichetta "ArcherTowers" con "Archer towers" nel config.yml
        // Other: Modificata colorazione dei messaggi in console: gli errori saranno in rosso, i warning in giallo //TODO: scrivere sulla wiki
        // Other: Aggiunti messaggi "No buildings" e "Disabled extractors" tra i messaggi traducibili
        //TODO: scrivere nei commenti sul config.yml che se una sezione delle costruzioni viene rimossa, questo significa disabilitare la funzionalità
        //TODO: aggiornare wiki con il nuovo config.yml


        //TODO: Aggiungere statistiche diverse al guardiano in base al livello del municipio (es. effetti di pozioni, diverse velocità, teletrasporto casuale dietro il giocatore (purché possa sempre raggiungerlo) ecc.) + aggiornare wiki

        //TODO: Creare video dimostrativo
        //TODO: Consigliare di utilizzare HealthBar (configurarlo in modo che funzioni solo nel mondo delle isole)
        //TODO: Aggiungere che alcuni livelli sono sbloccabili solo con determinati livelli del municipio
        //TODO: aggiornare wiki con il nuovo config.yml

        //TODO: aggiungere boost risorse (temporaneo, es. di 1 ora): ogni volta che un giocatore boostato riceve delle risorse, queste vengono moltiplicate (es. anziché ricevere 10 trofei a seguito della vincita di un raid ne riceve 20)

        try {
            if (!checkVersion()) {
                MessageUtil.sendError("This version is only compatible with the following versions:" + String.join(", ", SUPPORTED_VERSIONS));
                disablePlugin();
                return;
            }

            instance = this;
            settings = new Config(this);
            try {
                database = DatabaseFactory.getInstance(this);
            } catch (final Exception ignored) {
                disablePlugin();
                return;
            }
            playerHandler = new PlayerHandler(this);
            clanHandler = new ClanHandler(this);
            islandHandler = new IslandHandler(this);
            schematicHandler = new SchematicHandler(this);
            warHandler = new WarHandler(this);
            archerHandler = new ArcherHandler(this);
            guardianHandler = new GuardianHandler();

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

            // Check if WorldEdit is necessary
            if (settings.useIslandSchematic() || settings.useElixirExtractorSchematic() || settings.useGoldExtractorSchematic() || settings.useArcherSchematic())
                checkDependencies();


            new Messages(this); // Setup messages files

            // Create and save island file
            setupIslandWorld();


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
        islandHandler = null;
        schematicHandler = null;
        warHandler = null;
        archerHandler = null;
        guardianHandler = null;

        MessageUtil.sendWarning("Clash of minecrafters has been disabled. (Version: " + getDescription().getVersion() + ")");
    }

    @Override
    public Config getConfig() {
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
    public IslandHandler getIslandHandler() {
        return islandHandler;
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

    private void setupIslandWorld() {
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
            throw new RuntimeException("Unknown server version: " + Bukkit.getServer().getClass().getPackage().getName()); //TODO: quando viene lanciata questa eccezione cosa succede? il plugin si disabilita? il mondo non viene più generato?
        }

        final World world = getServer().createWorld(new WorldCreator("Islands").generator(chunkGenerator)); // Load or create world

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


        MessageUtil.sendInfo("World of islands has been loaded.");
    }

    private void checkDependencies() {
        final Plugin worldEdit = Bukkit.getPluginManager().getPlugin("WorldEdit");
        if (worldEdit == null || !worldEdit.isEnabled()) {
            MessageUtil.sendError("WorldEdit should handle the schematic(s) but it isn't enabled.");
            MessageUtil.sendError("Clash of minecrafters " + getDescription().getVersion() + " was disabled.");
            disablePlugin();
        }
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
        CommandFramework.register(this, new SchemCommand(this));
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
    //TODO aggiungere ai costruttori che non posso essere aggiunti valori nulli + mettere final ovunque (dove si pu�)


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

    //TODO aggiungere controllo al config che controlla tutti i numberi che non possano essere negativi
    //TODO mettere le rule dei vari mondi impostabili dal config
    //TODO aggiungere vari livelli delle costruzioni (es. livello 1 dell'estrattore hai una cosa. livello 2 ne hai un'altra ecc.)---> abilitabile del config. se disabilitato usa la schematic del livello 1
    //TODO clan chat
    //TODO controllare che la torre degli arcieri e gli estrattori stiano totalmente dentro l'isola e non solo il punto in cui clicca il player
    // Creare meteore: https://bukkit.org/threads/tutorial-custom-entities-meteor.93899/ (potrei usarlo per creare la palla del mortaio)

    //TODO: https://www.spigotmc.org/resources/essentials-mysql-storage-extension.25673/ -> Estensione di Essentials per salvare su MySQL

    //TODO: guardare codice di https://www.spigotmc.org/resources/aoneblock.96861/

    //TODO: i proprietari del clan possono impostare un minimo di trofei necessario affinché i giocatori possano unirsi al clan

    //TODO: rimpire per il 50% la cache con gli ultimi giocatori che sono entrati nel server (o meno, se non ne sono mai entrati così tanti)

    //TODO: quando possibile non usare questo metodo statico ma passare l'istanza col costruttore
    public static CoM getInstance() {
        return instance;
    }
}