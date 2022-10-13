package de.framedev.essentialsmini.commands.worldcommands;

import de.framedev.essentialsmini.main.EssentialsMiniAPI;
import de.framedev.essentialsmini.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorldTPCMD implements CommandExecutor, Listener, TabCompleter {

    private final Main plugin;

    public WorldTPCMD(Main plugin) {
        this.plugin = plugin;
        plugin.getCommands().put("worldtp", this);
        plugin.getCommands().put("addworld", this);
        plugin.getTabCompleters().put("addworld", this);
        plugin.getListeners().add(this);
        worldWithKeys = getWorldWithKeys();
    }

    public static ArrayList<String> worldWithKeys;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission(new Permission(plugin.getVariables().getPermissionBase() + "worldutils", PermissionDefault.OP))) {
                if (command.getName().equalsIgnoreCase("worldtp")) {
                    if (args.length == 1) {
                        Player player = (Player) sender;
                        String worldName = args[0];
                        if (Bukkit.getWorld(worldName) != null) {
                            if (worldWithKeys.contains(worldName)) {
                                if (EssentialsMiniAPI.getInstance().hasPlayerKey(player)) {
                                    player.teleport(Bukkit.getWorld(worldName).getSpawnLocation());
                                    player.sendMessage(plugin.getPrefix() + "§aDu wurdest in die Welt §6" + worldName + " §aTeleportiert!");

                                } else {
                                    player.sendMessage(plugin.getPrefix() + "§cDu hast keinen Key damit du diese Welt betreten kannst!");
                                }
                            } else {
                                player.teleport(Bukkit.getWorld(worldName).getSpawnLocation());
                                player.sendMessage(plugin.getPrefix() + "§aDu wurdest in die Welt §6" + worldName + " §aTeleportiert!");
                            }
                        } else {
                            player.sendMessage(plugin.getPrefix() + "§cDiese Welt existiert nicht! §6" + worldName);
                        }
                    } else if (args.length == 2) {
                        Player player = (Player) sender;
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target != null) {
                            String worldName = args[0];
                            if (Bukkit.getWorld(worldName) != null) {
                                if (worldWithKeys.contains(worldName)) {
                                    if (EssentialsMiniAPI.getInstance().hasPlayerKey(target)) {
                                        target.teleport(Bukkit.getWorld(worldName).getSpawnLocation());
                                        player.sendMessage(plugin.getPrefix() + "§6" + target.getName() + " §awurde in die Welt §6" + worldName + " §aTeleportiert!");
                                    } else {
                                        player.sendMessage(plugin.getPrefix() + "§6" + target.getName() + " §chat keinen Key damit er diese Welt betreten kann!");
                                    }
                                } else {
                                    target.teleport(Bukkit.getWorld(worldName).getSpawnLocation());
                                    player.sendMessage(plugin.getPrefix() + "§6" + target.getName() + " §awurde in die Welt §6" + worldName + " §aTeleportiert!");
                                }
                            } else {
                                player.sendMessage(plugin.getPrefix() + "§cDiese Welt existiert nicht! §6" + worldName);
                            }
                        } else {
                            player.sendMessage(plugin.getPrefix() + plugin.getVariables().getPlayerNameNotOnline(args[0]));
                        }
                    } else {
                        sender.sendMessage(plugin.getPrefix() + plugin.getWrongArgs("§6/worldtp <WorldName> §coder §6/worldtp <WorldName> <PlayerName>"));
                    }
                }
                if (command.getName().equalsIgnoreCase("addworld")) {
                    if (args.length == 1) {
                        String worldName = args[0];
                        if (Bukkit.getWorld(worldName) != null) {
                            if (!worldWithKeys.contains(worldName)) {
                                worldWithKeys.add(worldName);
                                sender.sendMessage(plugin.getPrefix() + "§6" + worldName + " §awurde zu den Welten hinzugefügt die nur durch Keys betret bar sind! §6" + worldName);
                                saveWorlds();
                                worldWithKeys = getWorldWithKeys();
                            } else {
                                worldWithKeys.remove(worldName);
                                sender.sendMessage(plugin.getPrefix() + "§6" + worldName + " §awurde von den Welten entfernt §6Diese Welt kann nun wieder ohne Key betretet werden! §6" + worldName);
                                saveWorlds();
                            }
                        } else {
                            sender.sendMessage(plugin.getPrefix() + "§cDiese Welt existiert nicht! §6" + worldName);
                        }
                    } else {
                        sender.sendMessage(plugin.getPrefix() + plugin.getWrongArgs("§6/addworld <WorldName>"));
                    }
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
            }
        } else {
            sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
        }
        return false;
    }

    @EventHandler
    public void onTPWorld(PlayerChangedWorldEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (worldWithKeys.contains(event.getPlayer().getWorld().getName())) {
                    if (!EssentialsMiniAPI.getInstance().hasPlayerKey(event.getPlayer())) {
                        event.getPlayer().teleport(event.getFrom().getSpawnLocation());
                    }
                }
            }
        }.runTaskLater(plugin, 60);
    }

    public void saveWorlds() {
        File file = new File(plugin.getDataFolder(), "worlds.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        cfg.set("Worlds", worldWithKeys);
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getWorldWithKeys() {
        File file = new File(plugin.getDataFolder(), "worlds.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        if (cfg.contains("Worlds")) {
            worldWithKeys = (ArrayList<String>) cfg.getStringList("Worlds");
            return (ArrayList<String>) cfg.getStringList("Worlds");
        }
        return worldWithKeys = new ArrayList<>();
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("addworld")) {
            if (args.length == 1) {
                List<String> worlds = new ArrayList<>();
                Bukkit.getWorlds().forEach(world -> worlds.add(world.getName()));
                List<String> empty = new ArrayList<>();
                for(String s : worlds) {
                    if(s.toLowerCase().startsWith(args[0].toLowerCase()))
                        empty.add(s);
                }
                Collections.sort(empty);
                return empty;
            }
        }
        return null;
    }
}