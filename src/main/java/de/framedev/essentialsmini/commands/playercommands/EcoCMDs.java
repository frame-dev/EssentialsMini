package de.framedev.essentialsmini.commands.playercommands;

import de.framedev.essentialsmini.main.Main;
import de.framedev.essentialsmini.managers.CommandBase;
import de.framedev.essentialsmini.utils.ReplaceCharConfig;
import de.framedev.essentialsmini.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * This Plugin was Created by FrameDev
 * Package : de.framedev.essentialsmin.commands
 * Date: 26.10.2020
 * Project: EssentialsMini
 * Copyrighted by FrameDev
 */
public class EcoCMDs extends CommandBase {

    private final Main plugin;

    public EcoCMDs(Main plugin) {
        super(plugin);
        this.plugin = plugin;
        setup("pay", this);
        setup("balance", this);
        setup("eco", this);
        setup("balancetop", this);
        setupTabCompleter("pay", this);
        setupTabCompleter("eco", this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("pay")) {
            if (sender instanceof Player) {
                if (sender.hasPermission(plugin.getPermissionName() + "pay")) {
                    if (args.length == 2) {
                        Player p = (Player) sender;
                        if (isDouble(args[0])) {
                            double amount = Double.parseDouble(args[0]);
                            if (args[1].equalsIgnoreCase("**")) {
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    if (player != null) {
                                        if (plugin.getVaultManager().getEco().has(p, amount)) {
                                            plugin.getVaultManager().getEco().withdrawPlayer(p, amount);
                                            plugin.getVaultManager().getEco().depositPlayer(player, amount);
                                            String send = plugin.getLanguageConfig(p).getString("Money.MSG.Pay");
                                            send = send.replace('&', '§');
                                            send = send.replace("[Target]", player.getName());
                                            send = send.replace("[Money]", amount + plugin.getCurrencySymbol());
                                            String got = plugin.getLanguageConfig(player).getString("Money.MSG.GotPay");
                                            if (got != null) {
                                                got = new TextUtils().replaceAndToParagraph(got);
                                                got = new TextUtils().replaceObject(got, "[Player]", sender.getName());
                                                got = new TextUtils().replaceObject(got, "[Money]", amount + plugin.getCurrencySymbol());
                                            }
                                            player.sendMessage(plugin.getPrefix() + got);
                                            sender.sendMessage(plugin.getPrefix() + send);
                                        } else {
                                            String moneySet = plugin.getLanguageConfig(player).getString("Money.MSG.NotEnough");
                                            moneySet = ReplaceCharConfig.replaceParagraph(moneySet);
                                            moneySet = ReplaceCharConfig.replaceObjectWithData(moneySet, "%Money%", plugin.getVaultManager().getEco().getBalance((Player) sender) + plugin.getCurrencySymbol());
                                            sender.sendMessage(plugin.getPrefix() + moneySet);
                                        }
                                    }
                                }
                            } else {
                                OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                                if (player.hasPlayedBefore()) {
                                    if (plugin.getVaultManager().getEco().has(p, amount)) {
                                        plugin.getVaultManager().getEco().withdrawPlayer(p, amount);
                                        plugin.getVaultManager().getEco().depositPlayer(player, amount);
                                        String send = plugin.getLanguageConfig(sender).getString("Money.MSG.Pay");
                                        send = send.replace('&', '§');
                                        send = send.replace("[Target]", player.getName());
                                        send = send.replace("[Money]", amount + plugin.getCurrencySymbol());
                                        String got = plugin.getCustomMessagesConfig().getString("Money.MSG.GotPay");
                                        if (got != null) {
                                            got = new TextUtils().replaceAndToParagraph(got);
                                            got = new TextUtils().replaceObject(got, "[Player]", sender.getName());
                                            got = new TextUtils().replaceObject(got, "[Money]", amount + plugin.getCurrencySymbol());
                                        }
                                        if (player.isOnline())
                                            ((Player) player).sendMessage(plugin.getPrefix() + got);
                                        sender.sendMessage(plugin.getPrefix() + send);
                                    } else {
                                        String moneySet = plugin.getLanguageConfig(sender).getString("Money.MSG.NotEnough");
                                        moneySet = ReplaceCharConfig.replaceParagraph(moneySet);
                                        moneySet = ReplaceCharConfig.replaceObjectWithData(moneySet, "%Money%", plugin.getVaultManager().getEco().getBalance((Player) sender) + plugin.getCurrencySymbol());
                                        sender.sendMessage(plugin.getPrefix() + moneySet);
                                    }
                                } else {
                                    sender.sendMessage(plugin.getPrefix() + plugin.getVariables().getPlayerNameNotOnline(args[1]));
                                }
                            }
                        } else {
                            sender.sendMessage(plugin.getPrefix() + "§6" + args[0] + " §cisn't a Number!");
                        }
                    } else {
                        sender.sendMessage(plugin.getPrefix() + plugin.getWrongArgs("§6/pay <Amount> <PlayerName>"));
                    }
                } else {
                    sender.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
            }
        }
        if (command.getName().equalsIgnoreCase("balance")) {
            if (args.length == 0) {
                if (sender.hasPermission(plugin.getPermissionName() + "balance")) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        String balance = plugin.getLanguageConfig(player).getString("Money.MSG.Balance");
                        balance = new TextUtils().replaceAndToParagraph(balance);
                        balance = new TextUtils().replaceObject(balance, "[Money]", plugin.getVaultManager().getEco().format(plugin.getVaultManager().getEco().getBalance(player)) + plugin.getCurrencySymbol());
                        player.sendMessage(plugin.getPrefix() + balance);
                    } else {
                        sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
                    }
                } else {
                    sender.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                }
                return true;
            } else if (args.length == 1) {
                if (sender.hasPermission(plugin.getPermissionName() + "balance.others")) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                    String balance = plugin.getLanguageConfig(sender).getString("Money.MoneyBalance.Other.MSG");
                    if (balance != null) {
                        balance = new TextUtils().replaceAndToParagraph(balance);
                    }
                    balance = new TextUtils().replaceObject(balance, "[Target]", player.getName());
                    balance = new TextUtils().replaceObject(balance, "[Money]", plugin.getVaultManager().getEco().format(plugin.getVaultManager().getEco().getBalance(player)) + plugin.getCurrencySymbol());
                    sender.sendMessage(plugin.getPrefix() + balance);
                } else {
                    sender.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                }
                return true;
            } else {
                sender.sendMessage(plugin.getPrefix() + plugin.getWrongArgs("§6/balance §cor §6/balance <PlayerName>"));
                return true;
            }
        }
        if (command.getName().equalsIgnoreCase("eco")) {
            try {
                if (args[0].equalsIgnoreCase("add")) {
                    if (args.length == 2) {
                        if (isDouble(args[1])) {
                            double amount = Double.parseDouble(args[1]);
                            if (sender.hasPermission(plugin.getPermissionName() + "eco.add")) {
                                if (sender instanceof Player) {
                                    Player player = (Player) sender;
                                    plugin.getVaultManager().getEco().depositPlayer(player, amount);
                                    String set = plugin.getLanguageConfig(player).getString("Money.MSG.Set");
                                    if (set != null) {
                                        set = new TextUtils().replaceAndToParagraph(set);
                                        set = new TextUtils().replaceObject(set, "[Money]", plugin.getVaultManager().getEco().getBalance(player) + plugin.getCurrencySymbol());
                                    }
                                    player.sendMessage(plugin.getPrefix() + set);
                                } else {
                                    sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
                                }
                            } else {
                                sender.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                            }
                        } else {
                            sender.sendMessage(plugin.getPrefix() + "§6" + args[0] + " §cisn't a Number!");
                        }
                    } else if (args.length == 3) {
                        if (sender.hasPermission(plugin.getPermissionName() + "eco.add.others")) {
                            if (isDouble(args[1])) {
                                double amount = Double.parseDouble(args[1]);
                                OfflinePlayer player = Bukkit.getOfflinePlayer(args[2]);
                                plugin.getVaultManager().getEco().depositPlayer(player, amount);
                                String setOther = plugin.getLanguageConfig(sender).getString("Money.MoneySet.Other.MSG");
                                if (setOther != null) {
                                    setOther = new TextUtils().replaceAndToParagraph(setOther);
                                    setOther = new TextUtils().replaceObject(setOther, "[Target]", player.getName());
                                    setOther = new TextUtils().replaceObject(setOther, "[Money]", plugin.getVaultManager().getEco().getBalance(player) + plugin.getCurrencySymbol());
                                }
                                String set = plugin.getCustomMessagesConfig().getString("Money.MSG.Set");
                                if (set != null) {
                                    set = new TextUtils().replaceAndToParagraph(set);
                                    set = new TextUtils().replaceObject(set, "[Money]", amount + plugin.getCurrencySymbol());
                                }
                                sender.sendMessage(plugin.getPrefix() + setOther);
                                if (!Main.getSilent().contains(sender.getName()))
                                    if (player.isOnline()) {
                                        Player online = (Player) player;
                                        online.sendMessage(plugin.getPrefix() + set);
                                    }
                            } else {
                                sender.sendMessage(plugin.getPrefix() + "§6" + args[0] + " §cisn't a Number!");
                            }
                        } else {
                            sender.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                        }
                    }
                }
                if (args[0].equalsIgnoreCase("remove")) {
                    if (args.length == 2) {
                        if (isDouble(args[1])) {
                            double amount = Double.parseDouble(args[1]);
                            if (sender.hasPermission(plugin.getPermissionName() + "eco.add")) {
                                if (sender instanceof Player) {
                                    Player player = (Player) sender;
                                    plugin.getVaultManager().getEco().withdrawPlayer(player, amount);
                                    String set = plugin.getLanguageConfig(player).getString("Money.MSG.Set");
                                    if (set != null) {
                                        set = new TextUtils().replaceAndToParagraph(set);
                                        set = new TextUtils().replaceObject(set, "[Money]", plugin.getVaultManager().getEco().getBalance(player) + plugin.getCurrencySymbol());
                                    }
                                    player.sendMessage(plugin.getPrefix() + set);
                                } else {
                                    sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
                                }
                            } else {
                                sender.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                            }
                        } else {
                            sender.sendMessage(plugin.getPrefix() + "§6" + args[0] + " §cisn't a Number!");
                        }
                    } else if (args.length == 3) {
                        if (sender.hasPermission(plugin.getPermissionName() + "eco.add.others")) {
                            if (isDouble(args[1])) {
                                double amount = Double.parseDouble(args[1]);
                                OfflinePlayer player = Bukkit.getOfflinePlayer(args[2]);
                                plugin.getVaultManager().getEco().depositPlayer(player, amount);
                                String setOther = plugin.getLanguageConfig(sender).getString("Money.MoneySet.Other.MSG");
                                if (setOther != null) {
                                    setOther = new TextUtils().replaceAndToParagraph(setOther);
                                    setOther = new TextUtils().replaceObject(setOther, "[Target]", player.getName());
                                    setOther = new TextUtils().replaceObject(setOther, "[Money]", plugin.getVaultManager().getEco().getBalance(player) + plugin.getCurrencySymbol());
                                }
                                String set = plugin.getCustomMessagesConfig().getString("Money.MSG.Set");
                                if (set != null) {
                                    set = new TextUtils().replaceAndToParagraph(set);
                                    set = new TextUtils().replaceObject(set, "[Money]", amount + plugin.getCurrencySymbol());
                                }
                                sender.sendMessage(plugin.getPrefix() + setOther);
                                if (!Main.getSilent().contains(sender.getName()))
                                    if (player.isOnline()) {
                                        Player online = (Player) player;
                                        online.sendMessage(plugin.getPrefix() + set);
                                    }
                            } else {
                                sender.sendMessage(plugin.getPrefix() + "§6" + args[0] + " §cisn't a Number!");
                            }
                        } else {
                            sender.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                        }
                    }
                }
                if (args[0].equalsIgnoreCase("set")) {
                    if (args.length == 2) {
                        if (isDouble(args[1])) {
                            double amount = Double.parseDouble(args[1]);
                            if (sender.hasPermission(plugin.getPermissionName() + "eco.set")) {
                                if (sender instanceof Player) {
                                    Player player = (Player) sender;
                                    plugin.getVaultManager().getEco().withdrawPlayer(player, plugin.getVaultManager().getEco().getBalance(player));
                                    plugin.getVaultManager().getEco().depositPlayer(player, amount);
                                    String set = plugin.getLanguageConfig(player).getString("Money.MSG.Set");
                                    if (set != null) {
                                        set = new TextUtils().replaceAndToParagraph(set);
                                        set = new TextUtils().replaceObject(set, "[Money]", amount + plugin.getCurrencySymbol());
                                    }
                                    player.sendMessage(plugin.getPrefix() + set);
                                } else {
                                    sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
                                }
                            } else {
                                sender.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                            }
                        } else {
                            sender.sendMessage(plugin.getPrefix() + "§6" + args[0] + " §cisn't a Number!");
                        }
                    } else if (args.length == 3) {
                        if (sender.hasPermission(plugin.getPermissionName() + "eco.set.others")) {
                            if (isDouble(args[1])) {
                                double amount = Double.parseDouble(args[1]);
                                OfflinePlayer player = Bukkit.getOfflinePlayer(args[2]);
                                plugin.getVaultManager().getEco().withdrawPlayer(player, plugin.getVaultManager().getEco().getBalance(player));
                                plugin.getVaultManager().getEco().depositPlayer(player, amount);
                                String setOther = plugin.getLanguageConfig(sender).getString("Money.MoneySet.Other.MSG");
                                if (setOther != null) {
                                    setOther = new TextUtils().replaceAndToParagraph(setOther);
                                    setOther = new TextUtils().replaceObject(setOther, "[Target]", player.getName());
                                    setOther = new TextUtils().replaceObject(setOther, "[Money]", amount + plugin.getCurrencySymbol());
                                }
                                String set = plugin.getCustomMessagesConfig().getString("Money.MSG.Set");
                                if (set != null) {
                                    set = new TextUtils().replaceAndToParagraph(set);
                                    set = new TextUtils().replaceObject(set, "[Money]", amount + plugin.getCurrencySymbol());
                                }
                                sender.sendMessage(plugin.getPrefix() + setOther);
                                if (!Main.getSilent().contains(sender.getName()))
                                    if (player.isOnline()) {
                                        Player online = (Player) player;
                                        online.sendMessage(plugin.getPrefix() + set);
                                    }
                            } else {
                                sender.sendMessage(plugin.getPrefix() + "§6" + args[0] + " §cisn't a Number!");
                            }
                        } else {
                            sender.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                        }
                    }
                }
            } catch (Exception ignored) {
                sender.sendMessage(plugin.getPrefix() + "§cPlease use §6/eco set <Amount> §cor §6/eco set <Amount> <PlayerName>§4§l!");
                return true;
            }
        }
        if (command.getName().equalsIgnoreCase("balancetop")) {
            if (sender.hasPermission(plugin.getPermissionName() + "balancetop")) {
                HashMap<String, Double> mostplayers = new HashMap<>();
                ValueComparator bvc = new ValueComparator(mostplayers);
                TreeMap<String, Double> sorted_map = new TreeMap<>(bvc);
                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (plugin.getVaultManager().getEco().getBanks().isEmpty()) {
                        mostplayers.put(all.getName(), plugin.getVaultManager().getEco().getBalance(all));
                    } else {
                        for (String bank : plugin.getVaultManager().getEco().getBanks()) {
                            if (plugin.getVaultManager().getEco().isBankMember(bank, all).transactionSuccess() || plugin.getVaultManager().getEco().isBankOwner(bank, all).transactionSuccess()) {
                                mostplayers.put(all.getName(), Double.parseDouble(plugin.getVaultManager().getEco().format(plugin.getVaultManager().getEco().getBalance(all)).replace(",",".")) + Double.parseDouble(plugin.getVaultManager().getEco().format(plugin.getVaultManager().getEco().bankBalance(bank).balance).replace(",",".")));
                            } else {
                                mostplayers.put(all.getName(), Double.parseDouble(plugin.getVaultManager().getEco().format(plugin.getVaultManager().getEco().getBalance(all)).replace(",",".")));
                            }
                        }
                    }
                }
                for (OfflinePlayer alloffline : Bukkit.getOfflinePlayers()) {
                    if (plugin.getVaultManager().getEco().getBanks().isEmpty()) {
                        mostplayers.put(alloffline.getName(), plugin.getVaultManager().getEco().getBalance(alloffline));
                    } else {
                        for (String bank : plugin.getVaultManager().getEco().getBanks()) {
                            if (plugin.getVaultManager().getEco().isBankMember(bank, alloffline).transactionSuccess() || plugin.getVaultManager().getEco().isBankOwner(bank, alloffline).transactionSuccess()) {
                                mostplayers.put(alloffline.getName(), Double.parseDouble(plugin.getVaultManager().getEco().format(plugin.getVaultManager().getEco().getBalance(alloffline)).replace(",",".")) + Double.parseDouble(plugin.getVaultManager().getEco().format(plugin.getVaultManager().getEco().bankBalance(bank).balance).replace(",",".")));
                            } else {
                                mostplayers.put(alloffline.getName(), Double.parseDouble(plugin.getVaultManager().getEco().format(plugin.getVaultManager().getEco().getBalance(alloffline)).replace(",",".")));
                            }
                        }
                    }
                }
                sorted_map.putAll(mostplayers);
                int i = 0;
                for (Map.Entry<String, Double> e : sorted_map.entrySet()) {
                    if (e == null) continue;
                    i++;
                    sender.sendMessage("§a" + i + "st [§6" + e.getKey() + " §b: " + e.getValue() + plugin.getCurrencySymbolMulti() + "§a]");
                    if (i == 3) {
                        break;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean isDouble(String text) {
        try {
            Double.parseDouble(text);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("pay")) {
            if (args.length == 1) {
                ArrayList<String> list = new ArrayList<>();
                if (sender instanceof Player) {
                    if (sender.hasPermission(plugin.getPermissionName() + "pay")) {
                        list.add(String.valueOf(plugin.getVaultManager().getEco().format(plugin.getVaultManager().getEco().getBalance((Player) sender))));
                    }
                    return list;
                }
            } else if (args.length == 2) {
                ArrayList<String> players = new ArrayList<>();
                ArrayList<String> empty = new ArrayList<>();
                players.add("**");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    players.add(player.getName());
                }
                for (String s : players) {
                    if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                        empty.add(s);
                    }
                }
                Collections.sort(empty);
                return empty;
            }
        }
        if (command.getName().equalsIgnoreCase("eco")) {
            if (args.length == 1) {
                List<String> commands = new ArrayList<>();
                commands.add("add");
                commands.add("remove");
                commands.add("set");
                List<String> empty = new ArrayList<>();
                for (String s : commands) {
                    if (s.toLowerCase().startsWith(args[0].toLowerCase()))
                        empty.add(s);
                }
                Collections.sort(empty);
                return empty;
            }
        }
        return null;
    }

    static class ValueComparator implements Comparator<String> {


        Map<String, Double> base;

        public ValueComparator(Map<String, Double> base) {
            this.base = base;
        }


        public int compare(String a, String b) {
            if (base.get(a) >= base.get(b)) {
                return -1;
            } else {
                return 1;
            } // returning 0 would merge keys
        }
    }
}
