package ch.framedev.essentialsmini.utils;

public class Utilities {

    private boolean dev;

    public boolean isDev() {
        return dev;
    }

    public void setDev(boolean dev) {
        this.dev = dev;
    }

    public boolean isPreRelease() {
        return new UpdateChecker().isOldVersionPreRelease();
    }

    public boolean hasUpdate() {
        return new UpdateChecker().hasUpdate();
    }
}
