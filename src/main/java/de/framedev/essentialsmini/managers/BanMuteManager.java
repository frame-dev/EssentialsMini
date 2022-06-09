package de.framedev.essentialsmini.managers;

import de.framedev.essentialsmini.commands.playercommands.BanCMD;
import de.framedev.essentialsmini.commands.playercommands.MuteCMD;
import de.framedev.essentialsmini.commands.playercommands.TempBanCMD;
import de.framedev.essentialsmini.database.MySQL;
import de.framedev.essentialsmini.database.SQL;
import de.framedev.essentialsmini.main.Main;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * / This Plugin was Created by FrameDev
 * / Package : de.framedev.essentialsmini.managers
 * / ClassName BanMuteManager
 * / Date: 03.06.21
 * / Project: EssentialsMini
 * / Copyrighted by FrameDev
 */

public class BanMuteManager {

    // Database for MySQl / SQLite
    private final String table = "essentialsmini_banmute";

    /**
     * Set TempMute to Database for Syncing
     *
     * @param player the Player
     * @param reason Reason {@link MuteCMD.MuteReason}
     * @param date   the actual date
     */
    public void setTempMute(OfflinePlayer player, MuteCMD.MuteReason reason, String date) {
        if (SQL.isTableExists(table)) {
            if (SQL.exists(table, "Player", player.getName())) {
                SQL.updateData(table, "TempMute", "'" + date + "'", "Player = '" + player.getName() + "'");
                SQL.updateData(table, "TempMuteReason", "'" + reason.getReason() + "'", "Player = '" + player.getName() + "'");
            } else {
                SQL.insertData(table, "'" + player.getName() + "','" + date + "','" + reason.getReason() + "'", "Player", "TempMute", "TempMuteReason");
            }
        } else {
            SQL.createTable(table, "Player VARCHAR(1255)", "TempMute TEXT", "TempMuteReason TEXT", "TempBan TEXT", "TempBanReason TEXT", "Ban TEXT", "BanReason TEXT");
            SQL.insertData(table, "'" + player.getName() + "','" + date + "','" + reason.getReason() + "'", "Player", "TempMute", "TempMuteReason");
        }
    }

    /**
     * Set TempMute to Database for Syncing
     *
     * @param player the Player
     * @param reason the Reason as String
     * @param date   the actual date
     */
    public void setTempMute(OfflinePlayer player, String reason, String date) {
        if (SQL.isTableExists(table)) {
            if (SQL.exists(table, "Player", player.getName())) {
                SQL.updateData(table, "TempMute", "'" + date + "'", "Player = '" + player.getName() + "'");
                SQL.updateData(table, "TempMuteReason", "'" + reason + "'", "Player = '" + player.getName() + "'");
            } else {
                SQL.insertData(table, "'" + player.getName() + "','" + date + "','" + reason + "'", "Player", "TempMute", "TempMuteReason");
            }
        } else {
            SQL.createTable(table, "Player VARCHAR(1255)", "TempMute TEXT", "TempMuteReason TEXT", "TempBan TEXT", "TempBanReason TEXT", "Ban TEXT", "BanReason TEXT");
            SQL.insertData(table, "'" + player.getName() + "','" + date + "','" + reason + "'", "Player", "TempMute", "TempMuteReason");
        }
    }

    /**
     * Remove the Registered TempMute in the Database
     *
     * @param player the selected Player for removing the tempmute
     */
    public void removeTempMute(OfflinePlayer player) {
        if (SQL.isTableExists(table)) {
            if (SQL.exists(table, "Player", player.getName())) {
                SQL.updateData(table, "TempMute", "'" + " " + "'", "Player = '" + player.getName() + "'");
                SQL.updateData(table, "TempMuteReason", "'" + " " + "'", "Player = '" + player.getName() + "'");
            }
        }
    }

    /**
     * Get the Data for the TempMute by the Player
     *
     * @param player the selected Player
     * @return return an HashMap of the Expire Date and the Reason
     */
    public HashMap<String, String> getTempMute(OfflinePlayer player) {
        HashMap<String, String> tempMute = new HashMap<>();
        if (SQL.isTableExists(table)) {
            if (SQL.exists(table, "Player", player.getName())) {
                if (SQL.get(table, "TempMute", "Player", player.getName()) != null) {
                    tempMute.put((String) SQL.get(table, "TempMute", "Player", player.getName()), (String) SQL.get(table, "TempMuteReason", "Player", player.getName()));
                    return tempMute;
                }
            }
        }
        return null;
    }

