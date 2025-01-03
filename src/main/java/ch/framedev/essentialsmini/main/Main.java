package ch.framedev.essentialsmini.main;

import ch.framedev.essentialsmini.commands.playercommands.BackpackCMD;
import ch.framedev.essentialsmini.commands.playercommands.EnchantCMD;
import ch.framedev.essentialsmini.commands.playercommands.SaveInventoryCMD;
import ch.framedev.essentialsmini.commands.playercommands.VanishCMD;
import ch.framedev.essentialsmini.commands.servercommands.LagCMD;
import ch.framedev.essentialsmini.database.*;
import ch.framedev.essentialsmini.managers.*;
import ch.framedev.essentialsmini.utils.*;
import ch.framedev.simplejavautils.SimpleJavaUtils;
import com.google.gson.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * License is <a href="file:../../../../../../LICENSE.md">LICENSE</a>
 *
 * @author FrameDev
 */
public class Main extends JavaPlugin {

    private ThreadLocal<Utilities> utilities = new ThreadLocal<>();

    private static ArrayList<String> silent;

    private Thread thread;

    /* Commands, TabCompleters and Listeners List */
    // Register Commands HashMap
    private HashMap<String, CommandExecutor> commands;

    // Register TabCompleter HashMap
    private HashMap<String, TabCompleter> tabCompleters;

    // Register Listener List
    private ArrayList<Listener> listeners;

    private Map<String, Object> limitedHomesPermission;

    /* Json Config.json */
    //@Getter
    //private JsonConfig jsonConfig;

    private boolean homeTP = false;

    /* Material Manager */
    private MaterialManager materialManager;

    // Variables
    private Variables variables;

    private KeyGenerator keyGenerator;

    private VaultManager vaultManager;

    /* Custom Config File */
    private File customConfigFile;

    private FileConfiguration customConfig;

    private LagCMD.SpigotTimer spigotTimer;

    public ArrayList<String> players;

    private static Main instance;

    // RegisterManager
    private RegisterManager registerManager;

    private Map<String, Object> limitedHomes;

    // Variables for DataBases
    private boolean mysql;
    private boolean sql;

    private String currencySymbol;

    private ArrayList<String> offlinePlayers;
    private File infoFile;

    private FileConfiguration infoCfg;

    private MongoDBUtils mongoDbUtils;

    private String configVersion;
    private File settingsFile;
    private FileConfiguration settingsCfg;
    private Logger logger;
    private BukkitTask bukkitTaskConfig;
    private BukkitTask limitedHomesTask;

