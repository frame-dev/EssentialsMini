/**
 * Dies ist ein Plugin von FrameDev
 * Bitte nichts §ndern, @Copyright by FrameDev
 */
package de.framedev.essentialsmini.commands.playercommands;

import de.framedev.essentialsmini.main.Main;
import de.framedev.essentialsmini.managers.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author DHZoc
 */
public class FlyCMD extends CommandBase {

    private final Main plugin;

    public FlyCMD(Main plugin) {
        super(plugin, "fly");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (sender.hasPermission("essentialsmini.fly")) {
                    if (!player.getAllowFlight()) {
                        player.setAllowFlight(true);
                        player.setFlying(true);
                        String flySelfOn = plugin.getLanguageConfig(player).getString("FlySelfOn");
                        if (flySelfOn.contains("&"))
                            flySelfOn = flySelfOn.replace('&', '§');
                        player.sendMessage(plugin.getPrefix() + flySelfOn);
                    } else {
                        player.setAllowFlight(false);
                        player.setFlying(false);
                        String flySelfOff = plugin.getLanguageConfig(player).getString("FlySelfOff");
                        if (flySelfOff.contains("&"))
                            flySelfOff = flySelfOff.replace('&', '§');
                        player.sendMessage(plugin.getPrefix() + flySelfOff);
                    }
                } else {
                    sender.sendMessage(plugin.getPrefix() + plugin.getNOPERMS());
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
            }
        } else if (args.length == 1) {
            if (sender.hasPermission("essentialsmini.fly")) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null) {
                    if (!target.getAllowFlight()) {
                        target.setAllowFlight(true);
                        target.setFlying(true);
                        if (!Main.getSilent().contains(sender.getName())) {
                            String flySelfOn = plugin.getLanguageConfig(target).getString("FlySelfOn");
                            if (flySelfOn.contains("&"))
                                flySelfOn = flySelfOn.replace('&', '§');
                            target.sendMessage(plugin.getPrefix() + flySelfOn);
                        }
                        String flyOtherOn = plugin.getLanguageConfig(sender).getString("FlyOtherOn");
                        if (flyOtherOn.contains("&"))
                            flyOtherOn = flyOtherOn.replace('&', '§');
                        if (flyOtherOn.contains("%Player%"))
                            flyOtherOn = flyOtherOn.replace("%Player%", target.getName());
                        sender.sendMessage(plugin.getPrefix() + flyOtherOn);
                    } else {
                        target.setAllowFlight(false);
                        target.setFlying(false);
                        if (!Main.getSilent().contains(sender.getName())) {
                            String flySelfOff = plugin.getLanguageConfig(target).getString("FlySelfOff");
                            if (flySelfOff.contains("&"))
                                flySelfOff = flySelfOff.replace('&', '§');
                            target.sendMessage(plugin.getPrefix() + flySelfOff);
                            target.sendMessage(plugin.getPrefix() + "§cDu kannst nun nicht mehr Fliegen!");
                        }
                        String flyOtherOff = plugin.getLanguageConfig(sender).getString("FlyOtherOff");
                        if (flyOtherOff.contains("&"))
                            flyOtherOff = flyOtherOff.replace('&', '§');
                        if (flyOtherOff.contains("%Player%"))
                            flyOtherOff = flyOtherOff.replace("%Player%", target.getName());
                        sender.sendMessage(plugin.getPrefix() + flyOtherOff);
                    }
                } else {
                    sender.sendMessage(plugin.getPrefix() + plugin.getVariables().getPlayerNameNotOnline(args[0]));
                }
            } else {
                sender.sendMessage(plugin.getPrefix() + plugin.getNOPERMS());
            }
        }
        return false;
    }
}
