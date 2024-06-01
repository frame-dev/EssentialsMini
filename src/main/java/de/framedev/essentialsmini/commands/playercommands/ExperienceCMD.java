package de.framedev.essentialsmini.commands.playercommands;

import ch.framedev.simplejavautils.TextUtils;
import de.framedev.essentialsmini.main.Main;
import de.framedev.essentialsmini.abstracts.CommandBase;
import de.framedev.essentialsmini.utils.AdminBroadCast;
import de.framedev.essentialsmini.utils.Variables;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * / This Plugin was Created by FrameDev
 * / Package : de.framedev.essentialsmini.commands.playercommands
 * / ClassName ExperienceCMD
 * / Date: 03.10.21
 * / Project: EssentialsMini
 * / Copyrighted by FrameDev
 */

public class ExperienceCMD extends CommandBase {

    private final TextUtils textUtils;

    public ExperienceCMD(Main plugin) {
        super(plugin, "xp");
        setupTabCompleter(this);
        this.textUtils = new TextUtils();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 4) {
            if (!sender.hasPermission(getPlugin().getPermissionName() + "xp")) {
                sender.sendMessage(getPlugin().getPrefix() + getPlugin().getNoPerms());
                new AdminBroadCast(this, "§cNo Permissions!", sender);
                return true;
            }
            if (args[0].equalsIgnoreCase("set")) {
                Object amount;
                if (args[1].contains(".")) {
                    amount = Float.parseFloat(args[1]);
                } else {
                    amount = Integer.parseInt(args[1]);
                }
                Player player = Bukkit.getPlayer(args[2]);
                if (player == null) {
                    sender.sendMessage(getPlugin().getPrefix() + getPlugin().getVariables().getPlayerNameNotOnline(args[2]));
                    return true;
                }
                String xpMessage = getPlugin().getCustomMessagesConfig().getString(Variables.EXPERIENCE + ".Self.XP");
                xpMessage = textUtils.replaceAndWithParagraph(xpMessage);
                xpMessage = textUtils.replaceObject(xpMessage, "%XP%", amount + "");
                String levelMessage = getPlugin().getCustomMessagesConfig().getString(Variables.EXPERIENCE + ".Self.Level");
                levelMessage = textUtils.replaceAndWithParagraph(levelMessage);
                levelMessage = textUtils.replaceObject(levelMessage, "%Level%", amount + "");
                if (args[3].equalsIgnoreCase("level")) {
                    assert amount instanceof Integer;
                    player.setLevel((Integer) amount);
                    player.sendMessage(getPlugin().getPrefix() + levelMessage);
                } else if (args[3].equalsIgnoreCase("xp")) {
                    assert amount instanceof Float;
                    player.setExp((Float) amount / 10);
                    player.sendMessage(getPlugin().getPrefix() + xpMessage);
                }
                return true;
            } else if (args[0].equalsIgnoreCase("add")) {
                int amount = Integer.parseInt(args[1]);
                Player player = Bukkit.getPlayer(args[2]);
                if (player == null) {
                    sender.sendMessage(getPlugin().getPrefix() + getPlugin().getVariables().getPlayerNameNotOnline(args[2]));
                    return true;
                }
                int level = player.getLevel();
                level += amount;
                String xpMessage = getPlugin().getCustomMessagesConfig().getString(Variables.EXPERIENCE + ".Self.XP");
                xpMessage = textUtils.replaceAndWithParagraph(xpMessage);
                xpMessage = textUtils.replaceObject(xpMessage, "%XP%", player.getTotalExperience() + amount + "");
                String levelMessage = getPlugin().getCustomMessagesConfig().getString(Variables.EXPERIENCE + ".Self.Level");
                levelMessage = textUtils.replaceAndWithParagraph(levelMessage);
                levelMessage = textUtils.replaceObject(levelMessage, "%Level%", level + "");
                if (args[3].equalsIgnoreCase("level")) {
                    player.setLevel(level);
                    player.sendMessage(getPlugin().getPrefix() + levelMessage);
                } else if (args[3].equalsIgnoreCase("xp")) {
                    player.giveExp(amount);
                    player.sendMessage(getPlugin().getPrefix() + xpMessage);
                }
                return true;
            } else if (args[0].equalsIgnoreCase("remove")) {
                int amount = Integer.parseInt(args[1]);
                Player player = Bukkit.getPlayer(args[2]);
                if (player == null) {
                    sender.sendMessage(getPlugin().getPrefix() + getPlugin().getVariables().getPlayerNameNotOnline(args[2]));
                    return true;
                }
                int level = player.getLevel();
                level -= amount;
                int xp = player.getTotalExperience();
                xp -= amount;
                String xpMessage = getPlugin().getCustomMessagesConfig().getString(Variables.EXPERIENCE + ".Self.XP");
                xpMessage = textUtils.replaceAndWithParagraph(xpMessage);
                xpMessage = textUtils.replaceObject(xpMessage, "%XP%", xp + "");
                String levelMessage = getPlugin().getCustomMessagesConfig().getString(Variables.EXPERIENCE + ".Self.Level");
                levelMessage = textUtils.replaceAndWithParagraph(levelMessage);
                levelMessage = textUtils.replaceObject(levelMessage, "%Level%", level + "");
                if (args[3].equalsIgnoreCase("level")) {
                    player.setLevel(level);
                    player.sendMessage(getPlugin().getPrefix() + levelMessage);
                }
                return true;
            }
        } else if (args.length == 5) {
            // xp send <Amount> <Player> <xp/level>
            if (args[0].equalsIgnoreCase("send")) {
                int amount = Integer.parseInt(args[1]);
                Player player = (Player) sender;
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    sender.sendMessage(getPlugin().getPrefix() + getPlugin().getVariables().getPlayerNameNotOnline(args[2]));
                    return true;
                }
                int level = target.getLevel();
                level += amount;
                int xp = target.getTotalExperience();
                xp += amount;
                String xpMessage = getPlugin().getCustomMessagesConfig().getString(Variables.EXPERIENCE + ".Self.XP");
                xpMessage = textUtils.replaceAndWithParagraph(xpMessage);
                xpMessage = textUtils.replaceObject(xpMessage, "%XP%", xp + "");
                String levelMessage = getPlugin().getCustomMessagesConfig().getString(Variables.EXPERIENCE + ".Self.Level");
                levelMessage = textUtils.replaceAndWithParagraph(levelMessage);
                levelMessage = textUtils.replaceObject(levelMessage, "%Level%", level + "");
                if (args[3].equalsIgnoreCase("level")) {
                    target.setLevel(level);
                    player.setLevel(player.getLevel() - amount);
                    target.sendMessage(getPlugin().getPrefix() + levelMessage);
                } else if (args[3].equalsIgnoreCase("xp")) {
                    target.setTotalExperience(xp);
                    player.setTotalExperience(player.getTotalExperience() - amount);
                    target.sendMessage(getPlugin().getPrefix() + xpMessage);
                }
            }
        }
        return super.onCommand(sender, command, label, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commands = new ArrayList<>();
        commands.add("set");
        commands.add("add");
        commands.add("remove");
        commands.add("send");
        if (args.length == 1) {
            List<String> empty = new ArrayList<>();
            for (String s : commands) {
                if (s.toLowerCase().startsWith(args[0].toLowerCase()))
                    empty.add(s);
            }
            Collections.sort(empty);
            return empty;
        } else if (args.length == 4) {
            List<String> cmds = new ArrayList<>();
            cmds.add("level");
            if (!args[0].equalsIgnoreCase("remove"))
                cmds.add("xp");
            List<String> empty = new ArrayList<>();
            for (String s : cmds) {
                if (s.toLowerCase().startsWith(args[3].toLowerCase()))
                    empty.add(s);
            }
            Collections.sort(empty);
            return empty;
        }
        return super.onTabComplete(sender, command, label, args);
    }
}
