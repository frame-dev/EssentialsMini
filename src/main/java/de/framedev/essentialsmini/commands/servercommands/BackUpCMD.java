package de.framedev.essentialsmini.commands.servercommands;

import de.framedev.essentialsmini.main.Main;
import de.framedev.essentialsmini.abstracts.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.apache.commons.io.FileUtils.copyFile;

public class BackUpCMD extends CommandBase {

    private final Main plugin;
    private final boolean makeBackup;

    public BackUpCMD(Main plugin) {
        super(plugin);
        this.plugin = plugin;
        this.makeBackup = plugin.getConfig().getBoolean("WorldBackup");
        setup("backup", this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("backup")) {
            if (sender.hasPermission(plugin.getPermissionName() + "backup")) {
                try {
                    backup();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sender.sendMessage(plugin.getPrefix() + "Backup Created!");
            } else {
                sender.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
            }
        }
        return super.onCommand(sender, command, label, args);
    }

    public void backup() throws IOException {
        List<File> files = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            if (!world.isAutoSave())
                world.setAutoSave(true);
            world.save();
            try {
                File file = new File("Backups/" + new SimpleDateFormat("HH:mm:ss|yyyy.MM.dd").format(new Date(System.currentTimeMillis())) + "/" + world.getName());
                copyDirectory(world.getWorldFolder(), file);
                files.add(new File(file.getParentFile().getName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File pluginDirectory = new File("plugins");
        System.out.println(pluginDirectory.getAbsolutePath());
        System.out.println(pluginDirectory.exists());
        File file = new File("Backups/" + new SimpleDateFormat("HH:mm:ss|yyyy.MM.dd").format(new Date(System.currentTimeMillis())) + "/plugins");
        copyDirectory(pluginDirectory, file);
        files.add(new File(file.getParentFile().getName()));
        String sourceFile = "Backups/" + files.get(0).getName();
        FileOutputStream fos = new FileOutputStream(files.get(0).getName() + ".zip");
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(sourceFile);

        zipFile(fileToZip, fileToZip.getName(), zipOut);
        zipOut.close();
        fos.close();
        deleteDirectory(new File("Backups/" + files.get(0).getName()));
        Files.move(new File(files.get(0).getName() + ".zip").toPath(), new File("Backups/" + files.get(0).getName() + ".zip").toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public void deleteDirectory(File directoryToBeDeleted) throws IOException {
        Files.walk(directoryToBeDeleted.toPath())
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    public Set<File> listFilesUsingDirectoryStream(String dir) throws IOException {
        Set<File> fileList = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    fileList.add(path.toFile());
                }
            }
        }
        return fileList;
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

    public void makeBackups() {
        if (makeBackup) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        backup();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Bukkit.getConsoleSender().sendMessage(plugin.getPrefix() + "Backup Created!");
                }
            }.runTaskTimer(plugin, 0, 20L * 60 * plugin.getConfig().getInt("BackupWorldTime"));
        }
    }

    private static void copyDirectory(File sourceDirectory, File destinationDirectory) throws IOException {
        if (!destinationDirectory.exists()) {
            destinationDirectory.mkdir();
        }
        for (String f : Objects.requireNonNull(sourceDirectory.list())) {
            copyDirectoryCompatibityMode(new File(sourceDirectory, f), new File(destinationDirectory, f));
        }
    }

    public static void copyDirectoryCompatibityMode(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            copyDirectory(source, destination);
        } else {
            copyFile(source, destination);
        }
    }
}
