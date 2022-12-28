package de.framedev.essentialsmini.commands.playercommands;

import de.framedev.essentialsmini.main.Main;
import de.framedev.essentialsmini.abstracts.CommandBase;
import de.framedev.essentialsmini.utils.AdminBroadCast;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 18.07.2020 13:32
 */
public class KillCMD extends CommandBase {

    private final Main plugin;
    public static boolean suicid = false;

    public KillCMD(Main plugin) {
        super(plugin, "killall", "suicid");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("killall")) {
            if (sender.hasPermission(new Permission(plugin.getPermissionName() + "killall", PermissionDefault.OP))) {
                if (args.length == 1) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (args[0].equalsIgnoreCase("animals")) {
                            for (Entity entity : player.getWorld().getEntities()) {
                                if (entity instanceof Animals) {
                                    entity.remove();
                                }
                            }
                            player.sendMessage(plugin.getPrefix() + "§aEs wurden alle Tiere in deiner Umgebung entfernt!");
                        } else if (args[0].equalsIgnoreCase("mobs")) {
                            for (Entity entity : player.getWorld().getEntities()) {
                                if (entity instanceof Mob) {
                                    entity.remove();
                                }
                            }
                            player.sendMessage(plugin.getPrefix() + "§aEs wurden alle Monster in deiner Umgebung entfernt!");
                        } else if (args[0].equalsIgnoreCase("players")) {
                            for (Entity entity : player.getWorld().getEntities()) {
                                if (entity instanceof Player) {
                                    entity.remove();
                                }
                            }

                            player.sendMessage(plugin.getPrefix() + "§aEs wurden alle Spieler in deiner Umgebung entfernt!");
                        } else if (args[0].equalsIgnoreCase("items")) {
                            for (Entity entity : player.getWorld().getEntities()) {
                                if (entity instanceof Item) {
                                    entity.remove();
                                }
                            }
                            player.sendMessage(plugin.getPrefix() + "§aEs wurden alle Items in deiner Umgebung entfernt!");
                        }
                    } else {
                        sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
                    }
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                new AdminBroadCast("killall","§cNo Permissions!", sender);
            }
        }
        if (command.getName().equalsIgnoreCase("suicid")) {
            if(args.length == 0) {
                if (sender.hasPermission(plugin.getPermissionName() + "suicid")) {
                    if (sender instanceof Player) {
                        suicid = true;
                        ((Player) sender).setHealth(0);
                        ((Player) sender).setFoodLevel(0);
                        ((Player) sender).getWorld().getPlayers().forEach(players -> players.sendMessage("§6" + sender.getName() + " §ahat Suicid begangen!"));
                    } else {
                        sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
                    }
                } else {
                    sender.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                    new AdminBroadCast("suicid","§cNo Permissions!", sender);
                }
            } else if(args.length == 1) {
                if(sender.hasPermission(plugin.getPermissionName() + "suicid.others")) {
                    Player player = Bukkit.getPlayer(args[0]);
                    if(player == null) {
                        sender.sendMessage(plugin.getPrefix() + plugin.getVariables().getPlayerNameNotOnline(args[0]));
                        return true;
                    }
                    suicid = true;
                    player.setHealth(0);
                    player.setFoodLevel(0);
                    player.getWorld().getPlayers().forEach(players -> players.sendMessage("§6" + player.getName() + " §ahat Suicid begangen!"));
                    sender.sendMessage(plugin.getPrefix() + "§6" + player.getName() + " §ahat Suicid begangen!");
                } else {
                    sender.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                    new AdminBroadCast("suicid","§cNo Permissions!", sender);
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("killall")) {
            if (sender.hasPermission(plugin.getPermissionName() + "killall")) {
                if (args.length == 1) {
                    ArrayList<String> cmds = new ArrayList<>();
                    ArrayList<String> empty = new ArrayList<>();
                    cmds.add("items");
                    cmds.add("players");
                    cmds.add("mobs");
                    cmds.add("animals");
                    for (String s : cmds) {
                        if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                            empty.add(s);
                        }
                    }
                    Collections.sort(empty);
                    return empty;
                }
            }
        }
        return null;
    }
}
