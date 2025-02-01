package ch.framedev.essentialsmini.utils;


/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 19.08.2020 18:13
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import ch.framedev.essentialsmini.main.Main;
import org.json.simple.JSONObject;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JsonHandler {

    private final File file;
    private JSONObject object = getObject();

    public JsonHandler(String name) {
        this.file = new File(Main.getInstance().getDataFolder(),name + ".json");
    }

    public boolean contains(String path) {
        return object.containsKey(path);
    }

    public void set(String path, Object obj) {
        if(object == null) this.object = getObject();
        object.put(path,obj);
    }

    public void setList(String path, ArrayList<Object> list) {
        if(object == null) this.object = getObject();
        object.put(path,list);
    }

    public ArrayList<String> getStringList(String path) {
        if(getObject().containsKey(path)) {
            return (ArrayList<String>) getObject().get(path);
        }
        return null;
    }

    public ArrayList<Object> getList(String path) throws NullPointerException {
        ArrayList<Object> list;
        if(object.get(path) != null) {
            list = (ArrayList<Object>) object.get(path);
            return list;
        } else {
            return null;
        }
    }

    public String getString(String path) {
        if(object.get(path) != null) {
            return (String) object.get(path);
        }
        return null;
    }

    public boolean getBoolean(String path) {
        if(object.get(path) != null) {
            return (boolean) object.get(path);
        }
        return false;
    }

    public int getInt(String path) {
        if(object.get(path) != null) {
            return (int) object.get(path);
        }
        return 0;
    }

    public Object get(String path) {
        if(object.containsKey(path)) {
            return object.get(path);
        }
        return null;
    }

    public Map<String, Object> getMap(String path) {
        if(contains(path)) {
            LinkedTreeMap<String,Object> hash = (LinkedTreeMap<String, Object>) object.get(path);
            return hash;
        }
        return new HashMap<>();
    }
    
    public float getFloat(String path) {
        if(object.get(path) != null) {
            return (float) object.get(path);
        }
        return 0.0F;
    }

    public double getDouble(String path) {
        if(object.get(path) != null) {
            return (double) object.get(path);
        }
        return 0.0d;
    }

    public BigInteger getBigInteger(String path) {
        if(object.get(path) != null) {
            return (BigInteger) object.get(path);
        }
        return BigInteger.ZERO;
    }

    public BigDecimal getBigDecimal(String path) {
        if(object == null) return BigDecimal.ZERO;
        return (BigDecimal) object.get(path);
    }

    public void saveConfig() {
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(new GsonBuilder().setPrettyPrinting().create().toJson(object));
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONObject getObject() {
        if(file != null &&!file.exists()) {
            try {
                FileReader fileReader = new FileReader(file);
                return new Gson().fromJson(fileReader, JSONObject.class);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        return new JSONObject();
    }

    public boolean isFileValid() {
        return file.exists();
    }

    public boolean isObjectValid() {
        return object != null;
    }
}
