package de.framedev.essentialsmini.commands.playercommands;

import de.framedev.essentialsmini.main.Main;
import de.framedev.essentialsmini.managers.CommandBase;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * This Plugin was Created by FrameDev
 * Package : de.framedev.essentialsmin.commands.playercommands
 * ClassName SilentCMD
 * Date: 26.03.21
 * Project: EssentialsMini
 * Copyrighted by FrameDev
 */

public class SilentCMD extends CommandBase {

    public SilentCMD(Main plugin) {
        super(plugin, "silent");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.hasPermission(getPlugin().getPermissionName() + "silent")) {
            if(!Main.getSilent().contains(sender.getName())) {
                Main.getSilent().add(sender.getName());
                sender.sendMessage(getPlugin().getPrefix() + "§aSilent wurde für dich Aktiviert!");
            } else {
                Main.getSilent().remove(sender.getName());
                sender.sendMessage(getPlugin().getPrefix() + "§cSilent wurde für dich Deaktiviert!");
            }
        } else {
           sender.sendMessage(getPlugin().getPrefix() + getPlugin().getNoPerms());
        }
        return super.onCommand(sender, command, label, args);
    }
}
