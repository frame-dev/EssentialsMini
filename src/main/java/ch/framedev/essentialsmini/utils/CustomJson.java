package ch.framedev.essentialsmini.utils;


/*
 * de.framedev.essentialsmin.utils
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 28.09.2020 11:16
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import ch.framedev.essentialsmini.main.Main;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class CustomJson {

    File file;
    JSONObject object;

    public CustomJson(File file) {
        this.file = file;
        this.object = getObject();
    }

    public CustomJson(String name) {
        this.file = new File(Main.getInstance().getDataFolder(), name + ".json");
        this.object = getObject();
    }

    public boolean contains(String path) {
        return object.containsKey(path);
    }

    public void remove(String path) {
        if (object == null) this.object = getObject();
        object.remove(path);
    }

    public void set(String path, Object obj) {
        if(object == null) this.object = getObject();
        object.put(path, obj);
    }

    public void setList(String path, ArrayList<Object> list) {
        object.put(path, list);
    }

    public ArrayList<String> getStringList(String path) {
        if (getObject().containsKey(path)) {
            return (ArrayList<String>) getObject().get(path);
        }
        return null;
    }

    public ArrayList<Object> getList(String path) throws NullPointerException {
        ArrayList<Object> list;
        if (object.get(path) != null) {
            list = (ArrayList<Object>) object.get(path);
            return list;
        } else {
            return null;
        }
    }

    public String getString(String path) {
        if (object.get(path) != null) {
            return (String) object.get(path);
        }
        return null;
    }

    public boolean getBoolean(String path) {
        if (object.get(path) != null) {
            return (boolean) object.get(path);
        }
        return false;
    }

    public int getInt(String path) {
        if (object.get(path) != null) {
            return (int) object.get(path);
        }
        return 0;
    }

    public Object get(String path) {
        return object.get(path);
    }

    public Map<String, Object> getMap(String path) {
        if (contains(path)) {
            return (LinkedTreeMap<String, Object>) object.get(path);
        }
        return new HashMap<>();
    }

    public float getFloat(String path) {
        if (object.get(path) != null) {
            return (float) object.get(path);
        }
        return 0.0F;
    }

    public double getDouble(String path) {
        if (object.get(path) != null) {
            return (double) object.get(path);
        }
        return 0.0d;
    }

    public BigInteger getBigInteger(String path) {
        if (object.get(path) != null) {
            return (BigInteger) object.get(path);
        }
        return BigInteger.ZERO;
    }

    public BigDecimal getBigDecimal(String path) {
        if (object == null) return BigDecimal.ZERO;
        return (BigDecimal) object.get(path);
    }

    public void update(String path, Object value) {
        object.replace(path, value);
    }

    public ItemStack getItemStack(String path) {
        return (ItemStack) object.get(path);
    }

    public String toJSON() {
        return object.toJSONString();
    }

    @Override
    public String toString() {
        return "CustomJson{" +
                "file=" + file +
                ", object=" + object.toString() +
                '}';
    }

    public void save(File file) throws IOException, NotFoundException {
        if (file == null) {
            throw new NotFoundException("File cannot be Found!");
        }

        try {
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(new GsonBuilder().setPrettyPrinting().create().toJson(object));
                fileWriter.flush();
            }
        } catch (IOException e) {
            throw new IOException();
        }
    }

    public void saveConfig() throws NotFoundException {
        if (file == null) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            if (file == null) {
                throw new NotFoundException("File cannot be Found!");
            }
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(new GsonBuilder().setPrettyPrinting().create().toJson(object));
                fileWriter.flush();
            }
            this.object = getObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONObject getObject() {
        JSONObject json = null;
        if (file != null && file.exists()) {
            try {
                FileReader fileReader = new FileReader(file);
                json = new Gson().fromJson(fileReader, JSONObject.class);
                this.object = json;
                return json;
            } catch (FileNotFoundException ignored) {
            }
        }
        this.object = new JSONObject();
        return new JSONObject();
    }

    public boolean isFileValid() {
        return file != null;
    }

    public boolean isObjectValid() {
        return object != null;
    }
}
