package ch.framedev.essentialsmini.managers;


/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 15.08.2020 13:48
 */

import ch.framedev.essentialsmini.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This Class is for Updating the Locations or the PlayerManager and Backups
 */
public class UpdateScheduler implements Runnable {

    public boolean started = true;

    @Override
    public void run() {
        boolean[] s = {true, true};
        if (started)
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (Main.getInstance().getConfig().getBoolean("LocationsBackup")) {
                        new LocationsManager().saveBackup();
                        // Main.getInstance().savePlayerHomes();
                        if (Main.getInstance().getConfig().getBoolean("LocationsBackupMessage")) {
                            Bukkit.getConsoleSender().sendMessage(Main.getInstance().getPrefix() + "§a" + new LocationsManager().getFileBackup().getName() + " §6LocationBackup gespeichert!");
                            Bukkit.getConsoleSender().sendMessage(Main.getInstance().getPrefix() + "§aDas Backup befindet sich in §6" + new LocationsManager().getFileBackup().getPath());
                        }
                    } else {
                        s[0] = false;
                    }
                    if (Main.getInstance().getConfig().getBoolean("BackupMessages")) {
                        Bukkit.getConsoleSender().sendMessage(Main.getInstance().getPrefix() + "§6User Data §aSaved!");
                    } else {
                        s[1] = false;
                    }
                    if (!s[0] && !s[1]) {
                        started = false;
                        cancel();
                    }
                }
            }.runTaskTimer(Main.getInstance(), 0, 20L * 60 * Main.getInstance().getConfig().getInt("BackupTime"));
    }
}
