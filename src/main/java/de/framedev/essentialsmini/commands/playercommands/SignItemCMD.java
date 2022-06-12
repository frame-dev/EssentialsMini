package de.framedev.essentialsmini.commands.playercommands;


/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 16.08.2020 20:31
 */

import de.framedev.essentialsmini.main.Main;
import de.framedev.essentialsmini.managers.CommandBase;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Material.AIR;

public class SignItemCMD extends CommandBase {

    private final Main plugin;

    File file;
    FileConfiguration cfg;

    public SignItemCMD(Main plugin) {
        super(plugin, "signitem");
        this.plugin = plugin;
        file = new File(plugin.getDataFolder(), "signditems.yml");
        cfg = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission(plugin.getPermissionName() + "signitem")) {
                if (((Player) sender).getInventory().getItemInMainHand().getType() != AIR) {
                    ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
                    ItemMeta meta = item.getItemMeta();
                    List<String> lore;
                    if (meta.getLore() == null) {
                        lore = new ArrayList<>();
                        String message = "";
                        for (int i = 0; i < args.length; i++) {
                            message = message + args[i] + " ";
                        }
                        lore.add("§6Signed by §d" + sender.getName());
                        lore.add(ChatColor.translateAlternateColorCodes('&', message));
                    } else {
                        lore = meta.getLore();
                        String message = "";
                        for (int i = 1; i < args.length; i++) {
                            message = message + args[i] + " ";
                        }
                        lore.set(0, "§6Signed by §d" + sender.getName());
                        lore.set(1, ChatColor.translateAlternateColorCodes('&', message));

                    }
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    sender.sendMessage(plugin.getPrefix() + "§aDas Item wurde personalisiert!");
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
            }
        } else {
            sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
        }
        return false;
    }
}
