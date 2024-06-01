package ch.framedev.essentialsmini.utils;


/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 19.08.2020 15:08
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.internal.LinkedTreeMap;
import ch.framedev.essentialsmini.main.Main;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;

public class JsonConfig {

    File file = new File(Main.getInstance().getDataFolder(), "config.json");
    JSONObject object = getObject();

    public boolean contains(String path) {
        return object.containsKey(path);
    }

    public void set(String path, String value) {
        object.put(path, value);
    }

    public void set(String path, Object obj) {
        object.put(path, obj);
    }

    public void set(String path, JsonElement jsonElement) {
        object.put(path, jsonElement);
    }

    public void set(String path, Integer value) {
        object.put(path, value);
    }

    public void set(String path, Boolean value) {
        object.put(path, value);
    }

    public void set(String path, ArrayList<Object> list) {
        object.put(path, list);
    }

    public ArrayList<String> getStringList(String path) {
        if (getObject().containsKey(path)) {
            return (ArrayList<String>) getObject().get(path);
        }
        return null;
    }

    public ArrayList<Object> getList(String path) {
        ArrayList<Object> list;
        if (object.get(path) != null) {
            list = (ArrayList<Object>) object.get(path);
            return list;
        }
        return null;
    }

    public void set(String path, Character value) {
        object.put(path, value);
    }

    public String getString(String path) {
        if (object.containsKey(path)) {
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

    public int geInt(String path) {
        if (object.get(path) != null) {
            return (int) object.get(path);
        }
        return 0;
    }

    public Map<String, Object> getHashMap(String path) {
        if (contains(path)) {
            LinkedTreeMap<String, Object> hash = (LinkedTreeMap<String, Object>) object.get(path);
            return hash;
        }
        return null;
    }

    public Object get(String path) {
        return object.get(path);
    }

    public void saveConfig() throws NotFoundException {
        if (file == null) {
            throw new NotFoundException("File cannot be Found!");
        }
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(new GsonBuilder().setPrettyPrinting().create().toJson(object));
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONObject getObject() {
        try {
            FileReader fileReader = new FileReader(file);
            return new Gson().fromJson(fileReader, JSONObject.class);
        } catch (FileNotFoundException e) {
            return new JSONObject();
        }
    }

    @Override
    public String toString() {
        return "JsonConfig{" +
                "file=" + file +
                ", object=" + object +
                '}';
    }
}
