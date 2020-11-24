package de.framedev.essentialsmin.commands;


/*
 * de.framedev.essentialsmin.commands
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 22.09.2020 11:38
 */

import de.framedev.essentialsmin.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHealthCMD implements CommandExecutor {

    private final Main plugin;

    public SetHealthCMD(Main plugin) {
        this.plugin = plugin;
        plugin.getCommands().put("sethealth", this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            double health = Double.parseDouble(args[0]);
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission(plugin.getPermissionName() + "sethealth")) {
                    player.setHealthScale(health);
                    player.sendMessage(plugin.getPrefix() + "§aDeine Herzen wurden auf §6" + health + " §agesetzt!");
                } else {
                    player.sendMessage(plugin.getPrefix() + plugin.getNOPERMS());
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
            }
        } else if (args.length == 2) {
            double health = Double.parseDouble(args[0]);
            Player player = Bukkit.getPlayer(args[1]);
            if (player != null) {
                if (sender.hasPermission(plugin.getPermissionName() + "sethealth.others")) {
                    player.setHealthScale(health);
                    player.sendMessage(plugin.getPrefix() + "§aDeine Herzen wurden auf §6" + health + " §agesetzt!");
                    sender.sendMessage(plugin.getPrefix() + "§aDie Herzen von §6" + player.getName() + " §awurden auf §6" + health + " §agesetzt!");
                } else {
                    sender.sendMessage(plugin.getPrefix() + plugin.getNOPERMS());
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + "§cDieser Spieler existiert nicht! §6" + args[1]);
            }
        }
        return false;
    }
}
