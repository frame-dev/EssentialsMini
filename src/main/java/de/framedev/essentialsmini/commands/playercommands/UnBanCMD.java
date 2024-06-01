package de.framedev.essentialsmini.commands.playercommands;

import de.framedev.essentialsmini.main.Main;
import de.framedev.essentialsmini.managers.BanFile;
import de.framedev.essentialsmini.managers.BanMuteManager;
import de.framedev.essentialsmini.abstracts.CommandBase;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class UnBanCMD extends CommandBase {

	public UnBanCMD(Main plugin) {
		super(plugin, "eunban");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.hasPermission(getPlugin().getPermissionName() + "unban")) {
			if(args.length == 1) {
				if(getPlugin().isMysql() || getPlugin().isSQL()) {
					new BanMuteManager().setPermBan(Bukkit.getOfflinePlayer(args[0]), BanCMD.BanType.HACKING, false);
				} else {
					BanFile.unBanPlayer(args[0]);
				}
			} else {
				sender.sendMessage(getPlugin().getPrefix() + getPlugin().getWrongArgs("/eunban <Player>"));
			}
		}
		return super.onCommand(sender,cmd,label,args);
	}
	
}
