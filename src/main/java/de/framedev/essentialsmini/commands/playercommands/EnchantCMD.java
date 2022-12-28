package de.framedev.essentialsmini.commands.playercommands;


/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 13.08.2020 19:44
 */

import de.framedev.essentialsmini.main.Main;
import de.framedev.essentialsmini.abstracts.CommandBase;
import de.framedev.essentialsmini.utils.AdminBroadCast;
import de.framedev.essentialsmini.utils.ReplaceCharConfig;
import de.framedev.essentialsmini.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static org.bukkit.Material.AIR;

public class EnchantCMD extends CommandBase {

    private final Main plugin;

    public EnchantCMD(Main plugin) {
        super(plugin, "enchant");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 2) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission(plugin.getPermissionName() + "enchant")) {
                    if (player.getInventory().getItemInMainHand().getType() != AIR) {
                        ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
                        if (args[0].equalsIgnoreCase("unbreakable")) {
                            if (args[1].equalsIgnoreCase("true")) {
                                meta.setUnbreakable(true);
                            } else if (args[1].equalsIgnoreCase("false")) {
                                meta.setUnbreakable(false);
                            }
                            player.getInventory().getItemInMainHand().setItemMeta(meta);
                        } else if (Enchantments.getByName(args[0]) != null) {
                            meta.addEnchant(Enchantments.getByName(args[0]), Integer.parseInt(args[1]), true);
                            player.getInventory().getItemInMainHand().setItemMeta(meta);
                        } else {
                            String message = plugin.getLanguageConfig(player).getString("EnchantNotExist");
                            if (message != null) {
                                message = new TextUtils().replaceAndToParagraph(message);
                            }
                            sender.sendMessage(plugin.getPrefix() + message);
                        }
                    } else {
                        String noItemInHand = plugin.getLanguageConfig(player).getString("NoItemFoundInHand");
                        noItemInHand = ReplaceCharConfig.replaceParagraph(noItemInHand);
                        player.sendMessage(plugin.getPrefix() + noItemInHand);
                    }
                } else {
                    player.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                    new AdminBroadCast(this,"§cNo Permissions!", sender);
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
            }
        } else if (args.length >= 3) {
            if (sender.hasPermission(plugin.getPermissionName() + "enchant.others")) {
                Player target = Bukkit.getPlayer(args[2]);
                if (target != null) {
                    if (target.getInventory().getItemInMainHand().getType() != AIR) {
                        ItemMeta meta = target.getInventory().getItemInMainHand().getItemMeta();
                        if (args[0].equalsIgnoreCase("unbreakable")) {
                            if (args[1].equalsIgnoreCase("true")) {
                                meta.setUnbreakable(true);
                            } else if (args[1].equalsIgnoreCase("false")) {
                                meta.setUnbreakable(false);
                            }
                            target.getInventory().getItemInMainHand().setItemMeta(meta);
                        } else if (Enchantments.getByName(args[0]) != null) {
                            meta.addEnchant(Enchantments.getByName(args[0]), Integer.parseInt(args[1]), true);
                            target.getInventory().getItemInMainHand().setItemMeta(meta);
                        } else {
                            String message = plugin.getLanguageConfig(sender).getString("EnchantNotExist");
                            if (message != null) {
                                message = new TextUtils().replaceAndToParagraph(message);
                            }
                            sender.sendMessage(plugin.getPrefix() + message);
                        }
                    } else {
                        String message = plugin.getLanguageConfig(sender).getString("NoItemFoundInHand");
                        if (message != null) {
                            message = new TextUtils().replaceAndToParagraph(message);
                        }
                        sender.sendMessage(plugin.getPrefix() + message);
                    }
                } else {
                    String message = plugin.getVariables().getPlayerNameNotOnline(args[2]);
                    sender.sendMessage(plugin.getPrefix() + message);
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                new AdminBroadCast(this,"§cNo Permissions!", sender);
            }
        } else {
            if (sender.hasPermission(plugin.getPermissionName() + "enchant")) {
                sender.sendMessage(plugin.getPrefix() + plugin.getWrongArgs("/enchant <Enchantment Name> <Level>"));
            }
            if (sender.hasPermission(plugin.getPermissionName() + "enchant.others")) {
                sender.sendMessage(plugin.getPrefix() + plugin.getWrongArgs("/enchant <Enchantment Name> <Level> <Player Name>"));
            }

        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            if (sender.hasPermission(plugin.getPermissionName() + "enchant") || sender.hasPermission(plugin.getPermissionName() + "enchant.others")) {
                ArrayList<String> empty = new ArrayList<>();
                for (Map.Entry<String, Enchantment> s : EnchantCMD.Enchantments.entrySet()) {
                    if (s.getKey().toLowerCase().startsWith(args[0])) {
                        empty.add(s.getKey());
                    }
                }
                Collections.sort(empty);
                return empty;
            }
        }
        return null;
    }

    public static class Enchantments {
        private static final Map<String, Enchantment> ENCHANTMENTS = new HashMap<String, Enchantment>();
        private static final Map<String, Enchantment> ALIASENCHANTMENTS = new HashMap<String, Enchantment>();

        public static Enchantment getByName(String name) {
            Enchantment enchantment = Enchantment.getByName(name.toUpperCase(Locale.ENGLISH));
            if (enchantment == null) {
                enchantment = ENCHANTMENTS.get(name.toLowerCase(Locale.ENGLISH));
            }
            if (enchantment == null) {
                enchantment = ALIASENCHANTMENTS.get(name.toLowerCase(Locale.ENGLISH));
            }
            return enchantment;
        }

        public static Set<Map.Entry<String, Enchantment>> entrySet() {
            return ENCHANTMENTS.entrySet();
        }

        public static void load() {
            ENCHANTMENTS.put("alldamage", Enchantment.DAMAGE_ALL);
            ALIASENCHANTMENTS.put("alldmg", Enchantment.DAMAGE_ALL);
            ENCHANTMENTS.put("sharpness", Enchantment.DAMAGE_ALL);
            ALIASENCHANTMENTS.put("sharp", Enchantment.DAMAGE_ALL);
            ALIASENCHANTMENTS.put("dal", Enchantment.DAMAGE_ALL);
            ENCHANTMENTS.put("ardmg", Enchantment.DAMAGE_ARTHROPODS);
            ENCHANTMENTS.put("baneofarthropods", Enchantment.DAMAGE_ARTHROPODS);
            ALIASENCHANTMENTS.put("baneofarthropod", Enchantment.DAMAGE_ARTHROPODS);
            ALIASENCHANTMENTS.put("arthropod", Enchantment.DAMAGE_ARTHROPODS);
            ALIASENCHANTMENTS.put("dar", Enchantment.DAMAGE_ARTHROPODS);
            ENCHANTMENTS.put("undeaddamage", Enchantment.DAMAGE_UNDEAD);
            ENCHANTMENTS.put("smite", Enchantment.DAMAGE_UNDEAD);
            ALIASENCHANTMENTS.put("du", Enchantment.DAMAGE_UNDEAD);
            ENCHANTMENTS.put("digspeed", Enchantment.DIG_SPEED);
            ENCHANTMENTS.put("efficiency", Enchantment.DIG_SPEED);
            ALIASENCHANTMENTS.put("minespeed", Enchantment.DIG_SPEED);
            ALIASENCHANTMENTS.put("cutspeed", Enchantment.DIG_SPEED);
            ALIASENCHANTMENTS.put("ds", Enchantment.DIG_SPEED);
            ALIASENCHANTMENTS.put("eff", Enchantment.DIG_SPEED);
            ENCHANTMENTS.put("durability", Enchantment.DURABILITY);
            ALIASENCHANTMENTS.put("dura", Enchantment.DURABILITY);
            ENCHANTMENTS.put("unbreaking", Enchantment.DURABILITY);
            ALIASENCHANTMENTS.put("d", Enchantment.DURABILITY);
            ENCHANTMENTS.put("thorns", Enchantment.THORNS);
            ENCHANTMENTS.put("highcrit", Enchantment.THORNS);
            ALIASENCHANTMENTS.put("thorn", Enchantment.THORNS);
            ALIASENCHANTMENTS.put("highercrit", Enchantment.THORNS);
            ALIASENCHANTMENTS.put("t", Enchantment.THORNS);
            ENCHANTMENTS.put("fireaspect", Enchantment.FIRE_ASPECT);
            ENCHANTMENTS.put("fire", Enchantment.FIRE_ASPECT);
            ALIASENCHANTMENTS.put("meleefire", Enchantment.FIRE_ASPECT);
            ALIASENCHANTMENTS.put("meleeflame", Enchantment.FIRE_ASPECT);
            ALIASENCHANTMENTS.put("fa", Enchantment.FIRE_ASPECT);
            ENCHANTMENTS.put("knockback", Enchantment.KNOCKBACK);
            ALIASENCHANTMENTS.put("kback", Enchantment.KNOCKBACK);
            ALIASENCHANTMENTS.put("kb", Enchantment.KNOCKBACK);
            ALIASENCHANTMENTS.put("k", Enchantment.KNOCKBACK);
            ALIASENCHANTMENTS.put("blockslootbonus", Enchantment.LOOT_BONUS_BLOCKS);
            ENCHANTMENTS.put("fortune", Enchantment.LOOT_BONUS_BLOCKS);
            ALIASENCHANTMENTS.put("fort", Enchantment.LOOT_BONUS_BLOCKS);
            ALIASENCHANTMENTS.put("lbb", Enchantment.LOOT_BONUS_BLOCKS);
            ALIASENCHANTMENTS.put("mobslootbonus", Enchantment.LOOT_BONUS_MOBS);
            ENCHANTMENTS.put("mobloot", Enchantment.LOOT_BONUS_MOBS);
            ENCHANTMENTS.put("looting", Enchantment.LOOT_BONUS_MOBS);
            ALIASENCHANTMENTS.put("lbm", Enchantment.LOOT_BONUS_MOBS);
            ALIASENCHANTMENTS.put("oxygen", Enchantment.OXYGEN);
            ENCHANTMENTS.put("respiration", Enchantment.OXYGEN);
            ALIASENCHANTMENTS.put("breathing", Enchantment.OXYGEN);
            ENCHANTMENTS.put("breath", Enchantment.OXYGEN);
            ALIASENCHANTMENTS.put("o", Enchantment.OXYGEN);
            ENCHANTMENTS.put("protection", Enchantment.PROTECTION_ENVIRONMENTAL);
            ALIASENCHANTMENTS.put("prot", Enchantment.PROTECTION_ENVIRONMENTAL);
            ENCHANTMENTS.put("protect", Enchantment.PROTECTION_ENVIRONMENTAL);
            ALIASENCHANTMENTS.put("p", Enchantment.PROTECTION_ENVIRONMENTAL);
            ALIASENCHANTMENTS.put("explosionsprotection", Enchantment.PROTECTION_EXPLOSIONS);
            ALIASENCHANTMENTS.put("explosionprotection", Enchantment.PROTECTION_EXPLOSIONS);
            ALIASENCHANTMENTS.put("expprot", Enchantment.PROTECTION_EXPLOSIONS);
            ALIASENCHANTMENTS.put("blastprotection", Enchantment.PROTECTION_EXPLOSIONS);
            ALIASENCHANTMENTS.put("bprotection", Enchantment.PROTECTION_EXPLOSIONS);
            ALIASENCHANTMENTS.put("bprotect", Enchantment.PROTECTION_EXPLOSIONS);
            ENCHANTMENTS.put("blastprotect", Enchantment.PROTECTION_EXPLOSIONS);
            ALIASENCHANTMENTS.put("pe", Enchantment.PROTECTION_EXPLOSIONS);
            ALIASENCHANTMENTS.put("fallprotection", Enchantment.PROTECTION_FALL);
            ENCHANTMENTS.put("fallprot", Enchantment.PROTECTION_FALL);
            ENCHANTMENTS.put("featherfall", Enchantment.PROTECTION_FALL);
            ALIASENCHANTMENTS.put("featherfalling", Enchantment.PROTECTION_FALL);
            ALIASENCHANTMENTS.put("pfa", Enchantment.PROTECTION_FALL);
            ALIASENCHANTMENTS.put("fireprotection", Enchantment.PROTECTION_FIRE);
            ALIASENCHANTMENTS.put("flameprotection", Enchantment.PROTECTION_FIRE);
            ENCHANTMENTS.put("fireprotect", Enchantment.PROTECTION_FIRE);
            ALIASENCHANTMENTS.put("flameprotect", Enchantment.PROTECTION_FIRE);
            ENCHANTMENTS.put("fireprot", Enchantment.PROTECTION_FIRE);
            ALIASENCHANTMENTS.put("flameprot", Enchantment.PROTECTION_FIRE);
            ALIASENCHANTMENTS.put("pf", Enchantment.PROTECTION_FIRE);
            ENCHANTMENTS.put("projectileprotection", Enchantment.PROTECTION_PROJECTILE);
            ENCHANTMENTS.put("projprot", Enchantment.PROTECTION_PROJECTILE);
            ALIASENCHANTMENTS.put("pp", Enchantment.PROTECTION_PROJECTILE);
            ENCHANTMENTS.put("silktouch", Enchantment.SILK_TOUCH);
            ALIASENCHANTMENTS.put("softtouch", Enchantment.SILK_TOUCH);
            ALIASENCHANTMENTS.put("st", Enchantment.SILK_TOUCH);
            ENCHANTMENTS.put("waterworker", Enchantment.WATER_WORKER);
            ENCHANTMENTS.put("aquaaffinity", Enchantment.WATER_WORKER);
            ALIASENCHANTMENTS.put("watermine", Enchantment.WATER_WORKER);
            ALIASENCHANTMENTS.put("ww", Enchantment.WATER_WORKER);
            ALIASENCHANTMENTS.put("firearrow", Enchantment.ARROW_FIRE);
            ENCHANTMENTS.put("flame", Enchantment.ARROW_FIRE);
            ENCHANTMENTS.put("flamearrow", Enchantment.ARROW_FIRE);
            ALIASENCHANTMENTS.put("af", Enchantment.ARROW_FIRE);
            ENCHANTMENTS.put("arrowdamage", Enchantment.ARROW_DAMAGE);
            ENCHANTMENTS.put("power", Enchantment.ARROW_DAMAGE);
            ALIASENCHANTMENTS.put("arrowpower", Enchantment.ARROW_DAMAGE);
            ALIASENCHANTMENTS.put("ad", Enchantment.ARROW_DAMAGE);
            ENCHANTMENTS.put("arrowknockback", Enchantment.ARROW_KNOCKBACK);
            ALIASENCHANTMENTS.put("arrowkb", Enchantment.ARROW_KNOCKBACK);
            ENCHANTMENTS.put("punch", Enchantment.ARROW_KNOCKBACK);
            ALIASENCHANTMENTS.put("arrowpunch", Enchantment.ARROW_KNOCKBACK);
            ALIASENCHANTMENTS.put("ak", Enchantment.ARROW_KNOCKBACK);
            ALIASENCHANTMENTS.put("infinitearrows", Enchantment.ARROW_INFINITE);
            ENCHANTMENTS.put("infarrows", Enchantment.ARROW_INFINITE);
            ENCHANTMENTS.put("infinity", Enchantment.ARROW_INFINITE);
            ALIASENCHANTMENTS.put("infinite", Enchantment.ARROW_INFINITE);
            ALIASENCHANTMENTS.put("unlimited", Enchantment.ARROW_INFINITE);
            ALIASENCHANTMENTS.put("unlimitedarrows", Enchantment.ARROW_INFINITE);
            ALIASENCHANTMENTS.put("ai", Enchantment.ARROW_INFINITE);
            ENCHANTMENTS.put("luck", Enchantment.LUCK);
            ALIASENCHANTMENTS.put("luckofsea", Enchantment.LUCK);
            ALIASENCHANTMENTS.put("luckofseas", Enchantment.LUCK);
            ALIASENCHANTMENTS.put("rodluck", Enchantment.LUCK);
            ENCHANTMENTS.put("lure", Enchantment.LURE);
            ALIASENCHANTMENTS.put("rodlure", Enchantment.LURE);
            ENCHANTMENTS.put("depthstrider", Enchantment.DEPTH_STRIDER);
            ALIASENCHANTMENTS.put("depth", Enchantment.DEPTH_STRIDER);
            ALIASENCHANTMENTS.put("strider", Enchantment.DEPTH_STRIDER);
            ENCHANTMENTS.put("frostwalker", Enchantment.FROST_WALKER);
            ALIASENCHANTMENTS.put("frost", Enchantment.FROST_WALKER);
            ALIASENCHANTMENTS.put("walker", Enchantment.FROST_WALKER);
            ENCHANTMENTS.put("mending", Enchantment.MENDING);
            ENCHANTMENTS.put("bindingcurse", Enchantment.BINDING_CURSE);
            ALIASENCHANTMENTS.put("bindcurse", Enchantment.BINDING_CURSE);
            ALIASENCHANTMENTS.put("binding", Enchantment.BINDING_CURSE);
            ALIASENCHANTMENTS.put("bind", Enchantment.BINDING_CURSE);
            ENCHANTMENTS.put("vanishingcurse", Enchantment.VANISHING_CURSE);
            ALIASENCHANTMENTS.put("vanishcurse", Enchantment.VANISHING_CURSE);
            ALIASENCHANTMENTS.put("vanishing", Enchantment.VANISHING_CURSE);
            ALIASENCHANTMENTS.put("vanish", Enchantment.VANISHING_CURSE);
            ENCHANTMENTS.put("sweepingedge", Enchantment.SWEEPING_EDGE);
            ALIASENCHANTMENTS.put("sweepedge", Enchantment.SWEEPING_EDGE);
            ALIASENCHANTMENTS.put("sweeping", Enchantment.SWEEPING_EDGE);
            ALIASENCHANTMENTS.put("sweep", Enchantment.SWEEPING_EDGE);
            ALIASENCHANTMENTS.put("se", Enchantment.SWEEPING_EDGE);
        }
    }
}
