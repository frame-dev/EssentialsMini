package ch.framedev.essentialsmini.utils;



/*
 * ch.framedev.essentialsmini.utils
 * =============================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * =============================================
 * This Class was created at 28.01.2025 22:37
 */

import ch.framedev.essentialsmini.main.Main;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import com.google.common.collect.Iterables;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProtocolLibUtil {

    public void createMotdHover() {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

        protocolManager.addPacketListener(new PacketAdapter(Main.getPlugin(Main.class), PacketType.Status.Server.SERVER_INFO) {

            @Override
            public void onPacketSending(PacketEvent event) {
                try {
                    WrappedServerPing ping = event.getPacket().getServerPings().read(0);

                    // Add hover text as part of the MOTD
                    ping.setMotD(WrappedChatComponent.fromJson("{\"text\":\"§cThis server is currently in maintenance mode!\",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":{\"text\":\"§cHover text: Maintenance ongoing.\"}}}"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
