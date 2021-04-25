package de.framedev.essentialsmin.commands.playercommands;


/*
 * de.framedev.essentialsmin.commands
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 20.09.2020 18:26
 */

import de.framedev.essentialsmin.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class GodCMD implements CommandExecutor {

    private final Main plugin;

    public GodCMD(Main plugin) {
        this.plugin = plugin;
        plugin.getCommands().put("godmode", this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
                return true;
            }
            Player player = (Player) sender;
            if (!player.hasPermission(new Permission(plugin.getPermissionName() + "god", PermissionDefault.OP))) {
                player.sendMessage(plugin.getPrefix() + plugin.getNOPERMS());
                return true;
            }
            if (player.isInvulnerable()) {
                player.setInvulnerable(false);
                String godSelfOff = plugin.getCustomMessagesConfig().getString("God.Self.Off");
                if(godSelfOff.contains("&"))
                    godSelfOff = godSelfOff.replace('&', '§');
                player.sendMessage(plugin.getPrefix() + godSelfOff);
            } else {
                player.setInvulnerable(true);
                String godSelfOn = plugin.getCustomMessagesConfig().getString("God.Self.On");
                if(godSelfOn.contains("&"))
                    godSelfOn = godSelfOn.replace('&', '§');
                player.sendMessage(plugin.getPrefix() + godSelfOn);
            }
            return true;
        } else if (args.length == 1) {
            Player player = Bukkit.getPlayer(args[0]);
            if (player != null) {
                if (!sender.hasPermission(new Permission(plugin.getPermissionName() + "god.others", PermissionDefault.OP))) {
                    sender.sendMessage(plugin.getPrefix() + plugin.getNOPERMS());
                    return true;
                }
                if (player.isInvulnerable()) {
                    player.setInvulnerable(false);
                    if (!Main.getSilent().contains(sender.getName())) {
                        String godSelfOff = plugin.getCustomMessagesConfig().getString("God.Self.Off");
                        if(godSelfOff.contains("&"))
                            godSelfOff = godSelfOff.replace('&', '§');
                        player.sendMessage(plugin.getPrefix() + godSelfOff);
                    }
                    String godOtherOff = plugin.getCustomMessagesConfig().getString("God.Other.Off");
                    if(godOtherOff.contains("%Player%"))
                        godOtherOff = godOtherOff.replace("%Player%", player.getName());
                    if(godOtherOff.contains("&"))
                        godOtherOff = godOtherOff.replace('&', '§');
                    sender.sendMessage(plugin.getPrefix() + godOtherOff);
                } else {
                    player.setInvulnerable(true);
                    if (!Main.getSilent().contains(sender.getName())) {
                        String godSelfOn = plugin.getCustomMessagesConfig().getString("God.Self.On");
                        if(godSelfOn.contains("&"))
                            godSelfOn = godSelfOn.replace('&', '§');
                        player.sendMessage(plugin.getPrefix() + godSelfOn);
                    }
                    String godOtherOff = plugin.getCustomMessagesConfig().getString("God.Other.On");
                    if(godOtherOff.contains("%Player%"))
                        godOtherOff = godOtherOff.replace("%Player%", player.getName());
                    if(godOtherOff.contains("&"))
                        godOtherOff = godOtherOff.replace('&', '§');
                    sender.sendMessage(plugin.getPrefix() + godOtherOff);
                }
                return true;
            } else {
                sender.sendMessage(plugin.getPrefix() + plugin.getVariables().getPlayerNameNotOnline(args[0]));
                return true;
            }
        } else {
            sender.sendMessage(plugin.getPrefix() + plugin.getWrongArgs("/god"));
            sender.sendMessage(plugin.getPrefix() + plugin.getWrongArgs("/god <SpielerName>"));
            return true;
        }
    }
}
