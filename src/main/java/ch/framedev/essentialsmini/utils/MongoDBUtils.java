package ch.framedev.essentialsmini.utils;

import ch.framedev.essentialsmini.database.BackendManager;
import ch.framedev.essentialsmini.database.MongoManager;
import ch.framedev.essentialsmini.main.Main;
import lombok.Getter;

import java.util.logging.Level;

/**
 * This Plugin was Created by FrameDev
 * Package : de.framedev.essentialsmin.utils
 * ClassName MongoDbUtils
 * Date: 06.04.21
 * Project: Unknown
 * Copyrighted by FrameDev
 */

@Getter
public class MongoDBUtils {

    /**
     * -- GETTER --
     *  This Method returns if MongoDB is enabled or not
     *
     * @return return if MongoDB is Enabled or not
     */
    private boolean mongoDb = false;
    /**
     * -- GETTER --
     *  This Method returns the MongoManager class
     *
     * @return return MongoManager class
     */
    private MongoManager mongoManager;
    /**
     * -- GETTER --
     *  This Method returns the BackendManager class
     *
     * @return return BackendManager class
     */
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

}
