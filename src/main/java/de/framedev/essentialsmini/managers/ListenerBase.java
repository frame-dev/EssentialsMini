package de.framedev.essentialsmini.managers;


/*
 * de.framedev.essentialsmin.managers
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 23.09.2020 19:13
 */

import de.framedev.essentialsmini.main.Main;
import org.bukkit.event.Listener;

public abstract class ListenerBase implements Listener {

    private final Main plugin;

    /**
     * Register a Listener
     *
     * @param listener the Listener for registering
     */
    public void setupListener(Listener listener) {
        plugin.getListeners().add(listener);
    }

    public ListenerBase(Main plugin) {
        this.plugin = plugin;
        setupListener(this);
    }

    public Main getPlugin() {
        return plugin;
    }

    public String getPrefix() {
        return plugin.getPrefix();
    }
}
