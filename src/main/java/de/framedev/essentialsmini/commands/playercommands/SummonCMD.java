package de.framedev.essentialsmini.commands.playercommands;


/*
 * de.framedev.essentialsmin.commands
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 22.09.2020 11:23
 */

import de.framedev.essentialsmini.main.Main;
import de.framedev.essentialsmini.managers.CommandBase;
import de.framedev.essentialsmini.utils.AdminBroadCast;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SummonCMD extends CommandBase {

    private final Main plugin;

    public SummonCMD(Main plugin) {
        super(plugin, "spawnmob");
        this.plugin = plugin;
        setup(this);
        setupTabCompleter(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission(plugin.getPermissionName() + "summon")) {
                    EntityType type = EntityType.fromName(args[0]);
                    if (type != null) {
                        Block block = player.getTargetBlock(null, 100);
                        Location bl = block.getLocation();
                        bl.setY(block.getLocation().getY() + 1.0D);
                        player.getWorld().spawnEntity(bl, type);
                        player.sendMessage(plugin.getPrefix() + "§6" + type.name() + " §awurde erfolgreich gespawnt!");
                        new AdminBroadCast("spawnmob",plugin.getPrefix() + "§6" + type.name() + " §awurde erfolgreich gespawnt!", sender);
                    } else {
                        sender.sendMessage(plugin.getPrefix() + "§cDieses Entity existiert nicht! §6" + type.name());
                    }
                } else {
                    player.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
            }
        } else if (args.length == 2) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission(plugin.getPermissionName() + "summon")) {
                    EntityType type = EntityType.fromName(args[0]);
                    if (type != null) {
                        try {
                            int amount = Integer.parseInt(args[1]);
                            Block block = player.getTargetBlock(null, 100);
                            Location bl = block.getLocation();
                            bl.setY(block.getLocation().getY() + 1.0D);
                            for (int i = 1; i <= amount; i++) {
                                player.getWorld().spawnEntity(bl, type);
                            }
                            player.sendMessage(plugin.getPrefix() + "§6" + type.name() + " §awurde erfolgreich gespawnt!");
                            new AdminBroadCast("spawnmob",plugin.getPrefix() + "§6" + type.name() + " §awurde erfolgreich gespawnt!", sender);
                        } catch (NumberFormatException ignored) {
                            player.sendMessage(plugin.getPrefix() + "§6" + args[1] + "§c ist keine Nummer!");
                        }
                    } else {
                        sender.sendMessage(plugin.getPrefix() + "§cDieses Entity existiert nicht! §6" + type.name());
                    }
                } else {
                    player.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                    new AdminBroadCast("spawnmob","§cNo Permissions!", sender);
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
            }
        } else {
            sender.sendMessage(plugin.getPrefix() + plugin.getWrongArgs("/summon <EntityName>"));
            sender.sendMessage(plugin.getPrefix() + plugin.getWrongArgs("/summon <EntityName> <Amount>"));
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            ArrayList<EntityType> entityList = new ArrayList<>(Arrays.asList(EntityType.values()));
            ArrayList<String> empty = new ArrayList<>();
            for (EntityType type : entityList) {
                if (type.name().toLowerCase().startsWith(args[0])) {
                    empty.add(type.name());
                }
            }
            Collections.sort(empty);
            return empty;
        }
        return null;
    }
}
