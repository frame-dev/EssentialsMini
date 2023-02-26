package de.framedev.essentialsmini.commands.playercommands;

import de.framedev.essentialsmini.main.Main;
import de.framedev.essentialsmini.utils.KeyGenerator;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GenerateKeyCMD implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public GenerateKeyCMD(Main plugin) {
        this.plugin = plugin;
        plugin.getCommands().put("key", this);
        plugin.getTabCompleters().put("key", this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            String collection = "essentialsmini_data";
            if (args[0].equalsIgnoreCase("generate")) {
                if (sender.hasPermission(plugin.getPermissionName() + "key")) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                    String key = new KeyGenerator().generateKeyAndSave(player);
                    if (Bukkit.getPluginManager().getPlugin("MDBConnection") != null) {
                        if (plugin.getConfig().getBoolean("MongoDB.Boolean") || plugin.getConfig().getBoolean("MongoDB.LocalHost")) {
                            plugin.getBackendManager().updateUser(player, "key", key, collection);
                            sender.sendMessage(plugin.getPrefix() + "§6" + player.getName() + " §ahat nun einen Key!");
                        }
                    } else {
                        sender.sendMessage(plugin.getPrefix() + "§6" + player.getName() + " §ahat nun einen Key!");
                    }
                } else {
                    sender.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                }
            }
            if (args[0].equalsIgnoreCase("remove")) {
                if (sender.hasPermission(plugin.getPermissionName() + "key")) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                    if (Bukkit.getPluginManager().getPlugin("MDBConnection") != null) {
                        if (plugin.getConfig().getBoolean("MongoDB.Boolean") || plugin.getConfig().getBoolean("MongoDB.LocalHost")) {
                            if (plugin.getBackendManager().exists(player, "key", collection)) {
                                new KeyGenerator().removeBetaKey(player);
                                plugin.getBackendManager().updateUser(player, "key", null, collection);
                                sender.sendMessage(plugin.getPrefix() + "§cKey von §6" + player.getName() + " §cwurde entfernt!");
                            }
                        }
                    } else {
                        new KeyGenerator().removeBetaKey(player);
                        sender.sendMessage(plugin.getPrefix() + "§cKey von §6" + player.getName() + " §cwurde entfernt!");
                    }
                } else {
                    sender.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                }
            }
            if (args[0].equalsIgnoreCase("haskey")) {
                if (sender.hasPermission(plugin.getPermissionName() + "key")) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                    if (Bukkit.getPluginManager().getPlugin("MDBConnection") != null) {
                        if (plugin.getConfig().getBoolean("MongoDB.Boolean") || plugin.getConfig().getBoolean("MongoDB.LocalHost")) {
                            if (plugin.getBackendManager().exists(player, "key", collection)) {
                                sender.sendMessage(plugin.getPrefix() + "§6" + player.getName() + " §ahat einen Key!");
                            } else {
                                sender.sendMessage(plugin.getPrefix() + "§6" + player.getName() + " §chat keinen Key!");
                            }
                        } else {
                            sender.sendMessage(plugin.getPrefix() + "§6" + player.getName() + " §chat keinen Key!");
                        }
                    } else {
                        if (new KeyGenerator().hasPlayerKey(player)) {
                            sender.sendMessage(plugin.getPrefix() + "§6" + player.getName() + " §ahat einen Key!");
                        }
                    }
                } else {
                    sender.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("key")) {
            if (args.length == 1) {
                List<String> commands = new ArrayList<>();
                commands.add("generate");
                commands.add("remove");
                commands.add("haskey");
                ArrayList<String> empty = new ArrayList<>();
                for (String s : commands) {
                    if (s.toLowerCase().startsWith(args[0])) {
                        empty.add(s);
                    }
                }
                Collections.sort(empty);
                return empty;
            }
        }
        return null;
    }
}