package ch.framedev.essentialsmini.managers;


/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 10.08.2020 16:18
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import ch.framedev.essentialsmini.main.Main;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

public class MaterialManager {

    private final File file;
    private final FileConfiguration cfg;


    public MaterialManager() {
        this.file = new File(Main.getInstance().getDataFolder(), "materials.yml");
        this.cfg = YamlConfiguration.loadConfiguration(file);
    }

    private void saveCfg() {
        try {
            cfg.save(file);
        } catch (IOException e) {
            Main.getInstance().getLogger4J().error(e);
        }
    }

    public void saveMaterials() {
        ArrayList<String> materials = new ArrayList<>();
        for (Material material : Material.values()) {
            materials.add(material.name());
        }
        cfg.set("Materials", materials);
        saveCfg();
    }

    /**
     * @return returns a list of all Materials
     */
    public ArrayList<Material> getMaterials() {
        if (!Main.getInstance().getConfig().getBoolean("JsonFormat")) {
            ArrayList<String> mats = (ArrayList<String>) cfg.getStringList("Materials");
            ArrayList<Material> materials = new ArrayList<>();
            for (String s : mats) {
                materials.add(Material.getMaterial(s.toUpperCase()));
            }
            return materials;
        } else {
            if (getMaterialsFromJson() != null) {
                return getMaterialsFromJson();
            }
            return new ArrayList<>();
        }
    }

    /**
     * @param name the Material Name
     * @return return the Material if not null
     */
    public Material getMaterial(String name) {
        for (Material material : getMaterials()) {
            if (material != null) {
                if (material.name().equalsIgnoreCase(name)) {
                    return material;
                }
            }
        }
        return null;
    }

    /**
     * @param material the Material
     * @return if exists or not
     */
    public boolean existsMaterial(Material material) {
        for (Material mats : getMaterials()) {
            if (mats == material) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "MaterialManager{" +
                "file=" + file +
                ", cfg=" + cfg +
                '}';
    }

    public String toJSON() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(Arrays.asList(Material.values()));
    }

    public void saveMaterialToJson() {
        File file = new File(Main.getInstance().getDataFolder() + "/JsonData", "Materials.json");
        if (!file.exists()) {
            if (!file.getParentFile().mkdirs())
                throw new RuntimeException("Could not create parent directories for file: " + file);
            try {
                if (!file.createNewFile())
                    throw new RuntimeException("Could not create file: " + file);
            } catch (IOException e) {
                Main.getInstance().getLogger4J().error(e);
            }
        }
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(toJSON());
            fileWriter.flush();
        } catch (IOException e) {
            Main.getInstance().getLogger4J().error(e);
        }
    }

    public ArrayList<Material> getMaterialsFromJson() {
        File file = new File(Main.getInstance().getDataFolder() + "/JsonData", "Materials.json");
        if (file.exists()) {
            try {
                FileReader fileReader = new FileReader(file);
                Type type = new TypeToken<ArrayList<Material>>() {
                }.getType();
                return new Gson().fromJson(fileReader, type);
            } catch (IOException e) {
                Main.getInstance().getLogger4J().error(e);
            }
        }
        return null;
    }
}