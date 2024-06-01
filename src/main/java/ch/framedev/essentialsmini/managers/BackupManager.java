package ch.framedev.essentialsmini.managers;

import ch.framedev.essentialsmini.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static org.apache.commons.io.FileUtils.copyFile;

public class BackupManager {

    public void backup() {
        for(World world : Bukkit.getWorlds()) {
            world.save();
        }
        for(World world : Bukkit.getWorlds()) {
            try {
                copyDirectory(world.getWorldFolder(),new File("Backups/Backups_"+ new SimpleDateFormat("yyyy.MM.dd|HH:mm:ss").format(new Date(System.currentTimeMillis())) + "/"+world.getName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void makeBackup() {
        backup();
        Bukkit.getConsoleSender().sendMessage(Main.getInstance().getPrefix() + "Backup Created!");
    }

    private static void copyDirectory(File sourceDirectory, File destinationDirectory) throws IOException {
        if (!destinationDirectory.exists()) {
            destinationDirectory.mkdir();
        }
        for (String f : Objects.requireNonNull(sourceDirectory.list())) {
            copyDirectoryCompatibilityMode(new File(sourceDirectory, f), new File(destinationDirectory, f));
        }
    }
    private static void copyDirectoryCompatibilityMode(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            copyDirectory(source, destination);
        } else {
            copyFile(source, destination);
        }
    }
}
