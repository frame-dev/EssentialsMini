package de.framedev.essentialsmini.commands.playercommands;

import de.framedev.essentialsmini.main.Main;
import de.framedev.essentialsmini.abstracts.CommandBase;
import de.framedev.essentialsmini.managers.SkinChanger;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * / This Plugin was Created by FrameDev
 * / Package : de.framedev.essentialsmini.commands.playercommands
 * / ClassName NickCMD
 * / Date: 10.10.21
 * / Project: EssentialsMini
 * / Copyrighted by FrameDev
 */

public class NickCMD extends CommandBase {

    private final HashMap<OfflinePlayer, String> nickPlayer;

    public NickCMD(Main plugin) {
        super(plugin, "nick", "nicklist");
        this.nickPlayer = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(getPlugin().getPrefix() + getPlugin().getOnlyPlayer());
            return true;
        }
        Player player = (Player) sender;
        if (command.getName().equalsIgnoreCase("nick")) {
            if (!player.hasPermission(getPlugin().getPermissionName() + "nick")) {
                player.sendMessage(getPlugin().getPrefix() + getPlugin().getNoPerms());
                return true;
            }
            if (args.length == 0) {
                if (nickPlayer.containsKey(player)) {
                    player.setDisplayName(nickPlayer.get(player));
                    player.setPlayerListName(nickPlayer.get(player));
                    player.sendMessage(getPlugin().getPrefix() + "§aNick removed!");
                    nickPlayer.remove(player);
                    SkinChanger.resetSkin(player);
                    return true;
                }
            } else if (args.length == 1) {
                String nick = args[0];
                nickPlayer.put(player, player.getName());
                player.setDisplayName(nick);
                player.setPlayerListName(nick);
                player.sendMessage(getPlugin().getPrefix() + "§aYour name has been changed to §6" + nick + "§c§l!");
                SkinChanger.changeSkin(player, nick);
                return true;
            }
        }
        return super.onCommand(sender, command, label, args);
    }
}
