package ch.framedev.essentialsmini.managers;


/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 21.08.2020 21:53
 */

import ch.framedev.essentialsmini.commands.playercommands.VanishCMD;
import ch.framedev.essentialsmini.utils.JsonHandler;

public class SaveLists {

    JsonHandler jsonHandler = new JsonHandler("SaveList");

    public void saveVanishList() {
        if (!VanishCMD.hided.isEmpty()) {
            jsonHandler.set("Vanished", VanishCMD.hided);
            jsonHandler.saveConfig();
        }
    }

    public void setVanished() {
        if (jsonHandler.contains("Vanished")) {
            VanishCMD.hided = jsonHandler.getStringList("Vanished");
            jsonHandler.set("Vanished", "");
            jsonHandler.saveConfig();
        }
    }
}
