package de.framedev.essentialsmin.commands;


/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 10.08.2020 19:21
 */

import de.framedev.essentialsmin.main.Main;
import de.framedev.essentialsmin.managers.PlayerManager;
import de.framedev.essentialsmin.managers.PlayerManagerCfgLoss;
import de.framedev.essentialsmin.managers.PlayerManagerMongoDB;
import org.bson.json.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayerDataCMD implements CommandExecutor {

    private final Main plugin;
    private final boolean jsonFormat;

    public PlayerDataCMD(Main plugin) {
        this.plugin = plugin;
        plugin.getCommands().put("playerdata",this);
        jsonFormat = plugin.getConfig().getBoolean("JsonFormat");
    }

    public boolean isJsonFormat() {
        return jsonFormat;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.hasPermission(new Permission("essentialsmini.playerdata", PermissionDefault.OP))) {
            if(args.length == 1) {
                if(!isJsonFormat()) {
                    final PlayerManager playerManager = new PlayerManager(args[0]);
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                    long login = playerManager.getLastLogin();
                    sender.sendMessage("§6Info About §a" + player.getName());
                    sender.sendMessage(
                            "§aLast Login : §6" + new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(new Date(login)));
                    long logout = playerManager.getLastLogout();
                    sender.sendMessage("§aLast Logout : §6"
                            + new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(new Date(logout)));
                    sender.sendMessage("§aTime Played : §6" + playerManager.getTimePlayed());
                    sender.sendMessage("§aOnline : §6" + player.isOnline());
                    sender.sendMessage("§aPlayerKills : §6" + playerManager.getPlayerKills());
                    sender.sendMessage("§aEntityKills : §6" + playerManager.getEntityKills());
                    sender.sendMessage("§aDamage : §6" + playerManager.getDamage());
                    sender.sendMessage("§aDeaths : §6" + playerManager.getDeaths());
                    sender.sendMessage("§aCommandsUsed : §6" + playerManager.getCommandsUsed());
                    sender.sendMessage("§aBlocksBroken : §6" + playerManager.getBlockBroken());
                    sender.sendMessage("§aBlocksPlaced : §6" + playerManager.getBlockPlace());
                } else if(!plugin.isMongoDb()) {
                    PlayerManagerCfgLoss playerManager = null;
                    //noinspection deprecation
                    if(Bukkit.getOfflinePlayer(args[0]).isOnline()) {
                        //noinspection deprecation
                        playerManager = plugin.getCfgLossHashMap().get(Bukkit.getOfflinePlayer(args[0]));
                    } else {
                        try {
                            //noinspection deprecation
                            playerManager = PlayerManagerCfgLoss.getPlayerManagerCfgLoss(Bukkit.getOfflinePlayer(args[0]));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    //noinspection deprecation
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                    if(plugin.isMysql()) {
                        if(player.isOnline()) {
                            if(!plugin.getCfgLossHashMap().isEmpty()) {
                                if(plugin.getCfgLossHashMap().containsKey(player))
                                playerManager = plugin.getCfgLossHashMap().get(player);
                            }
                        } else {
                            if (PlayerManagerCfgLoss.loadPlayerData(player) != null) {
                                playerManager = PlayerManagerCfgLoss.loadPlayerData(player);
                            } else {
                                playerManager = new PlayerManagerCfgLoss(player);
                            }
                        }
                    }
                    long login = playerManager.getLastlogin();
                    sender.sendMessage("§6Info About §a" + player.getName());
                    sender.sendMessage(
                            "§aLast Login : §6" + new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(new Date(login)));
                    long logout = playerManager.getLastLogout();
                    sender.sendMessage("§aLast Logout : §6"
                            + new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(new Date(logout)));
                    sender.sendMessage("§aTime Played : §6" + playerManager
                            .getTimePlayed());
                    //noinspection deprecation
                    sender.sendMessage("§aOnline : §6" + player.isOnline());
                    sender.sendMessage("§aPlayerKills : §6" + playerManager.getPlayerKills());
                    sender.sendMessage("§aEntityKills : §6" + playerManager.getEntityKills());
                    sender.sendMessage("§aDamage : §6" + playerManager.getDamage());
                    sender.sendMessage("§aDeaths : §6" + playerManager.getDeaths());
                    sender.sendMessage("§aCommandsUsed : §6" + playerManager.getCommandsUsed());
                    sender.sendMessage("§aBlocksBroken : §6" + playerManager.getBlockBroken());
                    sender.sendMessage("§aBlocksPlaced : §6" + playerManager.getBlockPlace());

                    try {
                        JsonWriter jsonWriter = new JsonWriter(new FileWriter("test.json"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    PlayerManagerMongoDB playerManager = PlayerManagerMongoDB.getPlayerManager(args[0],"test");
                    long login = playerManager.getLastLogin();
                    sender.sendMessage("§6Info About §a" + Bukkit.getOfflinePlayer(args[0]).getName());
                    sender.sendMessage(
                            "§aLast Login : §6" + new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(new Date(login)));
                    long logout = playerManager.getLastLogout();
                    sender.sendMessage("§aLast Logout : §6"
                            + new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(new Date(logout)));
                    sender.sendMessage("§aOnline : §6" + !playerManager.isOffline());
                    sender.sendMessage("§aPlayerKills : §6" + playerManager.getKills());
                    sender.sendMessage("§aEntityKills : §6" + playerManager.getEntityKills());
                    sender.sendMessage("§aDamage : §6" + playerManager.getDamage());
                    sender.sendMessage("§aDeaths : §6" + playerManager.getDeaths());
                    sender.sendMessage("§aCommandsUsed : §6" + playerManager.getCommandsUsed());
                    sender.sendMessage("§aBlocksBroken : §6" + playerManager.getBlocksBroken().size());
                    sender.sendMessage("§aBlocksPlaced : §6" + playerManager.getBlocksPlacen().size());
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + plugin.getWrongArgs("/playerdata <PlayerName>"));
            }
        } else {
            sender.sendMessage(plugin.getPrefix() + plugin.getNOPERMS());
        }
        return false;
    }
}
