package ch.framedev.essentialsmini.commands.playercommands;


/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 16.08.2020 20:31
 */

import ch.framedev.essentialsmini.main.Main;
import ch.framedev.essentialsmini.abstracts.CommandBase;
import ch.framedev.essentialsmini.utils.AdminBroadCast;
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
            if (sender.hasPermission(plugin.getPermissionBase() + "signitem")) {
                if (((Player) sender).getInventory().getItemInMainHand().getType() != AIR) {
                    ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
                    ItemMeta meta = item.getItemMeta();
                    if(meta == null) {
                        sender.sendMessage(plugin.getPrefix() + "§cThis Item can't be signed!");
                        return true;
                    }
                    List<String> lore;
                    if (meta.getLore() == null) {
                        lore = new ArrayList<>();
                        StringBuilder message = new StringBuilder();
                        if(args.length == 0) {
                            sender.sendMessage(plugin.getPrefix() + plugin.getWrongArgs("/signitem <Message>"));
                            return true;
                        }
                        for (int i = 1; i < args.length; i++) {
                            message.append(args[i]).append(" ");
                        }
                        lore.add("§6Signed by §d" + sender.getName());
                        lore.add(ChatColor.translateAlternateColorCodes('&', message.toString()));
                    } else {
                        lore = meta.getLore();
                        StringBuilder message = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            message.append(args[i]).append(" ");
                        }
                        lore.set(0, "§6Signed by §d" + sender.getName());
                        lore.set(1, ChatColor.translateAlternateColorCodes('&', message.toString()));

                    }
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    sender.sendMessage(plugin.getPrefix() + "§aDas Item wurde personalisiert!");
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                new AdminBroadCast("signitem","§cNo Permissions!", sender);
            }
        } else {
            sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
        }
        return false;
    }
}
