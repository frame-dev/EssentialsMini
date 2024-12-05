package ch.framedev.essentialsmini.main;

import ch.framedev.essentialsmini.commands.playercommands.*;
import ch.framedev.essentialsmini.commands.worldcommands.WorldTPCMD;
import ch.framedev.essentialsmini.managers.*;
import ch.framedev.essentialsmini.utils.InventoryStringDeSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EssentialsMiniAPI {

    // The Main class from this Plugin
    private final Main plugin;

    // The Singleton for the API
    private static EssentialsMiniAPI instance;
    private final boolean jsonFormat;

    // Is Boolean enabled or not
    private final boolean economy;

    protected EssentialsMiniAPI(Main plugin) {
        instance = this;
        this.plugin = plugin;
        jsonFormat = plugin.getConfig().getBoolean("JsonFormat");
        economy = plugin.getVaultManager() != null;
    }

    public static EssentialsMiniAPI getInstance() {
        new EssentialsMiniAPI(Main.getInstance());
        return instance;
    }

    public boolean isPlayerVanish(Player player) {
        return VanishCMD.hided.contains(player.getName());
    }

    public boolean isPlayerSilent(Player player) {
        return Main.getSilent().contains(player.getName());
    }

    public boolean hasPlayerKey(OfflinePlayer player) {
        if (plugin.isMongoDB()) {
            if (plugin.getConfig().getBoolean("MongoDB.Boolean") || plugin.getConfig().getBoolean("MongoDB.LocalHost")) {
                return plugin.getBackendManager().exists(player, "key", "essentialsmini_data");
            }
        }
        plugin.getKeyGenerator().loadCfg();
        return plugin.getKeyGenerator().hasPlayerKey(player);
    }

    public boolean canPlayerFly(Player player) {
        return player.getAllowFlight();
    }

    public HashMap<String, Location> getPlayerHomes(OfflinePlayer player) throws NullPointerException {
        HashMap<String, Location> homes = new HashMap<>();
        if (new LocationsManager().getCfg().contains(player.getName() + ".home")) {
            ConfigurationSection cs = new LocationsManager().getCfg().getConfigurationSection(player.getName() + ".home");
            if (cs != null) {
                for (String s : cs.getKeys(false)) {
                    if (new LocationsManager().getCfg().get(player.getName() + ".home." + s) != null) {
                        if (!new LocationsManager().getCfg().get(player.getName() + ".home." + s).equals(" ")) {
                            homes.put(s, new LocationsManager().getLocation(player.getName() + ".home." + s));
                        }
                    }
                }
                return homes;
            }
        }
        return null;
    }

    public HashMap<String, Location> getPositions() {
        HashMap<String, Location> positions = new HashMap<>();
        ConfigurationSection cs = new LocationsManager().getCfg().getConfigurationSection("position");
        if (cs != null) {
            for (String s : cs.getKeys(false)) {
                if (new LocationsManager().getCfg().get("position." + s) != null) {
                    if (!new LocationsManager().getCfg().get("position." + s).equals(" ")) {
                        positions.put(s, new LocationsManager().getLocation("position." + s));
                    }
                }
            }
            return positions;
        }
        return positions;
    }

    public SkullBuilder createSkullBuilder(UUID playersUUID) {
        return new SkullBuilder(playersUUID);
    }

    public SkullBuilder createSkullBuilder(OfflinePlayer player) {
        return new SkullBuilder(player);
    }

    public SkullBuilder createSkullBuilder(String playerName) {
        return new SkullBuilder(playerName);
    }

    public BossBarManager createNewBossBarManager(@NotNull String title, @NotNull BarColor barColor, @NotNull BarStyle barStyle) {
        return new BossBarManager(title, barColor, barStyle);
    }

    public BossBarManager createNewBossBarManager(@NotNull String title) {
        return new BossBarManager(title);
    }

    /**
     * This Method saves a Location to the Location File
     *
     * @param locationName the LocationName to save
     * @param location     the Location to save
     */
    public void setLocation(String locationName, Location location) {
        new LocationsManager(locationName).setLocation(location);
    }

    /**
     * This Method Returns the Saved Location
     *
     * @param locationName the LocationName
     * @return returns the Saved Location
     */
    public Location getLocation(String locationName) {
        return new LocationsManager(locationName).getLocation();
    }

    public MaterialManager getMaterialManager() {
        return new MaterialManager();
    }

    public void printAllHomesFromPlayers() {
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            getPlayerHomes(offlinePlayer).forEach((s, location) -> Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + "§7" + offlinePlayer.getName() + " Homes : " + s + " = " + new LocationsManager().locationToString(location)));
        }
    }

    public void openPlayersBackpack(Player player, OfflinePlayer target) {
        Inventory inventory = Bukkit.createInventory(null, 3 * 9, target.getName() + "'s Inventory");
        if (BackpackCMD.itemsStringHashMap.containsKey(target.getUniqueId().toString())) {
            try {
                inventory.setContents(InventoryStringDeSerializer.itemStackArrayFromBase64(BackpackCMD.itemsStringHashMap.get(target.getUniqueId().toString())));
            } catch (IOException e) {
                e.printStackTrace();
            }
            player.openInventory(inventory);
        } else {
            player.sendMessage(plugin.getPrefix() + "§cDieser Spieler hat noch kein BackPack!");
        }
    }

    public Inventory getPlayersBackPack(OfflinePlayer target) {
        Inventory inventory = Bukkit.createInventory(null, 3 * 9, target.getName() + "'s Inventory");
        if (BackpackCMD.itemsStringHashMap.containsKey(target.getUniqueId().toString())) {
            try {
                inventory.setContents(InventoryStringDeSerializer.itemStackArrayFromBase64(BackpackCMD.itemsStringHashMap.get(target.getUniqueId().toString())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return inventory;
    }

    /**
     * @return Returns a List of all Warp Names
     */
    public ArrayList<Location> getWarps() {
        ArrayList<Location> locations = new ArrayList<>();
        if (new LocationsManager().getCfg().contains("warps")) {
            ConfigurationSection cs = new LocationsManager().getCfg().getConfigurationSection("warps");
            if (cs != null)
                for (String s : cs.getKeys(false))
                    locations.add(new LocationsManager(s).getLocation());
        }
        return locations;
    }

    private int time = 100;
    private double progress = 1.0;

    /**
     * Restarting the Server with Timer
     */
    public void restartServer() {
        // Restarting the Server
        BossBarManager.removeAll();
        BossBarManager bossBarManager = new BossBarManager("§4Server Restart", BarColor.RED, BarStyle.SEGMENTED_6);
        bossBarManager.create();
        bossBarManager.setProgress(progress);
        Bukkit.getConsoleSender().sendMessage(Main.getInstance().getPrefix() + "§cServer Restart wird eingeführt!");
        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {
            if (time <= 0) {
                Bukkit.spigot().restart();
            } else {
                switch (time) {
                    case 60:
                        Bukkit.broadcastMessage(Main.getInstance().getPrefix() + "§cServer Restartet in §61 Minute§4§l!");
                        Bukkit.getOnlinePlayers().forEach(players -> {
                            bossBarManager.addPlayer(players);
                            bossBarManager.update();
                        });
                        break;
                    case 30:
                    case 3:
                    case 15:
                    case 10:
                    case 5:
                    case 2:
                        Bukkit.broadcastMessage(Main.getInstance().getPrefix() + "§cServer Restartet in §6" + time + " Sekunden§4§l!");
                        bossBarManager.setProgress(1.0);
                        break;
                    case 1:
                        Bukkit.broadcastMessage(Main.getInstance().getPrefix() + "§cServer Restartet in §6einer Sekunde§4§l!");
                        bossBarManager.setProgress(1.0);
                        break;
                }
                time--;
                if (progress != 0.00) {
                    progress = progress - 0.01;
                    bossBarManager.setProgress(progress);
                    bossBarManager.update();
                }
            }
        }, 0, 20);
    }

    public boolean isWorldUsingKey(World world) {
        return WorldTPCMD.worldWithKeys.contains(world.getName());
    }

    public InventoryManager createNewInventoryManager() {
        return new InventoryManager();
    }

    public InventoryManager createNewInventoryManager(String title) {
        return new InventoryManager(title);
    }

    public InventoryManager createNewInventoryManager(String title, int size) {
        return new InventoryManager(title, size);
    }

    public ItemBuilder createNewItemBuilder(ItemStack itemStack) {
        return new ItemBuilder(itemStack);
    }

    public ItemBuilder createNewItemBuilder(Material material) {
        return new ItemBuilder(material);
    }

    public boolean setJsonFormat(boolean jsonFormat) {
        try {
            plugin.getConfig().set("JsonFormat", jsonFormat);
            Bukkit.getPluginManager().disablePlugin(plugin);
            Bukkit.getPluginManager().enablePlugin(plugin);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isPlayerRegistered(OfflinePlayer player) {
        if (!plugin.getVariables().isOnlineMode()) {
            return RegisterCMD.getCfg().contains(Objects.requireNonNull(player.getName()));
        }
        return true;
    }

    public BackupManager getBackupManager() {
        return new BackupManager();
    }

    public GameMode getGameModeById(int id) {
        return GameModeCMD.getGameModeById(id);
    }

    public String toPrettyJson(Object obj) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(obj);
    }

    public String toJson(Object obj) {
        return new Gson().toJson(obj);
    }

    /*
    protected Villager villagerCreate(Player player) {
        Villager villager = (Villager) player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
        ArrayList<MerchantRecipe> recipes = new ArrayList<>();
        MerchantRecipe recipe = new MerchantRecipe(new ItemBuilder(Material.STICK).setAmount(1).setDisplayName("§aParty!").addEnchantment(Enchantment.KNOCKBACK, 10, true).build(), Integer.MAX_VALUE);
        ItemStack stack = new ItemStack(Material.STICK);
        stack.setAmount(2);
        recipe.addIngredient(stack);
        villager.setProfession(Villager.Profession.ARMORER);
        villager.setVillagerExperience(1);
        recipes.add(recipe);
        villager.setRecipes(recipes);
        villager.setVillagerLevel(1);
        villager.setVillagerType(Villager.Type.DESERT);
        return villager;
    }
     */

    /**
     * Create a new Kit for the Plugin
     *
     * @param kitName the KitName for the new Created Kit
     * @param items   an Array of Items with all Items witch are contained in the Kit
     */
    public void createKit(String kitName, ItemStack[] items) {
        new KitManager().createKit(kitName, items);
    }

    /**
     * return is the Player TempMuted or not
     *
     * @param player the selected Player
     * @return if Player is tempMuted or not
     */
    public boolean isPlayerTempMuted(OfflinePlayer player) {
        return !plugin.getRegisterManager().getMuteCMD().isExpired((Player) player);
    }

    /**
     * return is the Player TempBanned or not
     *
     * @param player the selected Player
     * @return return is the Player is TempBanned or not
     */
    public boolean isPlayerTempBanned(OfflinePlayer player) {
        if (plugin.isMysql() || plugin.isSQL()) {
            return new BanMuteManager().isTempBan(player);
        }

        return Bukkit.getServer().getBanList(BanList.Type.NAME).isBanned(Objects.requireNonNull(player.getName()));
    }

    /**
     * return is the Player PermBanned or not
     *
     * @param player the selected Player
     * @return return is the Player is PermBan or not
     */
    public boolean isPlayerPermBanned(OfflinePlayer player) {
        if (plugin.isMysql() || plugin.isSQL()) {
            return new BanMuteManager().isPermBan(player);
        }
        return BanFile.isBanned(player.getName());
    }

    /**
     * get the TempMute Reason and to Expire Date
     *
     * @param player the selected Player
     * @return return the TempMute Reason and Expire Date
     */
    public HashMap<String, String> getTempMuteReasonAndExpireDateFromPlayer(OfflinePlayer player) {
        if (plugin.isMysql() || plugin.isSQL()) {
            return (HashMap<String, String>) new BanMuteManager().getTempMute(player);
        }
        HashMap<String, String> hash = new HashMap<>();
        hash.put(MuteCMD.cfg.getString(player.getName() + ".expire"), MuteCMD.cfg.getString(player.getName() + ".reason"));
        return hash;
    }

    /**
     * get the TempBan Reason and to Expire Date
     *
     * @param player the selected Player
     * @return return the Tempban Reason and Expire Date
     */
    public HashMap<String, String> getTempBanReasonAndExpireDateFromPlayer(OfflinePlayer player) {
        if (plugin.isMysql() || plugin.isSQL()) {
            return (HashMap<String, String>) new BanMuteManager().getTempBan(player);
        }
        HashMap<String, String> hash = new HashMap<>();
        String date = new SimpleDateFormat("dd.MM.yyyy | HH:mm:ss").format(Objects.requireNonNull(Bukkit.getServer().getBanList(BanList.Type.NAME).getBanEntry(Objects.requireNonNull(player.getName()))).getExpiration());
        String reason = Objects.requireNonNull(Bukkit.getServer().getBanList(BanList.Type.NAME).getBanEntry(player.getName())).getReason();
        hash.put(date, reason);
        return hash;
    }

    /**
     * Get the Perm ban Reason from the selected Player
     *
     * @param player the selected Player
     * @return return the Perm ban Reason from the selected Player
     */
    public String getPermBanReason(OfflinePlayer player) {
        if (plugin.isMysql() || plugin.isSQL()) {
            return new BanMuteManager().getPermBanReason(player);
        }
        return BanFile.getBannedReason(player.getName());
    }

    public boolean isEconomyActivated() {
        return plugin.getConfig().getBoolean("Economy.Activated");
    }

    public boolean isMySQL() {
        return plugin.isMysql();
    }

    public boolean isSQL() {
        return plugin.isSQL();
    }

    public boolean isMongoDB() {
        return plugin.isMongoDB();
    }

    public void setPlayerGodMode(Player player, boolean godMode) {
        player.setInvulnerable(godMode);
    }

    /**
     * Get a List of all Bankmembers in the Bank
     *
     * @param bankName the Bank Name
     * @return A list of all Bankmembers
     */
    public List<String> getBankMembers(String bankName) {
        if (economy) {
            return plugin.getVaultManager().getBankMembers(bankName);
        }
        return null;
    }

    /**
     * Return all Banks in String
     *
     * @return returns a List of String from all Bank Names
     */
    public List<String> getBanks() {
        if (economy)
            return plugin.getVaultManager().getEco().getBanks();
        return null;
    }

    /**
     * Return all Player accounts
     *
     * @return return a List of all Registered Player Accounts
     */
    public List<String> getAccounts() {
        if (economy)
            plugin.getVaultManager().getAccounts();
        return null;
    }

    public Main getPlugin() {
        return plugin;
    }

    public double getProgress() {
        return progress;
    }

    public int getTime() {
        return time;
    }
}
