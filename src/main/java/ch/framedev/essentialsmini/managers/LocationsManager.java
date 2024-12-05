package ch.framedev.essentialsmini.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import ch.framedev.essentialsmini.main.Main;
import ch.framedev.essentialsmini.utils.NotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 03.07.2020 20:00
 */
public class LocationsManager {

    private String name;
    private final File fileBackup = new File(Main.getInstance().getDataFolder(), "locationsBackup.yml");
    private final File fileBackupJson = new File(Main.getInstance().getDataFolder(), "locationsBackupJson.json");
    private final File file = new File(Main.getInstance().getDataFolder(), "locations.yml");
    private final FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
    private final FileConfiguration cfgBackup = YamlConfiguration.loadConfiguration(fileBackup);

    private final Main instance = Main.getInstance();
    private final boolean jsonFormat;

    public void setJsonLocation(String name, Location location) {
        if (getLocations() != null) {
            if (!getLocations().containsKey(name)) {
                HashMap<String, String> locs = getLocations();
                try {
                    FileWriter writer = new FileWriter(new File(Main.getInstance().getDataFolder(), "locs.json"));
                    locs.put(name, locationToString(location));
                    writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(locs));
                    writer.flush();
                    writer.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            HashMap<String, String> locs = new HashMap<>();
            try {
                FileWriter writer = new FileWriter(new File(Main.getInstance().getDataFolder(), "locs.json"));
                locs.put(name, locationToString(location));
                writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(locs));
                writer.flush();
                writer.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * @param name the Location Name
     * @return return true if a Home exists
     */
    public boolean existsHome(String name) {
        return getLocations().containsKey(name) && !getLocations().get(name).equalsIgnoreCase(" ");
    }

    public void setJsonLocation(String name, String location) {
        if (!getLocations().containsKey(name)) {
            try {
                FileWriter writer = new FileWriter(new File(Main.getInstance().getDataFolder(), "locs.json"));
                getLocations().put(name, " ");
                writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(getLocations()));
                writer.flush();
                writer.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public LocationsManager(String name) {
        this.jsonFormat = Main.getInstance().getConfig().getBoolean("JsonFormat");
        this.name = name;
    }

    public LocationsManager() {
        this.jsonFormat = Main.getInstance().getConfig().getBoolean("JsonFormat");
    }

    /**
     * Save Locations Config
     */
    public void saveCfg() {
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return returns the Locations Config
     */
    public FileConfiguration getCfg() {
        return cfg;
    }

    /**
     * @param name     the Location Name
     * @param location the Location to save
     */
    public void setLocation(String name, Location location) {
        if (jsonFormat) {
            setJsonLocation(name, location);
        } else {
            cfg.set(name + ".world", location.getWorld().getName());
            cfg.set(name + ".x", location.getX());
            cfg.set(name + ".y", location.getY());
            cfg.set(name + ".z", location.getZ());
            cfg.set(name + ".yaw", location.getYaw());
            cfg.set(name + ".pitch", location.getPitch());
            cfg.set(name + ".createdAt", System.currentTimeMillis());
            saveCfg();
        }
    }

    /**
     * use the Name variable from Constructor
     *
     * @param location the Location to Save
     */
    public void setLocation(Location location) {
        if (jsonFormat) {
            setJsonLocation(name, location);
        } else {
            cfg.set(name + ".world", location.getWorld().getName());
            cfg.set(name + ".x", location.getX());
            cfg.set(name + ".y", location.getY());
            cfg.set(name + ".z", location.getZ());
            cfg.set(name + ".yaw", location.getYaw());
            cfg.set(name + ".pitch", location.getPitch());
            cfg.set(name + ".createdAt", System.currentTimeMillis());
            saveCfg();
        }
    }

    /*public void setLocation(String name, LocationJson loc) {
        this.name = name;
        if (jsonFormat) {
            List<LocationJson> locs = getLocations();
            List<LocationJson> updated = new ArrayList<>(getLocations());
            final boolean[] success = {false};
            locs.forEach(locationJson -> {
                if (locationJson.getLocationName().equalsIgnoreCase(name)) {
                    updated.remove(locationJson);
                    success[0] = true;
                }
            });
            if (!success[0]) {
                System.out.println("not");
            }
            saveLocations(updated);
        } else {
            cfg.set(name + ".world", loc.getWorldName());
            cfg.set(name + ".x", loc.getX());
            cfg.set(name + ".y", loc.getY());
            cfg.set(name + ".z", loc.getZ());
            cfg.set(name + ".yaw", loc.getYaw());
            cfg.set(name + ".pitch", loc.getPitch());
            cfg.set(name + ".createdAt", System.currentTimeMillis());
            saveCfg();
        }
    }*/

    public String getCreatedAt(String name) {
        if (cfg.contains(name + ".createdAt")) {
            Date date = new Date(cfg.getLong(name + ".createdAt"));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
            return simpleDateFormat.format(date);
        }
        return "";
    }

    public void saveLocations(List<LocationJson> locs) {
        File file = new File(Main.getInstance().getDataFolder(), "locations.json");
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file);
            writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(locs));
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * remove the Json Location
     *
     * @param name the Location Name
     */
    public void removeLocation(String name) {
        if (jsonFormat) {
            if (getLocations() == null) return;
            getLocations().remove(name);
            setJsonLocation(name, " ");
        } else {
            if (cfg.contains(name)) {
                cfg.set(name, " ");
                saveCfg();
            }
        }
    }

    /**
     * @return returns all found Locations
     */
    public HashMap<String, String> getLocations() {
        HashMap<String, String> locs = new HashMap<>();
        try {
            FileReader reader = new FileReader(new File(Main.getInstance().getDataFolder(), "locs.json"));
            Type type = new TypeToken<HashMap<String, String>>() {
            }.getType();
            locs = new Gson().fromJson(reader, type);
        } catch (Exception ignored) {
        }
        return locs;
    }

    /**
     * @param s the Location Name
     * @return returns the Location from the File
     * @throws NullPointerException throw a NullPointerException if the Location was not found
     */
    public Location getLocation(String s) throws NotFoundException {
        if (jsonFormat) {
            return locationFromString(getLocations().get(s));
        } else {
            if (cfg.contains(s)) {
                try {
                    World world = Bukkit.getWorld(Objects.requireNonNull(cfg.getString(s + ".world")));
                    double x = cfg.getDouble(s + ".x");
                    double y = cfg.getDouble(s + ".y");
                    double z = cfg.getDouble(s + ".z");
                    float yaw = cfg.getInt(s + ".yaw");
                    float pitch = cfg.getInt(s + ".pitch");
                    if (world == null) {
                        throw new NotFoundException("World");
                    }
                    return new Location(world, x, y, z, yaw, pitch);
                } catch (IllegalArgumentException ignored) {
                }
            }
            return null;
        }
    }

    public void setWarp(String warpName, Location location) {
        setLocation("warps." + warpName, location);
        saveCfg();
    }

    public void setWarp(String warpName, Location location, double cost) {
        setWarp(warpName, location);
        cfg.set("warps." + warpName + ".cost", cost);
        saveCfg();
    }

    public boolean costWarp(String warpName) {
        return cfg.contains("warps." + warpName + ".cost");
    }

    public double getWarpCost(String warpName) {
        if(cfg.contains("warps." + warpName + ".cost"))
            return cfg.getDouble("warps." + warpName + ".cost");
        return 0;
    }

    public Location getWarp(String warpName) {
        if (cfg.contains("warps." + warpName)) {
            return getLocation("warps." + warpName);
        }
        return null;
    }

    public FileConfiguration getCfgBackup() {
        return cfgBackup;
    }

    public File getFileBackup() {
        return fileBackup;
    }

    public File getFile() {
        return file;
    }

    /**
     * uses the Variable Name from the Constructor
     *
     * @return return the Location from the file
     */
    public Location getLocation() {
        if (jsonFormat) {
            return locationFromString(getLocations().get(name));
        } else {
            if (cfg.contains(name)) {
                try {
                    World world = Bukkit.getWorld(Objects.requireNonNull(cfg.getString(name + ".world")));
                    double x = cfg.getDouble(name + ".x");
                    double y = cfg.getDouble(name + ".y");
                    double z = cfg.getDouble(name + ".z");
                    float yaw = cfg.getInt(name + ".yaw");
                    float pitch = cfg.getInt(name + ".pitch");
                    if (world != null) {

                    } else {
                        throw new NotFoundException("World");
                    }
                    return new Location(world, x, y, z, yaw, pitch);
                } catch (IllegalArgumentException ignored) {
                }
            }
            return null;
        }
    }

    /**
     * throw NullPointerException if the World is null
     *
     * @param location the Location to convert to an string
     * @return the Location converted to String
     */
    public static String locationToString(Location location) {
        String s = "";
        if (location.getWorld() == null) {
            return null;
        }
        s += location.getWorld().getName() + ";";
        s += location.getX() + ";";
        s += location.getY() + ";";
        s += location.getZ() + ";";
        s += location.getYaw() + ";";
        s += location.getPitch();
        /* World;10;63;12;160;-24 */
        return s;
    }

    /**
     * @param string the converted StringLocation
     * @return returns a completed Location from the String
     */
    public static Location locationFromString(String string) {
        String[] s = string.split(";");
        return new Location(Bukkit.getWorld(s[0]), Double.parseDouble(s[1]), Double.parseDouble(s[2]), Double.parseDouble(s[3]), Float.parseFloat(s[4]), Float.parseFloat(s[5]));
    }

    public List<Location> getWarps() {
        ConfigurationSection cs = cfg.getConfigurationSection("warps");
        List<Location> warps = new ArrayList<>();
        if (cs == null) return warps;
        for (String s : cs.getKeys(false)) {
            warps.add(getLocation(s));
        }
        return warps;
    }

    public List<String> getWarpNames() {
        ConfigurationSection cs = cfg.getConfigurationSection("warps");
        List<String> warps = new ArrayList<>();
        if (cs == null) return warps;
        warps.addAll(cs.getKeys(false));
        return warps;
    }

    /**
     * Saves the Backups
     */
    public void saveBackupCfg() {
        try {
            cfgBackup.save(fileBackup);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final HashMap<String, String> backups = new HashMap<>();

    public void saveBackup() {
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            String ss = offlinePlayer.getName();
            if (ss != null) {
                ConfigurationSection cs = getCfg().getConfigurationSection(ss + ".home");
                if (cs != null) {
                    for (String s : cs.getKeys(false)) {
                        if (getCfg().get(ss + ".home." + s) != null && !getCfg().get(ss + ".home." + s).equals(" ")) {
                            cfgBackup.set(ss + ".home." + s, locationToString(getLocation(ss + ".home." + s)));
                            backups.put(ss + ".home." + s, locationToString(getLocation(ss + ".home." + s)));
                        }
                    }
                }
            }
        }
        saveBackupCfg();
        // saveBackUps();
    }

    /**
     * Deletes the Position Locations every Reload or restart
     */
    public void deleteLocations() {
        ConfigurationSection cs = new LocationsManager().getCfg().getConfigurationSection("position");
        if (cs != null) {
            for (String s : cs.getKeys(false)) {
                if (s != null) {
                    getCfg().set("position." + s, " ");
                    try {
                        getCfg().save(getFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void saveBackUps() {
        try {
            FileWriter fileWriter = new FileWriter(fileBackupJson);
            fileWriter.write(new GsonBuilder().setPrettyPrinting().serializeNulls().create().toJson(backups));
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * uses for convert an Location to an Json Location
     * otherwise it will throw an Serialize Stackoverflow exception
     */
    public static class LocationJson {

        private String locationName, worldName;
        private double x, y, z;
        private float yaw, pitch;
        private final long createdAt = System.currentTimeMillis();

        public LocationJson(String name, Location location) {
            this.locationName = name;
            this.worldName = location.getWorld().getName();
            this.x = location.getX();
            this.y = location.getY();
            this.z = location.getZ();
            this.yaw = location.getYaw();
            this.pitch = location.getPitch();
        }

        public LocationJson() {
        }

        public String getLocationName() {
            return locationName;
        }

        public void setLocationName(String locationName) {
            this.locationName = locationName;
        }

        public String getWorldName() {
            return worldName;
        }

        public void setWorldName(String worldName) {
            this.worldName = worldName;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public double getZ() {
            return z;
        }

        public void setZ(double z) {
            this.z = z;
        }

        public float getYaw() {
            return yaw;
        }

        public void setYaw(float yaw) {
            this.yaw = yaw;
        }

        public float getPitch() {
            return pitch;
        }

        public void setPitch(float pitch) {
            this.pitch = pitch;
        }

        public long getCreatedAt() {
            return createdAt;
        }

        public Location getLocation() {
            return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
        }
    }
}
