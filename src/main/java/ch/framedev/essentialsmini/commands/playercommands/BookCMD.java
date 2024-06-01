package ch.framedev.essentialsmini.commands.playercommands;

import ch.framedev.essentialsmini.main.Main;
import ch.framedev.essentialsmini.abstracts.CommandBase;
import ch.framedev.essentialsmini.utils.AdminBroadCast;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;

/**
 * This Plugin was Created by FrameDev
 * Package : de.framedev.essentialsmini.commands.playercommands
 * ClassName BookCMD
 * Date: 15.05.21
 * Project: EssentialsMini
 * Copyrighted by FrameDev
 */

public class BookCMD extends CommandBase {

    public BookCMD(Main plugin) {
        super(plugin, "copybook", "book");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(getPlugin().getPrefix() + getPlugin().getOnlyPlayer());
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("essentialsmini.book")) {
            player.sendMessage(getPlugin().getPrefix() + getPlugin().getNoPerms());
            new AdminBroadCast("copybook/book", "§cNo Permissions!", sender);
            return true;
        }

        if (command.getName().equalsIgnoreCase("book")) {
            if (player.getInventory().getItemInMainHand().getType() == Material.WRITTEN_BOOK) {
                ItemStack itemStack = player.getInventory().getItemInMainHand();
                BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
                List<String> pages = bookMeta.getPages();
                ItemStack item = new ItemStack(Material.WRITABLE_BOOK);
                BookMeta meta = (BookMeta) item.getItemMeta();
                if (meta != null) {
                    meta.setPages(pages);
                    item.setItemMeta(meta);
                }
                player.getInventory().remove(itemStack);
                player.getInventory().setItemInMainHand(item);
                player.sendMessage(getPlugin().getPrefix() + "§aYou can now Open the Book");
            } else {
                player.sendMessage(getPlugin().getPrefix() + "§cYou don't have an Written Book in your Hand!");
            }
        } else if (command.getName().equalsIgnoreCase("copybook")) {
            if (player.getInventory().getItemInMainHand().getType() == Material.WRITTEN_BOOK) {
                ItemStack itemStack = player.getInventory().getItemInMainHand();
                ItemStack itemStackCopy = itemStack.clone();
                BookMeta bookMeta = (BookMeta) itemStackCopy.getItemMeta();
                if (bookMeta != null)
                    bookMeta.setGeneration(BookMeta.Generation.COPY_OF_ORIGINAL);
                itemStackCopy.setItemMeta(bookMeta);
                if (args.length == 0) {
                    itemStackCopy.setAmount(1);
                } else if (args.length == 1) {
                    try {
                        itemStackCopy.setAmount(Integer.parseInt(args[0]));
                    } catch (Exception ignored) {
                        player.sendMessage(getPlugin().getPrefix() + "§6" + args[0] + "§cisn't a Number!");
                        itemStackCopy.setAmount(1);
                    }
                }
                player.getInventory().addItem(itemStackCopy);
                player.sendMessage(getPlugin().getPrefix() + "§aYou have successfully Copyed the Book!");
            } else {
                player.sendMessage(getPlugin().getPrefix() + "§cYou don't have an Written Book in your Hand!");
            }
        }
        return super.onCommand(sender, command, label, args);
    }
}
