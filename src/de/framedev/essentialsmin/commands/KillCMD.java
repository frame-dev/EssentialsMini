package de.framedev.essentialsmin.commands;

import de.framedev.essentialsmin.main.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
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
public class KillCMD implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public KillCMD(Main plugin) {
        this.plugin = plugin;
        plugin.getCommands().put("killall", this);
        plugin.getTabCompleters().put("killall", this);

        plugin.getCommands().put("suicid", this);
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
                sender.sendMessage(plugin.getPrefix() + plugin.getNOPERMS());
            }
        }
        if (command.getName().equalsIgnoreCase("suicid")) {
            if (sender.hasPermission(plugin.getPermissionName() + "suicid")) {
                if (sender instanceof Player) {
                    ((Player) sender).setHealth(0);
                    ((Player) sender).getWorld().getPlayers().forEach(players -> {
                        players.sendMessage("§6" + sender.getName() + " §ahat Suicid begangen!");
                    });
                } else {
                    sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + plugin.getNOPERMS());
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
