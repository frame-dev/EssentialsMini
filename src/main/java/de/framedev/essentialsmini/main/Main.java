package de.framedev.essentialsmini.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.framedev.essentialsmini.commands.playercommands.BackpackCMD;
import de.framedev.essentialsmini.commands.playercommands.EnchantCMD;
import de.framedev.essentialsmini.commands.playercommands.SaveInventoryCMD;
import de.framedev.essentialsmini.commands.playercommands.VanishCMD;
import de.framedev.essentialsmini.commands.servercommands.LagCMD;
import de.framedev.essentialsmini.database.*;
import de.framedev.essentialsmini.managers.*;
import de.framedev.essentialsmini.utils.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.apache.commons.io.FileUtils;
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
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * License is <a href="file:../../../../../../LICENSE.md">LICENSE</a>
 *
 * @author FrameDev
 */
public class Main extends JavaPlugin {

    private Utilities utilities;
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
    private JsonConfig jsonConfig;

    private boolean homeTP = false;

    /* Material Manager */
    private MaterialManager materialManager;
    // Variables
    private Variables variables;
    private KeyGenerator keyGenerator;

    // VaultManager Require Vault
    private VaultManager vaultManager;
    /* Custom Config File */
    private File customConfigFile;
    private FileConfiguration customConfig;

    private LagCMD.SpigotTimer spigotTimer;

    public ArrayList<String> players;

    /* Singleton */
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

    @Override
    public void onEnable() {
        // Singleton initializing
        instance = this;

        this.utilities = new Utilities();

        // Set Dev Build
        // TODO: Update
        utilities.setDev(false);

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
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        getConfig().options().header("MySQL and SQLite uses MySQLAPI[https://framedev.ch/sites/downloads/mysqlapi] \n" +
                "Position activates /position <LocationName> or /pos <LocationName> Command\n" +
                "SkipNight activates skipnight. This means that only one Player need to lay in bed!\n" +
                "LocationsBackup Activates creating Backup from all Homes \n" +
                "OnlyEssentialsFeatures if its deactivated only Commands and Economy can be used when is activated the PlayerData will be saved \n" +
                "Economy.Activate activates the integration of the Vault API use for Economy \n" +
                "PlayerShop is that Players can create their own Shop \n" +
                "PlayerEvents also named as PlayerData events \n" +
                "Only 3 Limited Homes Group can be created. Please do not rename the Groups! \n" +
                "TeleportDelay is the Time you have to wait befor you got Teleported!\n" +
                "MySQL, MongoDB and SQLite can now be added in the config.yml MySQLAPI Plugin is no longer required!");
        getConfig().options().copyHeader(true);
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
            new File("plugins/EssentialsMini/Config_Examples.zip").delete();
        }
        try {
            this.settingsCfg.save(settingsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.configVersion = getConfig().getString("Config-Version");

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
        this.jsonConfig = new JsonConfig();

        silent = new ArrayList<>();

        /*this.file = new File(getDataFolder(), "offline.yml");
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

        if (Bukkit.getServer().getPluginManager().getPlugin("MDBConnection") != null) {
            this.mongoDbUtils = new MongoDBUtils();
            if (isMongoDB()) {
                for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                    getBackendManager().createUser(player, "essentialsmini_data");
                }
                Bukkit.getConsoleSender().sendMessage(getPrefix() + "§aMongoDB Enabled!");
            }
        }
        /* MongoDB Finish */

        // Load Enchantments
        EnchantCMD.Enchantments.load();

        // LocationBackup
        if (getConfig().getBoolean("LocationsBackup")) {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + "§aLocation Backups enabled!");
        }
        UpdateScheduler updateScheduler = new UpdateScheduler();
        /* Thread for the Schedulers for save restart and .... */
        if (!getConfig().getBoolean("OnlyEssentialsFeatures")) {
            thread = new Thread(updateScheduler);
            if (!thread.isAlive()) {
                thread.start();
            } else {
                if (updateScheduler.started) {
                    Bukkit.getConsoleSender().sendMessage(getPrefix() + "§aSchedulers Started!");
                }
            }
        }

        // LimitedHomes Init
        HashMap<String, Integer> limitedHomes = new HashMap<>();
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
        /* Json Config add Key's and Value's */
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
        }
        new SaveLists().setVanished();
        /*KitManager kit = new KitManager();
        kit.saveKit("Stone");*/
        //EssentialsMiniAPI.getInstance().printAllHomesFromPlayers();

        this.mysql = getConfig().getBoolean("MySQL.Use");
        this.sql = getConfig().getBoolean("SQLite.Use");

        if (sql) {
            new SQLite(getConfig().getString("SQLite.Path"), getConfig().getString("SQLite.FileName"));
        }

