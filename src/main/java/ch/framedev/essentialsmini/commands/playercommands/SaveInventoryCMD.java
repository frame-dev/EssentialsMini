package ch.framedev.essentialsmini.commands.playercommands;

import ch.framedev.essentialsmini.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 26.07.2020 00:54
 */
public class SaveInventoryCMD implements CommandExecutor, Listener {

    private final Main plugin;
    static File file;
    static FileConfiguration cfg;
    public static HashMap<String, ItemStack[]> itemsStringHashMap  = new HashMap<>();

    public SaveInventoryCMD(Main plugin) {
        this.plugin = plugin;
        if(plugin.getConfig().getBoolean("SaveInventory")) {
            plugin.getCommands().put("saveinventory",this);
            plugin.getListeners().add(this);
        }
        file = new File(Main.getInstance().getDataFolder(),"inventorys.yml");
        cfg = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(player.hasPermission("essentialsmini.saveinventory")) {
                if(plugin.getConfig().getBoolean("SaveInventory")) {
                    Inventory inventory = Bukkit.createInventory(null, 54, player.getName() + "'s Inventory");
                    if (itemsStringHashMap.containsKey(player.getUniqueId().toString())) {
                        inventory.setContents(itemsStringHashMap.get(player.getUniqueId().toString()));
                        player.openInventory(inventory);
                    } else {
                        player.openInventory(inventory);
                    }
                }
            }
        } else {
            sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
        }
        return false;
    }

    @EventHandler
    public void onCloseGui(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if(event.getView().getTitle().equalsIgnoreCase(player.getName() + "'s Inventory")) {
            itemsStringHashMap.put(player.getUniqueId().toString(), event.getInventory().getContents());
        }
    }

    public static void restore() {
        if(cfg.contains("Inventory")) {
            cfg.getConfigurationSection("Inventory").getKeys(false).forEach(key -> {
                ItemStack[] content = ((List<ItemStack>) cfg.get("Inventory." + key)).toArray(new ItemStack[0]);
                itemsStringHashMap.put(key, content);
            });
            cfg.set("Inventory",null);
            try {
                cfg.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void save() {
        if(!itemsStringHashMap.isEmpty()) {
            for(Map.Entry<String,ItemStack[]> entry : itemsStringHashMap.entrySet()) {
                cfg.set("Inventory." + entry.getKey(),entry.getValue());
            }
            try {
                cfg.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
