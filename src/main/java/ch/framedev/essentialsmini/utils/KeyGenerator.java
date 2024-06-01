/**
 * Dies ist ein Plugin von FrameDev
 * Bitte nichts ändern, @Copyright by FrameDev
 */
package ch.framedev.essentialsmini.utils;

import ch.framedev.essentialsmini.main.Main;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KeyGenerator {

    private static final Main plugin = Main.getInstance();

    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String OTHER_CHAR = "!@#$%&*()_+-=[]?";

    private static final String STRING_ALLOW_BASE = CHAR_LOWER + CHAR_UPPER + NUMBER + OTHER_CHAR;

    // optional, make it more random
    private static final String STRING_ALLOW_BASE_SHUFFLE = shuffleString(STRING_ALLOW_BASE);
    private static final String STRING_ALLOW = STRING_ALLOW_BASE_SHUFFLE;

    private static final SecureRandom random = new SecureRandom();

    private final File file = new File(Main.getInstance().getDataFolder(), "keys.yml");
    private final FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

    public static String generatorString(int length) {
        if (length < 1)
            throw new IllegalArgumentException();

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {

            int rndCharAt = random.nextInt(STRING_ALLOW.length());
            char rndChar = STRING_ALLOW.charAt(rndCharAt);
            sb.append(rndChar);
        }
        return sb.toString();
    }

    // shuffle
    public static String shuffleString(String string) {
        List<String> letters = Arrays.asList(string.split(""));
        Collections.shuffle(letters);
        return String.join("", letters);
    }

    public String generateKeyAndSave(OfflinePlayer player) {
        String key = generatorString(32);
        if (player.getName() == null) return "";
        cfg.set(player.getName(), key);
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return key;
    }

    public boolean hasPlayerKey(OfflinePlayer player) {
        if (player.getName() == null) return false;
        return cfg.contains(player.getName());
    }

    /**
     * @param player the Player
     * @return the Key fromPlayer
     * @throws NullPointerException return NullPointerException if Player is Null
     */
    public String getKey(OfflinePlayer player) throws NullPointerException {
        if (player.getName() == null) return "";
        return cfg.getString(player.getName());
    }

    public void removeBetaKey(OfflinePlayer player) {
        if (player.getName() == null) return;
        cfg.set(player.getName(), null);
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadCfg() {
        try {
            cfg.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