        if (mysql)
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
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        matchConfig(customConfig, customConfigFile);
        try {
            this.limitedHomesPermission = getJsonConfig().getHashMap("LimitedHomesPermission");
            this.limitedHomes = getJsonConfig().getHashMap("LimitedHomes");
        } catch (Exception ignored) {
            getServer().reload();
        }
        if (getConfig().getBoolean("SendPlayerUpdateMessage")) {
            Bukkit.getOnlinePlayers().forEach(this::hasNewUpdate);
        }

        /* OfflinePlayer Register */
        this.offlinePlayers = new ArrayList<>();
        if (getJson() != null) {
            this.offlinePlayers = getJson();
        } else {
            this.offlinePlayers = new ArrayList<>();
            offlinePlayers.add("FramePlays");
            savePlayers();
        }

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
            }
        }

        Bukkit.getConsoleSender().sendMessage(getPrefix() + "§cSome Settings have been moved to the settings.yml in §6'plugins/EssentialsMini/settings.yml'§4§l!");

        // Write permissions.txt File
        writePermissions();
        Bukkit.getConsoleSender().sendMessage(getPrefix() + "§aPermissions can be viewed in the File §6'plugins/EssentialsMini/permissions.txt'");

        Bukkit.getConsoleSender().sendMessage(getPrefix() + "§aEnabled!");

        // Checking for Update and when enabled Download the Latest Version automatically
        checkUpdate(getConfig().getBoolean("AutoDownload"));
        if (new UpdateChecker().isOldVersionPreRelease()) {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + "§cYour Version is a Pre-Release. §6§lThere can be Errors!");
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!configVersion.equalsIgnoreCase("1.0.1")) {
            configUpdater();
        }

        if (utilities.isDev()) {
            Bukkit.getConsoleSender().sendMessage(getPrefix() + "§c§lYou running a Dev Build, §r§cErrors can be happening!");
        }
    }

    public void configUpdater() {
        try {
            FileUtils.moveFile(new File(getDataFolder(), "config.yml"), new File(getDataFolder(), "config_old.yml"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                getConfig().options().header("MySQL and SQLite uses MySQLAPI[https://framedev.ch/sites/downloads/mysqlapi] \n" +
                        "Position activates /position <LocationName> or /pos <LocationName> Command\n" +
                        "SkipNight activates skipnight. This means that only one Player need to lay in bed!\n" +
                        "LocationsBackup Activates creating Backup from all Homes \n" +
                        "OnlyEssentialsFeatures if its deactivated only Commands and Economy can be used when is activated the PlayerData will be saved \n" +
                        "Economy.Activate activates the integration of the Vault API use for Economy \n" +
                        "PlayerShop is that Players can create their own Shop \n" +
                        "PlayerEvents also named as PlayerData events \n" +
                        "Only 3 Limited Homes Group can be created. Please do not rename the Groups! \n" +
                        "TeleportDelay is the Time you have to wait befor you got Teleported!\n" +
                        "MySQL, MongoDB and SQLite can now be added in the config.yml MySQLAPI Plugin is no longer required!");
                getConfig().options().copyHeader(true);
                getConfig().options().copyDefaults(true);
                saveDefaultConfig();
                Config.updateConfig();
                Config.loadConfig();
                Config.saveDefaultConfigValues();
                Bukkit.getServer().reload();
                Bukkit.getConsoleSender().sendMessage(getPrefix() + "§cConfig Replaced! Please edit your Config Sections!");
            }
        }.runTaskLater(this, 60);
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
        if (thread != null && thread.isAlive())
            thread.getThreadGroup().interrupt();
        Bukkit.getConsoleSender().sendMessage(getPrefix() + "§cDisabled! Bye");
    }

    /**
     * Return Language Config from Players selected Game Language
     * Current Language English, German, France
     *
     * @param player selected Player to check the Language Player
     * @return return the Messages File from the selected Language
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
     * Return all Players there are Silent
     *
     * @return the List of PlayerNames they are set to Silent
     */
    public static ArrayList<String> getSilent() {
        return silent;
    }

    /**
     * Debug a Object
     *
     * @param data the Data to Debugging
     */
    public void debug(String data) {
        getLogger().info(data);
    }

    /**
     * Return all OfflinePlayers
     *
     * @return returns all OfflinePlayers
     */
    public ArrayList<String> getOfflinePlayers() {
        return offlinePlayers;
    }

    public void savePlayers() {
        File file = new File(getDataFolder(), "players.json");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file);
            writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(offlinePlayers));
            writer.flush();
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        infoCfg.set("OfflinePlayers", offlinePlayers);
        try {
            infoCfg.save(infoFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return the Info File for this Plugin
     *
     * @return returns the Info Config
     */
    public FileConfiguration getInfoCfg() {
        return infoCfg;
    }

    protected void matchConfig(FileConfiguration config, File file) {
        try {
            InputStream is = getResource(file.getName());
            if (is != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(file);
                for (String key : defConfig.getConfigurationSection("").getKeys(false))
                    if (!config.contains(key)) config.set(key, defConfig.getConfigurationSection(key));

                for (String key : config.getConfigurationSection("").getKeys(false))
                    if (!defConfig.contains(key)) config.set(key, null);

                config.save(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected ArrayList<String> getJson() {
        File file = new File(getDataFolder(), "players.json");
        String[] players = null;
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
     * This Methods return the KeyGenerator
     *
     * @return return KeyGenerator class
     */
    public KeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    /**
     * This Method returns the VaultManager class
     * This Method can return a Null Object Surround it with a Not null Check!
     *
     * @return return VaultManager class
     */
    public VaultManager getVaultManager() {
        return vaultManager;
    }

    /**
     * This Method returns the Currency Symbol from the Config
     *
     * @return return the Currency Symbol from the Config
     */
    public String getCurrencySymbol() {
        return currencySymbol;
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

    public boolean isMysql() {
        return mysql;
    }

    public void addOfflinePlayer(OfflinePlayer player) {
        if (!getOfflinePlayers().contains(player.getName()))
            offlinePlayers.add(player.getName());
    }

    public void removeOfflinePlayer(OfflinePlayer player) {
        if (getOfflinePlayers().contains(player.getName()))
            offlinePlayers.remove(player.getName());
    }

    /**
     * Return a list of all OfflinePlayers
     *
     * @return return a list of all OfflinePlayers
     */
    public ArrayList<String> getPlayers() {
        return players;
    }

    /**
     * Return the Thread where the Schedulers are running
     *
     * @return return the Thread
     */
    public Thread getThread() {
        return thread;
    }

    public File getCustomConfigFile() {
        return customConfigFile;
    }

    public FileConfiguration getCustomConfig() {
        return customConfig;
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

    public JsonConfig getJsonConfig() {
        return jsonConfig;
    }

    public void createCustomMessagesConfig() {
        customConfigFile = new File(Main.getInstance().getDataFolder(), "messages_en-EN.yml");
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            Main.getInstance().saveResource("messages_en-EN.yml", false);
        }

        customConfig = new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Diese Methode gibt die Klasse Variables zurück
     *
     * @return die Variablen die gespeichert wurden verfügbar mit dem Getter
     */
    public Variables getVariables() {
        return variables;
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
        } catch (IOException e) {
            e.printStackTrace();
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
        if (customConfig == null) ;
        customConfig = YamlConfiguration.loadConfiguration(customConfigFile);

        Reader defConfigStream = new InputStreamReader(Objects.requireNonNull(Main.getInstance().getResource("messages_en-EN.yml")), StandardCharsets.UTF_8);
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            customConfig.setDefaults(defConfig);
        }
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

    public boolean isHomeTP() {
        return homeTP;
    }

    /**
     * Check if the Plugin need an update or not if Download is true it will download the Latest Version for you and after an Reload the new Version is active
     *
     * @param download if is True it will automatically download the Latest for you and after an Reload it will be active
     * @return if check for update was successfully or not
     */
    public boolean checkUpdate(boolean download) {
        Bukkit.getConsoleSender().sendMessage(getPrefix() + "Checking for updates...");
        try {
            URLConnection conn = new URL("https://framedev.ch/sites/downloads/essentialsminiversion.txt").openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String oldVersion = Main.getInstance().getDescription().getVersion();
            String newVersion = br.readLine();
            if (!newVersion.equalsIgnoreCase(oldVersion)) {
                if (!oldVersion.contains("PRE-RELEASE")) {
                    if (download) {
                        downloadLatest();
                        Bukkit.getConsoleSender().sendMessage(getPrefix() + "Latest Version will be Downloaded : New Version : " + newVersion);
                    } else {
                        Bukkit.getConsoleSender().sendMessage(getPrefix() + "A new update is available: version " + newVersion);
                    }
                    br.close();
                    return true;
                } else {
                    Bukkit.getConsoleSender().sendMessage(getPrefix() + "§cThis Plugin is a Pre-Release | §6There could still be errors");
                    Bukkit.getConsoleSender().sendMessage(getPrefix() + "There should be a new Version check if its newer than your Version : " + newVersion + ".| Your Version : §6" + oldVersion);
                }
            } else {
                Bukkit.getConsoleSender().sendMessage(getPrefix() + "You're running the newest plugin version!");
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(getPrefix() + "Failed to check for updates on framedev.ch");
            // Bukkit.getConsoleSender().sendMessage(getPrefix() + "§cPlease write an Email to framedev@framedev.stream with the Error");
        }
        return false;
    }

    /**
     * Download the Latest Plugin from the Website https://framedev.ch
     */
    public void downloadLatest() {
        final File pluginFile = getDataFolder().getParentFile();
        final File updaterFile = new File(pluginFile, "update");
        if (!updaterFile.exists())
            updaterFile.mkdir();
        new UpdateChecker().download("https://framedev.ch/downloads/EssentialsMini-Latest.jar", getServer().getUpdateFolder(), "EssentialsMini.jar");
    }

    public MaterialManager getMaterialManager() {
        return materialManager;
    }

    public String getPermissionName() {
        return "essentialsmini.";
    }


    /**
     * @return the TabCompleters
     */
    public HashMap<String, TabCompleter> getTabCompleters() {
        return tabCompleters;
    }

    /**
     * @return the commands
     */
    public HashMap<String, CommandExecutor> getCommands() {
        return commands;
    }

    /**
     * @return the Listeners
     */
    public ArrayList<Listener> getListeners() {
        return listeners;
    }

    /**
     * @return the Prefix
     */
    public String getPrefix() {
        String prefix = getConfig().getString("Prefix");
        if (prefix == null) {
            throw new NullPointerException("Prefix cannot be Found in Config.yml");
        }
        if (prefix.contains("&"))
            prefix = prefix.replace('&', '§');
        if (prefix.contains(">>"))
            prefix = prefix.replace(">>", "»");
        return prefix;
    }

    public void hasNewUpdate(Player player) {
        if (getConfig().getBoolean("SendPlayerUpdateMessage")) {
            if (player.hasPermission("essentialsmini.checkupdates")) {
                try {
                    URLConnection conn = new URL("https://framedev.ch/sites/downloads/essentialsminiversion.txt").openConnection();
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String oldVersion = Main.getInstance().getDescription().getVersion();
                    String newVersion = br.readLine();
                    if (!newVersion.equalsIgnoreCase(oldVersion)) {
                        if (!oldVersion.endsWith("PRE-RELEASE")) {
                            BaseComponent base = new TextComponent();
                            base.addExtra(getPrefix() + "§aNew Version = §6" + newVersion + " §b§l[Please Click Here to Download the newest Plugin!]");
                            base.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://framedev.ch/sites/downloads/essentialsmini"));
                            base.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§6Click Here to Open the Download Link")));
                            player.spigot().sendMessage(base);
                        }
                    }
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    player.sendMessage(getPrefix() + "Failed to check for updates on framedev.ch");
                }
            }
        }
    }

    /**
     * @return the Singleton of this Class (this Plugin)
     */
    public static Main getInstance() {
        return instance;
    }

    public RegisterManager getRegisterManager() {
        return registerManager;
    }

    public Map<String, String> getLimitedHomesPermission() {
        Map<String, String> limited = new HashMap<>();
        for (Map.Entry<String, Object> entry : limitedHomesPermission.entrySet()) {
            limited.put(entry.getKey(), (String) entry.getValue());
        }
        return limited;
    }

    public Map<String, String> getLimitedHomes() {
        Map<String, String> limited = new HashMap<>();
        for (Map.Entry<String, Object> entry : limitedHomes.entrySet()) {
            limited.put(entry.getKey(), (String) entry.getValue());
        }
        return limited;
    }

    /**
     * This is used for returning the SpigotTimer for the Lag Command
     *
     * @return returns the Lag Timer
     */
    public LagCMD.SpigotTimer getSpigotTimer() {
        return spigotTimer;
    }

    public String getCurrencySymbolMulti() {
        return getConfig().getString("Currency.Multi");
    }

    /**
     * Return the Config version
     *
     * @return return the Config Version
     */
    public String getConfigVersion() {
        return configVersion;
    }

    public FileConfiguration getSettingsCfg() {
        return settingsCfg;
    }

    public void saveSettings() {
        try {
            this.settingsCfg.save(settingsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void writePermissions() {
        File file = new File(getDataFolder(), "permissions.txt");
        File commandsFile = new File(getDataFolder(), "commands.txt");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (Permission permission : getDescription().getPermissions()) {
                writer.append(permission.getName()).append("\n");
            }
            writer.flush();
            writer.close();
            BufferedWriter writerCommands = new BufferedWriter(new FileWriter(commandsFile));
            for (String command : getCommands().keySet()) {
                writerCommands.append("/").append(command).append("\n");
            }
            writerCommands.flush();
            writerCommands.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
