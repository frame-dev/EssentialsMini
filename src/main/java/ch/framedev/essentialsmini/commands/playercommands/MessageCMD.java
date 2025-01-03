package ch.framedev.essentialsmini.commands.playercommands;

import ch.framedev.essentialsmini.main.Main;
import ch.framedev.essentialsmini.abstracts.CommandBase;
import ch.framedev.essentialsmini.utils.AdminBroadCast;
import ch.framedev.essentialsmini.utils.ReplaceCharConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MessageCMD extends CommandBase {

    private final Main plugin;
    private final Map<Player, Player> reply = new HashMap<>();
    private final Set<Player> spy = new HashSet<>();
    private final Set<Player> msgToggle = new HashSet<>();

    public MessageCMD(Main plugin) {
        super(plugin, "msg", "r", "spy", "msgtoggle");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        return switch (command.getName().toLowerCase()) {
            case "msgtoggle" -> {
                handleMsgToggle(sender);
                yield true;
            }
            case "msg" -> {
                handleMsg(sender, args);
                yield true;
            }
            case "r" -> {
                handleReply(sender, args);
                yield true;
            }
            case "spy" -> {
                handleSpy(sender);
                yield true;
            }
            default -> false;
        };
    }

    // ------------------------------------------
    // ✅ MSGTOGGLE COMMAND
    // ------------------------------------------
    private void handleMsgToggle(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
            return;
        }

        Player player = (Player) sender;
        if (!player.hasPermission(plugin.getPermissionBase() + "msgtoggle")) {
            player.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
            new AdminBroadCast("msgtoggle", "§cNo Permissions!", sender);
            return;
        }

        if (msgToggle.contains(player)) {
            msgToggle.remove(player);
            sendFormattedMessage(player, "MsgToggle.Deactivated");
        } else {
            msgToggle.add(player);
            sendFormattedMessage(player, "MsgToggle.Activated");
        }
    }

    // ------------------------------------------
    // ✅ MSG COMMAND
    // ------------------------------------------
    private void handleMsg(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
            return;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("essentialsmini.msg")) {
            player.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
            return;
        }

        if (args.length < 2) {
            player.sendMessage(plugin.getPrefix() + plugin.getWrongArgs("/msg <PlayerName> <Message>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(plugin.getPrefix() + "§cPlayer not found.");
            return;
        }

        if (msgToggle.contains(target) && !player.hasPermission(plugin.getPermissionBase() + "msgtoggle.bypass")) {
            sendFormattedMessage(player, "MsgToggle.Message");
            return;
        }

        String message = buildMessageFromArgs(args, 1);
        sendMessageBetweenPlayers(player, target, message);
    }

    // ------------------------------------------
    // ✅ REPLY COMMAND
    // ------------------------------------------
    private void handleReply(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
            return;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("essentialsmini.msg")) {
            player.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
            return;
        }

        if (!reply.containsKey(player)) {
            player.sendMessage(plugin.getPrefix() + "§cNo recent messages to reply to!");
            return;
        }

        Player target = reply.get(player);
        String message = buildMessageFromArgs(args, 0);
        sendMessageBetweenPlayers(player, target, message);
    }

    // ------------------------------------------
    // ✅ SPY COMMAND
    // ------------------------------------------
    private void handleSpy(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getPrefix() + plugin.getOnlyPlayer());
            return;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("essentialsmini.spy")) {
            player.sendMessage(plugin.getPrefix() + plugin.getNoPerms());
            return;
        }

        if (spy.contains(player)) {
            spy.remove(player);
            sendFormattedMessage(player, "Spy.Deactivate");
        } else {
            spy.add(player);
            sendFormattedMessage(player, "Spy.Activate");
        }
    }

    // ------------------------------------------
    // 🔧 HELPER METHODS
    // ------------------------------------------
    private String buildMessageFromArgs(String[] args, int start) {
        StringBuilder message = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            message.append(args[i]).append(" ");
        }
        return message.toString().trim();
    }

    private void sendMessageBetweenPlayers(Player sender, Player receiver, String message) {

        String messageTo = plugin.getConfig().getString("msg.notificationTo");
        if(messageTo == null) {
            sender.sendMessage(plugin.getPrefix() + "§cConfig 'msg.notificationTo' not found! Please contact the Admin!");
            return;
        }

        messageTo = ReplaceCharConfig.replaceObjectWithData(
                messageTo, "%MESSAGE%", message
        );
        messageTo = ReplaceCharConfig.replaceObjectWithData(messageTo, "%TARGET%", receiver.getName());
        messageTo = ReplaceCharConfig.replaceParagraph(messageTo);
        sender.sendMessage(messageTo);

        String messageFrom = plugin.getConfig().getString("msg.notificationFrom");
        if(messageFrom == null) {
            sender.sendMessage(plugin.getPrefix() + "§cConfig 'msg.notificationFrom' not found! Please contact the Admin!");
            return;
        }

        messageFrom = ReplaceCharConfig.replaceObjectWithData(
                messageFrom, "%PLAYER%", sender.getName()
        );
        messageFrom = ReplaceCharConfig.replaceObjectWithData(messageFrom, "%TARGET%", receiver.getName());
        messageFrom = ReplaceCharConfig.replaceObjectWithData(messageFrom, "%MESSAGE%", message);
        messageFrom = ReplaceCharConfig.replaceParagraph(messageFrom);
        receiver.sendMessage(messageFrom);

        for (Player opPlayer : spy) {
            if (opPlayer.hasPermission("essentialsmini.spy")) {
                String spyMessage = plugin.getLanguageConfig(opPlayer).getString("SpyMessage");
                if(spyMessage == null) {
                    opPlayer.sendMessage(plugin.getPrefix() + "§cConfig 'SpyMessage' not found! Please contact the Admin!");
                    return;
                }
                spyMessage = ReplaceCharConfig.replaceParagraph(spyMessage);
                spyMessage = ReplaceCharConfig.replaceObjectWithData(
                        spyMessage, "%Player%", sender.getName()
                );
                spyMessage = ReplaceCharConfig.replaceObjectWithData(
                        spyMessage, "%Target%", receiver.getName()
                );
                spyMessage = ReplaceCharConfig.replaceObjectWithData(
                        spyMessage, "%Message%", message
                );
                opPlayer.sendMessage(spyMessage);
            }
        }

        reply.put(receiver, sender);
    }

    private void sendFormattedMessage(Player player, String configKey) {
        String msg = ReplaceCharConfig.replaceParagraph(plugin.getLanguageConfig(player).getString(configKey));
        player.sendMessage(plugin.getPrefix() + msg);
    }
}
