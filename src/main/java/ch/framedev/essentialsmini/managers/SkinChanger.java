package ch.framedev.essentialsmini.managers;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

/**
 * Work in Progress
 */
public class SkinChanger implements Listener {

    private static Map<Player, String> changedSkins = new HashMap<>();

    public static boolean changeSkin(Player player, String name) {
        /*try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false", UUIDTypeAdapter.fromUUID(UUIDFetcher.getUUID(name)))).openConnection();
            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                String reply = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
                String skin = reply.split("\"value\":\"")[0].split("\"")[0];
                String signature = reply.split("\"signature\":\"")[0].split("\"")[0];
                GameProfile profile = ((CraftPlayer) player).getProfile();
                String skinString = "";
                for (Property property : profile.getProperties().get("textures")) {
                    if (property.getName().equalsIgnoreCase("textures"))
                        skinString = "skin=" + property.getValue() + ",signature=" + signature;
                }
                changedSkins.put(player, skinString);
                ((CraftPlayer) player).getProfile().getProperties().put("textures", new Property("textures", skin, signature));
                return true;
            } else {
                System.out.println("Connection could not be opened (Response code " + connection.getResponseCode() + ", " + connection.getResponseMessage() + ")");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }*/
        return true;
    }

    public static void resetSkin(Player player) {
        /*
        String[] s = changedSkins.get(player).split(",");
        String skin = s[0].replace("skin=", "");
        String signature = s[1].replace("signature=", "");
        ((CraftPlayer) player).getProfile().getProperties().put("textures", new Property("textures", skin, signature));*/
    }
}