    @Override
    public void onEnable() {
        this.limitedHomes = new HashMap<>();
        this.limitedHomesPermission = new HashMap<>();
        logger = Logger.getLogger("EssentialsMini");
        BasicConfigurator.configure();

        // Singleton initializing
        instance = this;

        this.utilities.set(new Utilities());

        // Set Dev Build
        // TODO: Update
        utilities.get().setDev(false);

        // Info FileConfiguration
        this.infoFile = new File(getDataFolder(), "info.yml");
        this.infoCfg = YamlConfiguration.loadConfiguration(infoFile);

        // Create Messages Files
        createCustomMessagesConfig();
        Config.saveDefaultConfigValues("messages");
        Config.saveDefaultConfigValues("messages_de-DE");
        Config.saveDefaultConfigValues("messages_en-EN");
        try {
            reloadCustomConfig();
        } catch (UnsupportedEncodingException ex) {
            Main.getInstance().getLogger4J().log(Level.ERROR, "Error", ex);
        }
        List<String> comments = getComments();
        getConfig().options().setHeader(comments);
        getConfig().options().parseComments(true);
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        Config.updateConfig();
        Config.loadConfig();
        Config.saveDefaultConfigValues();
        Config.saveDefaultConfigValues("settings");
        this.settingsFile = new File(getDataFolder(), "settings.yml");
        this.settingsCfg = YamlConfiguration.loadConfiguration(settingsFile);

        if (!new File("plugins/EssentialsMini/messages_de-DE.yml").exists() && !new File("plugins/EssentialsMini/messages_en-EN.yml").exists()) {
            new UpdateChecker().download("https://framedev.ch/sites/downloads/essentialsminidata/Config_Examples.zip", "plugins/EssentialsMini", "Config_Examples.zip");
            try {
                new UnzipUtility().unzip("plugins/EssentialsMini/Config_Examples.zip", "plugins/EssentialsMini");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (!new File("plugins/EssentialsMini/Config_Examples.zip").delete()) {
                getLogger4J().error("Could not delete Config_Examples.zip");
            }
        }
        if (!new File(getDataFolder() + "/messages-examples").exists())
            if (!new File(getDataFolder() + "/messages-examples").mkdir()) {
                getLogger4J().error("Could not create directory " + getDataFolder() + "/messages-examples");
            }
        try {
            SimpleJavaUtils utils = new SimpleJavaUtils();
            Files.copy(utils.getFromResourceFile("messages_de-DE-examples.yml", Main.class).toPath(),
                    new File(getDataFolder() + "/messages-examples/messages-de-DE-examples.yml").toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(utils.getFromResourceFile("messages_en-EN-examples.yml", Main.class).toPath(),
                    new File(getDataFolder() + "/messages-examples/messages_en-EN-examples.yml").toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(utils.getFromResourceFile("messages_fr-FR-examples.yml", Main.class).toPath(),
                    new File(getDataFolder() + "/messages-examples/messages_fr-FR-examples.yml").toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex) {
            Main.getInstance().getLogger4J().log(Level.ERROR, "Error", ex);
        }
        try {
            this.settingsCfg.save(settingsFile);
        } catch (IOException ex) {
            Main.getInstance().getLogger4J().log(Level.ERROR, "Error", ex);
        }

        // API Init
        new EssentialsMiniAPI(this);
        getLogger().info("API Loaded");

        if (getConfig().getBoolean("HomeTP")) {
            homeTP = true;
        }

        /* HashMaps / Lists Initialling */
        this.commands = new HashMap<>();
        this.listeners = new ArrayList<>();
        this.tabCompleters = new HashMap<>();

        /* MaterialManager initialling */
        this.materialManager = new MaterialManager();
        this.materialManager.saveMaterials();

        // Variables
        this.variables = new Variables();

        if (getVariables().isJsonFormat())
            this.materialManager.saveMaterialToJson();

        /* TPS Command Timer */
        this.spigotTimer = new LagCMD.SpigotTimer();

        this.keyGenerator = new KeyGenerator();

        // Create kits.yml File
        new KitManager().createCustomConfig();

        /* JsonConfig */
        //this.jsonConfig = new JsonConfig();

        silent = new ArrayList<>();

        /*
        this.file = new File(getDataFolder(), "offline.yml");
        this.cfg = YamlConfiguration.loadConfiguration(file);
        if (!cfg.contains("players")) {
            players = new ArrayList<>();
            cfg.set("players", players);
            try {
                cfg.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.players = (ArrayList<String>) cfg.getStringList("players");
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (!players.contains(offlinePlayer.getName())) {
                players.add(offlinePlayer.getName());
            }
        }
        cfg.set("players", players);
        saveCfg();*/
        if (getConfig().getBoolean("MongoDB.Boolean") || getConfig().getBoolean("MongoDB.LocalHost")) {
            this.mongoDbUtils = new MongoDBUtils();
            if (isMongoDB()) {
                for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                    getBackendManager().createUser(player, "essentialsmini_data", new BackendManager.Callback<Void>() {
                        @Override
                        public void onResult(Void result) {
                        }

                        @Override
                        public void onError(Exception exception) {
                        }
                    });
                }
                Bukkit.getConsoleSender().sendMessage(getPrefix() + "§aMongoDB Enabled!");
            }
        }
        /* MongoDB Finish */

        // LocationBackup
        if (getConfig().getBoolean("LocationsBackup")) {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + "§aLocation Backups enabled!");
        }
        UpdateScheduler updateScheduler = new UpdateScheduler();
        /* Thread for the Schedulers for save restart and .... */
        thread = new Thread(updateScheduler);
        if (!thread.isAlive()) {
            thread.start();
        } else {
            if (updateScheduler.started) {
                Bukkit.getConsoleSender().sendMessage(getPrefix() + "§aSchedulers Started!");
            }
        }

        // LimitedHomes Init
        /*limitedHomes = new HashMap<>();
        ConfigurationSection cs = getConfig().getConfigurationSection("LimitedHomes");
        if (cs != null) {
            for (String s : cs.getKeys(false)) {
                limitedHomes.put(s, getConfig().getInt("LimitedHomes." + s));
            }
        }

        HashMap<String, String> limitedHomesPermissions = new HashMap<>();
        ConfigurationSection css = getConfig().getConfigurationSection("LimitedHomesPermission");
        if (css != null) {
            for (String s : css.getKeys(false)) {
                limitedHomesPermissions.put(s, getConfig().getString("LimitedHomesPermission." + s));
            }
        }
        HashMap<String, Object> json = new HashMap<>();
        json.put("Backpack", true);
        json.put("SpawnTP", false);
        json.put("SkipNight", false);
        json.put("LocationsBackup", false);
        json.put("BackupTime", 5);
        json.put("LocationsBackupMessage", false);
        json.put("IgnoreJoinLeave", false);
        json.put("Limited", false);
        json.put("LimitedHomes", limitedHomes);
        json.put("LimitedHomesPermission", limitedHomesPermissions);
        json.put("HomeTP", true);
        json.put("ShowItem", true);
        json.put("ShowCrafting", true);
        json.put("ShowLocation", true);
        json.put("Position", true);
        json.put("JsonFormat", true);
        json.put("BackupMessages", true);
        json.put("SendPlayerUpdateMessage", true);
        json.put("ZeitgesteuerterRestart", 60);
        json.put("ZeitGesteuerterRestartBoolean", true);
        if (!getJsonConfig().contains("Prefix")) {
            for (Map.Entry<String, Object> entry : json.entrySet()) {
                getJsonConfig().set(entry.getKey(), entry.getValue());
            }
            getJsonConfig().set("Prefix", "§6[§aEssentials§bMini§6] §c» §f");
            getJsonConfig().set("JoinBoolean", true);
            getJsonConfig().set("LeaveBoolean", true);
            getJsonConfig().set("SaveInventory", false);

            getJsonConfig().saveConfig();
        }*/
        new SaveLists().setVanished();
        /*KitManager kit = new KitManager();
        kit.saveKit("Stone");*/
        //EssentialsMiniAPI.getInstance().printAllHomesFromPlayers();

        this.mysql = getConfig().getBoolean("MySQL.Use");
        this.sql = getConfig().getBoolean("SQLite.Use");

        if (sql) {
            //noinspection InstantiationOfUtilityClass
            new SQLite(getConfig().getString("SQLite.Path"), getConfig().getString("SQLite.FileName"));
        }

        if (mysql)
            //noinspection InstantiationOfUtilityClass
            new MySQL();

        if (getConfig().getBoolean("Economy.Activate")) {
            if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
                this.vaultManager = new VaultManager(this);
            }
        }

        this.registerManager = new RegisterManager(this);
        registerManager.getBackupCMD().makeBackups();
        // BackPack restore
        if (getConfig().getBoolean("Backpack")) {
            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                BackpackCMD.restore(offlinePlayer);
            }
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                BackpackCMD.restore(onlinePlayer);
            }
        }

