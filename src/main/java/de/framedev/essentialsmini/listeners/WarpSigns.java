package de.framedev.essentialsmini.listeners;

import de.framedev.essentialsmini.main.Main;
import de.framedev.essentialsmini.managers.ListenerBase;
import de.framedev.essentialsmini.managers.LocationsManager;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

/**
 * / This Plugin was Created by FrameDev
 * / Package : de.framedev.essentialsmini.listeners
 * / ClassName WarpSigns
 * / Date: 12.07.21
 * / Project: EssentialsMini
 * / Copyrighted by FrameDev
 */

public class WarpSigns extends ListenerBase {
    public WarpSigns(Main plugin) {
        super(plugin);
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (event.getLine(0) == null) return;
        if (event.getLine(1) == null) return;
        if (event.getLine(0).equalsIgnoreCase("warp")) {
            if (event.getPlayer().hasPermission("essentialsmini.signs.create")) {
                boolean success = false;
                for (String location : new LocationsManager().getWarpNames()) {
                    if (location == null) continue;
                    if (event.getLine(1).equalsIgnoreCase(location)) {
                        event.setLine(0, "§6[§bWARP§6]");
                        event.setLine(1, "§a" + location);
                        success = true;
                    }
                }
                if (!success)
                    event.getPlayer().sendMessage(getPlugin().getPrefix() + "§cDieser Warp existiert nicht!");
            } else {
                event.getPlayer().sendMessage(getPlugin().getPrefix() + getPlugin().getNoPerms());
            }
        }
    }

    @EventHandler
    public void onClickWarp(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getHand() == EquipmentSlot.OFF_HAND) {
                return;
            }
            if (event.getHand().equals(EquipmentSlot.HAND) &&
                    event.getClickedBlock().getState() instanceof Sign) {
                Sign s = (Sign) event.getClickedBlock().getState();
                String[] lines = s.getLines();
                if (lines[0].equalsIgnoreCase("§6[§bWARP§6]")) {
                    if (event.getPlayer().hasPermission("essentialsmini.signs.use")) {
                        String warpName = lines[1].replace("§a", "");
                        Location location = new LocationsManager().getLocation("warps." + warpName);
                        if (event.getPlayer().hasPermission("essentialsmini.warp")) {
                            event.getPlayer().teleport(location);
                            Player player = event.getPlayer();
                            String message = getPlugin().getCustomMessagesConfig().getString("Warp.Teleport");
                            if (message.contains("&"))
                                message = message.replace('&', '§');
                            if (message.contains("%WarpName%"))
                                message = message.replace("%WarpName%", warpName);
                            player.sendMessage(getPlugin().getPrefix() + message);
                        }
                    } else {
                        event.getPlayer().sendMessage(getPlugin().getPrefix() + getPlugin().getNoPerms());
                    }
                    event.setCancelled(true);
                }
            }
        }
    }
}
