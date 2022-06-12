package de.framedev.essentialsmini.commands.playercommands;


/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 13.08.2020 20:07
 */

import de.framedev.essentialsmini.main.Main;
import de.framedev.essentialsmini.managers.CommandBase;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorkbenchCMD extends CommandBase {

    private final Main plugin;

    public WorkbenchCMD(Main plugin) {
        super(plugin, "workbench");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 0) {
                if (sender.hasPermission(plugin.getPermissionName() + "workbench")) {
                    ((Player) sender).openWorkbench(((Player) sender).getLocation(), true);
                    sender.sendMessage(getPrefix() + "§aWorkbench opend");
                } else {
                    sender.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + plugin.getWrongArgs("/workbench"));
            }
        } else {
            sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
        }
        return false;
    }
}