        //saveCfg();
        this.currencySymbol = (String) getConfig().get("Currency.Single");
        saveCustomMessagesConfig();
        try {
            reloadCustomConfig();
        } catch (UnsupportedEncodingException ex) {
            Main.getInstance().getLogger4J().log(Level.ERROR, "Error", ex);
        }
        matchConfig(customConfig, customConfigFile);
        configVersion = getConfig().getString("Config-Version");

        setupLimitedHomes();

        if (getConfig().getBoolean("SendPlayerUpdateMessage")) {
            Bukkit.getOnlinePlayers().forEach(this::hasNewUpdate);
        }

        /* OfflinePlayer Register */
        this.offlinePlayers = new ArrayList<>();
        if (getOfflinePlayerAsJson() != null) {
            this.offlinePlayers = getOfflinePlayerAsJson();
        } else {
            this.offlinePlayers = new ArrayList<>();
            offlinePlayers.add("FramePlays");
            savePlayers();
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (isMysql() || isSQL() && getConfig().getBoolean("PlayerInfoSave")) {
                    if (!SQL.isTableExists(getName().toLowerCase() + "_data")) {
                        SQL.createTable(getName().toLowerCase() + "_data",
                                "playeruuid VARCHAR(1200)",
                                "playername TEXT(120)",
                                "sleeptimes INT",
                                "damage DOUBLE",
                                "playerkills INT",
                                "entitykills INT",
                                "deaths INT",
                                "blocksbroken INT",
                                "blocksplacen INT",
                                "lastlogin LONG",
                                "lastlogout LONG",
                                "commandsused INT",
                                "blocksBrokenList TEXT",
                                "blocksPlacenList TEXT",
                                "entityTypes TEXT");
                        Bukkit.getConsoleSender().sendMessage(getPrefix() + "§aMySQL Table Created!");
                        cancel();
                    }
                }
            }
        }.runTaskAsynchronously(this);

        Bukkit.getConsoleSender().sendMessage(getPrefix() + "§cSome Settings have been moved to the settings.yml in §6'plugins/EssentialsMini/settings.yml'§4§l!");

        // Write permissions.txt File
        writePermissionsAndCommands();
        Bukkit.getConsoleSender().sendMessage(getPrefix() + "§aPermissions can be viewed in the File §6'plugins/EssentialsMini/permissions.txt'");

        Bukkit.getConsoleSender().sendMessage(getPrefix() + "§aEnabled!");

        // Checking for Update and when enabled, Download the Latest Version automatically
        if (!checkUpdate(getConfig().getBoolean("AutoDownload"))) {

            if (!new SimpleJavaUtils().isOnline("framedev.ch", 443)) {
                Bukkit.getConsoleSender().sendMessage(getPrefix() + "§c§lThere was an error downloading or retrieving the new version.");
                Bukkit.getConsoleSender().sendMessage(getPrefix() + "§c§lPlease check your internet connection.");
            }
        }

        infoCfg.set("PluginName", this.getDescription().getName());
        infoCfg.set("PluginVersion", this.getVariables().getVersion());
        infoCfg.set("API-Version", this.getVariables().getApiVersion());
        infoCfg.set("Authors", this.getVariables().getAuthors());
        infoCfg.set("MongoDB", isMongoDB());
        infoCfg.set("MySQL", isMysql());
        infoCfg.set("SQLite", isSQL());
        infoCfg.set("isOnlineMode", getVariables().isOnlineMode());
        infoCfg.set("PlayerDataSave", getConfig().getBoolean("PlayerInfoSave"));
        infoCfg.set("Economy", getConfig().getBoolean("Economy.Activate"));
        infoCfg.set("Updates", updateScheduler.started);
        try {
            infoCfg.save(infoFile);
        } catch (IOException ex) {
            Main.getInstance().getLogger4J().log(Level.ERROR, "Error", ex);
        }

        if (utilities.get().isDev()) {
            Bukkit.getConsoleSender().sendMessage(
                    getPrefix() + "§c§lYou running a Dev Build, §r§cErrors can be happening!");
        }
        if (utilities.get().isPreRelease()) {
            Bukkit.getConsoleSender().sendMessage(
                    getPrefix() + "§c§lYou are running a Pre-Release. §r§cErrors may occur! Make sure to update to a stable version as soon as possible.");
        }
        if (!configVersion.equalsIgnoreCase("1.0.1")) {
            configUpdater();
        }
        // Bukkit.getConsoleSender().sendMessage(getPrefix() + "§cUpdater disabled. §6Website not Online!");
    }

    private void setupLimitedHomes() {
        limitedHomesTask = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    ConfigurationSection limitedHomeSection = getConfig().getConfigurationSection("LimitedHomes");
                    if (limitedHomeSection != null) {
                        for (String cs : limitedHomeSection.getKeys(false)) {
                            if (getConfig().getInt("LimitedHomes." + cs) != 0) {
                                limitedHomes.put(cs, getConfig().getInt("LimitedHomes." + cs));
                            }
                        }
                    }
                    ConfigurationSection limitedHomesPermissionSection = getConfig().getConfigurationSection("LimitedHomesPermission");
                    if (limitedHomesPermissionSection != null) {
                        for (String cs : limitedHomesPermissionSection.getKeys(false)) {
                            if (getConfig().get("LimitedHomesPermission." + cs) != null) {
                                limitedHomesPermission.put(cs, getConfig().get("LimitedHomesPermission." + cs));
                            }
                        }
                    }
                    cancel();
                } catch (Exception ex) {
                    logger.log(Level.ERROR, "Error", ex);
                    Bukkit.getConsoleSender().sendMessage(getPrefix() + "§cThere was an error while trying to initialize homes and homes permissions!");
                }
            }
        }.runTaskLater(this, 320);
    }

    public static Main getInstance() {
        return instance;
    }

    private @NotNull List<String> getComments() {
        List<String> comments = new ArrayList<>();
        comments.add("Position activates /position <LocationName> or /pos <LocationName> Command");
        comments.add("SkipNight activates skipnight. This means that only one Player need to lay in bed!");
        comments.add("LocationsBackup Activates creating Backup from all Homes");
        comments.add("OnlyEssentialsFeatures if its deactivated only Commands and Economy can be used when is activated the PlayerData will be saved");
        comments.add("Economy.Activate activates the integration of the Vault API use for Economy");
        comments.add("PlayerShop is that Players can create their own Shop");
        comments.add("PlayerEvents also named as PlayerData events");
        comments.add("Only 3 Limited Homes Group can be created. Please do not rename the Groups!");
        comments.add("TeleportDelay is the Time you have to wait before you got Teleported!");
        comments.add("MySQL, MongoDB and SQLite can now be added in the config.yml MySQLAPI Plugin is no longer required!");
        return comments;
    }

    public void configUpdater() {
        try {
            FileUtils.moveFile(new File(getDataFolder(), "config.yml"), new File(getDataFolder(), "config_old.yml"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Main.getInstance().getLogger4J().log(Level.ERROR, "Error", ex);
        }
        bukkitTaskConfig = new BukkitRunnable() {
            @Override
            public void run() {
                List<String> comments = getComments();
                getConfig().options().setHeader(comments);
                getConfig().options().parseComments(true);
                getConfig().options().copyDefaults(true);
                saveDefaultConfig();
                Config.updateConfig();
                Config.loadConfig();
                Config.saveDefaultConfigValues();
                Bukkit.getServer().reload();
                Bukkit.getConsoleSender().sendMessage(getPrefix() + "§cConfig Replaced! Please edit your Config Sections!");
                cancel();
            }
        }.runTaskLaterAsynchronously(this, 60);
        Config.saveDefaultConfigValues("messages_en-EN");
        Config.saveDefaultConfigValues("messages_de-DE");
    }

    @Override
    public void onLoad() {

        Bukkit.getConsoleSender().sendMessage("§aEssentialsMini §cloading...");
    }

    @Override
    public void onDisable() {
        if (this.getConfig().getBoolean("SaveInventory")) {
            SaveInventoryCMD.save();
        }
        if (!BackpackCMD.itemsStringHashMap.isEmpty()) {
            if (getConfig().getBoolean("Backpack")) {
                for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                    BackpackCMD.save(offlinePlayer);
                }
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    BackpackCMD.save(onlinePlayer);
                }
            }
        }
        if (getConfig().getBoolean("LocationsBackup")) {
            new LocationsManager().saveBackup();
        }
        new LocationsManager().deleteLocations();
        new SaveLists().saveVanishList();
        if (!VanishCMD.hided.isEmpty()) VanishCMD.hided.forEach(players -> {
            if (Bukkit.getPlayer(players) != null) {
                Objects.requireNonNull(Bukkit.getPlayer(players)).sendMessage(getPrefix() + "§cNach dem Reload wirst du nicht mehr im Vanish sein!");
            }
        });
        savePlayers();
        Bukkit.getConsoleSender().sendMessage(getPrefix() + " Stop Thread...");
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
        Bukkit.getConsoleSender().sendMessage(getPrefix() + " Thread stopped successfully");
        utilities.remove();
        utilities = null;
        this.spigotTimer = null;

        Bukkit.getConsoleSender().sendMessage(getPrefix() + " Clear List's and Map's");
        limitedHomes.clear();
        limitedHomesPermission.clear();
        commands.clear();
        listeners.clear();
        Bukkit.getConsoleSender().sendMessage(getPrefix() + " List's and Map's successfully cleared");
        try {
            if (limitedHomesTask != null) limitedHomesTask.cancel();
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to cancel limitedHomesTask: " + e.getMessage());
        }

        try {
            if (bukkitTaskConfig != null) bukkitTaskConfig.cancel();
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to cancel bukkitTaskConfig: " + e.getMessage());
        }
        if (mysql) {
            MySQL.close();
        }
        if (sql) {
            SQLite.close();
        }
        Bukkit.getScheduler().cancelTasks(this);
        HandlerList.unregisterAll(this);
        BackpackCMD.itemsStringHashMap.clear();
        disablePlugin();
        Bukkit.getConsoleSender().sendMessage(getPrefix() + "§cDisabled! Bye");
    }

    /**
     * Return Language Config from Players selected Game Language
     * Current Language English, German, France
     *
     * @param player selected Player to check the Language Player
     * @return return the Message File from the selected Language
     */
    public FileConfiguration getLanguageConfig(CommandSender player) {
        if (player instanceof Player) {
            String playerLocale = ((Player) player).getLocale();
            if (playerLocale.contains("en")) {
                File file = new File(getDataFolder(), "messages_en-EN.yml");
                return YamlConfiguration.loadConfiguration(file);
            } else if (playerLocale.contains("de")) {
                return YamlConfiguration.loadConfiguration(new File(getDataFolder(), "messages_de-DE.yml"));
            } else if (playerLocale.contains("fr")) {
                return YamlConfiguration.loadConfiguration(new File(getDataFolder(), "messages_fr-FR.yml"));
            }
            File file = new File(getDataFolder(), "messages_en-EN.yml");
            return YamlConfiguration.loadConfiguration(file);
        } else {
            File file = new File(getDataFolder(), "messages_en-EN.yml");
            return YamlConfiguration.loadConfiguration(file);
        }
    }

    public Language getLanguage(Player player) {
        String playerLocale = player.getLocale();
        if (playerLocale.contains("en")) return Language.EN;
        if (playerLocale.contains("de")) return Language.DE;
        if (playerLocale.contains("fr")) return Language.FR;
        return Language.EN;
    }

    /**
     * Require the MySQLAPI Developed by Me
     * Return if SQLite is enabled!
     *
     * @return return if SQLite is Enabled!
     */
    public boolean isSQL() {
        return sql;
    }

    /**
     * Debug a Object
     *
     * @param data the Data to Debugging
     */
    @SuppressWarnings("unused")
    public void debug(String data) {
        getLogger().info(data);
    }

    public void savePlayers() {
        File file = new File(getDataFolder(), "players.json");
        try {
            if (!file.exists()) {
                if (!file.createNewFile())
                    getLogger4J().error("Could not create File : " + file.getAbsolutePath());
            }
            FileWriter writer = new FileWriter(file);
            writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(offlinePlayers));
            writer.flush();
            writer.close();
        } catch (Exception ex) {
            Main.getInstance().getLogger4J().log(Level.ERROR, "Error", ex);
        }
        infoCfg.set("OfflinePlayers", offlinePlayers);
        try {
            infoCfg.save(infoFile);
        } catch (IOException ex) {
            Main.getInstance().getLogger4J().log(Level.ERROR, "Error", ex);
        }
    }

    protected void matchConfig(FileConfiguration config, File file) {
        try {
            InputStream is = getResource(file.getName());
            if (is != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(file);
                ConfigurationSection defConfigSection = defConfig.getConfigurationSection("");
                if (defConfigSection != null)
                    for (String key : defConfigSection.getKeys(false))
                        if (!config.contains(key)) config.set(key, defConfig.getConfigurationSection(key));
                ConfigurationSection configSection = config.getConfigurationSection("");
                if (configSection != null)
                    for (String key : configSection.getKeys(false))
                        if (!defConfig.contains(key)) config.set(key, null);

                config.save(file);
            }
        } catch (Exception ex) {
            Main.getInstance().getLogger4J().log(Level.ERROR, "Error", ex);
        }
    }

    protected ArrayList<String> getOfflinePlayerAsJson() {
        File file = new File(getDataFolder(), "players.json");
        String[] players;
        try {
            if (file.exists()) {
                FileReader reader = new FileReader(file);
                players = new Gson().fromJson(reader, String[].class);
                reader.close();
                return new ArrayList<>(Arrays.asList(players));
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * This Method returns if MongoDB is enabled or not
     *
     * @return return if MongoDB is enabled or not
     */
    public boolean isMongoDB() {
        if (mongoDbUtils == null) return false;
        return this.mongoDbUtils.isMongoDb();
    }

    @SuppressWarnings("unused")
    public void addOfflinePlayer(OfflinePlayer player) {
        if (!getOfflinePlayers().contains(player.getName()))
            offlinePlayers.add(player.getName());
    }

    @SuppressWarnings("unused")
    public void removeOfflinePlayer(OfflinePlayer player) {
        if (getOfflinePlayers().contains(player.getName()))
            offlinePlayers.remove(player.getName());
    }

    public MongoManager getMongoManager() {
        if (mongoDbUtils == null) return null;
        return mongoDbUtils.getMongoManager();
    }

    public BackendManager getBackendManager() {
        if (mongoDbUtils == null) return null;
        return mongoDbUtils.getBackendManager();
    }

    public FileConfiguration getCustomMessagesConfig() {
        return customConfig;
    }

    public void createCustomMessagesConfig() {
        customConfigFile = new File(Main.getInstance().getDataFolder(), "messages_en-EN.yml");
        if (!customConfigFile.exists()) {
            if(!customConfigFile.getParentFile().mkdirs()) {
                Main.getInstance().getLogger4J().log(Level.ERROR, "Failed to create directory for custom messages config");
            }
            Main.getInstance().saveResource("messages_en-EN.yml", false);
        }

        customConfig = new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException ex) {
            getLogger4J().log(Level.ERROR, "Failed to load configuration", ex);
        }
    }

    public String getOnlyPlayer() {
        String onlyPlayer = getCustomMessagesConfig().getString("OnlyPlayer");
        if (onlyPlayer == null) return "";
        onlyPlayer = onlyPlayer.replace('&', '§');
        return onlyPlayer;
    }

    public void saveCustomMessagesConfig() {
        try {
            customConfig.save(customConfigFile = new File(Main.getInstance().getDataFolder(), "messages_en-EN.yml"));
        } catch (IOException ex) {
            Main.getInstance().getLogger4J().log(Level.ERROR, "Error", ex);
        }
    }

    public String getWrongArgs(String cmdName) {
        String wrongArgs = getCustomMessagesConfig().getString("WrongArgs");
        if (wrongArgs == null) return "";
        wrongArgs = wrongArgs.replace("%cmdUsage%", cmdName);
        wrongArgs = wrongArgs.replace('&', '§');
        return wrongArgs;
    }

    public void reloadCustomConfig() throws UnsupportedEncodingException {
        if (customConfig == null)
            customConfig = YamlConfiguration.loadConfiguration(customConfigFile);

        Reader defConfigStream = new InputStreamReader(Objects.requireNonNull(Main.getInstance().getResource("messages_en-EN.yml")), StandardCharsets.UTF_8);
        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
        customConfig.setDefaults(defConfig);
    }


    /**
     * Return the no Perms Variable
     *
     * @return Return the Message for no Permissions
     */
    public String getNoPerms() {
        String permission = getCustomMessagesConfig().getString("NoPermissions");
        if (permission == null) return "";
        permission = permission.replace('&', '§');
        return permission;
    }

    /**
     * Check if the Plugin needs an update or not if Download is true it will download the Latest Version for you,
     * and after a Reload the new Version is active
     *
     * @param download if is True it will automatically download the Latest for you and after a Reload it will be active
     * @return if check for update was successfully or not
     *  TODO requires updating the url and reading the latest version
     *      - Require Debugging and Testing
     */
    public boolean checkUpdate(boolean download) {
        Bukkit.getConsoleSender().sendMessage(getPrefix() + "Checking for updates...");
        URLConnection conn;
        BufferedReader br = null;
        try {
            conn = new URL("https://framedev.ch/others/versions/essentialsmini-versions.json").openConnection();
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            JsonElement jsonElement = JsonParser.parseReader(br);
            String latestVersion = jsonElement.getAsJsonObject().get("latest").getAsString();
            String oldVersion = Main.getInstance().getDescription().getVersion();
            if (!latestVersion.equalsIgnoreCase(oldVersion)) {
                if (!oldVersion.contains("PRE-RELEASE")) {
                    if (download) {
                        downloadLatest();
                        Bukkit.getConsoleSender().sendMessage(getPrefix() + "Latest Version will be Downloaded : New Version : " + latestVersion);
                    } else {
                        Bukkit.getConsoleSender().sendMessage(getPrefix() + "A new update is available: version " + latestVersion);
                    }
                    return true;
                } else {
                    if(new UpdateChecker().hasPreReleaseUpdate()) {
                        Bukkit.getConsoleSender().sendMessage(getPrefix() + "A new pre-release update is available: version " + new UpdateChecker().getLatestPreRelease());
                        return true;
                    }
                }
            } else {
                Bukkit.getConsoleSender().sendMessage(getPrefix() + "You're running the newest plugin version!");
                return false;
            }
        } catch (IOException ex) {
            getLogger4J().log(Level.ERROR, "Error", ex);
            Bukkit.getConsoleSender().sendMessage(getPrefix() + "Failed to check for updates on framedev.ch");
            // Bukkit.getConsoleSender().sendMessage(getPrefix() + "§cPlease write an Email to framedev@framedev.stream with the Error");
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                getLogger4J().error(e.getMessage(), e);
            }
        }
        return false;
    }

    /**
     * Download the Latest Plugin from the Website <a href="https://framedev.ch">https://framedev.ch</a>
     */
    public void downloadLatest() {
        final File pluginFile = getDataFolder().getParentFile();
        final File updaterFile = new File(pluginFile, "update");
        if (!updaterFile.exists())
            if(!updaterFile.mkdir())
                getLogger4J().error("Could not create Update Directory : " + updaterFile.getAbsolutePath());
        try {
            URL url = new URL("https://framedev.ch/others/versions/essentialsmini-versions.json");
            JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader(url.openConnection().getInputStream()));
            String latest = jsonElement.getAsJsonObject().get("latest").getAsString();
            new UpdateChecker().download("https://framedev.ch/downloads/EssentialsMini-" + latest + ".jar", getServer().getUpdateFolder(), "EssentialsMini.jar");
        } catch (IOException ex) {
            getLogger4J().error(ex.getMessage(), ex);
        }
    }

    public String getPermissionBase() {
        return "essentialsmini.";
    }


    /**
     * & will be replaced with §
     * >> will be replaced with »
     * << will be replaced with «
     * -> will be replaced with →
     * <- will be replaced with ←
     * @return the Prefix
     */
    public String getPrefix() {
        String prefix = getConfig().getString("prefix");
        if (prefix == null) {
            throw new NullPointerException("Prefix cannot be Found in Config.yml add (prefix:'YourPrefix') to the config.yml");
        }
        if (prefix.contains("&"))
            prefix = prefix.replace('&', '§');
        if (prefix.contains(">>"))
            prefix = prefix.replace(">>", "»");
        if (prefix.contains("<<"))
            prefix = prefix.replace("<<", "«");
        if (prefix.contains("->"))
            prefix = prefix.replace("->", "→");
        if (prefix.contains("<-"))
            prefix = prefix.replace("<-", "←");
        return prefix;
    }

    /**
     * TODO requires updating the url and reading the latest version
     *  - Require Debugging and Testing
     */
    public void hasNewUpdate(Player player) {
        if (getConfig().getBoolean("SendPlayerUpdateMessage")) {
            if (player.hasPermission("essentialsmini.checkupdates")) {
                try {
                    URLConnection conn = new URL("https://framedev.ch/others/versions/essentialsmini-versions.json").openConnection();
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String latestVersion = JsonParser.parseReader(br).getAsJsonObject().get("latest").getAsString();
                    String oldVersion = Main.getInstance().getDescription().getVersion();
                    if (!latestVersion.equalsIgnoreCase(oldVersion)) {
                        if (!oldVersion.endsWith("PRE-RELEASE")) {
                            BaseComponent base = new TextComponent();
                            base.addExtra(getPrefix() + "§aNew Version = §6" + latestVersion + " §b§l[Please Click Here to Download the newest Plugin!]");
                            base.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://framedev.ch/sites/downloads/essentialsmini"));
                            base.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§6Click Here to Open the Download Link")));
                            player.spigot().sendMessage(base);
                        }
                    }
                    br.close();
                } catch (IOException ex) {
                    getLogger4J().error(ex.getMessage(), ex);
                    player.sendMessage(getPrefix() + "Failed to check for updates on framedev.ch");
                }
            }
        }
    }

    public Map<String, String> getLimitedHomesPermission() {
        Map<String, String> limited = new HashMap<>();
        for (Map.Entry<String, Object> entry : limitedHomesPermission.entrySet()) {
            limited.put(entry.getKey(), (String) entry.getValue());
        }
        return limited;
    }

    @SuppressWarnings("unused")
    public Map<String, String> getLimitedHomes() {
        Map<String, String> limited = new HashMap<>();
        for (Map.Entry<String, Object> entry : limitedHomes.entrySet()) {
            limited.put(entry.getKey(), (String) entry.getValue());
        }
        return limited;
    }

    public String getCurrencySymbolMulti() {
        return getConfig().getString("Currency.Multi");
    }

    public void saveSettings() {
        try {
            this.settingsCfg.save(settingsFile);
        } catch (IOException ex) {
            Main.getInstance().getLogger4J().log(Level.ERROR, "Error", ex);
        }
    }

    protected void writePermissionsAndCommands() {
        File file = new File(getDataFolder(), "permissions.txt");
        File commandsFile = new File(getDataFolder(), "commands.txt");
        File commandsAndPermissions = new File(getDataFolder(), "commands-permissions.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file));
             BufferedWriter writerCommands = new BufferedWriter(new FileWriter(commandsFile));
             BufferedWriter writerCommandsPermissions = new BufferedWriter(new FileWriter(commandsAndPermissions))) {

            // Writing permissions to the permissions.txt file
            for (Permission permission : getDescription().getPermissions()) {
                writer.append(permission.getName()).append(System.lineSeparator());
            }

            // Writing commands to the commands.txt file
            for (String command : getCommands().keySet()) {
                writerCommands.append("/").append(command).append(System.lineSeparator());
            }

            // Writing commands and their associated permissions to the commands-permissions.txt file
            Map<String, Map<String, Object>> commands = getDescription().getCommands();

            for (String command : commands.keySet()) {
                writerCommandsPermissions.append("/").append(command);

                // Get the permission associated with the command, if any
                Object permissionObj = commands.get(command).get("permission");
                if (permissionObj != null) {
                    writerCommandsPermissions.append(" - Permission: ").append(permissionObj.toString());
                } else {
                    writerCommandsPermissions.append(" - Permission: None");
                }

                writerCommandsPermissions.append(System.lineSeparator());
            }

        } catch (IOException ex) {
            Main.getInstance().getLogger4J().log(Level.ERROR, "Failed to write permissions or commands to file", ex);
        }
    }

    public Logger getLogger4J() {
        return logger;
    }

    public FileConfiguration getSettingsCfg() {
        return settingsCfg;
    }

    public ThreadLocal<Utilities> getUtilities() {
        return utilities;
    }

    public static ArrayList<String> getSilent() {
        return silent;
    }

    @SuppressWarnings("unused")
    public Thread getThread() {
        return thread;
    }

    public HashMap<String, CommandExecutor> getCommands() {
        return commands;
    }

    public HashMap<String, TabCompleter> getTabCompleters() {
        return tabCompleters;
    }

    public ArrayList<Listener> getListeners() {
        return listeners;
    }

    public boolean isHomeTP() {
        return homeTP;
    }

    @SuppressWarnings("unused")
    public MaterialManager getMaterialManager() {
        return materialManager;
    }

    public Variables getVariables() {
        return variables;
    }

    public KeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    public VaultManager getVaultManager() {
        return vaultManager;
    }

    @SuppressWarnings("unused")
    public File getCustomConfigFile() {
        return customConfigFile;
    }

    @SuppressWarnings("unused")
    public FileConfiguration getCustomConfig() {
        return customConfig;
    }

    public LagCMD.SpigotTimer getSpigotTimer() {
        return spigotTimer;
    }

    public ArrayList<String> getPlayers() {
        return players;
    }

    public RegisterManager getRegisterManager() {
        return registerManager;
    }

    public boolean isMysql() {
        return mysql;
    }

    public boolean isSql() {
        return sql;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public ArrayList<String> getOfflinePlayers() {
        return offlinePlayers;
    }

    @SuppressWarnings("unused")
    public File getInfoFile() {
        return infoFile;
    }

    @SuppressWarnings("unused")
    public FileConfiguration getInfoCfg() {
        return infoCfg;
    }

    @SuppressWarnings("unused")
    public MongoDBUtils getMongoDbUtils() {
        return mongoDbUtils;
    }

    @SuppressWarnings("unused")
    public String getConfigVersion() {
        return configVersion;
    }

    @SuppressWarnings("unused")
    public File getSettingsFile() {
        return settingsFile;
    }

    @SuppressWarnings("unused")
    public BukkitTask getBukkitTaskConfig() {
        return bukkitTaskConfig;
    }

    @SuppressWarnings("unused")
    public BukkitTask getLimitedHomesTask() {
        return limitedHomesTask;
    }

    private void disablePlugin() {
        this.vaultManager = null;
        this.customConfig = null;
        this.customConfigFile = null;
        this.registerManager = null;
        this.keyGenerator = null;
        this.infoCfg = null;
        this.infoFile = null;
        this.settingsCfg = null;
        this.settingsFile = null;
    }
}
