package de.framedev.essentialsmini.commands.playercommands;

import com.google.common.collect.Lists;
import de.framedev.essentialsmini.main.Main;
import de.framedev.essentialsmini.abstracts.CommandBase;
import de.framedev.essentialsmini.utils.AdminBroadCast;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * / This Plugin was Created by FrameDev
 * / Package : de.framedev.essentialsmini.commands.playercommands
 * / ClassName FireworkCMD
 * / Date: 27.06.21
 * / Project: EssentialsMini
 * / Copyrighted by FrameDev
 */

public class FireWorkCMD extends CommandBase {

    public FireWorkCMD(Main plugin) {
        super(plugin, "firework");
        setupTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(getPlugin().getPrefix() + getPlugin().getOnlyPlayer());
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission(getPlugin().getPermissionName() + "firework")) {
            player.sendMessage(getPlugin().getPrefix() + getPlugin().getNoPerms());
            new AdminBroadCast(this, "§cNo Permissions!", sender);
            return true;
        }
        if (args[0].equalsIgnoreCase("create")) {
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            if (itemStack.getType() != Material.FIREWORK_ROCKET) {
                player.sendMessage(getPlugin().getPrefix() + "§cKein Feuerwerksrackete in der Hand gefunden!");
                return true;
            }
            FireworkMeta effect = (FireworkMeta) itemStack.getItemMeta();
            FireworkEffect.Type type = FireworkEffect.Type.valueOf(args[1].toUpperCase());
            if (FireworkEffect.Type.valueOf(args[1].toUpperCase()) == null) {
                sender.sendMessage(getPlugin().getPrefix() + "§aFeuerwerk Typ nicht gefunden!");
                return true;
            }
            if (args.length == 3) {
                List<Color> colors = new ArrayList<>();
                if (args[2].contains(":")) {
                    String[] s = args[2].split(":");
                    for (String a : s) {
                        colors.add(DyeColor.valueOf(a.toUpperCase()).getColor());
                    }
                } else {
                    colors.add(DyeColor.valueOf(args[2].toUpperCase()).getColor());
                }
                effect.addEffect(FireworkEffect.builder().with(type).withColor(colors.toArray(new Color[0])).build());
                itemStack.setItemMeta(effect);
                return true;
            }

            if (args.length == 4) {
                List<Color> colors = new ArrayList<>();
                if (args[2].contains(":")) {
                    String[] s = args[2].split(":");
                    for (String a : s) {
                        colors.add(DyeColor.valueOf(a.toUpperCase()).getColor());
                    }
                } else {
                    colors.add(DyeColor.valueOf(args[2].toUpperCase()).getColor());
                }
                effect.addEffect(FireworkEffect.builder().with(type).withColor(colors.toArray(new Color[0])).build());
                effect.setPower(Integer.parseInt(args[3]));
                itemStack.setItemMeta(effect);
            }
            if (args.length == 5) {
                List<Color> colors = new ArrayList<>();
                if (args[2].contains(":")) {
                    String[] s = args[2].split(":");
                    for (String a : s) {
                        colors.add(DyeColor.valueOf(a.toUpperCase()).getColor());
                    }
                } else {
                    colors.add(DyeColor.valueOf(args[2].toUpperCase()).getColor());
                }
                effect.addEffect(FireworkEffect.builder().with(type).withColor(colors.toArray(new Color[0])).flicker(Boolean.parseBoolean(args[4])).build());
                effect.setPower(Integer.parseInt(args[3]));
                itemStack.setItemMeta(effect);
            }
            if (args.length == 6) {
                List<Color> colors = new ArrayList<>();
                if (args[2].contains(":")) {
                    String[] s = args[2].split(":");
                    for (String a : s) {
                        colors.add(DyeColor.valueOf(a.toUpperCase()).getColor());
                    }
                } else {
                    colors.add(DyeColor.valueOf(args[2].toUpperCase()).getColor());
                }
                effect.addEffect(FireworkEffect.builder().with(type).withColor(colors.toArray(new Color[0])).flicker(Boolean.parseBoolean(args[4])).trail(Boolean.parseBoolean(args[5])).build());
                effect.setPower(Integer.parseInt(args[3]));
                itemStack.setItemMeta(effect);
            }
        }
        if (args[0].equalsIgnoreCase("clear")) {
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            if (itemStack.getType() != Material.FIREWORK_ROCKET) {
                player.sendMessage(getPlugin().getPrefix() + "§cKein Feuerwerksrackete in der Hand gefunden!");
                return true;
            }
            FireworkMeta effect = (FireworkMeta) itemStack.getItemMeta();
            if (effect != null && effect.hasEffects())
                effect.clearEffects();
            itemStack.setItemMeta(effect);
        }
        if (args[0].equalsIgnoreCase("fire")) {
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            if (itemStack.getType() != Material.FIREWORK_ROCKET) {
                player.sendMessage(getPlugin().getPrefix() + "§cKein Feuerwerksrackete in der Hand gefunden!");
                return true;
            }
            boolean direction = false;
            int amount = Integer.parseInt(args[1]);
            for (int i = 0; i < amount; i++) {
                final Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
                final FireworkMeta fmeta = (FireworkMeta) itemStack.getItemMeta();
                if (fmeta != null) {
                    firework.setFireworkMeta(fmeta);
                }
            }
        }
        return super.onCommand(sender, command, label, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return new ArrayList<String>(Arrays.asList("create", "clear", "fire"));
        } else if (args.length == 2) {
            FireworkEffect.Type[] types = FireworkEffect.Type.values();
            ArrayList<String> empty = new ArrayList<>();
            for (FireworkEffect.Type type : types) {
                if (type.name().toLowerCase().startsWith(args[1])) {
                    empty.add(type.name());
                }
            }
            Collections.sort(empty);
            return empty;
        } else if (args.length == 3) {
            final List<String> options = Lists.newArrayList();
            final String prefix;
            if (args[2].contains(":")) {
                prefix = args[0].substring(0, args[0].lastIndexOf(':') + 1);
            } else {
                prefix = "";
            }
            for (final DyeColor color : DyeColor.values()) {
                options.add(prefix + color.name().toUpperCase() + ":");
            }
            return options;
        } else if (args.length == 4) {
            ArrayList<String> data = new ArrayList<>();
            data.add("1");
            data.add("2");
            data.add("3");
            return data;
        } else if (args.length == 5) {
            return new ArrayList<String>(Arrays.asList("true", "false"));
        } else if (args.length == 6) {
            return new ArrayList<String>(Arrays.asList("true", "false"));
        }
        return super.onTabComplete(sender, command, label, args);
    }
}
