package de.framedev.essentialsmini.commands.servercommands;

/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 21.08.2020 21:23
 */

import de.framedev.essentialsmini.main.Main;
import de.framedev.essentialsmini.managers.BossBarManager;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RestartCMD implements CommandExecutor {

    private final Main plugin;

    public RestartCMD(Main plugin) {
        this.plugin = plugin;
        plugin.getCommands().put("srestart", this);
    }

    // Time
    int time = 100;

    // Progress
    double progress = 1.0;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender.hasPermission(plugin.getVariables().getPermissionBase() + "restart")) {
                BossBarManager.removeAll();
                /* Create BossBar */
                BossBarManager bossBarManager = new BossBarManager("§4Server Restart", BarColor.BLUE /* BarColor */, BarStyle.SEGMENTED_12 /* BarStyle */);
                bossBarManager.create();
                bossBarManager.setProgress(progress);
                sender.sendMessage(plugin.getPrefix() + "§cServer Restart wird eingeführt!");
                Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    if (time <= 0) {
                        /* Server Restart */
                        Bukkit.spigot().restart();
                    } else {
                        switch (time) {
                            case 60:
                                Bukkit.broadcastMessage(plugin.getPrefix() + "§cServer Restartet in §61 Minute§4§l!");
                                Bukkit.getOnlinePlayers().forEach(bossBarManager::addPlayer);
                                bossBarManager.update();
                                break;
                            case 30:
                            case 3:
                            case 15:
                            case 10:
                            case 5:
                            case 2:
                                Bukkit.broadcastMessage(plugin.getPrefix() + "§cServer Restartet in §6" + time + " Sekunden§4§l!");
                                bossBarManager.setProgress(1.0);
                                break;
                            case 1:
                                Bukkit.broadcastMessage(plugin.getPrefix() + "§cServer Restartet in §6einer Sekunde§4§l!");
                                bossBarManager.setProgress(1.0);
                                break;
                        }
                        time--;
                        if(progress != 0.1) {
                            // 0.01
                            progress = progress - 0.0098;
                            bossBarManager.setProgress(progress);
                            bossBarManager.update();
                        }
                    }
                }, 0, 20);
            } else {
                sender.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
            }
        }
        return false;
    }
}
