package ch.framedev.essentialsmini.commands.playercommands;


/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 16.08.2020 21:04
 */

import ch.framedev.essentialsmini.managers.LocationsManager;
import ch.framedev.essentialsmini.main.Main;
import ch.framedev.essentialsmini.utils.AdminBroadCast;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShowLocationCMD implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public ShowLocationCMD(Main plugin) {
        this.plugin = plugin;
        plugin.getCommands().put("showlocation", this);
        plugin.getCommands().put("position", this);
        plugin.getTabCompleters().put("position", this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("showlocation")) {
            if (sender instanceof Player) {
                if (sender.hasPermission(plugin.getPermissionBase() + "showlocation")) {
                    if (args.length == 0) {
                        Player player = (Player) sender;
                        Location location = player.getLocation();
                        Bukkit.getOnlinePlayers().forEach(players -> {
                            players.sendMessage(plugin.getPrefix() + "§6" + player.getName() + " §ahat eine Location mitgeteilt!");
                            players.sendMessage("§aWelt = §6" + location.getWorld().getName());
                            players.sendMessage("§aX = §6" + location.getX());
                            players.sendMessage("§aY = §6" + location.getY());
                            players.sendMessage("§aZ = §6" + location.getZ());
                        });
                    } else if (args.length > 1) {
                        Player player = (Player) sender;
                        Location location = player.getLocation();
                        StringBuilder message = new StringBuilder();
                        for (String arg : args) {
                            message.append(arg).append(" ");
                        }
                        message = new StringBuilder(message.toString().replace('&', '§'));
                        String finalMessage = message.toString();
                        Bukkit.getOnlinePlayers().forEach(players -> {
                            players.sendMessage(plugin.getPrefix() + "§6" + player.getName() + " §ahat eine Location mitgeteilt! §aName der Location §6" + finalMessage);
                            players.sendMessage("§aWelt = §6" + location.getWorld().getName());
                            players.sendMessage("§aX = §6" + location.getX());
                            players.sendMessage("§aY = §6" + location.getY());
                            players.sendMessage("§aZ = §6" + location.getZ());
                        });
                    } else {
                        sender.sendMessage(plugin.getPrefix() + plugin.getWrongArgs("/showlocation §coder §6/showlocation <Name>"));
                    }
                } else {
                    sender.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
            }
        } else if (command.getName().equalsIgnoreCase("position")) {
            if (sender.hasPermission(plugin.getPermissionBase() + "position")) {
                if (args.length == 1) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (!new LocationsManager().getCfg().contains("position." + args[0]) || new LocationsManager().getCfg().get("position." + args[0]).equals(" ")) {
                            if (!new LocationsManager().getCfg().contains("position." + args[0]) || new LocationsManager().getCfg().get("position." + args[0]).equals(" ")) {
                                new LocationsManager("position." + args[0]).setLocation(player.getLocation());
                                Bukkit.getOnlinePlayers().forEach(players -> {
                                    players.sendMessage(plugin.getPrefix() + "§6" + player.getName() + " §ahat eine Location gespeichert! §c: " + args[0] + "\n §6X§b[" + player.getLocation().getBlockX() + "] §6Y§b["
                                            + player.getLocation().getBlockY() + "] §6Z§b[" + player.getLocation().getBlockZ() + "]");
                                    int x = player.getLocation().getBlockX();
                                    int y = player.getLocation().getBlockY();
                                    int z = player.getLocation().getBlockZ();
                                    TextComponent textComponent = new TextComponent("Click here to teleport to location");
                                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + x + " " + y + " " + z));
                                    players.spigot().sendMessage(textComponent);
                                });
                            }
                        } else {
                            if (!new LocationsManager().getCfg().contains("position." + args[0]) || !new LocationsManager().getCfg().get("position." + args[0]).equals(" ")) {
                                player.sendMessage(plugin.getPrefix() + "§aLocation : §6X§b[" + new LocationsManager("position." + args[0]).getLocation().getBlockX() + "] §6Y§b["
                                        + new LocationsManager("position." + args[0]).getLocation().getBlockY() + "] §6Z§b["
                                        + new LocationsManager("position." + args[0]).getLocation().getBlockZ() + "]");
                            } else {
                                player.sendMessage(plugin.getPrefix() + "§cKeine Location gefunden!");
                            }
                        }
                    } else {
                        sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
                    }
                } else if (args.length == 2) {
                    if (args[1].equalsIgnoreCase("none")) {
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            if (!new LocationsManager().getCfg().contains("position." + args[0]) || new LocationsManager().getCfg().get("position." + args[0]).equals(" ")) {
                                if (new LocationsManager().getCfg().getString("position." + args[0]).equalsIgnoreCase(" ")) {
                                    new LocationsManager("position." + args[0]).setLocation(player.getLocation());
                                    player.sendMessage(plugin.getPrefix() + "§6" + player.getName() + " §ahat eine Location gespeichert! §c: " + args[0] + "\n §6X§b[" + player.getLocation().getBlockX() + "] §6Y§b["
                                            + player.getLocation().getBlockY() + "] §6Z§b[" + player.getLocation().getBlockZ() + "]");
                                }
                            } else {
                                if (!new LocationsManager().getCfg().contains("position." + args[0]) || !new LocationsManager().getCfg().get("position." + args[0]).equals(" ")) {
                                    player.sendMessage(plugin.getPrefix() + "§aLocation : §6X§b[" + new LocationsManager("position." + args[0]).getLocation().getBlockX() + "] §6Y§b["
                                            + new LocationsManager("position." + args[0]).getLocation().getBlockY() + "] §6Z§b["
                                            + new LocationsManager("position." + args[0]).getLocation().getBlockZ() + "]");
                                } else {
                                    player.sendMessage(plugin.getPrefix() + "§cKeine Location gefunden!");
                                }
                            }
                        } else {
                            sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
                        }
                    }
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                new AdminBroadCast("position","§cNo Permissions!", sender);
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("position")) {
            if (sender.hasPermission(plugin.getPermissionBase() + "position")) {
                if (args.length == 1) {
                    ArrayList<String> empty = new ArrayList<>();
                    ConfigurationSection cs = new LocationsManager().getCfg().getConfigurationSection("position");
                    if (cs != null) {
                        for (String s : cs.getKeys(false)) {
                            if (s.toLowerCase().startsWith(args[0])) {
                                if (new LocationsManager().getCfg().get("position." + s) != null) {
                                    if (!new LocationsManager().getCfg().get("position." + s).equals(" ")) {
                                        empty.add(s);
                                    }
                                }
                            }
                        }
                        Collections.sort(empty);
                        return empty;
                    }
                }
            }
        }
        return null;
    }
}
