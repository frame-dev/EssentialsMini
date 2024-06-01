package ch.framedev.essentialsmini.utils;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Utilities {

    private boolean dev;

    public boolean isPreRelease() {
        return new UpdateChecker().isOldVersionPreRelease();
    }

    public boolean hasUpdate() {
        return new UpdateChecker().hasUpdate();
    }
}
