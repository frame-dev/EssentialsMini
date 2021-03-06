package de.framedev.essentialsmin.managers;

import com.google.gson.Gson;
import com.mongodb.Block;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertOneOptions;
import de.framedev.essentialsmin.main.Main;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.UUID;

public class BackendManager {
    private final Main plugin;

    public enum DATA {
        NAME("name"),
        MONEY("money"),
        DAMAGE("damage"),
        ENTITYKILLS("entityKills"),
        DEATHS("deaths"),
        BLOCKSBROKEN("blocksBroken"),
        BLOCKSPLACEN("blocksPlacen"),
        COMMANDSUSED("commandsUsed"),
        KEY("key"),
        ENTITYTYPES("entityTypes"),
        OFFLINE("offline"),
        SLEEPTIMES("sleepTimes"),
        CREATEDATE("createDate"),
        LASTLOGIN("lastLogin"),
        LASTLOGOUT("lastLogout");

        private final String name;

        DATA(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public BackendManager(Main plugin) {
        this.plugin = plugin;
    }

    public void createUserMoney(OfflinePlayer player, String collection) {
        String uuid = player.getUniqueId().toString();
        if (existsCollection(collection)) {
            MongoCollection<Document> collections = this.plugin.getMongoManager().getDatabase().getCollection(collection);
            Document result = collections.find(new Document("uuid", uuid)).first();
            if (result == null) {
                Document dc = (new Document("uuid", uuid))
                        .append("name", player.getName())
                        .append("money", 0.0)
                        .append("bank", 0.0)
                        .append("kills", 0)
                        .append("damage", 0.0)
                        .append("entityKills", 0)
                        .append("deaths", 0)
                        .append("blocksBroken", new ArrayList<String>())
                        .append("blocksPlacen", new ArrayList<String>())
                        .append("commandsUsed", 0)
                        .append("key", null)
                        .append("entityTypes", new ArrayList<String>())
                        .append("offline", false)
                        .append(DATA.SLEEPTIMES.getName(),0)
                        .append("createDate", System.currentTimeMillis() + "")
                        .append("lastLogin", 0L + "")
                        .append("lastLogout", 0L + "");
                collections.insertOne(dc, (new InsertOneOptions()).bypassDocumentValidation(false));
            }
        } else {
            this.plugin.getMongoManager().getDatabase().createCollection(collection);
            MongoCollection<Document> collections = this.plugin.getMongoManager().getDatabase().getCollection(collection);
            Document result = collections.find(new Document("uuid", uuid)).first();
            if (result == null) {
                Document dc = (new Document("uuid", uuid))
                        .append("name", player.getName())
                        .append("money", 0)
                        .append("bank", 0)
                        .append("kills", 0)
                        .append("damage", 0.0)
                        .append("entityKills", 0)
                        .append("deaths", 0)
                        .append("blocksBroken", new ArrayList<String>())
                        .append("blocksPlacen", new ArrayList<String>())
                        .append("commandsUsed", 0)
                        .append("key", null)
                        .append("entityTypes", new ArrayList<String>())
                        .append("offline", false)
                        .append(DATA.SLEEPTIMES.getName(),0)
                        .append("createDate", System.currentTimeMillis())
                        .append("lastLogin", 0L)
                        .append("lastLogout", 0L);
                collections.insertOne(dc, (new InsertOneOptions()).bypassDocumentValidation(false));
            }
        }
    }


    public void updateUser(OfflinePlayer player, String where, Object data, String collection) {
        if (existsCollection(collection)) {
            String uuid = player.getUniqueId().toString();
            MongoCollection<Document> collections = this.plugin.getMongoManager().getDatabase().getCollection(collection);
            Document document = collections.find(new Document("uuid", uuid)).first();
            if (document != null) {
                Document document1 = new Document(where, data);
                Document document2 = new Document("$set", document1);
                if(document.get(where) != null) {
                    collections.updateOne(document, document2);
                } else {
                    document.put(where,data);
                    collections.updateOne(collections.find(new Document("uuid", uuid)).first(), document);
                }
            }
        } else {
            String uuid = player.getUniqueId().toString();
            this.plugin.getMongoManager().getDatabase().createCollection(collection);
            MongoCollection<Document> collections = this.plugin.getMongoManager().getDatabase().getCollection(collection);
            Document document = collections.find(new Document("uuid", uuid)).first();
            if (document != null) {
                Document document1 = new Document(where, data);
                Document document2 = new Document("$set", document1);
                collections.updateOne(document, document2);
            }
        }
    }

    public boolean exists(OfflinePlayer player, String where, String collection) {
        if (existsCollection(collection)) {
            String uuid = player.getUniqueId().toString();
            MongoCollection<Document> collections = this.plugin.getMongoManager().getDatabase().getCollection(collection);
            Document document = collections.find(new Document("uuid", uuid)).first();
            if (document != null) {
                return document.get("key") != null;
            }
        }
        return false;
    }

    public void deleteUser(OfflinePlayer player, String collection) {
        if (existsCollection(collection)) {
            String uuid = player.getUniqueId().toString();
            MongoCollection<Document> collections = this.plugin.getMongoManager().getDatabase().getCollection(collection);
            Document document = collections.find(new Document("uuid", uuid)).first();
            if (document != null) {
                collections.deleteOne(document);
            }
        } else {
            String uuid = player.getUniqueId().toString();
            this.plugin.getMongoManager().getDatabase().createCollection(collection);
            MongoCollection<Document> collections = this.plugin.getMongoManager().getDatabase().getCollection(collection);
            Document document = collections.find(new Document("uuid", uuid)).first();
            if (document != null) {
                collections.deleteOne(document);
            }
        }
    }

    public Object get(OfflinePlayer player, String where, String collection) {
        if (existsCollection(collection)) {
            MongoCollection<Document> mongoCollection = this.plugin.getMongoManager().getDatabase().getCollection(collection);
            String str = player.getUniqueId().toString();
            Document document1 = mongoCollection.find(new Document("uuid", str)).first();
            if (document1 != null) {
                return document1.get(where);
            }
            return null;
        }
        this.plugin.getMongoManager().getDatabase().createCollection(collection);
        MongoCollection<Document> collections = this.plugin.getMongoManager().getDatabase().getCollection(collection);
        String uuid = player.getUniqueId().toString();
        collections.insertOne(new Document());
        Document document = collections.find(new Document("uuid", uuid)).first();
        if (document != null) {
            return document.get(where);
        }
        return null;
    }


    public boolean existsCollection(String collection) {
        MongoCollection<Document> collections = this.plugin.getMongoManager().getDatabase().getCollection(collection);
        return collections != null;
    }


    @SuppressWarnings("deprecation")
    public ArrayList<OfflinePlayer> getOfflinePlayers(String collection) {
        ArrayList<OfflinePlayer> players = new ArrayList<>();
        if (existsCollection(collection)) {
            MongoCollection<Document> collections = this.plugin.getMongoManager().getDatabase().getCollection(collection);
            collections.find(new Document("offline", true)).forEach((Block<? super Document>) document -> {
                if (document != null) {
                    UUID uuid = UUID.fromString(document.getString("uuid"));
                    players.add(Bukkit.getOfflinePlayer(uuid));
                }
            });
            return players;
        }
        return null;
    }
}


