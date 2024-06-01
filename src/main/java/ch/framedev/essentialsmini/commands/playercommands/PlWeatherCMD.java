package ch.framedev.essentialsmini.commands.playercommands;

import ch.framedev.essentialsmini.abstracts.CommandBase;
import ch.framedev.essentialsmini.main.Main;
import org.bukkit.WeatherType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlWeatherCMD extends CommandBase {

    public PlWeatherCMD(Main plugin) {
        super(plugin, "plweather", "resetplweather");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("plweather")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(getPlugin().getPrefix() + getPlugin().getOnlyPlayer());
                return true;
            }
            if (!sender.hasPermission("essentialsmini.plweather")) {
                sender.sendMessage(getPrefix() + getPlugin().getNoPerms());
                return true;
            }
            Player player = (Player) sender;
            if (args.length == 1) {
                String weather = args[0];
                WeatherType effectiveWeather = null;
                switch (weather) {
                    case "sun":
                    case "clear":
                        effectiveWeather = WeatherType.CLEAR;
                        break;
                    case "downfall":
                    case "rain":
                    case "thunder":
                        effectiveWeather = WeatherType.DOWNFALL;
                        break;
                }
                if (effectiveWeather == null) {
                    player.sendMessage(getPrefix() + "§cThis Weather Type doesn't exist!");
                    return true;
                }
                player.setPlayerWeather(effectiveWeather);
                player.sendMessage(getPrefix() + "§aYour Player Weather has been changed to §6" + effectiveWeather.name());
                return true;
            }
        } else if(command.getName().equalsIgnoreCase("resetplweather")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(getPlugin().getPrefix() + getPlugin().getOnlyPlayer());
                return true;
            }
            if (!sender.hasPermission("essentialsmini.plweather")) {
                sender.sendMessage(getPrefix() + getPlugin().getNoPerms());
                return true;
            }
            ((Player) sender).resetPlayerWeather();
            sender.sendMessage(getPrefix() + "§aYour Player Weather has been reset!");
            return true;
        }
        return super.onCommand(sender, command, label, args);
    }
}
