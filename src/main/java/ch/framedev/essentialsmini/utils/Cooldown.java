package ch.framedev.essentialsmini.utils;

import ch.framedev.essentialsmini.commands.playercommands.KitCMD;
import lombok.Getter;

/**
 * Cooldown Class
 * This Class is for Creating a new Cooldown examples can be found in {@link KitCMD}
 * @author FrameDev
 */
public class Cooldown {

	private final int seconds;
	@Getter
    private long secondsLeft;
	@Getter
    private long milliSeconds;
	private final long actualTime;

	public Cooldown(int seconds, long actualTime) {
		this.seconds = seconds;
		this.actualTime = actualTime;
	}
	
	public Cooldown(int seconds) {
		this.seconds = seconds;
		this.actualTime = System.currentTimeMillis();
	}

    public int getSeconds() {
		return seconds;
	}

    public boolean check() {
		secondsLeft = ((actualTime / 1000) + seconds) - (System.currentTimeMillis() / 1000);
		milliSeconds = actualTime + (seconds * 1000L) - System.currentTimeMillis();
        return secondsLeft <= 0;
    }

}
