package ch.framedev.essentialsmini.commands.playercommands;


/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 16.08.2020 20:49
 */

import ch.framedev.essentialsmini.main.Main;
import ch.framedev.essentialsmini.abstracts.CommandBase;
import ch.framedev.essentialsmini.utils.AdminBroadCast;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import static org.bukkit.Material.AIR;

public class RenameItemCMD extends CommandBase {

    private final Main plugin;

    public RenameItemCMD(Main plugin) {
        super(plugin, "renameitem");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            if(sender.hasPermission(plugin.getPermissionName() + "renameitem")) {
                if(((Player) sender).getInventory().getItemInMainHand().getType() != AIR) {
                    if (((Player) sender).getInventory().getItemInMainHand().getItemMeta().hasDisplayName()) {
                        String oldDisplayName = ((Player) sender).getInventory().getItemInMainHand().getItemMeta().getDisplayName();
                        ItemMeta meta = ((Player) sender).getInventory().getItemInMainHand().getItemMeta();
                        StringBuilder display = new StringBuilder();
                        for (int i = 0; i < args.length; i++) {
                            display.append(args[i]).append(" ");
                        }
                        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', display.toString()));
                        ((Player) sender).getInventory().getItemInMainHand().setItemMeta(meta);
                        sender.sendMessage(plugin.getPrefix() + "§aDas Item wurde unbenannt von §6" + oldDisplayName + " §azu §6" + ChatColor.translateAlternateColorCodes('&', display.toString()));
                    } else {
                        String oldDisplayName = ((Player) sender).getInventory().getItemInMainHand().getType().name();
                        ItemMeta meta = ((Player) sender).getInventory().getItemInMainHand().getItemMeta();
                        StringBuilder display = new StringBuilder();
                        for (int i = 0; i < args.length; i++) {
                            display.append(args[i]).append(" ");
                        }
                        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', display.toString()));
                        ((Player) sender).getInventory().getItemInMainHand().setItemMeta(meta);
                        sender.sendMessage(plugin.getPrefix() + "§aDas Item wurde unbenannt von §6" + oldDisplayName + " §azu §6" + ChatColor.translateAlternateColorCodes('&', display.toString()));
                    }
                } else {
                    sender.sendMessage(plugin.getPrefix() + "§cAir kann nicht unbenannt werden!");
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                new AdminBroadCast("renameitem","§cNo Permissions!", sender);
            }
        } else {
            sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
        }
        return false;
    }
}
