package de.framedev.essentialsmin.managers;


/*
 * ===================================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * ===================================================
 * This Class was created at 21.08.2020 22:52
 */

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class BossBarManager {

    @NonNull
    private BarStyle barStyle;
    @NonNull
    private String title;
    @NonNull
    private BarColor barColor;
    @NonNull
    private BossBar bossBar;

    private double progress;

    private static ArrayList<BossBar> bossBars;

    /**
     * Create a new Spigot BossBar with this Class!
     *
     * @param title    the Title from the BossBar
     * @param barColor the Color from the BossBar
     * @param barStyle the Style from the BossBar
     */
    public BossBarManager(@NonNull String title, @NonNull BarColor barColor, @NonNull BarStyle barStyle) {
        this.title = title;
        this.barColor = barColor;
        this.barStyle = barStyle;
        if (title == null) {
            throw new NullPointerException("Title cannot be null!");
        }
        if (barColor == null) {
            throw new NullPointerException("BarColor cannot be Null!");
        }
        if (barStyle == null) {
            throw new NullPointerException("BarStyle cannot be Null!");
        }
    }

    /**
     * @param title the Title from the BossBar
     *              BarColor is Blue
     *              BarStyle is Segmented_12
     */
    public BossBarManager(@NonNull String title) {
        this.title = title;
        this.barStyle = BarStyle.SEGMENTED_12;
        this.barColor = BarColor.BLUE;
        if (title == null) {
            throw new NullPointerException("Title cannot be null!");
        }
    }

    /**
     * @param barStyle set's the BarStyle new
     */
    public void setBarStyle(BarStyle barStyle) {
        this.barStyle = barStyle;
    }

    /**
     * @param barColor set's the Color for the BossBar new!
     */
    public void setBarColor(BarColor barColor) {
        this.barColor = barColor;
    }

    /**
     * @param progress set the Progress of the BossBar
     */
    public BossBarManager setProgress(double progress) {
        this.progress = progress;
        return this;
    }

    /**
     * @return the Progress of the BossBar
     */
    public double getProgress() {
        return progress;
    }

    /**
     * @return the BossBar Style
     */
    public BarStyle getBarStyle() {
        return barStyle;
    }

    /**
     * @return the Title from the BossBar
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the Title how do you want to set to the BossBar
     * @return this BossBarManger Class!
     */
    public BossBarManager setTitle(@NonNull String title) {
        this.title = title;
        return this;
    }

    public BarColor getBarColor() {
        return barColor;
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public BossBarManager setBossBar(BossBar bossBar) {
        this.bossBar = bossBar;
        return this;
    }

    /**
     * This Method add's the Player to the BossBar
     *
     * @param player the Player how need to be Added to the BossBar
     * @return this(BossBarManager)
     */
    public BossBarManager addPlayer(Player player) {
        bossBar.addPlayer(player);
        return this;
    }

    /**
     * @param player the Player how dou you want to remove from the BossBar
     * @return this BossBarManager Class
     */
    public BossBarManager removePlayer(Player player) {
        bossBar.removePlayer(player);
        return this;
    }

    /**
     * This Method creates the BossBar after this you can add Players tho the BossBar!
     *
     * @throws NullPointerException if Title,BarColor or BarStyle is Null
     */
    public BossBarManager create() throws NullPointerException {
        if (bossBars == null) {
            bossBars = new ArrayList<>();
        }
        this.bossBar = Bukkit.createBossBar(title, barColor, barStyle);
        bossBars.add(bossBar);
        return this;
    }

    /**
     * This Method update the Title and the Progress!
     *
     * @return this BossBarManager Class
     */
    public BossBarManager update() {
        if (this.bossBar == null) return this;
        bossBar.setTitle(title);
        bossBar.setProgress(progress);
        return this;
    }

    /**
     * remove the BossBar
     *
     * @return this BossBarManager Class
     */
    public BossBarManager remove() {
        if (this.bossBar == null) return this;
        this.bossBar.hide();
        this.bossBar.setVisible(false);
        return this;
    }

    /**
     * Removes all BossBars
     */
    public static void removeAll() {
        if (bossBars != null) {
            for (BossBar bossBar : bossBars) {
                for (Player player : bossBar.getPlayers()) {
                    if (player != null)
                        bossBar.removePlayer(player);
                }
            }
        }
    }
}
