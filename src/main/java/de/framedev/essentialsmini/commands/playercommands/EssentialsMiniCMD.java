package de.framedev.essentialsmini.commands.playercommands;

import de.framedev.essentialsmini.main.Main;
import de.framedev.essentialsmini.managers.CommandBase;
import de.framedev.essentialsmini.utils.AdminBroadCast;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This Plugin was Created by FrameDev
 * Package : de.framedev.essentialsmin.commands.playercommands
 * Date: 05.02.21
 * Project: EssentialsMini
 * Copyrighted by FrameDev
 */

public class EssentialsMiniCMD extends CommandBase {

    private final Main plugin;

    public EssentialsMiniCMD(Main plugin) {
        super(plugin, "essentialsmini");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("essentialsmini")) {
            if (sender.hasPermission("essentialsmini.utils")) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("reload")) {
                        plugin.saveConfig();
                        plugin.reloadConfig();
                        sender.sendMessage(plugin.getPrefix() + "§cIf something doesn't work. Please Reload the Server!");
                    }
                    if (args[0].equalsIgnoreCase("info")) {
                        boolean jsonFormat = plugin.getConfig().getBoolean("JsonFormat");
                        boolean economyEnabled = plugin.getConfig().getBoolean("Economy.Activate");
                        sender.sendMessage(plugin.getPrefix() + "=================");
                        sender.sendMessage("§ais JsonFormat Enabled §6: " + jsonFormat);
                        sender.sendMessage("§ais Economy Enabled §6: " + economyEnabled);
                    }
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("spawntp")) {
                        boolean isSet = Boolean.parseBoolean(args[1]);
                        plugin.getConfig().set("SpawnTP", isSet);
                        plugin.saveConfig();
                        sender.sendMessage(plugin.getPrefix() + "§6SpawnTP §awurde auf §6" + isSet + " §agesetzt!");
                        sender.sendMessage(plugin.getPrefix() + "§6Wenn du fertig bis Reloade bitte einmal den Server!");
                    }
                    if (args[0].equalsIgnoreCase("backpack")) {
                        boolean isSet = Boolean.parseBoolean(args[1]);
                        plugin.getConfig().set("Backpack", isSet);
                        plugin.saveConfig();
                        sender.sendMessage(plugin.getPrefix() + "§6Backpack §awurde auf §6" + isSet + " §agesetzt!");
                        sender.sendMessage(plugin.getPrefix() + "§6Wenn du fertig bis Reloade bitte einmal den Server!");
                    }
                    if (args[0].equalsIgnoreCase("saveinventory")) {
                        boolean isSet = Boolean.parseBoolean(args[1]);
                        plugin.getConfig().set("SaveInventory", isSet);
                        plugin.saveConfig();
                        sender.sendMessage(plugin.getPrefix() + "§6SaveInventory §awurde auf §6" + isSet + " §agesetzt!");
                        sender.sendMessage(plugin.getPrefix() + "§6Wenn du fertig bis Reloade bitte einmal den Server!");
                    }
                    if (args[0].equalsIgnoreCase("back")) {
                        boolean isSet = Boolean.parseBoolean(args[1]);
                        plugin.getConfig().set("Back", isSet);
                        plugin.saveConfig();
                        sender.sendMessage(plugin.getPrefix() + "§6Back §awurde auf §6" + isSet + " §agesetzt!");
                        sender.sendMessage(plugin.getPrefix() + "§6Wenn du fertig bis Reloade bitte einmal den Server!");
                    }
                    if (args[0].equalsIgnoreCase("skipnight")) {
                        boolean isSet = Boolean.parseBoolean(args[1]);
                        plugin.getConfig().set("SkipNight", isSet);
                        plugin.saveConfig();
                        sender.sendMessage(plugin.getPrefix() + "§6SkipNight §awurde auf §6" + isSet + " §agesetzt!");
                        sender.sendMessage(plugin.getPrefix() + "§6Wenn du fertig bis Reloade bitte einmal den Server!");
                    }
                    if (args[0].equalsIgnoreCase("showlocation")) {
                        boolean isSet = Boolean.parseBoolean(args[1]);
                        plugin.getConfig().set("ShowLocation", isSet);
                        plugin.saveConfig();
                        sender.sendMessage(plugin.getPrefix() + "§6ShowLocation §awurde auf §6" + isSet + " §agesetzt!");
                        sender.sendMessage(plugin.getPrefix() + "§6Wenn du fertig bis Reloade bitte einmal den Server!");
                    }
                    if (args[0].equalsIgnoreCase("showcrafting")) {
                        boolean isSet = Boolean.parseBoolean(args[1]);
                        plugin.getConfig().set("ShowCrafting", isSet);
                        plugin.saveConfig();
                        sender.sendMessage(plugin.getPrefix() + "§6ShowCrafting §awurde auf §6" + isSet + " §agesetzt!");
                        sender.sendMessage(plugin.getPrefix() + "§6Wenn du fertig bis Reloade bitte einmal den Server!");
                    }
                    if (args[0].equalsIgnoreCase("showitem")) {
                        boolean isSet = Boolean.parseBoolean(args[1]);
                        plugin.getConfig().set("ShowItem", isSet);
                        plugin.saveConfig();
                        sender.sendMessage(plugin.getPrefix() + "§6ShowItem §awurde auf §6" + isSet + " §agesetzt!");
                        sender.sendMessage(plugin.getPrefix() + "§6Wenn du fertig bis Reloade bitte einmal den Server!");
                    }
                    if (args[0].equalsIgnoreCase("position")) {
                        boolean isSet = Boolean.parseBoolean(args[1]);
                        plugin.getConfig().set("Position", isSet);
                        plugin.saveConfig();
                        sender.sendMessage(plugin.getPrefix() + "§6Position §awurde auf §6" + isSet + " §agesetzt!");
                        sender.sendMessage(plugin.getPrefix() + "§6Wenn du fertig bis Reloade bitte einmal den Server!");
                    }
                    if (args[0].equalsIgnoreCase("jsonformat")) {
                        boolean isSet = Boolean.parseBoolean(args[1]);
                        plugin.getConfig().set("JsonFormat", isSet);
                        plugin.saveConfig();
                        sender.sendMessage(plugin.getPrefix() + "§6JsonFormat §awurde auf §6" + isSet + " §agesetzt!");
                        sender.sendMessage(plugin.getPrefix() + "§6Wenn du fertig bis Reloade bitte einmal den Server!");
                    }
                    if (args[0].equalsIgnoreCase("backupmessages")) {
                        boolean isSet = Boolean.parseBoolean(args[1]);
                        plugin.getConfig().set("BackupMessages", isSet);
                        plugin.saveConfig();
                        sender.sendMessage(plugin.getPrefix() + "§6BackupMessages §awurde auf §6" + isSet + " §agesetzt!");
                        sender.sendMessage(plugin.getPrefix() + "§6Wenn du fertig bis Reloade bitte einmal den Server!");
                    }
                    if (args[0].equalsIgnoreCase("autorestart")) {
                        boolean isSet = Boolean.parseBoolean(args[1]);
                        plugin.getConfig().set("ZeitGesteuerterRestartBoolean", isSet);
                        plugin.saveConfig();
                        sender.sendMessage(plugin.getPrefix() + "§6Auto Restart §awurde auf §6" + isSet + " §agesetzt!");
                        sender.sendMessage(plugin.getPrefix() + "§6Wenn du fertig bis Reloade bitte einmal den Server!");
                    }
                    if (args[0].equalsIgnoreCase("worldbackup")) {
                        boolean isSet = Boolean.parseBoolean(args[1]);
                        plugin.getConfig().set("WorldBackup", isSet);
                        plugin.saveConfig();
                        sender.sendMessage(plugin.getPrefix() + "§6WorldBackup §awurde auf §6" + isSet + " §agesetzt!");
                        sender.sendMessage(plugin.getPrefix() + "§6Wenn du fertig bis Reloade bitte einmal den Server!");
                    }
                    if (args[0].equalsIgnoreCase("economy")) {
                        boolean isSet = Boolean.parseBoolean(args[1]);
                        plugin.getConfig().set("Economy.Activate", isSet);
                        plugin.saveConfig();
                        sender.sendMessage(plugin.getPrefix() + "§6Economy §awurde auf §6" + isSet + " §agesetzt!");
                        sender.sendMessage(plugin.getPrefix() + "§6Wenn du fertig bis Reloade bitte einmal den Server!");
                    }
                    if (args[0].equalsIgnoreCase("onlyessentials")) {
                        boolean isSet = Boolean.parseBoolean(args[1]);
                        plugin.getConfig().set("OnlyEssentialsFeatures", isSet);
                        plugin.saveConfig();
                        sender.sendMessage(plugin.getPrefix() + "§6OnlyEssentialsFeatures §awurde auf §6" + isSet + " §agesetzt!");
                        sender.sendMessage(plugin.getPrefix() + "§6Wenn du fertig bis Reloade bitte einmal den Server!");
                    }
                    if(args[0].equalsIgnoreCase("adminbroadcast")) {
                        for(AdminBroadCast broadCast : AdminBroadCast.getAdminBroadCasts()) {
                            sender.sendMessage(broadCast.toString());
                        }
                        sender.sendMessage("§aAll BroadCasts");
                    }
                }
            }
        }
        return super.onCommand(sender, command, label, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("essentialsmini")) {
            if (args.length == 1) {
                if (sender.hasPermission("essentialsmini.utils")) {
                    ArrayList<String> cmds = new ArrayList<>();
                    ArrayList<String> empty = new ArrayList<>();
                    cmds.add("backupmessages");
                    cmds.add("autorestart");
                    cmds.add("reload");
                    cmds.add("back");
                    cmds.add("backpack");
                    cmds.add("saveinventory");
                    cmds.add("skipnight");
                    cmds.add("spawntp");
                    cmds.add("showlocation");
                    cmds.add("showcrafting");
                    cmds.add("showitem");
                    cmds.add("position");
                    cmds.add("jsonformat");
                    cmds.add("worldbackup");
                    cmds.add("economy");
                    cmds.add("info");
                    cmds.add("onlyessentials");
                    for (String s : cmds) {
                        if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                            empty.add(s);
                        }
                    }
                    Collections.sort(empty);
                    return empty;
                }
            } else if (args.length == 2) {
                if (!args[0].equalsIgnoreCase("reload") && !args[0].equalsIgnoreCase("info")) {
                    ArrayList<String> cmds = new ArrayList<>();
                    ArrayList<String> empty = new ArrayList<>();
                    cmds.add("true");
                    cmds.add("false");
                    for (String s : cmds) {
                        if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                            empty.add(s);
                        }
                    }
                    Collections.sort(empty);
                    return empty;
                }
            }
        }
        return super.onTabComplete(sender, command, alias, args);
    }
}
