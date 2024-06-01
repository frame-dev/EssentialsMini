package ch.framedev.essentialsmini.utils;

import ch.framedev.essentialsmini.main.Main;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * This Plugin was Created by FrameDev
 * Package : de.framedev.essentialsmin.utils
 * ClassName UpdateChecker
 * Date: 04.04.21
 * Project: EssentialsMini
 * Copyrighted by FrameDev
 */

public class UpdateChecker {

    public void download(String fileUrl, String fileName, String name) {
        File file = new File(fileName, name);
        if (!file.exists())
            file.getParentFile().mkdirs();
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            URL url = new URL(fileUrl);
            in = new BufferedInputStream(url.openStream());
            fout = new FileOutputStream(new File(fileName, name));
            final byte[] data = new byte[4096];
            int count;
            while ((count = in.read(data, 0, 4096)) != -1) {
                fout.write(data, 0, count);
            }
        } catch (Exception e) {
            System.out.println("Updater tried to download the update, but was unsuccessful.");
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
            try {
                if (fout != null) {
                    fout.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean hasUpdate() {
        try {
            URLConnection conn = new URL("https://framedev.ch/sites/downloads/essentialsminiversion.txt").openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String oldVersion = Main.getInstance().getDescription().getVersion();
            String newVersion = br.readLine();
            if (!newVersion.equalsIgnoreCase(oldVersion))
                if (!oldVersion.contains("PRE-RELEASE"))
                    return true;
        } catch (IOException ignored) {
        }
        return false;
    }

    public boolean isOldVersionPreRelease() {
        try {
            URLConnection conn = new URL("https://framedev.ch/sites/downloads/essentialsminiversion.txt").openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String oldVersion = Main.getInstance().getDescription().getVersion();
            String newVersion = br.readLine();
            if (!newVersion.equalsIgnoreCase(oldVersion))
                if (oldVersion.contains("PRE-RELEASE"))
                    return true;
        } catch (IOException ignored) {
        }
        return false;
    }
}
