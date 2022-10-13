package de.framedev.essentialsmini.listeners;


/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 18.08.2020 22:47
 */

import de.framedev.essentialsmini.api.events.*;
import de.framedev.essentialsmini.commands.playercommands.KillCMD;
import de.framedev.essentialsmini.commands.playercommands.SpawnCMD;
import de.framedev.essentialsmini.commands.playercommands.VanishCMD;
import de.framedev.essentialsmini.database.BackendManager;
import de.framedev.essentialsmini.main.Main;
import de.framedev.essentialsmini.managers.LocationsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import static org.bukkit.Bukkit.getServer;

public class PlayerListeners implements Listener {

    private final Main plugin;
    private final boolean jsonFormat;
    private final String collection = "essentialsmini_data";

    private final boolean onlyEssentialsFeatures;

    public PlayerListeners(Main plugin) {
        this.plugin = plugin;
        plugin.getListeners().add(this);
        String permissionBase = plugin.getPermissionName();
        jsonFormat = plugin.getConfig().getBoolean("JsonFormat");
        this.onlyEssentialsFeatures = plugin.getConfig().getBoolean("OnlyEssentialsFeatures");
    }

    public boolean isOnlyEssentialsFeatures() {
        return onlyEssentialsFeatures;
    }

    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("PlayerInfoSave");
    }

    public boolean isJsonFormat() {
        return jsonFormat;
    }

    @EventHandler
    public void onColorChat(AsyncPlayerChatEvent event) {
        if (plugin.getConfig().getBoolean("ColoredChat")) {
            String message = event.getMessage();
            if (message.contains("&"))
                message = message.replace('&', '§');
            event.setMessage(message);
        }
    }

    @EventHandler
    public void onSignColo(SignChangeEvent event) {
        if (plugin.getConfig().getBoolean("ColoredSigns")) {
            for (int i = 0; i < event.getLines().length; i++) {
                if (event.getLines()[i].contains("&")) {
                    String line = event.getLines()[i];
                    line = line.replace('&', '§');
                    event.setLine(i, line);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!VanishCMD.hided.contains(event.getPlayer().getName())) {
            if (plugin.getConfig().getBoolean("JoinBoolean")) {
                if (plugin.getConfig().getBoolean("IgnoreJoinLeave")) {
                    if (event.getPlayer().hasPermission("essentialsmini.ignorejoin")) {
                        event.setJoinMessage(null);
                    } else {
                        String joinMessage = plugin.getLanguageConfig(event.getPlayer()).getString("JoinMessage");
                        if (joinMessage == null) return;
                        if (joinMessage.contains("&"))
                            joinMessage = joinMessage.replace('&', '§');
                        if (joinMessage.contains("%Player%"))
                            joinMessage = joinMessage.replace("%Player%", event.getPlayer().getName());
                        event.setJoinMessage(joinMessage);
                    }
                } else {
                    String joinMessage = plugin.getLanguageConfig(event.getPlayer()).getString("JoinMessage");
                    if (joinMessage == null) return;
                    if (joinMessage.contains("&"))
                        joinMessage = joinMessage.replace('&', '§');
                    if (joinMessage.contains("%Player%"))
                        joinMessage = joinMessage.replace("%Player%", event.getPlayer().getName());
                    event.setJoinMessage(joinMessage);
                }
            }
        } else {
            event.setJoinMessage(null);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (plugin.getConfig().getBoolean("SpawnTP")) {
                    if (!event.getPlayer().getWorld().getName().equalsIgnoreCase("plotme1")) {
                        LocationsManager spawnLocation = new LocationsManager("spawn");
                        try {
                            event.getPlayer().teleport(spawnLocation.getLocation());
                        } catch (IllegalArgumentException ex) {
                            event.getPlayer().teleport(event.getPlayer().getWorld().getSpawnLocation());
                        }
                    }
                }
                cancel();

            }
        }.runTaskLater(plugin, 20);
        /*if (plugin.getConfig().getBoolean("PlayerEvents")) {
            if (isEnabled() && !onlyEssentialsFeatures) {
                if (!isJsonFormat()) {
                    new PlayerManager(event.getPlayer().getUniqueId()).setLastLogin(System.currentTimeMillis());
                } else {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            try {
                                if (!plugin.getCfgLossHashMap().containsKey(event.getPlayer())) {
                                    if (plugin.isMysql()) {
                                        if (PlayerManagerCfgLoss.loadPlayerData(event.getPlayer()) != null) {
                                            plugin.getCfgLossHashMap().put(event.getPlayer(), PlayerManagerCfgLoss.loadPlayerData(event.getPlayer()));
                                        } else {
                                            plugin.getCfgLossHashMap().put(event.getPlayer(), new PlayerManagerCfgLoss(event.getPlayer()));
                                        }
                                        plugin.getCfgLossHashMap().get(event.getPlayer()).setLastLogin(System.currentTimeMillis());
                                    } else {
                                        plugin.getCfgLossHashMap().put(event.getPlayer(), PlayerManagerCfgLoss.getPlayerManagerCfgLoss(event.getPlayer()));
                                        plugin.getCfgLossHashMap().get(event.getPlayer()).setLastLogin(System.currentTimeMillis());
                                    }
                                }
                            } catch (FileNotFoundException e) {
                                if (!plugin.getCfgLossHashMap().containsKey(event.getPlayer())) {
                                    if (plugin.isMysql()) {
                                        if (PlayerManagerCfgLoss.loadPlayerData(event.getPlayer()) != null) {
                                            plugin.getCfgLossHashMap().put(event.getPlayer(), PlayerManagerCfgLoss.loadPlayerData(event.getPlayer()));
                                        } else {
                                            plugin.getCfgLossHashMap().put(event.getPlayer(), new PlayerManagerCfgLoss(event.getPlayer()));
                                        }
                                    } else {
                                        PlayerManagerCfgLoss cfgLoss = new PlayerManagerCfgLoss(event.getPlayer());
                                        plugin.getCfgLossHashMap().put(event.getPlayer(), cfgLoss);
                                        plugin.getCfgLossHashMap().get(event.getPlayer()).setLastLogin(System.currentTimeMillis());
                                    }
                                }
                            }
                            cancel();
                        }
                    }.runTaskLater(plugin, 20 * 6);
                }
            }

            if (isEnabled() && !onlyEssentialsFeatures) {
                if (Bukkit.getPluginManager().getPlugin("MDBConnection") != null) {
                    if (Main.cfgMongoDB.getBoolean("MongoDB.LocalHost") || Main.cfgMongoDB.getBoolean("MongoDB.Boolean")) {
                        plugin.getBackendManager().createUserMoney(event.getPlayer(), collection);
                        plugin.getBackendManager().updateUser(event.getPlayer(), "lastLogin", System.currentTimeMillis() + "", collection);
                        plugin.getBackendManager().updateUser(event.getPlayer(), "offline", false, collection);
                        PlayerManagerMongoDB.getPlayerManager(event.getPlayer().getUniqueId(), collection).save(collection);
                    }
                }

                plugin.removeOfflinePlayer(event.getPlayer());
            }
        }*/
        if (plugin.getVaultManager() != null && plugin.getVaultManager().getEco() != null) {
            if (plugin.isMongoDB()) {
                if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
                    if (plugin.getVaultManager().getEco().hasAccount(event.getPlayer())) {
                        plugin.getBackendManager().updateUser(event.getPlayer(), BackendManager.DATA.MONEY.getName(), plugin.getVaultManager().getEco().getBalance(event.getPlayer()), collection);
                    }
                }
            }
            plugin.getVaultManager().getEco().createPlayerAccount(event.getPlayer());
        }
        if (!event.getPlayer().hasPlayedBefore()) {
            if (plugin.getConfig().getBoolean("StartBalance.Boolean")) {
                double startBalance = plugin.getConfig().getDouble("StartBalance.Amount");
                plugin.getVaultManager().getEco().depositPlayer(event.getPlayer(), startBalance);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        /*if (plugin.getConfig().getBoolean("PlayerEvents")) {
            if (isEnabled() && !onlyEssentialsFeatures) {
                plugin.addOfflinePlayer(event.getPlayer());
                plugin.savePlayers();
                if (!isJsonFormat()) {
                    new PlayerManager(event.getPlayer().getUniqueId()).setLastLogout(System.currentTimeMillis());
                    if (plugin.isMysql() || plugin.isSQL()) {
                        new PlayerManager(event.getPlayer()).savePlayerData(event.getPlayer());
                        String table = "essentialsmini_playerdata";
                        if (SQL.isTableExists("essentialsmini_playerdata")) {
                            if (SQL.exists(table, "UUID", event.getPlayer().getUniqueId().toString())) {
                                SQL.updateData(table, "PlayerData", "'" + new GsonBuilder().serializeNulls().create().
                                        toJson(PlayerManager.loadPlayerData(event.getPlayer())) + "'", "UUID ='" + event.getPlayer().getUniqueId().toString() + "'");
                            } else {
                                SQL.insertData(table, "'" + event.getPlayer().getUniqueId().toString() + "','" + event.getPlayer().getName() + "'," +
                                        "'" + new GsonBuilder().serializeNulls().create().
                                        toJson(PlayerManager.loadPlayerData(event.getPlayer())) + "'", "UUID", "PlayerName", "PlayerData");
                            }
                        } else {
                            SQL.createTable("essentialsmini_playerdata", "UUID VARCHAR(1225)", "PlayerName TEXT", "PlayerData TEXT");
                            SQL.insertData(table, "'" + event.getPlayer().getUniqueId().toString() + "','" + event.getPlayer().getName() + "'," +
                                    "'" + new GsonBuilder().serializeNulls().create().
                                    toJson(PlayerManager.loadPlayerData(event.getPlayer())) + "'", "UUID", "PlayerName", "PlayerData");
                        }
                    }
                } else {
                    if (plugin.getCfgLossHashMap().containsKey(event.getPlayer())) {
                        plugin.getCfgLossHashMap().get(event.getPlayer()).setLastLogout(System.currentTimeMillis());
                        plugin.getCfgLossHashMap().get(event.getPlayer()).saveTimePlayed();
                        plugin.getCfgLossHashMap().get(event.getPlayer()).savePlayerManager();
                        plugin.getCfgLossHashMap().get(event.getPlayer()).savePlayerManager();
                        if (plugin.isMysql() || plugin.isSQL()) {
                            String table = "essentialsmini_playerdata";
                            if (SQL.isTableExists("essentialsmini_playerdata")) {
                                if (SQL.exists(table, "UUID", event.getPlayer().getUniqueId().toString())) {
                                    SQL.updateData(table, "PlayerData", "'" + new GsonBuilder().serializeNulls().create().
                                            toJson(plugin.getCfgLossHashMap().get(event.getPlayer())) + "'", "UUID ='" + event.getPlayer().getUniqueId().toString() + "'");
                                } else {
                                    SQL.insertData(table, "'" + event.getPlayer().getUniqueId().toString() + "','" + event.getPlayer().getName() + "'," +
                                            "'" + new GsonBuilder().serializeNulls().create().
                                            toJson(plugin.getCfgLossHashMap().get(event.getPlayer())) + "'", "UUID", "PlayerName", "PlayerData");
                                }
                            } else {
                                SQL.createTable("essentialsmini_playerdata", "UUID VARCHAR(1225)", "PlayerName TEXT", "PlayerData TEXT");
                                SQL.insertData(table, "'" + event.getPlayer().getUniqueId().toString() + "','" + event.getPlayer().getName() + "'," +
                                        "'" + new GsonBuilder().serializeNulls().create().
                                        toJson(plugin.getCfgLossHashMap().get(event.getPlayer())) + "'", "UUID", "PlayerName", "PlayerData");
                            }
                            plugin.getCfgLossHashMap().get(event.getPlayer()).setLastLogout(System.currentTimeMillis());
                            plugin.getCfgLossHashMap().get(event.getPlayer()).savePlayerData(event.getPlayer());
                        }
                    }
                }
            }
        }*/
        if (!VanishCMD.hided.contains(event.getPlayer().getName())) {
            if (plugin.getConfig().getBoolean("LeaveBoolean")) {
                if (plugin.getConfig().getBoolean("IgnoreJoinLeave")) {
                    if (event.getPlayer().hasPermission("essentialsmini.ignoreleave")) {
                        event.setQuitMessage(null);
                    } else {
                        String joinMessage = plugin.getLanguageConfig(event.getPlayer()).getString("LeaveMessage");
                        if (joinMessage == null) return;
                        if (joinMessage.contains("&"))
                            joinMessage = joinMessage.replace('&', '§');
                        if (joinMessage.contains("%Player%"))
                            joinMessage = joinMessage.replace("%Player%", event.getPlayer().getName());
                        event.setQuitMessage(joinMessage);
                    }
                } else {
                    String joinMessage = plugin.getLanguageConfig(event.getPlayer()).getString("LeaveMessage");
                    if (joinMessage == null) return;
                    if (joinMessage.contains("&"))
                        joinMessage = joinMessage.replace('&', '§');
                    if (joinMessage.contains("%Player%"))
                        joinMessage = joinMessage.replace("%Player%", event.getPlayer().getName());
                    event.setQuitMessage(joinMessage);
                }
            }
        } else {
            event.setQuitMessage(null);
        }
        /*if (plugin.getConfig().getBoolean("PlayerEvents")) {
            if (isEnabled() && !onlyEssentialsFeatures) {
                if (Bukkit.getPluginManager().getPlugin("MDBConnection") != null) {
                    if (Main.cfgMongoDB.getBoolean("MongoDB.LocalHost") || Main.cfgMongoDB.getBoolean("MongoDB.Boolean")) {
                        plugin.getBackendManager().createUserMoney(event.getPlayer(), collection);
                        plugin.getBackendManager().updateUser(event.getPlayer(), "lastLogout", System.currentTimeMillis() + "", collection);
                        plugin.getBackendManager().updateUser(event.getPlayer(), "offline", true, collection);
                    }
                }
            }
        }*/
    }

    @EventHandler
    public void onPlayerCrafted(InventoryCloseEvent event) {
        /*if (plugin.getConfig().getBoolean("PlayerEvents")) {
            if (event.getInventory().getType() == InventoryType.CRAFTING) {
                if (isJsonFormat()) {
                    if (plugin.getCfgLossHashMap().containsKey((Player) event.getPlayer())) {
                        plugin.getCfgLossHashMap().get((Player) event.getPlayer()).addCrafted();
                    }
                } else {
                    new PlayerManager(event.getPlayer().getUniqueId()).addCrafted();
                }
            }
        }*/
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        plugin.addOfflinePlayer(event.getPlayer());
        plugin.savePlayers();
        /*if (isEnabled() && !onlyEssentialsFeatures) {
            if (!isJsonFormat()) {
                new PlayerManager(event.getPlayer().getUniqueId()).setLastLogout(System.currentTimeMillis());
                if (plugin.isMysql() || plugin.isSQL()) {
                    new PlayerManager(event.getPlayer()).savePlayerData(event.getPlayer());
                    String table = "essentialsmini_playerdata";
                    if (SQL.isTableExists("essentialsmini_playerdata")) {
                        if (SQL.exists(table, "UUID", event.getPlayer().getUniqueId().toString())) {
                            SQL.updateData(table, "PlayerData", "'" + new GsonBuilder().serializeNulls().create().
                                    toJson(PlayerManager.loadPlayerData(event.getPlayer())) + "'", "UUID ='" + event.getPlayer().getUniqueId().toString() + "'");
                        } else {
                            SQL.insertData(table, "'" + event.getPlayer().getUniqueId().toString() + "','" + event.getPlayer().getName() + "'," +
                                    "'" + new GsonBuilder().serializeNulls().create().
                                    toJson(PlayerManager.loadPlayerData(event.getPlayer())) + "'", "UUID", "PlayerName", "PlayerData");
                        }
                    } else {
                        SQL.createTable("essentialsmini_playerdata", "UUID VARCHAR(1225)", "PlayerName TEXT", "PlayerData TEXT");
                        SQL.insertData(table, "'" + event.getPlayer().getUniqueId().toString() + "','" + event.getPlayer().getName() + "'," +
                                "'" + new GsonBuilder().serializeNulls().create().
                                toJson(PlayerManager.loadPlayerData(event.getPlayer())) + "'", "UUID", "PlayerName", "PlayerData");
                    }
                }
            } else {
                if (plugin.getCfgLossHashMap().containsKey(event.getPlayer())) {
                    plugin.getCfgLossHashMap().get(event.getPlayer()).setLastLogout(System.currentTimeMillis());
                    plugin.getCfgLossHashMap().get(event.getPlayer()).saveTimePlayed();
                    plugin.getCfgLossHashMap().get(event.getPlayer()).savePlayerManager();
                    plugin.getCfgLossHashMap().get(event.getPlayer()).savePlayerManager();
                    if (plugin.isMysql() || plugin.isSQL()) {
                        String table = "essentialsmini_playerdata";
                        if (SQL.isTableExists("essentialsmini_playerdata")) {
                            if (SQL.exists(table, "UUID", event.getPlayer().getUniqueId().toString())) {
                                SQL.updateData(table, "PlayerData", "'" + new GsonBuilder().serializeNulls().create().
                                        toJson(plugin.getCfgLossHashMap().get(event.getPlayer())) + "'", "UUID ='" + event.getPlayer().getUniqueId().toString() + "'");
                            } else {
                                SQL.insertData(table, "'" + event.getPlayer().getUniqueId().toString() + "','" + event.getPlayer().getName() + "'," +
                                        "'" + new GsonBuilder().serializeNulls().create().
                                        toJson(plugin.getCfgLossHashMap().get(event.getPlayer())) + "'", "UUID", "PlayerName", "PlayerData");
                            }
                        } else {
                            SQL.createTable("essentialsmini_playerdata", "UUID VARCHAR(1225)", "PlayerName TEXT", "PlayerData TEXT");
                            SQL.insertData(table, "'" + event.getPlayer().getUniqueId().toString() + "','" + event.getPlayer().getName() + "'," +
                                    "'" + new GsonBuilder().serializeNulls().create().
                                    toJson(plugin.getCfgLossHashMap().get(event.getPlayer())) + "'", "UUID", "PlayerName", "PlayerData");
                        }
                        plugin.getCfgLossHashMap().get(event.getPlayer()).setLastLogout(System.currentTimeMillis());
                        plugin.getCfgLossHashMap().get(event.getPlayer()).savePlayerData(event.getPlayer());
                    }
                }
            }
            if (Bukkit.getPluginManager().getPlugin("MDBConnection") != null) {
                if (Main.cfgMongoDB.getBoolean("MongoDB.LocalHost") || Main.cfgMongoDB.getBoolean("MongoDB.Boolean")) {
                    plugin.getBackendManager().updateUser(event.getPlayer(), DATA.LAST_LOGOUT.getName(), System.currentTimeMillis() + "", collection);
                    plugin.getBackendManager().updateUser(event.getPlayer(), "offline", true, collection);
                }
            }
        }*/
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        /*if (plugin.getConfig().getBoolean("PlayerEvents")) {
            if (event.getDamager() instanceof Player) {
                if (isEnabled() && !onlyEssentialsFeatures) {
                    Player damager = (Player) event.getDamager();
                    if (isJsonFormat()) {
                        if (plugin.getCfgLossHashMap().containsKey(damager)) {
                            plugin.getCfgLossHashMap().get(damager).addDamage(event.getFinalDamage());
                        }
                    } else {
                        new PlayerManager(damager).addDamage(event.getFinalDamage());
                    }
                    if (Bukkit.getPluginManager().getPlugin("MDBConnection") != null) {
                        if (Main.cfgMongoDB.getBoolean("MongoDB.LocalHost") || Main.cfgMongoDB.getBoolean("MongoDB.Boolean")) {
                            double damage = (double) plugin.getBackendManager().get(damager, "damage", collection);
                            damage += event.getFinalDamage();
                            plugin.getBackendManager().updateUser(damager, "damage", damage, collection);
                        }
                    }
                }
            }
        }*/
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            if (event.getEntity() instanceof Player) {
                getServer().getPluginManager().callEvent(new PlayerKillPlayerEvent((Player) event.getEntity(), event.getEntity().getKiller(), event.getDrops(), event.getDroppedExp()));
            }
            Bukkit.getPluginManager().callEvent(new PlayerKillEntityEvent(event.getEntity().getKiller(), event.getEntity(), event.getDrops(), event.getDroppedExp()));
        }
        /*if (plugin.getConfig().getBoolean("PlayerEvents")) {
            LivingEntity livingEntity = event.getEntity();
            if (isEnabled() && !onlyEssentialsFeatures) {
                if (livingEntity.getKiller() != null) {
                    if (livingEntity.getKiller() != null) {
                        if (isJsonFormat()) {
                            if (plugin.getCfgLossHashMap().containsKey(livingEntity.getKiller())) {
                                plugin.getCfgLossHashMap().get(livingEntity.getKiller()).addEntityType(event.getEntityType());
                                plugin.getCfgLossHashMap().get(livingEntity.getKiller()).addEntityKill();
                            }
                        } else {
                            new PlayerManager(livingEntity.getKiller()).addEntityKill();
                            new PlayerManager(livingEntity.getKiller()).addEntityType(event.getEntityType());
                        }
                        if (Bukkit.getPluginManager().getPlugin("MDBConnection") != null) {
                            if (Main.cfgMongoDB.getBoolean("MongoDB.LocalHost") || Main.cfgMongoDB.getBoolean("MongoDB.Boolean")) {
                                int entityKill = (int) plugin.getBackendManager().get(livingEntity.getKiller(), "entityKills", collection);
                                entityKill++;
                                plugin.getBackendManager().updateUser(livingEntity.getKiller(), "entityKills", entityKill, collection);
                                ArrayList<String> materis = (ArrayList<String>) plugin.getBackendManager().get(livingEntity.getKiller(), "entityTypes", collection);
                                if (materis != null) {
                                    if (!materis.contains(event.getEntityType().name())) {
                                        materis.add(event.getEntityType().name());
                                        plugin.getBackendManager().updateUser(livingEntity.getKiller(), "entityTypes", materis, collection);
                                    }
                                } else {
                                    ArrayList<String> types = new ArrayList<>();
                                    types.add(event.getEntityType().name());
                                    plugin.getBackendManager().updateUser(livingEntity.getKiller(), "entityTypes", types, collection);
                                }
                            }
                        }
                    }
                }
            }
        }*/
    }

    /**
     * @param event Respawn event {@link SpawnCMD}
     */
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        try {
            event.setRespawnLocation(new LocationsManager("spawn").getLocation());
        } catch (Exception ignored) {
            event.setRespawnLocation(event.getPlayer().getWorld().getSpawnLocation());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (KillCMD.suicid) {
            event.setDeathMessage(null);
            KillCMD.suicid = false;
        }/* else {
            if (event.getEntity().getKiller() != null) {
                if (event.getEntity().getKiller() != null) {
                    if (isEnabled() && !onlyEssentialsFeatures) {
                        if (isJsonFormat()) {
                            if (plugin.getCfgLossHashMap().containsKey(event.getEntity().getKiller())) {
                                plugin.getCfgLossHashMap().get(event.getEntity().getKiller()).addPlayerKill();
                            }
                        } else {
                            new PlayerManager(event.getEntity().getKiller()).addPlayerKill();
                        }
                        if (Bukkit.getPluginManager().getPlugin("MDBConnection") != null) {
                            if (Main.cfgMongoDB.getBoolean("MongoDB.LocalHost") || Main.cfgMongoDB.getBoolean("MongoDB.Boolean")) {
                                int kills = (int) plugin.getBackendManager().get(event.getEntity().getKiller(), "kills", collection);
                                kills++;
                                plugin.getBackendManager().updateUser(event.getEntity().getKiller(), "kills", kills, collection);
                            }
                        }
                    }
                }
            }
        }*/
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().contains("/clear")) {
            getServer().getPluginManager().callEvent(new PlayerInventoryClearEvent(event.getPlayer(), event.getPlayer().getInventory()));
        }
    }

    @EventHandler
    public void onPlayerDeathByEntity(EntityDeathEvent event) {
        /*if (plugin.getConfig().getBoolean("PlayerEvents")) {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                if (isEnabled() && !onlyEssentialsFeatures) {
                    if (isJsonFormat()) {
                        if (plugin.getCfgLossHashMap().containsKey(player)) {
                            plugin.getCfgLossHashMap().get(player).addDeath();
                        }
                    } else {
                        new PlayerManager(player).addDeath();
                    }
                    if (Bukkit.getPluginManager().getPlugin("MDBConnection") != null) {
                        if (Main.cfgMongoDB.getBoolean("MongoDB.LocalHost") || Main.cfgMongoDB.getBoolean("MongoDB.Boolean")) {
                            int deaths = (int) plugin.getBackendManager().get((Player) event.getEntity(), DATA.DEATHS.getName(), collection);
                            deaths++;
                            plugin.getBackendManager().updateUser((Player) event.getEntity(), DATA.DEATHS.getName(), deaths, collection);
                        }
                    }
                }
            }
        }*/
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        /*if (plugin.getConfig().getBoolean("PlayerEvents")) {
            if (isEnabled() && !onlyEssentialsFeatures) {
                if (isJsonFormat()) {
                    if (plugin.getCfgLossHashMap().containsKey(event.getPlayer())) {
                        plugin.getCfgLossHashMap().get(event.getPlayer()).addBlockBroken();
                        plugin.getCfgLossHashMap().get(event.getPlayer()).addBlockBrokensMaterial(event.getBlock().getType());
                    }
                } else {
                    new PlayerManager(event.getPlayer()).addBlockBroken();
                    new PlayerManager(event.getPlayer()).addBlocksBroken(event.getBlock().getType());
                }
                if (Bukkit.getPluginManager().getPlugin("MDBConnection") != null) {
                    if (Main.cfgMongoDB.getBoolean("MongoDB.LocalHost") || Main.cfgMongoDB.getBoolean("MongoDB.Boolean")) {
                        ArrayList<String> materis = (ArrayList<String>) plugin.getBackendManager().get(event.getPlayer(), "blocksBroken", collection);
                        if (!materis.contains(event.getBlock().getType().name())) {
                            materis.add(event.getBlock().getType().name());
                            plugin.getBackendManager().updateUser(event.getPlayer(), "blocksBroken", materis, collection);
                        }
                        PlayerManagerMongoDB pl = PlayerManagerMongoDB.getPlayerManager(event.getPlayer().getUniqueId(), collection);
                        pl.setBlockBroken(pl.getBlockBroken() + 1);
                        pl.save(collection);
                    }
                }
            }
        }*/
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        /*if (plugin.getConfig().getBoolean("PlayerEvents")) {
            if (isEnabled() && !onlyEssentialsFeatures) {
                if (isJsonFormat()) {
                    if (plugin.getCfgLossHashMap().containsKey(event.getPlayer())) {
                        plugin.getCfgLossHashMap().get(event.getPlayer()).addBlockPlace();
                        plugin.getCfgLossHashMap().get(event.getPlayer()).addBlocksPlace(event.getBlock().getType());
                    }
                } else {
                    new PlayerManager(event.getPlayer()).addBlockPlace();
                    new PlayerManager(event.getPlayer()).addBlocksPlace(event.getBlock().getType());
                }
                if (Bukkit.getPluginManager().getPlugin("MDBConnection") != null) {
                    if (Main.cfgMongoDB.getBoolean("MongoDB.LocalHost") || Main.cfgMongoDB.getBoolean("MongoDB.Boolean")) {
                        ArrayList<String> materis = (ArrayList<String>) plugin.getBackendManager().get(event.getPlayer(), "blocksPlacen", collection);
                        if (!materis.contains(event.getBlock().getType().name())) {
                            materis.add(event.getBlock().getType().name());
                            plugin.getBackendManager().updateUser(event.getPlayer(), "blocksPlacen", materis, collection);
                        }
                        PlayerManagerMongoDB pl = PlayerManagerMongoDB.getPlayerManager(event.getPlayer().getUniqueId(), collection);
                        pl.setBlockPlacen(pl.getBlockPlacen() + 1);
                        pl.save(collection);
                    }
                }
            }
        }*/
    }

    @EventHandler
    public void onCommandUsed(PlayerCommandPreprocessEvent event) {
        /*if (plugin.getConfig().getBoolean("PlayerEvents")) {
            if (isEnabled() && !onlyEssentialsFeatures) {
                if (isJsonFormat()) {
                    if (plugin.getCfgLossHashMap().containsKey(event.getPlayer())) {
                        plugin.getCfgLossHashMap().get(event.getPlayer()).addCommandsUsed();
                    }
                } else {
                    new PlayerManager(event.getPlayer()).addCommandsUsed();
                }
                if (Bukkit.getPluginManager().getPlugin("MDBConnection") != null) {
                    if (Main.cfgMongoDB.getBoolean("MongoDB.LocalHost") || Main.cfgMongoDB.getBoolean("MongoDB.Boolean")) {
                        int commandsUsed = (int) plugin.getBackendManager().get(event.getPlayer(), "commandsUsed", collection);
                        commandsUsed++;
                        plugin.getBackendManager().updateUser(event.getPlayer(), "commandsUsed", commandsUsed, collection);
                    }
                }
            }
        }*/
    }

    @EventHandler
    public void onHitByArrow(ProjectileHitEvent event) {
        if (event.getHitBlock() != null) return;
        if (event.getHitEntity() == null) return;
        if (event.getHitEntity() != null && event.getHitEntity() instanceof Player && event.getEntity().getShooter() != null) {
            if (event.getEntity().getShooter() instanceof Entity)
                getServer().getPluginManager().callEvent(new PlayerHitByProjectileEvent((Player) event.getHitEntity(), (Entity) event.getEntity().getShooter()));
        }
        if (event.getHitEntity() != null && event.getHitEntity() instanceof Entity && event.getEntity().getShooter() != null) {
            getServer().getPluginManager().callEvent(new EntityHitByProjectileEvent(event.getHitEntity(), (Entity) event.getEntity().getShooter()));
        }
    }
}