    /**
     * Return if the selected Player is TempMuted
     *
     * @param player the selected Player
     * @return return if the selected Player is TempMuted
     */
    public boolean isTempMute(OfflinePlayer player) {
        if (SQL.isTableExists(table)) {
            if (SQL.exists(table, "Player", player.getName())) {
                if (SQL.get(table, "TempMute", "Player", player.getName()) != null) {
                    return !((String) SQL.get(table, "TempMute", "Player", player.getName())).equalsIgnoreCase(" ");
                }
            }
        }
        return false;
    }

    public void setTempBan(OfflinePlayer player, TempBanCMD.Ban reason, String date) {
        if (SQL.isTableExists(table)) {
            if (SQL.exists(table, "Player", player.getName())) {
                SQL.updateData(table, "TempBan", "'" + date + "'", "Player = '" + player.getName() + "'");
                SQL.updateData(table, "TempBanReason", "'" + reason.getReason() + "'", "Player = '" + player.getName() + "'");
            } else {
                SQL.insertData(table, "'" + player.getName() + "','" + date + "','" + reason.getReason() + "'", "Player", "TempBan", "TempBanReason");
            }
        } else {
            SQL.createTable(table, "Player VARCHAR(1255)", "TempMute TEXT", "TempMuteReason TEXT", "TempBan TEXT", "TempBanReason TEXT", "Ban TEXT", "BanReason TEXT");
            SQL.insertData(table, "'" + player.getName() + "','" + date + "','" + reason.getReason() + "'", "Player", "TempBan", "TempBanReason");
        }
    }

    public void setTempBan(OfflinePlayer player, String reason, String date) {
        if (SQL.isTableExists(table)) {
            if (SQL.exists(table, "Player", player.getName())) {
                SQL.updateData(table, "TempBan", "'" + date + "'", "Player = '" + player.getName() + "'");
                SQL.updateData(table, "TempBanReason", "'" + reason + "'", "Player = '" + player.getName() + "'");
            } else {
                SQL.insertData(table, "'" + player.getName() + "','" + date + "','" + reason + "'", "Player", "TempBan", "TempBanReason");
            }
        } else {
            SQL.createTable(table, "Player VARCHAR(1255)", "TempMute TEXT", "TempMuteReason TEXT", "TempBan TEXT", "TempBanReason TEXT", "Ban TEXT", "BanReason TEXT");
            SQL.insertData(table, "'" + player.getName() + "','" + date + "','" + reason + "'", "Player", "TempBan", "TempBanReason");
        }
    }

    public void removeTempBan(OfflinePlayer player) {
        if (player.getName() == null) return;
        if (SQL.isTableExists(table)) {
            if (SQL.exists(table, "Player", player.getName())) {
                Bukkit.getServer().getBanList(BanList.Type.NAME).pardon(player.getName());
                SQL.updateData(table, "TempBan", "'" + " " + "'", "Player = '" + player.getName() + "'");
                SQL.updateData(table, "TempBanReason", "'" + " " + "'", "Player = '" + player.getName() + "'");
            }
        }
    }

    public HashMap<String, String> getTempBan(OfflinePlayer player) {
        HashMap<String, String> tempBan = new HashMap<>();
        if (SQL.isTableExists(table)) {
            if (SQL.exists(table, "Player", player.getName())) {
                if (SQL.get(table, "TempBan", "Player", player.getName()) != null) {
                    tempBan.put((String) SQL.get(table, "TempBan", "Player", player.getName()), (String) SQL.get(table, "TempBanReason", "Player", player.getName()));
                    return tempBan;
                }
            }
        }
        return null;
    }

