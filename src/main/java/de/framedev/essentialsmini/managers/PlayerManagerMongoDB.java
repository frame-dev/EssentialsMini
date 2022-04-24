package de.framedev.essentialsmini.managers;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import de.framedev.essentialsmini.main.Main;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This Plugin was Created by FrameDev
 * Package : de.framedev.essentialsmin.managers
 * Date: 23.11.2020
 * Project: EssentialsMini
 * Copyrighted by FrameDev
 */

public class PlayerManagerMongoDB {

    private UUID uuid;
    private String name;
    private double money;
    private String bankname;
    private ArrayList<String> bankmembers;
    private String bankowner;
    private double bank;
    private int kills;
    private double damage;
    private int entityKills;
    private int deaths;
    private List<String> blocksBroken;
    private List<String> blocksPlacen;
    private int blockBroken;
    private int blockPlacen;
    private int commandsUsed;
    private String key;
    private int sleepTimes;
    private List<String> entityTypes;
    private boolean offline;
    private String createDate;
    private String lastLogin;
    private String lastLogout;

    public PlayerManagerMongoDB(UUID uuid, String name, double money, String bankname, ArrayList<String> bankmembers, String bankowner, double bank, int kills, double damage, int entityKills, int deaths, List<String> blocksBroken, List<String> blocksPlacen, int blockBroken, int blockPlacen, int commandsUsed, String key, int sleepTimes, List<String> entityTypes, boolean offline, String createDate, String lastLogin, String lastLogout) {
        this.uuid = uuid;
        this.name = name;
        this.money = money;
        this.bankname = bankname;
        this.bankmembers = bankmembers;
        this.bankowner = bankowner;
        this.bank = bank;
        this.kills = kills;
        this.damage = damage;
        this.entityKills = entityKills;
        this.deaths = deaths;
        this.blocksBroken = blocksBroken;
        this.blocksPlacen = blocksPlacen;
        this.blockBroken = blockBroken;
        this.blockPlacen = blockPlacen;
        this.commandsUsed = commandsUsed;
        this.key = key;
        this.sleepTimes = sleepTimes;
        this.entityTypes = entityTypes;
        this.offline = offline;
        this.createDate = createDate;
        this.lastLogin = lastLogin;
        this.lastLogout = lastLogout;
    }

    public PlayerManagerMongoDB(UUID uuid) {
        this.uuid = uuid;
        this.name = Bukkit.getOfflinePlayer(uuid).getName();
    }

    @Deprecated
    public PlayerManagerMongoDB(String name) {
        this.name = name;
        this.uuid = Bukkit.getOfflinePlayer(name).getUniqueId();
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public ArrayList<String> getBankmembers() {
        return bankmembers;
    }

    public void setBankmembers(ArrayList<String> bankmembers) {
        this.bankmembers = bankmembers;
    }

    public String getBankowner() {
        return bankowner;
    }

    public void setBankowner(String bankowner) {
        this.bankowner = bankowner;
    }

    public int getSleepTimes() {
        return sleepTimes;
    }

    public void setSleepTimes(int sleepTimes) {
        this.sleepTimes = sleepTimes;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public void setBank(double bank) {
        this.bank = bank;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void setEntityKills(int entityKills) {
        this.entityKills = entityKills;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void setBlocksBroken(List<String> blocksBroken) {
        this.blocksBroken = blocksBroken;
    }

    public void setBlocksPlacen(List<String> blocksPlacen) {
        this.blocksPlacen = blocksPlacen;
    }

    public void setCommandsUsed(int commandsUsed) {
        this.commandsUsed = commandsUsed;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setEntityTypes(List<String> entityTypes) {
        this.entityTypes = entityTypes;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setLastLogout(String lastLogout) {
        this.lastLogout = lastLogout;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public double getMoney() {
        return money;
    }

    public double getBank() {
        return bank;
    }

    public int getKills() {
        return kills;
    }

    public double getDamage() {
        return damage;
    }

    public int getEntityKills() {
        return entityKills;
    }

    public int getDeaths() {
        return deaths;
    }

    public List<String> getBlocksBroken() {
        return blocksBroken;
    }

    public List<String> getBlocksPlacen() {
        return blocksPlacen;
    }

    public int getCommandsUsed() {
        return commandsUsed;
    }

    public String getKey() {
        return key;
    }

    public List<String> getEntityTypes() {
        return entityTypes;
    }

    public boolean isOffline() {
        return offline;
    }

    public long getCreateDate() {
        return Long.parseLong(createDate);
    }

    public long getLastLogin() {
        return Long.parseLong(lastLogin);
    }

    public long getLastLogout() {
        return Long.parseLong(lastLogout);
    }

    public int getBlockBroken() {
        return blockBroken;
    }

    public void setBlockBroken(int blockBroken) {
        this.blockBroken = blockBroken;
    }

    public int getBlockPlacen() {
        return blockPlacen;
    }

    public void setBlockPlacen(int blockPlacen) {
        this.blockPlacen = blockPlacen;
    }

    public static PlayerManagerMongoDB getPlayerManager(String playerName, String collection) {
        MongoCollection<Document> collections = Main.getInstance().getMongoManager().getDatabase().getCollection(collection);
        Document result = collections.find(new Document("name", playerName)).first();
        if (result != null)
            return new Gson().fromJson(result.toJson(), PlayerManagerMongoDB.class);
        return new PlayerManagerMongoDB(playerName);
    }

    public static PlayerManagerMongoDB getPlayerManager(UUID playerUUID, String collection) {
        MongoCollection<Document> collections = Main.getInstance().getMongoManager().getDatabase().getCollection(collection);
        Document result = collections.find(new Document("uuid", playerUUID.toString())).first();
        if (result != null)
            return new Gson().fromJson(result.toJson(), PlayerManagerMongoDB.class);
        return new PlayerManagerMongoDB(playerUUID);
    }

    public void save(String collection) {
        MongoCollection<Document> collections = Main.getInstance().getMongoManager().getDatabase().getCollection(collection);
        String json = new Gson().toJson(this);
        Document document = Document.parse(json);
        Document result = collections.find(new Document("name", name)).first();
        if (result == null) {
            collections.insertOne(document);
        } else {
            for (BackendManager.DATA data : BackendManager.DATA.values()) {
                try {
                    String cap = data.getName().substring(0, 1).toUpperCase() + data.getName().substring(1);
                    if (data == BackendManager.DATA.LAST_LOGOUT) {
                        document.put(data.getName(), this.getClass().getMethod("get" + cap).invoke(this).toString());
                    } else if (data == BackendManager.DATA.LAST_LOGIN) {
                        document.put(data.getName(), this.getClass().getMethod("get" + cap).invoke(this).toString());
                    } else if (data == BackendManager.DATA.OFFLINE) {
                        document.put(data.getName(), this.getClass().getMethod("is" + cap).invoke(this));
                    } else if (data == BackendManager.DATA.CREATEDATE) {
                        document.put(data.getName(), this.getClass().getMethod("get" + cap).invoke(this).toString());
                    } else
                        document.put(data.getName(), this.getClass().getMethod("get" + cap).invoke(this));
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            collections.replaceOne(collections.find(new Document("uuid", result.get("uuid"))).first(), document);
        }
    }
}
