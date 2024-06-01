package ch.framedev.essentialsmini.commands.playercommands;


/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 13.08.2020 19:44
 */

import ch.framedev.simplejavautils.TextUtils;
import ch.framedev.essentialsmini.main.Main;
import ch.framedev.essentialsmini.abstracts.CommandBase;
import ch.framedev.essentialsmini.utils.AdminBroadCast;
import ch.framedev.essentialsmini.utils.ReplaceCharConfig;
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
        if (args.length == 2) {
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
                                message = new TextUtils().replaceAndWithParagraph(message);
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
                    new AdminBroadCast(this, "§cNo Permissions!", sender);
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
            }
        } else if (args.length == 3) {
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
                                message = new TextUtils().replaceAndWithParagraph(message);
                            }
                            sender.sendMessage(plugin.getPrefix() + message);
                        }
                    } else {
                        String message = plugin.getLanguageConfig(sender).getString("NoItemFoundInHand");
                        if (message != null) {
                            message = new TextUtils().replaceAndWithParagraph(message);
                        }
                        sender.sendMessage(plugin.getPrefix() + message);
                    }
                } else {
                    String message = plugin.getVariables().getPlayerNameNotOnline(args[2]);
                    sender.sendMessage(plugin.getPrefix() + message);
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
                new AdminBroadCast(this, "§cNo Permissions!", sender);
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
            ENCHANTMENTS.put("alldamage", Enchantment.SHARPNESS);
            ALIASENCHANTMENTS.put("alldmg", Enchantment.SHARPNESS);
            ENCHANTMENTS.put("sharpness", Enchantment.SHARPNESS);
            ALIASENCHANTMENTS.put("sharp", Enchantment.SHARPNESS);
            ALIASENCHANTMENTS.put("dal", Enchantment.SHARPNESS);
            ENCHANTMENTS.put("ardmg", Enchantment.POWER);
            ENCHANTMENTS.put("baneofarthropods", Enchantment.SMITE);
            ALIASENCHANTMENTS.put("baneofarthropod", Enchantment.BANE_OF_ARTHROPODS);
            ALIASENCHANTMENTS.put("arthropod", Enchantment.BANE_OF_ARTHROPODS);
            ALIASENCHANTMENTS.put("dar", Enchantment.BANE_OF_ARTHROPODS);
            ENCHANTMENTS.put("undeaddamage", Enchantment.SMITE);
            ENCHANTMENTS.put("smite", Enchantment.SMITE);
            ALIASENCHANTMENTS.put("du", Enchantment.SMITE);
            ENCHANTMENTS.put("digspeed", Enchantment.EFFICIENCY);
            ENCHANTMENTS.put("efficiency", Enchantment.EFFICIENCY);
            ALIASENCHANTMENTS.put("minespeed", Enchantment.EFFICIENCY);
            ALIASENCHANTMENTS.put("cutspeed", Enchantment.EFFICIENCY);
            ALIASENCHANTMENTS.put("ds", Enchantment.EFFICIENCY);
            ALIASENCHANTMENTS.put("eff", Enchantment.EFFICIENCY);
            ENCHANTMENTS.put("durability", Enchantment.UNBREAKING);
            ALIASENCHANTMENTS.put("dura", Enchantment.UNBREAKING);
            ENCHANTMENTS.put("unbreaking", Enchantment.UNBREAKING);
            ALIASENCHANTMENTS.put("d", Enchantment.UNBREAKING);
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
            ALIASENCHANTMENTS.put("blockslootbonus", Enchantment.FORTUNE);
            ENCHANTMENTS.put("fortune", Enchantment.FORTUNE);
            ALIASENCHANTMENTS.put("fort", Enchantment.FORTUNE);
            ALIASENCHANTMENTS.put("lbb", Enchantment.FORTUNE);
            ALIASENCHANTMENTS.put("mobslootbonus", Enchantment.LOOTING);
            ENCHANTMENTS.put("mobloot", Enchantment.LOOTING);
            ENCHANTMENTS.put("looting", Enchantment.LOOTING);
            ALIASENCHANTMENTS.put("lbm", Enchantment.LOOTING);
            ALIASENCHANTMENTS.put("oxygen", Enchantment.RESPIRATION);
            ENCHANTMENTS.put("respiration", Enchantment.RESPIRATION);
            ALIASENCHANTMENTS.put("breathing", Enchantment.RESPIRATION);
            ENCHANTMENTS.put("breath", Enchantment.RESPIRATION);
            ALIASENCHANTMENTS.put("o", Enchantment.RESPIRATION);
            ENCHANTMENTS.put("protection", Enchantment.PROTECTION);
            ALIASENCHANTMENTS.put("prot", Enchantment.PROTECTION);
            ENCHANTMENTS.put("protect", Enchantment.PROTECTION);
            ALIASENCHANTMENTS.put("p", Enchantment.PROTECTION);
            ALIASENCHANTMENTS.put("explosionsprotection", Enchantment.BLAST_PROTECTION);
            ALIASENCHANTMENTS.put("explosionprotection", Enchantment.BLAST_PROTECTION);
            ALIASENCHANTMENTS.put("expprot", Enchantment.BLAST_PROTECTION);
            ALIASENCHANTMENTS.put("blastprotection", Enchantment.BLAST_PROTECTION);
            ALIASENCHANTMENTS.put("bprotection", Enchantment.BLAST_PROTECTION);
            ALIASENCHANTMENTS.put("bprotect", Enchantment.BLAST_PROTECTION);
            ENCHANTMENTS.put("blastprotect", Enchantment.BLAST_PROTECTION);
            ALIASENCHANTMENTS.put("pe", Enchantment.BLAST_PROTECTION);
            ALIASENCHANTMENTS.put("fallprotection", Enchantment.FEATHER_FALLING);
            ENCHANTMENTS.put("fallprot", Enchantment.FEATHER_FALLING);
            ENCHANTMENTS.put("featherfall", Enchantment.FEATHER_FALLING);
            ALIASENCHANTMENTS.put("featherfalling", Enchantment.FEATHER_FALLING);
            ALIASENCHANTMENTS.put("pfa", Enchantment.FEATHER_FALLING);
            ALIASENCHANTMENTS.put("fireprotection", Enchantment.FIRE_PROTECTION);
            ALIASENCHANTMENTS.put("flameprotection", Enchantment.FIRE_PROTECTION);
            ENCHANTMENTS.put("fireprotect", Enchantment.FIRE_PROTECTION);
            ALIASENCHANTMENTS.put("flameprotect", Enchantment.FIRE_PROTECTION);
            ENCHANTMENTS.put("fireprot", Enchantment.FIRE_PROTECTION);
            ALIASENCHANTMENTS.put("flameprot", Enchantment.FIRE_PROTECTION);
            ALIASENCHANTMENTS.put("pf", Enchantment.FIRE_PROTECTION);
            ENCHANTMENTS.put("projectileprotection", Enchantment.PROJECTILE_PROTECTION);
            ENCHANTMENTS.put("projprot", Enchantment.PROJECTILE_PROTECTION);
            ALIASENCHANTMENTS.put("pp", Enchantment.PROJECTILE_PROTECTION);
            ENCHANTMENTS.put("silktouch", Enchantment.SILK_TOUCH);
            ALIASENCHANTMENTS.put("softtouch", Enchantment.SILK_TOUCH);
            ALIASENCHANTMENTS.put("st", Enchantment.SILK_TOUCH);
            ENCHANTMENTS.put("waterworker", Enchantment.AQUA_AFFINITY);
            ENCHANTMENTS.put("aquaaffinity", Enchantment.AQUA_AFFINITY);
            ALIASENCHANTMENTS.put("watermine", Enchantment.AQUA_AFFINITY);
            ALIASENCHANTMENTS.put("ww", Enchantment.AQUA_AFFINITY);
            ALIASENCHANTMENTS.put("firearrow", Enchantment.FLAME);
            ENCHANTMENTS.put("flame", Enchantment.FLAME);
            ENCHANTMENTS.put("flamearrow", Enchantment.FLAME);
            ALIASENCHANTMENTS.put("af", Enchantment.FLAME);
            ENCHANTMENTS.put("arrowdamage", Enchantment.POWER);
            ENCHANTMENTS.put("power", Enchantment.POWER);
            ALIASENCHANTMENTS.put("arrowpower", Enchantment.POWER);
            ALIASENCHANTMENTS.put("ad", Enchantment.POWER);
            ENCHANTMENTS.put("arrowknockback", Enchantment.PUNCH);
            ALIASENCHANTMENTS.put("arrowkb", Enchantment.PUNCH);
            ENCHANTMENTS.put("punch", Enchantment.PUNCH);
            ALIASENCHANTMENTS.put("arrowpunch", Enchantment.PUNCH);
            ALIASENCHANTMENTS.put("ak", Enchantment.PUNCH);
            ALIASENCHANTMENTS.put("infinitearrows", Enchantment.INFINITY);
            ENCHANTMENTS.put("infarrows", Enchantment.INFINITY);
            ENCHANTMENTS.put("infinity", Enchantment.INFINITY);
            ALIASENCHANTMENTS.put("infinite", Enchantment.INFINITY);
            ALIASENCHANTMENTS.put("unlimited", Enchantment.INFINITY);
            ALIASENCHANTMENTS.put("unlimitedarrows", Enchantment.INFINITY);
            ALIASENCHANTMENTS.put("ai", Enchantment.INFINITY);
            ENCHANTMENTS.put("luck", Enchantment.LUCK_OF_THE_SEA);
            ALIASENCHANTMENTS.put("luckofsea", Enchantment.LUCK_OF_THE_SEA);
            ALIASENCHANTMENTS.put("luckofseas", Enchantment.LUCK_OF_THE_SEA);
            ALIASENCHANTMENTS.put("rodluck", Enchantment.LUCK_OF_THE_SEA);
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
