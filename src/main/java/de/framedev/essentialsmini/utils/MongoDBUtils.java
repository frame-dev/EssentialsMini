package de.framedev.essentialsmini.utils;

import de.framedev.essentialsmini.database.BackendManager;
import de.framedev.essentialsmini.database.MongoManager;
import de.framedev.essentialsmini.main.Main;

import java.util.logging.Level;

/**
 * This Plugin was Created by FrameDev
 * Package : de.framedev.essentialsmin.utils
 * ClassName MongoDbUtils
 * Date: 06.04.21
 * Project: Unknown
 * Copyrighted by FrameDev
 */

public class MongoDBUtils {

    private boolean mongoDb = false;
    private MongoManager mongoManager;
    private BackendManager backendManager;

    public MongoDBUtils() {

        Main plugin = Main.getInstance();
        /* MongoDB */
        if (plugin.getConfig().getBoolean("MongoDB.Boolean") || plugin.getConfig().getBoolean("MongoDB.LocalHost")) {
            this.mongoDb = true;
        }
        if (plugin.getConfig().getBoolean("MongoDB.Boolean") || plugin.getConfig().getBoolean("MongoDB.LocalHost")) {
            if (plugin.getConfig().getBoolean("MongoDB.LocalHost")) {
                this.mongoManager = new MongoManager();
                this.mongoManager.connectLocalHost();
                Main.getInstance().getLogger().log(Level.INFO, "MongoDB Enabled");
            }
            if (plugin.getConfig().getBoolean("MongoDB.Boolean")) {
                this.mongoManager = new MongoManager();
                this.mongoManager.connect();
                Main.getInstance().getLogger().log(Level.INFO, "MongoDB Enabled");
            }
            if (plugin.getConfig().getBoolean("MongoDB.LocalHost")) {
                this.backendManager = new BackendManager(Main.getInstance());
            }
            if (plugin.getConfig().getBoolean("MongoDB.Boolean")) {
                this.backendManager = new BackendManager(Main.getInstance());
            }
        }
    }

    /**
     * This Method returns if MongoDB is enabled or not
     *
     * @return return if MongoDB is Enabled or not
     */
    public boolean isMongoDb() {
        return mongoDb;
    }

    /**
     * This Method returns the MongoManager class
     *
     * @return return MongoManager class
     */
    public MongoManager getMongoManager() {
        return mongoManager;
    }

    /**
     * This Method returns the BackendManager class
     *
     * @return return BackendManager class
     */
    public BackendManager getBackendManager() {
        return backendManager;
    }
}
