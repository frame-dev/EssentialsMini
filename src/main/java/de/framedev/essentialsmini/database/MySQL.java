package de.framedev.essentialsmini.database;

import de.framedev.essentialsmini.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Executors;


public class MySQL {

    public static String MySQLPrefix = "§a[§bMySQL§a]";
    public static String host;
    public static String user;
    public static String password;
    public static String database;
    public static String port;
    public static Connection con;

    public MySQL() {
        FileConfiguration cfg = Main.getInstance().getConfig();
        host = cfg.getString("MySQL.Host");
        user = cfg.getString("MySQL.User");
        password = cfg.getString("MySQL.Password");
        database = cfg.getString("MySQL.Database");
        port = cfg.getString("MySQL.Port");
    }

    public static Connection getConnection() {
        if (con == null) {
            close();
            try {
                con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useUnicode=yes&characterEncoding=UTF-8&useSSL=false", user, password);
                con.setNetworkTimeout(Executors.newFixedThreadPool(100), 1000000);
                // con.createStatement().executeUpdate("SET GLOBAL max_connections=1200;");
                return con;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            close();
            try {
                con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useUnicode=yes&characterEncoding=UTF-8&useSSL=false", user, password);
                con.setNetworkTimeout(Executors.newFixedThreadPool(100), 1000000);
                // con.createStatement().executeUpdate("SET GLOBAL max_connections=1200;");
                return con;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return con;
    }

    // connect
    public static void connect() {
        if (con == null) {
            try {
                con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useUnicode=yes&characterEncoding=UTF-8&useSSL=false", user, password);
                con.setNetworkTimeout(Executors.newFixedThreadPool(100), 1000000);
                // con.createStatement().executeUpdate("SET GLOBAL max_connections=1200;");
                Bukkit.getConsoleSender().sendMessage(MySQLPrefix + "-Verbindung wurde aufgebaut!");
            } catch (SQLException e) {
                Bukkit.getConsoleSender().sendMessage(MySQLPrefix + " §cEin Fehler ist aufgetreten: §a" + e.getMessage());
            }
        }
    }

    public static void close() {
        if (con != null) {
            try {
                if (con != null) {
                    con.close();
                }
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
