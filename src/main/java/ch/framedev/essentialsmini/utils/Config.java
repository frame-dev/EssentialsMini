package ch.framedev.essentialsmini.utils;

import ch.framedev.essentialsmini.main.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;


public class Config {
    public static void loadConfig() {
        Main.getInstance().getConfig().options().parseComments(true);
        Objects.requireNonNull(Main.getInstance().getConfig().getDefaults()).options().copyDefaults(true);
        Main.getInstance().getConfig().options().copyDefaults(true);
        Main.getInstance().saveDefaultConfig();
    }

    public static void updateConfig() {
        try {
            if ((new File(Main.getInstance().getDataFolder() + "/config.yml")).exists()) {
                boolean changesMade = false;
                YamlConfiguration tmp = new YamlConfiguration();
                tmp.load(Main.getInstance().getDataFolder() + "/config.yml");
                for (String str : Main.getInstance().getConfig().getKeys(true)) {
                    if (!tmp.getKeys(true).contains(str)) {
                        tmp.set(str, Main.getInstance().getConfig().get(str));
                        changesMade = true;
                        tmp.save(Main.getInstance().getDataFolder() + "/config.yml");
                        tmp.load(Main.getInstance().getDataFolder() + "/config.yml");
                    }
                }
                if (changesMade) {

                    tmp.save(Main.getInstance().getDataFolder() + "/config.yml");
                    tmp.load(Main.getInstance().getDataFolder() + "/config.yml");
                }
            }
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
            Main.getInstance().getLogger4J().error(e);
        }
    }

    public static void saveDefaultConfigValues(String fileName) {
        File file = new File(Main.getInstance().getDataFolder(), fileName + ".yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        //Defaults in jar
        Reader defConfigStream;
        defConfigStream = new InputStreamReader(Objects.requireNonNull(Main.getInstance().getResource(fileName + ".yml")), StandardCharsets.UTF_8);
        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
        cfg.setDefaults(defConfig);
        //Copy default values
        cfg.options().copyDefaults(true);
        try {
            cfg.save(file);
        } catch (IOException e) {
            Main.getInstance().getLogger4J().error(e);
        }
        Main.getInstance().saveConfig();
        //OR use this to copy default values
        //this.saveDefaultConfig();
    }

    public static void saveDefaultConfigValues() {
        File file = new File(Main.getInstance().getDataFolder(), "config.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        //Defaults in jar
        Reader defConfigStream;
        defConfigStream = new InputStreamReader(Objects.requireNonNull(Main.getInstance().getResource("config.yml")), StandardCharsets.UTF_8);
        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
        cfg.setDefaults(defConfig);
        cfg.options().copyDefaults(true);
        Main.getInstance().saveDefaultConfig();
    }
}


