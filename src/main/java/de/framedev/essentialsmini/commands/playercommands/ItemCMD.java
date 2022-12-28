package de.framedev.essentialsmini.commands.playercommands;


/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 11.08.2020 23:04
 */

import de.framedev.essentialsmini.main.Main;
import de.framedev.essentialsmini.abstracts.CommandBase;
import de.framedev.essentialsmini.managers.MaterialManager;
import de.framedev.essentialsmini.utils.AdminBroadCast;
import de.framedev.essentialsmini.utils.ReplaceCharConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemCMD extends CommandBase {

    private final Main plugin;

    public ItemCMD(Main plugin) {
        super(plugin, "item");
        setupTabCompleter(this);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission(new Permission("essentialsmini.item", PermissionDefault.OP))) {
            if (args.length == 1) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.hasPermission(new Permission("essentialsmini.item", PermissionDefault.OP))) {
                        String name = args[0];
                        if (new MaterialManager().existsMaterial(Material.getMaterial(name.toUpperCase()))) {
                            player.getInventory().addItem(new ItemStack(new MaterialManager().getMaterial(name.toUpperCase())));
                            String message = plugin.getLanguageConfig(player).getString("Item.Get");
                            message = ReplaceCharConfig.replaceParagraph(message);
                            message = ReplaceCharConfig.replaceObjectWithData(message, "%Item%", name);
                            message = ReplaceCharConfig.replaceObjectWithData(message, "%Amount%", "" + 1);
                            player.sendMessage(plugin.getPrefix() + message);
                        } else {
                            player.sendMessage(plugin.getPrefix() + "§cDieses Item existiert nicht! §6" + name);
                        }
                    } else {
                        player.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                        new AdminBroadCast("item","§cNo Permissions!", sender);
                    }
                } else {
                    sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
                }
            } else if (args.length == 2) {
                try {
                    if (args[1].equalsIgnoreCase(String.valueOf(Integer.parseInt(args[1])))) {
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            if (player.hasPermission(new Permission("essentialsmini.item", PermissionDefault.OP))) {
                                String name = args[0];
                                if (new MaterialManager().existsMaterial(Material.getMaterial(name.toUpperCase()))) {
                                    int amount = Integer.parseInt(args[1]);
                                    player.getInventory().addItem(new ItemStack(new MaterialManager().getMaterial(name.toUpperCase()), amount));
                                    if (!Main.getSilent().contains(sender.getName())) {
                                        String message = plugin.getLanguageConfig(player).getString("Item.Get");
                                        message = ReplaceCharConfig.replaceParagraph(message);
                                        message = ReplaceCharConfig.replaceObjectWithData(message, "%Item%", name);
                                        message = ReplaceCharConfig.replaceObjectWithData(message, "%Amount%", "" + amount);
                                        player.sendMessage(plugin.getPrefix() + message);
                                    }
                                } else {
                                    player.sendMessage(plugin.getPrefix() + "§cDieses Item existiert nicht! §6" + name);
                                }
                            } else {
                                player.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                                new AdminBroadCast("item","§cNo Permissions!", sender);
                            }
                        } else {
                            sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
                        }
                    }
                } catch (NumberFormatException ignored) {
                    if (sender.hasPermission(new Permission("essentialsmini.item", PermissionDefault.OP))) {
                        String name = args[0];
                        if (new MaterialManager().existsMaterial(Material.getMaterial(name.toUpperCase()))) {
                            Player player1 = Bukkit.getPlayer(args[1]);
                            if (player1 != null) {
                                player1.getInventory().addItem(new ItemStack(new MaterialManager().getMaterial(name.toUpperCase())));
                                String message = plugin.getLanguageConfig(sender).getString("Item.Other");
                                message = ReplaceCharConfig.replaceParagraph(message);
                                message = ReplaceCharConfig.replaceObjectWithData(message, "%Item%", name);
                                message = ReplaceCharConfig.replaceObjectWithData(message, "%Player%", player1.getName());
                                message = ReplaceCharConfig.replaceObjectWithData(message, "%Amount%", "" + 1);
                                sender.sendMessage(plugin.getPrefix() + message);
                            } else {
                                sender.sendMessage(plugin.getPrefix() + plugin.getVariables().getPlayerNameNotOnline(args[1]));
                            }
                        } else {
                            sender.sendMessage(plugin.getPrefix() + "§cDieses Item existiert nicht! §6" + name);
                        }
                    } else {
                        sender.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                        new AdminBroadCast("item","§cNo Permissions!", sender);
                    }
                }
            } else if (args.length == 3) {
                if (sender.hasPermission(new Permission("essentialsmini.item", PermissionDefault.OP))) {
                    String name = args[0];
                    if (new MaterialManager().existsMaterial(Material.getMaterial(name.toUpperCase()))) {
                        int amount = Integer.parseInt(args[1]);
                        Player player1 = Bukkit.getPlayer(args[2]);
                        if (player1 != null) {
                            player1.getInventory().addItem(new ItemStack(new MaterialManager().getMaterial(name.toUpperCase()), amount));
                            String message = plugin.getLanguageConfig(sender).getString("Item.Other");
                            message = ReplaceCharConfig.replaceParagraph(message);
                            message = ReplaceCharConfig.replaceObjectWithData(message, "%Item%", name);
                            message = ReplaceCharConfig.replaceObjectWithData(message, "%Player%", player1.getName());
                            message = ReplaceCharConfig.replaceObjectWithData(message, "%Amount%", "" + amount);
                            sender.sendMessage(plugin.getPrefix() + message);
                        } else {
                            sender.sendMessage(plugin.getPrefix() + plugin.getVariables().getPlayerNameNotOnline(args[2]));
                        }
                    } else {
                        sender.sendMessage(plugin.getPrefix() + "§cDieses Item existiert nicht! §6" + name);
                    }
                } else {
                    sender.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                    new AdminBroadCast("item","§cNo Permissions!", sender);
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + plugin.getWrongArgs("/item <Item>"));
                sender.sendMessage(plugin.getPrefix() + plugin.getWrongArgs("/item <Item> <SpielerName>"));
                sender.sendMessage(plugin.getPrefix() + plugin.getWrongArgs("/item <Item> <Anzahl>"));
                sender.sendMessage(plugin.getPrefix() + plugin.getWrongArgs("/item <Item> <Anzahl> <SpielerName>"));
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            if (sender.hasPermission("essentialsmini.item")) {
                ArrayList<String> empty = new ArrayList<>();
                ArrayList<Material> materials = new MaterialManager().getMaterials();
                for (Material material : materials) {
                    if (material.name().toLowerCase().startsWith(args[0].toLowerCase())) {
                        empty.add(material.name());
                    }
                }
                Collections.sort(empty);
                return empty;
            }
        }
        return null;
    }
}
