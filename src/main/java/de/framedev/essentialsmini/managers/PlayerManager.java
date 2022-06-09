package de.framedev.essentialsmini.managers;

import com.google.gson.Gson;
import de.framedev.essentialsmini.database.SQL;
import de.framedev.essentialsmini.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 25.07.2020 22:51
 */
public class PlayerManager implements Serializable {

    transient private final File file;
    transient FileConfiguration cfg;
    private final String name;
    private final UUID uuid;
    transient private final OfflinePlayer player;
    private Long lastLogin;
    private Long lastLogout;
    private double damage;
    private int kills;
    private int entityKills;
    private int deaths;
    private int sleepTimes;
    private int blockBroken;
    private int blockPlace;
    private ArrayList<Material> blocksBroken = new ArrayList<>();
    private ArrayList<Material> blocksPlace = new ArrayList<>();
    private ArrayList<EntityType> entityTypes = new ArrayList<>();
    private int commandsUsed;
    private int crafted;

    public PlayerManager(UUID uuid) {
        this.uuid = uuid;
        this.name = Bukkit.getOfflinePlayer(uuid).getName();
        file = new File(Main.getInstance().getDataFolder() + "/PlayerData", uuid.toString() + ".yml");
        cfg = YamlConfiguration.loadConfiguration(file);
        if (file.exists()) {
            try {
                cfg.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
        cfg.set("Name", Bukkit.getOfflinePlayer(uuid).getName());
        cfg.set("UUID", uuid.toString());
        this.player = Bukkit.getOfflinePlayer(uuid);
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PlayerManager(OfflinePlayer player) {
        this.name = player.getName();
        this.uuid = player.getUniqueId();
        file = new File(Main.getInstance().getDataFolder() + "/PlayerData", uuid.toString() + ".yml");
        cfg = YamlConfiguration.loadConfiguration(file);
        if (file.exists()) {
            try {
                cfg.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
        cfg.set("Name", name);
        cfg.set("UUID", uuid.toString());
        this.player = player;
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public PlayerManager(String name) {
        this.name = name;
        this.uuid = Bukkit.getOfflinePlayer(name).getUniqueId();
        file = new File(Main.getInstance().getDataFolder() + "/PlayerData", Bukkit.getOfflinePlayer(name).getUniqueId().toString() + ".yml");
        cfg = YamlConfiguration.loadConfiguration(file);
        if (file.exists()) {
            try {
                cfg.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
        this.player = Bukkit.getOfflinePlayer(uuid);
        cfg.set("Name", name);
        cfg.set("UUID", uuid.toString());
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean kill() {
        if (player.isOnline()) {
            Player onPlayer = (Player) player;
            onPlayer.setHealth(0);
            return true;
        }
        return false;
    }

    public void setCrafted(int crafted) {
        cfg.set("Crafted", crafted);
        if (!saveConfig()) {
            System.err.println("Cannot Save PlayerData");
        }
        this.crafted = crafted;
    }

    public int getCrafted() {
        if (cfg.contains("Crafted"))
            return cfg.getInt("Crafted");
        return crafted;
    }

    public void addCrafted() {
        int craft = getCrafted();
        craft++;
        setCrafted(craft);
    }

    public void setStatistic(Statistic statistic, int i) {
        player.setStatistic(statistic, i);
    }

    public void setStatistic(Statistic statistic, EntityType type, int i) {
        player.setStatistic(statistic, type, i);
    }

    public void setStatistic(Statistic statistic, Material material, int i) {
        player.setStatistic(statistic, material, i);
    }

    public void setEntityTypes(ArrayList<EntityType> entityTypes) {
        this.entityTypes = entityTypes;
        ArrayList<String> names = new ArrayList<>();
        for (EntityType material : entityTypes) {
            names.add(material.name());
        }
        cfg.set("EntityTypesKilled", names);
        saveConfig();
    }

    public ArrayList<EntityType> getEntityTypes() {
        if (cfg.contains("EntityTypesKilled")) {
            List<String> typeNames = cfg.getStringList("EntityTypesKilled");
            ArrayList<EntityType> mats = new ArrayList<>();
            for (String s : typeNames) {
                mats.add(EntityType.fromName(s));
            }
            this.entityTypes = mats;
        }
        return entityTypes;
    }

    public void addEntityType(EntityType entityType) {
        this.entityTypes = getEntityTypes();
        if (!entityTypes.contains(entityType))
            entityTypes.add(entityType);
        setEntityTypes(entityTypes);
    }

    public int getCommandsUsed() {
        if (cfg.contains("CommandsUsed") && cfg.isInt("CommandsUsed")) {
            return cfg.getInt("CommandsUsed");
        }
        return commandsUsed;
    }

    public void setCommandsUsed(int commandsUsed) {
        this.commandsUsed = commandsUsed;
        cfg.set("CommandsUsed", commandsUsed);
        saveConfig();
    }

    public void addCommandsUsed() {
        int commands = getCommandsUsed();
        commands++;
        setCommandsUsed(commands);
    }

    public ArrayList<Material> getBlocksPlace() {
        if (cfg.contains("BlocksPlace")) {
            List<String> typeNames = cfg.getStringList("BlocksPlace");
            ArrayList<Material> mats = new ArrayList<>();
            for (String s : typeNames) {
                mats.add(Material.getMaterial(s));
            }
            this.blocksPlace = mats;
        }
        return blocksPlace;
    }

    public void setBlocksPlace(ArrayList<Material> blocksPlace) {
        this.blocksPlace = blocksPlace;
        ArrayList<String> names = new ArrayList<>();
        for (Material material : blocksPlace) {
            names.add(material.name());
        }
        cfg.set("BlocksPlace", names);
        saveConfig();
    }

    public ArrayList<Material> getBlocksBroken() {
        if (cfg.contains("BlocksBroken")) {
            List<String> typeNames = cfg.getStringList("BlocksBroken");
            ArrayList<Material> mats = new ArrayList<>();
            for (String s : typeNames) {
                mats.add(Material.getMaterial(s));
            }
            this.blocksBroken = mats;
        }
        return blocksBroken;
    }

    public void addBlocksPlace(Material material) {
        this.blocksPlace = getBlocksPlace();
        if (!blocksPlace.contains(material)) {
            getBlocksPlace().add(material);
            setBlocksPlace(blocksPlace);
        }
    }

    public void setBlocksBroken(ArrayList<Material> blocksBroken) {
        this.blocksBroken = blocksBroken;
        ArrayList<String> names = new ArrayList<>();
        for (Material material : blocksBroken) {
            names.add(material.name());
        }
        cfg.set("BlocksBroken", names);
        saveConfig();
    }

    public void addBlocksBroken(Material material) {
        this.blocksBroken = getBlocksBroken();
        if (!blocksBroken.contains(material)) {
            blocksBroken.add(material);
            setBlocksBroken(blocksBroken);
        }
    }

    public int getBlockPlace() {
        if (cfg.contains("BlockPlace") && cfg.isInt("BlockPlace")) {
            return cfg.getInt("BlockPlace");
        }
        return blockPlace;
    }

    public void setBlockPlace(int blockPlace) {
        this.blockPlace = blockPlace;
        cfg.set("BlockPlace", blockPlace);
        saveConfig();
    }

    public void addBlockPlace() {
        int blockPlace = getBlockPlace();
        blockPlace++;
        setBlockPlace(blockPlace);
    }

    public int getBlockBroken() {
        if (cfg.contains("BlockBroken") && cfg.isInt("BlockBroken")) {
            return cfg.getInt("BlockBroken");
        }
        return blockBroken;
    }

    public void setBlockBroken(int blockBroken) {
        this.blockBroken = blockBroken;
        cfg.set("BlockBroken", blockBroken);
        saveConfig();
    }

    public void addBlockBroken() {
        int blockBroken = getBlockBroken();
        blockBroken++;
        setBlockBroken(blockBroken);
    }

    public int getSleepTimes() {
        if (cfg.contains("SleepTimes") && cfg.isInt("SleepTimes")) {
            return cfg.getInt("SleepTimes");
        }
        return sleepTimes;
    }

    public void setSleepTimes(int sleepTimes) {
        this.sleepTimes = sleepTimes;
        cfg.set("SleepTimes", sleepTimes);
        saveConfig();
    }

    public void addSleepTimes() {
        int sleep = getSleepTimes();
        sleep++;
        setSleepTimes(sleep);
    }

    private double round(double value, int decimalPoints) {
        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setMaximumFractionDigits(decimalPoints);
        return Double.parseDouble(formatter.format(value));
    }

    public String getName() {
        if (name != null) {
            return name;
        } else {
            return cfg.getString("Name");
        }
    }

    public UUID getUuid() {
        if (uuid != null) {
            return uuid;
        } else {
            return UUID.fromString(Objects.requireNonNull(cfg.getString("UUID")));
        }
    }

    public void setLastLogin(Long lastlogin) {
        this.lastLogin = lastlogin;
        cfg.set("LastLogin", lastlogin);
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLastLogout(Long lastLogout) {
        this.lastLogout = lastLogout;
        cfg.set("LastLogout", lastLogout);
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Long getLastLogin() {
        if (cfg.contains("LastLogin") && cfg.isLong("LastLogin")) {
            return cfg.getLong("LastLogin");
        } else {
            if (lastLogin != null) {
                return lastLogin;
            } else {
                return 0L;
            }
        }
    }

    public Long getLastLogout() {
        if (cfg.contains("LastLogout") && cfg.isLong("LastLogout")) {
            return cfg.getLong("LastLogout");
        } else {
            if (lastLogin != null) {
                return lastLogout;
            } else {
                return 0L;
            }
        }
    }

    public void saveTimePlayed() {
        cfg.set("TimePlayed", getTimePlayed());
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public String getHoursPlayed() {
        long ticks = Bukkit.getOfflinePlayer(uuid).getStatistic(Statistic.PLAY_ONE_MINUTE);
        long seconds = ticks / 20 * 1000;
        Date date = new Date();
        date.setDate(0);
        date.setTime(seconds);
        String time = new SimpleDateFormat("HH").format(date);
        return time;
    }

    @Deprecated
    public String getTimePlayed() {
        long ticks = Bukkit.getOfflinePlayer(uuid).getStatistic(Statistic.PLAY_ONE_MINUTE);
        long seconds = ticks / 20 * 1000;
        Date date = new Date();
        date.setDate(0);
        date.setTime(seconds);
        String time = new SimpleDateFormat("D | HH:mm:ss").format(date);
        return time;
    }

    public void setDamage(double damage) {
        this.damage = damage;
        cfg.set("Damage", damage);
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addDamage(double amount) {
        double damage = getDamage();
        damage = damage + amount;
        setDamage(damage);
    }

    public double getDamage() {
        if (cfg.contains("Damage") && cfg.isDouble("Damage")) {
            return round(cfg.getDouble("Damage"), 3);
        } else {
            return damage;
        }
    }

    public void setPlayerKills(int kills) {
        this.kills = kills;
        cfg.set("PlayerKills", kills);
        if (!saveConfig()) {
            System.err.println("Cannot Save PlayerData");
        }
    }

    public int getPlayerKills() {
        if (cfg.contains("PlayerKills") || cfg.isInt("PlayerKills")) {
            return cfg.getInt("PlayerKills");
        }
        return kills;
    }

    public void addPlayerKill() {
        int kill = getPlayerKills();
        kill++;
        setPlayerKills(kill);
    }

    public void setEntityKills(int entityKills) {
        this.entityKills = entityKills;
        cfg.set("EntityKills", entityKills);
        if (!saveConfig()) {
            System.err.println("Cannot Save PlayerData");
        }
    }

    public int getEntityKills() {
        if (cfg.contains("EntityKills") || cfg.isInt("EntityKills")) {
            return cfg.getInt("EntityKills");
        }
        return entityKills;
    }

    public void addEntityKill() {
        int entityKills = getEntityKills();
        entityKills++;
        setEntityKills(entityKills);
    }

    public void setDeath(int death) {
        this.deaths = death;
        cfg.set("Deaths", death);
        if (!saveConfig()) {
            System.err.println("Cannot Save PlayerData");
        }
    }

    public int getDeaths() {
        if (cfg.contains("Deaths") || cfg.isInt("Deaths")) {
            return cfg.getInt("Deaths");
        }
        return deaths;
    }

    public void addDeath() {
        int deaths = getDeaths();
        deaths++;
        setDeath(deaths);
    }

    private boolean saveConfig() {
        try {
            cfg.save(file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    public void savePlayerData(OfflinePlayer player) {
        if (Bukkit.getServer().getOnlineMode()) {
            if (SQL.exists(Main.getInstance().getName().toLowerCase() + "_data", "playeruuid", player.getUniqueId() + "")) {
                SQL.updateData(Main.getInstance().getName().toLowerCase() + "_data", "sleeptimes", "'" + getSleepTimes() + "'", "playeruuid = '" + player.getUniqueId() + "'");
                SQL.updateData(Main.getInstance().getName().toLowerCase() + "_data", "damage", "'" + getDamage() + "'", "playeruuid = '" + player.getUniqueId() + "'");
                SQL.updateData(Main.getInstance().getName().toLowerCase() + "_data", "playerkills", "'" + getPlayerKills() + "'", "playeruuid = '" + player.getUniqueId() + "'");
                SQL.updateData(Main.getInstance().getName().toLowerCase() + "_data", "entitykills", "'" + getEntityKills() + "'", "playeruuid = '" + player.getUniqueId() + "'");
                SQL.updateData(Main.getInstance().getName().toLowerCase() + "_data", "deaths", "'" + getDeaths() + "'", "playeruuid = '" + player.getUniqueId() + "'");
                SQL.updateData(Main.getInstance().getName().toLowerCase() + "_data", "blocksbroken", "'" + getBlockBroken() + "'", "playeruuid = '" + player.getUniqueId() + "'");
                SQL.updateData(Main.getInstance().getName().toLowerCase() + "_data", "blocksplacen", "'" + getBlockPlace() + "'", "playeruuid = '" + player.getUniqueId() + "'");
                SQL.updateData(Main.getInstance().getName().toLowerCase() + "_data", "lastlogin", "'" + getLastLogin() + "'", "playeruuid = '" + player.getUniqueId() + "'");
                SQL.updateData(Main.getInstance().getName().toLowerCase() + "_data", "lastlogout", "'" + getLastLogout() + "'", "playeruuid = '" + player.getUniqueId() + "'");
                SQL.updateData(Main.getInstance().getName().toLowerCase() + "_data", "commandsused", "'" + getCommandsUsed() + "'", "playeruuid = '" + player.getUniqueId() + "'");
                SQL.updateData(Main.getInstance().getName().toLowerCase() + "_data", "blocksBrokenList", "'" + new Gson().toJson(getBlocksBroken()) + "'", "playeruuid = '" + player.getUniqueId() + "'");
                SQL.updateData(Main.getInstance().getName().toLowerCase() + "_data", "blocksPlacenList", "'" + new Gson().toJson(getBlocksPlace()) + "'", "playeruuid = '" + player.getUniqueId() + "'");
                SQL.updateData(Main.getInstance().getName().toLowerCase() + "_data", "entityTypes", "'" + new Gson().toJson(getEntityTypes()) + "'", "playeruuid = '" + player.getUniqueId() + "'");
            } else {
                SQL.insertData(Main.getInstance().getName().toLowerCase() + "_data", "'" + player.getUniqueId() + "','" + getSleepTimes() + "','" + getDamage() + "','" + getPlayerKills() + "'," +
                                "'" + getEntityKills() + "','" + getDeaths() + "','" + getBlockBroken() + "','" + getBlockPlace() + "','" +
                                getLastLogin() + "','" + getLastLogout() + "','" + getCommandsUsed() + "','" + new Gson().toJson(getBlocksBroken()) + "','" + new Gson().toJson(getBlocksPlace()) + "','" + new Gson().toJson(getEntityTypes()) + "'",
                        "playeruuid", "sleeptimes", "damage", "playerkills", "entitykills", "deaths", "blocksbroken", "blocksplacen", "lastlogin", "lastlogout", "commandsused", "blocksBrokenList", "blocksPlacenList", "entityTypes");
            }
        } else {
            if (SQL.exists(Main.getInstance().getName().toLowerCase() + "_data", "playername", player.getName() + "")) {
                SQL.updateData(Main.getInstance().getName().toLowerCase() + "_data", "sleeptimes", "'" + getSleepTimes() + "'", "playername = '" + player.getName() + "'");
                SQL.updateData(Main.getInstance().getName().toLowerCase() + "_data", "damage", "'" + getDamage() + "'", "playername = '" + player.getName() + "'");
                SQL.updateData(Main.getInstance().getName().toLowerCase() + "_data", "playerkills", "'" + getPlayerKills() + "'", "playername = '" + player.getName() + "'");
                SQL.updateData(Main.getInstance().getName().toLowerCase() + "_data", "entitykills", "'" + getEntityKills() + "'", "playername = '" + player.getName() + "'");
                SQL.updateData(Main.getInstance().getName().toLowerCase() + "_data", "deaths", "'" + getDeaths() + "'", "playername = '" + player.getName() + "'");
                SQL.updateData(Main.getInstance().getName().toLowerCase() + "_data", "blocksbroken", "'" + getBlockBroken() + "'", "playername = '" + player.getName() + "'");
                SQL.updateData(Main.getInstance().getName().toLowerCase() + "_data", "blocksplacen", "'" + getBlockPlace() + "'", "playername = '" + player.getName() + "'");
                SQL.updateData(Main.getInstance().getName().toLowerCase() + "_data", "lastlogin", "'" + getLastLogin() + "'", "playername = '" + player.getName() + "'");
                SQL.updateData(Main.getInstance().getName().toLowerCase() + "_data", "lastlogout", "'" + getLastLogout() + "'", "playername = '" + player.getName() + "'");
                SQL.updateData(Main.getInstance().getName().toLowerCase() + "_data", "commandsused", "'" + getCommandsUsed() + "'", "playername = '" + player.getName() + "'");
                SQL.updateData(Main.getInstance().getName().toLowerCase() + "_data", "blocksBrokenList", "'" + new Gson().toJson(getBlocksBroken()) + "'", "playername = '" + player.getName() + "'");
                SQL.updateData(Main.getInstance().getName().toLowerCase() + "_data", "blocksPlacenList", "'" + new Gson().toJson(getBlocksPlace()) + "'", "playername = '" + player.getName() + "'");
                SQL.updateData(Main.getInstance().getName().toLowerCase() + "_data", "entityTypes", "'" + new Gson().toJson(getEntityTypes()) + "'", "playername = '" + player.getName() + "'");
            } else {
                SQL.insertData(Main.getInstance().getName().toLowerCase() + "_data", "'" + player.getName() + "','" + getSleepTimes() + "','" + getDamage() + "','" + getPlayerKills() + "','" + getEntityKills() + "'," +
                                "'" + getDeaths() + "','" + getBlockBroken() + "','" + getBlockPlace() + "','" +
                                getLastLogin() + "','" + getLastLogout() + "','" + getCommandsUsed() + "','" + new Gson().toJson(getBlocksBroken()) + "','" + new Gson().toJson(getBlocksPlace()) + "','" + new Gson().toJson(getEntityTypes() + "'"),
                        "playeruuid", "sleeptimes", "damage", "playerkills", "entitykills", "deaths", "blocksbroken", "blocksplacen", "lastlogin", "lastlogout", "commandsused", "blocksBrokenList", "blocksPlacenList", "entityTypes");
            }
        }
    }

    public static PlayerManager loadPlayerData(OfflinePlayer player) {
        PlayerManager playerManager = null;
        if (Bukkit.getServer().getOnlineMode()) {
            if (SQL.exists(Main.getInstance().getName().toLowerCase() + "_data", "playeruuid", player.getUniqueId() + "")) {
                playerManager = new PlayerManager(player.getUniqueId());
                int level = (int) SQL.get(Main.getInstance().getName().toLowerCase() + "_data", "sleeptimes", "playeruuid", player.getUniqueId() + "");
                double damage = (double) SQL.get(Main.getInstance().getName().toLowerCase() + "_data", "damage", "playeruuid", player.getUniqueId() + "");
                int playerkills = (int) SQL.get(Main.getInstance().getName().toLowerCase() + "_data", "playerkills", "playeruuid", player.getUniqueId() + "");
                int entitykills = (int) SQL.get(Main.getInstance().getName().toLowerCase() + "_data", "entitykills", "playeruuid", player.getUniqueId() + "");
                int deaths = (int) SQL.get(Main.getInstance().getName().toLowerCase() + "_data", "deaths", "playeruuid", player.getUniqueId() + "");
                int blocksbroken = (int) SQL.get(Main.getInstance().getName().toLowerCase() + "_data", "blocksbroken", "playeruuid", player.getUniqueId() + "");
                int blocksplacen = (int) SQL.get(Main.getInstance().getName().toLowerCase() + "_data", "blocksplacen", "playeruuid", player.getUniqueId() + "");
                String lastlogin = String.valueOf(SQL.get(Main.getInstance().getName().toLowerCase() + "_data", "lastlogin", "playeruuid", player.getUniqueId() + ""));
                String lastlogout = String.valueOf(SQL.get(Main.getInstance().getName().toLowerCase() + "_data", "lastlogout", "playeruuid", player.getUniqueId() + ""));
                int commandsused = (int) SQL.get(Main.getInstance().getName().toLowerCase() + "_data", "commandsused", "playeruuid", player.getUniqueId() + "");
                Material[] blocksBrokenList = new Gson().fromJson((String) SQL.get(Main.getInstance().getName().toLowerCase() + "_data", "blocksBrokenList", "playeruuid", player.getUniqueId() + ""), Material[].class);
                Material[] blocksPlacenList = new Gson().fromJson((String) SQL.get(Main.getInstance().getName().toLowerCase() + "_data", "blocksPlacenList", "playeruuid", player.getUniqueId() + ""), Material[].class);
                EntityType[] entityTypes = new Gson().fromJson((String) SQL.get(Main.getInstance().getName().toLowerCase() + "_data", "entityTypes", "playeruuid", player.getUniqueId() + ""), EntityType[].class);
                playerManager.setSleepTimes(level);
                playerManager.setDamage(damage);
                playerManager.setPlayerKills(playerkills);
                playerManager.setEntityKills(entitykills);
                playerManager.setDeath(deaths);
                playerManager.setBlockBroken(blocksbroken);
                playerManager.setBlockPlace(blocksplacen);
                playerManager.setLastLogin(Long.parseLong(lastlogin));
                playerManager.setLastLogout(Long.parseLong(lastlogout));
                playerManager.setCommandsUsed(commandsused);
                playerManager.setBlocksPlace(new ArrayList<Material>(Arrays.asList(blocksPlacenList)));
                playerManager.setBlocksBroken(new ArrayList<Material>(Arrays.asList(blocksBrokenList)));
                playerManager.setEntityTypes(new ArrayList<EntityType>(Arrays.asList(entityTypes)));
            }
        } else {
            if (SQL.exists(Main.getInstance().getName().toLowerCase() + "_data", "playername", player.getName() + "")) {
                playerManager = new PlayerManager(player.getName());
                int level = (int) SQL.get(Main.getInstance().getName().toLowerCase() + "_data", "sleeptimes", "playername", player.getName() + "");
                double damage = (double) SQL.get(Main.getInstance().getName().toLowerCase() + "_data", "damage", "playername", player.getName() + "");
                int playerkills = (int) SQL.get(Main.getInstance().getName().toLowerCase() + "_data", "playerkills", "playername", player.getName() + "");
                int entitykills = (int) SQL.get(Main.getInstance().getName().toLowerCase() + "_data", "entitykills", "playername", player.getName() + "");
                int deaths = (int) SQL.get(Main.getInstance().getName().toLowerCase() + "_data", "deaths", "playername", player.getName() + "");
                int blocksbroken = (int) SQL.get(Main.getInstance().getName().toLowerCase() + "_data", "blocksbroken", "playername", player.getName() + "");
                int blocksplacen = (int) SQL.get(Main.getInstance().getName().toLowerCase() + "_data", "blocksplacen", "playername", player.getName() + "");
                String lastlogin = String.valueOf(SQL.get(Main.getInstance().getName().toLowerCase() + "_data", "lastlogin", "playername", player.getName() + ""));
                String lastlogout = String.valueOf(SQL.get(Main.getInstance().getName().toLowerCase() + "_data", "lastlogout", "playername", player.getName() + ""));
                int commandsused = (int) SQL.get(Main.getInstance().getName().toLowerCase() + "_data", "commandsused", "playername", player.getName() + "");
                Material[] blocksBrokenList = new Gson().fromJson((String) SQL.get(Main.getInstance().getName().toLowerCase() + "_data", "blocksBrokenList", "playername", player.getName() + ""), Material[].class);
                Material[] blocksPlacenList = new Gson().fromJson((String) SQL.get(Main.getInstance().getName().toLowerCase() + "_data", "blocksPlacenList", "playername", player.getName() + ""), Material[].class);
                EntityType[] entityTypes = new Gson().fromJson((String) SQL.get(Main.getInstance().getName().toLowerCase() + "_data", "entityTypes", "playername", player.getName() + ""), EntityType[].class);
                playerManager.setSleepTimes(level);
                playerManager.setDamage(damage);
                playerManager.setPlayerKills(playerkills);
                playerManager.setEntityKills(entitykills);
                playerManager.setDeath(deaths);
                playerManager.setBlockBroken(blocksbroken);
                playerManager.setBlockPlace(blocksplacen);
                playerManager.setLastLogin(Long.parseLong(lastlogin));
                playerManager.setLastLogout(Long.parseLong(lastlogout));
                playerManager.setCommandsUsed(commandsused);
                playerManager.setBlocksPlace(new ArrayList<Material>(Arrays.asList(blocksPlacenList)));
                playerManager.setBlocksBroken(new ArrayList<Material>(Arrays.asList(blocksBrokenList)));
                playerManager.setEntityTypes(new ArrayList<EntityType>(Arrays.asList(entityTypes)));
            }
        }
        return playerManager;
    }
}