    public boolean isExpiredTempBan(OfflinePlayer player) {
        if (Main.getInstance().isMysql() || Main.getInstance().isSQL()) {
            if (new BanMuteManager().isTempBan(player)) {
                final Date[] date = {new Date()};
                new BanMuteManager().getTempBan(player).forEach((s, s2) -> {
                    try {
                        date[0] = new SimpleDateFormat("dd.MM.yyyy | HH:mm:ss").parse(s);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                });
                if (date[0] != null)
                    return date[0].getTime() < System.currentTimeMillis();
            } else {
                return true;
            }
        } else {
            if (BanFile.cfg.contains(player.getName() + ".reason")) {
                Date date = (Date) BanFile.cfg.get(player.getName() + ".expire");
                if (date != null)
                    return date.getTime() < System.currentTimeMillis();
            }
        }
        return true;
    }

    public boolean isTempBan(OfflinePlayer player) {
        if (SQL.isTableExists(table)) {
            if (SQL.exists(table, "Player", player.getName())) {
                if (SQL.get(table, "TempBan", "Player", player.getName()) != null) {
                    return !((String) SQL.get(table, "TempBan", "Player", player.getName())).equalsIgnoreCase(" ");
                }
            }
        }
        return false;
    }

    public void setPermaBan(OfflinePlayer player, BanCMD.BanType reason, boolean permaBan) {
        if (SQL.isTableExists(table)) {
            if (SQL.exists(table, "Player", player.getName())) {
                SQL.updateData(table, "Ban", "'" + permaBan + "'", "Player = '" + player.getName() + "'");
                SQL.updateData(table, "BanReason", "'" + reason.getReason() + "'", "Player = '" + player.getName() + "'");
            } else {
                SQL.insertData(table, "'" + player.getName() + "','" + permaBan + "','" + reason.getReason() + "'", "Player", "Ban", "BanReason");
            }
        } else {
            SQL.createTable(table, "Player VARCHAR(1255)", "TempMute TEXT", "TempMuteReason TEXT", "TempBan TEXT", "TempBanReason TEXT", "Ban TEXT", "BanReason TEXT");
            SQL.insertData(table, "'" + player.getName() + "','" + permaBan + "','" + reason.getReason() + "'", "Player", "Ban", "BanReason");
        }
    }

    public void setPermaBan(OfflinePlayer player, String reason, boolean permaBan) {
        if (SQL.isTableExists(table)) {
            if (SQL.exists(table, "Player", player.getName())) {
                SQL.updateData(table, "Ban", "'" + permaBan + "'", "Player = '" + player.getName() + "'");
                SQL.updateData(table, "BanReason", "'" + reason + "'", "Player = '" + player.getName() + "'");
            } else {
                SQL.insertData(table, "'" + player.getName() + "','" + permaBan + "','" + reason + "'", "Player", "Ban", "BanReason");
            }
        } else {
            SQL.createTable(table, "Player VARCHAR(1255)", "TempMute TEXT", "TempMuteReason TEXT", "TempBan TEXT", "TempBanReason TEXT", "Ban TEXT", "BanReason TEXT");
            SQL.insertData(table, "'" + player.getName() + "','" + permaBan + "','" + reason + "'", "Player", "Ban", "BanReason");
        }
    }

    public boolean isPermaBan(OfflinePlayer player) {
        if (SQL.isTableExists(table)) {
            if (SQL.exists(table, "Player", player.getName())) {
                if (SQL.get(table, "Ban", "Player", player.getName()) != null)
                    return Boolean.parseBoolean((String) SQL.get(table, "Ban", "Player", player.getName()));
            }
        }
        return false;
    }

    public String getPermaBanReason(OfflinePlayer player) {
        if (SQL.isTableExists(table)) {
            if (SQL.exists(table, "Player", player.getName())) {
                if (SQL.get(table, "BanReason", "Player", player.getName()) != null)
                    return (String) SQL.get(table, "BanReason", "Player", player.getName());
            }
        }
        return "";
    }

    public List<String> getAllBannedPlayers() {
        List<String> playerNames = new ArrayList<>();
        if (SQL.isTableExists(table)) {
            try {
                Statement statement = MySQL.getConnection().createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM " + table);
                while (resultSet.next()) {
                    if (resultSet.getString("Player") != null && isPermaBan(Bukkit.getOfflinePlayer(resultSet.getString("Player"))))
                        playerNames.add(resultSet.getString("Player"));
                }
            } catch (Exception ex) {

            }
        }
        return playerNames;
    }

    public List<String> getAllTempBannedPlayers() {
        List<String> playerNames = new ArrayList<>();
        if (SQL.isTableExists(table)) {
            try {
                Statement statement = MySQL.getConnection().createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM " + table);
                while (resultSet.next()) {
                    if (resultSet.getString("Player") != null && isTempBan(Bukkit.getOfflinePlayer(resultSet.getString("Player"))) && !isExpiredTempBan(Bukkit.getOfflinePlayer(resultSet.getString("Player"))))
                        playerNames.add(resultSet.getString("Player"));
                }
            } catch (Exception ex) {

            }
        }
        return playerNames;
    }
}
