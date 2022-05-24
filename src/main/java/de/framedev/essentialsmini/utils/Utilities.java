package de.framedev.essentialsmini.utils;

import de.framedev.essentialsmini.main.Main;

public class Utilities {

    private boolean dev;

    public void setDev(boolean isDev) {
        this.dev = isDev;
    }

    public boolean isDev() {
        return dev;
    }

    public boolean isPreRelease() {
        return new UpdateChecker().isOldVersionPreRelease();
    }

    public boolean hasUpdate() {
        return new UpdateChecker().hasUpdate();
    }
}
