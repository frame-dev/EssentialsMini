package de.framedev.essentialsmini.commands.playercommands;


/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 13.08.2020 19:11
 */

import de.framedev.essentialsmini.main.Main;
import de.framedev.essentialsmini.abstracts.CommandBase;
import de.framedev.essentialsmini.utils.AdminBroadCast;
import de.framedev.essentialsmini.utils.ReplaceCharConfig;
import de.framedev.essentialsmini.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageCMD extends CommandBase {

    private final Main plugin;

    public MessageCMD(Main plugin) {
        super(plugin, "msg", "r", "spy", "msgtoggle");
        this.plugin = plugin;
    }

    HashMap<Player, Player> reply = new HashMap<>();
    ArrayList<Player> spy = new ArrayList<>();
    ArrayList<Player> msgToggle = new ArrayList<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (command.getName().equalsIgnoreCase("msgtoggle")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission(plugin.getPermissionName() + "msgtoggle")) {
                    if (msgToggle.contains(player)) {
                        msgToggle.remove(player);
                        String msgToggleMessage = plugin.getLanguageConfig(player).getString("MsgToggle.Deactivated");
                        msgToggleMessage = new TextUtils().replaceAndToParagraph(msgToggleMessage);
                        player.sendMessage(plugin.getPrefix() + msgToggleMessage);
                        return true;
                    } else {
                        String msgToggleMessage = plugin.getLanguageConfig(player).getString("MsgToggle.Activated");
                        msgToggleMessage = new TextUtils().replaceAndToParagraph(msgToggleMessage);
                        player.sendMessage(plugin.getPrefix() + msgToggleMessage);
                        msgToggle.add(player);
                        return true;
                    }
                } else {
                    player.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                    new AdminBroadCast("msgtoggle", "§cNo Permissions!", sender);
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
            }
        }
        if (command.getName().equalsIgnoreCase("msg")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                String message = "";
                if (player.hasPermission("essentialsmini.msg")) {
                    if (args.length >= 2) {
                        Player target = Bukkit.getPlayer(args[0]);
                        if (!msgToggle.contains(target)) {
                            if (target != null) {
                                for (int i = 1; i < args.length; i++) {
                                    message = message + args[i] + " ";
                                }
                                for (Player opPlayer : Bukkit.getOnlinePlayers()) {
                                    if (opPlayer.hasPermission("essentialsmini.spy")) {
                                        if (spy.contains(opPlayer)) {
                                            String spy = plugin.getLanguageConfig(player).getString("SpyMessage");
                                            spy = ReplaceCharConfig.replaceParagraph(spy);
                                            spy = ReplaceCharConfig.replaceObjectWithData(spy, "%Player%", player.getName());
                                            spy = ReplaceCharConfig.replaceObjectWithData(spy, "%Target%", target.getName());
                                            spy = ReplaceCharConfig.replaceObjectWithData(spy, "%Message%", message);
                                            opPlayer.sendMessage(spy);
                                        }
                                    }
                                }
                                player.sendMessage("§cme §r-> §a " + target.getName() + " §f» " + message);
                                target.sendMessage("§a" + player.getName() + " §r-> §cme  §f» " + message);
                                message = "";
                                reply.put(target, player);
                            }
                        } else if (player.hasPermission(plugin.getPermissionName() + "msgtoggle.bypass")) {
                            if (target != null) {
                                for (int i = 1; i < args.length; i++) {
                                    message = message + args[i] + " ";
                                }
                                for (Player opPlayer : Bukkit.getOnlinePlayers()) {
                                    if (opPlayer.hasPermission("essentialsmini.spy")) {
                                        if (spy.contains(opPlayer)) {
                                            String spy = plugin.getLanguageConfig(player).getString("SpyMessage");
                                            spy = ReplaceCharConfig.replaceParagraph(spy);
                                            spy = ReplaceCharConfig.replaceObjectWithData(spy, "%Player%", player.getName());
                                            spy = ReplaceCharConfig.replaceObjectWithData(spy, "%Target%", target.getName());
                                            spy = ReplaceCharConfig.replaceObjectWithData(spy, "%Message%", message);
                                            opPlayer.sendMessage(spy);
                                        }
                                    }
                                }
                                player.sendMessage("§cme §r-> §a " + target.getName() + " §f» " + message);
                                target.sendMessage("§a" + player.getName() + " §r-> §cme  §f» " + message);
                                message = "";
                                reply.put(target, player);
                            }
                        } else {
                            if (target != null) {
                                String msgToggleMessage = plugin.getLanguageConfig(player).getString("MsgToggle.Message");
                                msgToggleMessage = new TextUtils().replaceAndToParagraph(msgToggleMessage);
                                player.sendMessage(plugin.getPrefix() + msgToggleMessage);
                            }
                        }
                    } else {
                        player.sendMessage(plugin.getPrefix() + plugin.getWrongArgs("/msg <PlayerName> <Message>"));
                    }
                } else {
                    player.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                }
            } else if (sender instanceof BlockCommandSender) {
                BlockCommandSender commandBlock = (BlockCommandSender) sender;
                Player target = Bukkit.getPlayer(args[0]);
                String message = "";
                for (int i = 1; i < args.length; i++) {
                    message = message + args[i] + " ";
                }
                if (target == null) {
                    return true;
                }
                target.sendMessage("§a" + commandBlock.getName() + " §r-> §cme  §f» " + message);
            } else {
                sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
            }
        }
        if (command.getName().equalsIgnoreCase("r")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("essentialsmini.msg")) {
                    if (args.length >= 1) {
                        if (!reply.isEmpty()) {
                            if (reply.containsKey(player)) {
                                Player target = reply.get(player);
                                String message = "";
                                if (target != null) {
                                    for (int i = 0; i < args.length; i++) {
                                        message = message + args[i] + " ";
                                    }
                                    for (Player opPlayer : Bukkit.getOnlinePlayers()) {
                                        if (opPlayer.hasPermission("essentialsmini.spy")) {
                                            if (spy.contains(opPlayer)) {
                                                String spy = plugin.getLanguageConfig(player).getString("SpyMessage");
                                                spy = ReplaceCharConfig.replaceParagraph(spy);
                                                spy = ReplaceCharConfig.replaceObjectWithData(spy, "%Player%", player.getName());
                                                spy = ReplaceCharConfig.replaceObjectWithData(spy, "%Target%", target.getName());
                                                spy = ReplaceCharConfig.replaceObjectWithData(spy, "%Message%", message);
                                                opPlayer.sendMessage(spy);
                                            }
                                        }
                                    }
                                    player.sendMessage("§cme §r-> §a " + target.getName() + " §f» " + message);
                                    target.sendMessage("§a" + player.getName() + " §r-> §cme  §f» " + message);
                                    message = "";
                                    reply.remove(player);
                                    reply.put(target, player);
                                }
                            } else {
                                player.sendMessage(plugin.getPrefix() + "§cDir wurde vor kurzem keine Nachricht geschrieben!");
                            }
                        }
                    } else {
                        player.sendMessage(plugin.getPrefix() + plugin.getWrongArgs("/r <Message>"));
                    }
                } else {
                    player.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
            }
        }
        if (command.getName().equalsIgnoreCase("spy")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("essentialsmini.spy")) {
                    if (!spy.contains(player)) {
                        String spyMessage = plugin.getLanguageConfig(player).getString("Spy.Activate");
                        spyMessage = new TextUtils().replaceAndToParagraph(spyMessage);
                        player.sendMessage(plugin.getPrefix() + spyMessage);
                        spy.add(player);
                    } else {
                        String spyMessage = plugin.getLanguageConfig(player).getString("Spy.Deactivate");
                        spyMessage = ReplaceCharConfig.replaceParagraph(spyMessage);
                        player.sendMessage(plugin.getPrefix() + spyMessage);
                        spy.remove(player);
                    }
                } else {
                    player.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
            }
        }
        return false;
    }
}
