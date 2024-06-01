package ch.framedev.essentialsmini.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ch.framedev.essentialsmini.main.Main;
import ch.framedev.essentialsmini.abstracts.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminBroadCast implements Serializable {

    private final List<AdminBroadCast> adminBroadCasts = new ArrayList<>();

    private final String command;
    private final String message;
    private final String commandSender;
    private boolean send;

    public AdminBroadCast(String command, String message, CommandSender commandSender) {
        this.command = "/" + command;
        this.message = message;
        this.commandSender = commandSender.getName();
        send();
        save();
    }

    public AdminBroadCast(CommandBase commandBase, String message, CommandSender commandSender) {
        this.command = "/" + commandBase.getCmdNames()[0];
        this.message = message;
        this.commandSender = commandSender.getName();
        send();
        save();
    }

    public AdminBroadCast(CommandBase commandBase, int index, String message, CommandSender commandSender) {
        this.command = "/" + commandBase.getCmdNames()[index];
        this.message = message;
        this.commandSender = commandSender.getName();
        send();
        save();
    }

    /**
     * This will send the Broadcast to the Players with the Permissions
     */
    @SuppressWarnings("deprecation")
    public void send() {
        if (Main.getInstance().getSettingsCfg().getBoolean(Variables.ADMIN_BROADCAST)) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("essentialsmini.adminbroadcast") && !Bukkit.getPlayer(commandSender).hasPermission("essentialsmini.adminbroadcast")) {
                    player.sendMessage(Main.getInstance().getPrefix() + "§6[§aAdmin§bBroadCast§6] §c>> §c[Command] §6" + command + " §c[Message] §b" + message + " §6has been send by §6" + commandSender);
                    send = true;
                }
            }
            // Send to Console
            Bukkit.getConsoleSender().sendMessage(Main.getInstance().getPrefix() + "§6[§aAdmin§bBroadCast§6] §c>> §c[Command] §6" + command + " §c[Message] §b" + message + " §6has been send by §6" + commandSender);
            adminBroadCasts.add(this);
        }
    }

    public boolean wasSent() {
        return send;
    }

    public static List<AdminBroadCast> getAdminBroadCasts() {
        List<AdminBroadCast> broadCastsLoad = new ArrayList<>();
        File file = new File(Main.getInstance().getDataFolder(),"adminbroadcasts.json");
        if(file.exists()) {
            try {
                FileReader fileReader = new FileReader(new File(Main.getInstance().getDataFolder(), "adminbroadcasts.json"));
                broadCastsLoad = Arrays.asList(new Gson().fromJson(fileReader, AdminBroadCast[].class));
                fileReader.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return broadCastsLoad;
    }

    public void save() {
        List<AdminBroadCast> broadCastsLoad = new ArrayList<>();
        try {
            File file = new File(Main.getInstance().getDataFolder(),"adminbroadcasts.json");
            if(file.exists()) {
                FileReader fileReader = new FileReader(new File(Main.getInstance().getDataFolder(), "adminbroadcasts.json"));
                broadCastsLoad = Arrays.asList(new Gson().fromJson(fileReader, AdminBroadCast[].class));
                fileReader.close();
            }
            List<AdminBroadCast> updated = new ArrayList<>();
            updated.add(this);
            updated.addAll(broadCastsLoad);
            FileWriter writer = new FileWriter(file);
            writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(updated));
            writer.flush();
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "AdminBroadCast{" +
                "command='" + command + '\'' +
                ", message='" + message + '\'' +
                ", commandSender='" + commandSender + '\'' +
                ", send=" + send +
                '}';
    }
}
