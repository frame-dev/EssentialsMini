package ch.framedev.essentialsmini.listeners;


/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 18.08.2020 22:47
 */

import ch.framedev.essentialsmini.api.events.*;
import ch.framedev.essentialsmini.commands.playercommands.KillCMD;
import ch.framedev.essentialsmini.commands.playercommands.SpawnCMD;
import ch.framedev.essentialsmini.commands.playercommands.VanishCMD;
import ch.framedev.essentialsmini.database.BackendManager;
import ch.framedev.essentialsmini.managers.LocationsManager;
import ch.framedev.essentialsmini.main.Main;
import lombok.Getter;
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
    @Getter
    private final boolean jsonFormat;

    public PlayerListeners(Main plugin) {
        this.plugin = plugin;
        plugin.getListeners().add(this);
        String permissionBase = plugin.getPermissionName();
        jsonFormat = plugin.getConfig().getBoolean("JsonFormat");
    }

    public boolean isEnabled() {
        return plugin.getConfig().getBoolean("PlayerInfoSave");
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
        if (plugin.getVaultManager() != null && plugin.getVaultManager().getEco() != null) {
            if (plugin.isMongoDB()) {
                if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
                    if (plugin.getVaultManager().getEco().hasAccount(event.getPlayer())) {
                        String collection = "essentialsmini_data";
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
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        if (plugin.getConfig().getBoolean("PlayerEvents")) {
            if (event.getEntity().getKiller() != null) {
                if (event.getEntity() instanceof Player) {
                    getServer().getPluginManager().callEvent(new PlayerKillPlayerEvent((Player) event.getEntity(), event.getEntity().getKiller(), event.getDrops(), event.getDroppedExp()));
                }
                Bukkit.getPluginManager().callEvent(new PlayerKillEntityEvent(event.getEntity().getKiller(), event.getEntity(), event.getDrops(), event.getDroppedExp()));
            }
        }
    }

    /**
     * @param event Respawn event {@link SpawnCMD}
     */
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        try {
            if (event.getPlayer().getBedSpawnLocation() == null || event.getPlayer().getBedSpawnLocation().equals(new LocationsManager("spawn").getLocation()) && !event.isBedSpawn())
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
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (plugin.getConfig().getBoolean("PlayerEvents"))
            if (event.getMessage().contains("/clear")) {
                getServer().getPluginManager().callEvent(new PlayerInventoryClearEvent(event.getPlayer(), event.getPlayer().getInventory()));
            }
    }

    @EventHandler
    public void onHitByArrow(ProjectileHitEvent event) {
        if (plugin.getConfig().getBoolean("PlayerEvents")) {
            if (event.getHitBlock() != null) return;
            if (event.getHitEntity() == null) return;
            if (event.getHitEntity() != null && event.getHitEntity() instanceof Player && event.getEntity().getShooter() != null) {
                if (event.getEntity().getShooter() instanceof Entity)
                    getServer().getPluginManager().callEvent(new PlayerHitByProjectileEvent((Player) event.getHitEntity(), (Entity) event.getEntity().getShooter()));
            }
            if (event.getHitEntity() != null && event.getHitEntity() != null && event.getEntity().getShooter() != null) {
                getServer().getPluginManager().callEvent(new EntityHitByProjectileEvent(event.getHitEntity(), (Entity) event.getEntity().getShooter()));
            }
        }
    }
}
