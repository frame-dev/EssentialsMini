package ch.framedev.essentialsmini.commands.playercommands;


/*
 * EssentialsMini
 * de.framedev.essentialsmini.commands
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 05.10.2020 20:50
 */

import ch.framedev.essentialsmini.main.Main;
import ch.framedev.essentialsmini.abstracts.CommandListenerBase;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

public class RegisterCMD extends CommandListenerBase {

    private static final File file = new File(Main.getInstance().getDataFolder(), "accounts.yml");
    private static final FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

    private final Main plugin;

    private final ArrayList<OfflinePlayer> registerd = new ArrayList<>();

    public RegisterCMD(Main plugin) {
        super(plugin, "register", "login");
        this.plugin = plugin;
    }

    String passwordToBase64(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes());
    }

    String base64ToPassword(String base64) {
        return new String(Base64.getDecoder().decode(base64.getBytes()));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!plugin.getVariables().isOnlineMode()) {
            if (command.getName().equalsIgnoreCase("register")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (!cfg.contains(player.getName())) {
                        if (args.length == 1) {
                            String pwd = args[0];
                            cfg.set(player.getName(), passwordToBase64(pwd));
                            try {
                                cfg.save(file);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            registerd.add(player);
                            player.sendMessage(plugin.getPrefix() + "§aDu wurdest Erfolgreich Regristiert!");
                        } else {
                            player.sendMessage(plugin.getPrefix() + plugin.getWrongArgs("/register <Passwort>"));
                        }
                    } else {
                        player.sendMessage(plugin.getPrefix() + "§cDu bist bereits Regristiert!");
                    }
                } else {
                    sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
                }
            }
            if (command.getName().equalsIgnoreCase("login")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (cfg.contains(player.getName())) {
                        if (!registerd.contains(player)) {
                            if (args.length == 1) {
                                String pwd = args[0];
                                if (cfg.getString(player.getName()).equalsIgnoreCase(passwordToBase64(pwd))) {
                                    registerd.add(player);
                                    player.sendMessage(plugin.getPrefix() + "§aDu bist Erfolgreich Eingeloggt!");
                                } else {
                                    player.sendMessage(plugin.getPrefix() + "§cFalsches Passwort!");
                                }
                            } else {
                                player.sendMessage(plugin.getPrefix() + plugin.getWrongArgs("/login <Passwort>"));
                            }
                        } else {
                            player.sendMessage(plugin.getPrefix() + "§aDu bist bereits Eingeloggt!");
                        }
                    } else {
                        player.sendMessage(plugin.getPrefix() + "§cBitte Regristiere dich zuerst!");
                    }
                } else {
                    sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
                }
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!plugin.getVariables().isOnlineMode()) {
            if (!registerd.contains(event.getPlayer())) {
                if (cfg.contains(event.getPlayer().getName())) {
                    event.getPlayer().sendMessage(plugin.getPrefix() + "§cBitte Log dich zuerst ein §6/login <Password>§4§l!");
                } else {
                    event.getPlayer().sendMessage(plugin.getPrefix() + "§cBitte Regristier dich zuerst §6/register <Password>§4§l!");
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!plugin.getVariables().isOnlineMode()) {
            if (!registerd.contains(event.getPlayer())) {
                if (cfg.contains(event.getPlayer().getName())) {
                    event.getPlayer().sendMessage(plugin.getPrefix() + "§cBitte Log dich zuerst ein §6/login <Password>§4§l!");
                } else {
                    event.getPlayer().sendMessage(plugin.getPrefix() + "§cBitte Regristier dich zuerst §6/register <Password>§4§l!");
                }
                event.setCancelled(true);
            }
        }
    }

    public static FileConfiguration getCfg() {
        return cfg;
    }
}
