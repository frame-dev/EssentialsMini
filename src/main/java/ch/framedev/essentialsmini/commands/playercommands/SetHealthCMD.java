package ch.framedev.essentialsmini.commands.playercommands;


/*
 * de.framedev.essentialsmin.commands
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 22.09.2020 11:38
 */

import ch.framedev.essentialsmini.main.Main;
import ch.framedev.essentialsmini.abstracts.CommandBase;
import ch.framedev.essentialsmini.utils.AdminBroadCast;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHealthCMD extends CommandBase {

    private final Main plugin;

    public SetHealthCMD(Main plugin) {
        super(plugin, "sethealth");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            double health = Double.parseDouble(args[0]);
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission(plugin.getPermissionBase() + "sethealth")) {
                    player.setHealthScale(health);
                    player.sendMessage(plugin.getPrefix() + "§aDeine Herzen wurden auf §6" + health + " §agesetzt!");
                } else {
                    player.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                    new AdminBroadCast("sethealth","§cNo Permissions!", sender);
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
            }
        } else if (args.length == 2) {
            double health = Double.parseDouble(args[0]);
            Player player = Bukkit.getPlayer(args[1]);
            if (player != null) {
                if (sender.hasPermission(plugin.getPermissionBase() + "sethealth.others")) {
                    player.setHealthScale(health);
                    if (!Main.getSilent().contains(sender.getName()))
                        player.sendMessage(plugin.getPrefix() + "§aDeine Herzen wurden auf §6" + health + " §agesetzt!");
                    sender.sendMessage(plugin.getPrefix() + "§aDie Herzen von §6" + player.getName() + " §awurden auf §6" + health + " §agesetzt!");
                } else {
                    sender.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                    new AdminBroadCast("sethealth","§cNo Permissions!", sender);
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + plugin.getVariables().getPlayerNameNotOnline(args[1]));
            }
        }
        return false;
    }
}
