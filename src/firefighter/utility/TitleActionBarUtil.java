package firefighter.utility;

import java.lang.reflect.*;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TitleActionBarUtil {

    // These are the Class instances. Needed to get fields or methods for classes.
    private static Class < ? > CRAFTPLAYERCLASS, PACKET_PLAYER_CHAT_CLASS, ICHATCOMP, CHATMESSAGE, PACKET_CLASS,
        CHAT_MESSAGE_TYPE_CLASS;

    private static Field PLAYERCONNECTION;
    private static Method GETHANDLE, SENDPACKET;

    // These are the constructors for those classes. Need to create new objects.
    private static Constructor < ? > PACKET_PLAYER_CHAT_CONSTRUCTOR, CHATMESSAGE_CONSTRUCTOR;

    // Used in 1.12+. Bytes are replaced with this enum
    private static Object CHAT_MESSAGE_TYPE_ENUM_OBJECT;

    // This is the server version. This is how we know the server version.
    private static final String SERVER_VERSION;
    private static boolean useByte = false;
    static {
        // This gets the server version.
        String name = Bukkit.getServer().getClass().getName();
        name = name.substring(name.indexOf("craftbukkit.") + "craftbukkit.".length());
        name = name.substring(0, name.indexOf("."));
        SERVER_VERSION = name;
        try {
            // This here sets the class fields.
            CRAFTPLAYERCLASS = Class.forName("org.bukkit.craftbukkit." + SERVER_VERSION + ".entity.CraftPlayer");
            PACKET_PLAYER_CHAT_CLASS = Class.forName("net.minecraft.server." + SERVER_VERSION + ".PacketPlayOutChat");
            PACKET_CLASS = Class.forName("net.minecraft.server." + SERVER_VERSION + ".Packet");
            ICHATCOMP = Class.forName("net.minecraft.server." + SERVER_VERSION + ".IChatBaseComponent");
            GETHANDLE = CRAFTPLAYERCLASS.getMethod("getHandle");
            PLAYERCONNECTION = GETHANDLE.getReturnType().getField("playerConnection");
            SENDPACKET = PLAYERCONNECTION.getType().getMethod("sendPacket", PACKET_CLASS);
            try {
                CHAT_MESSAGE_TYPE_CLASS = Class.forName("net.minecraft.server." + SERVER_VERSION + ".ChatMessageType");
                CHAT_MESSAGE_TYPE_ENUM_OBJECT = CHAT_MESSAGE_TYPE_CLASS.getEnumConstants()[2];

                PACKET_PLAYER_CHAT_CONSTRUCTOR = PACKET_PLAYER_CHAT_CLASS.getConstructor(ICHATCOMP,
                    CHAT_MESSAGE_TYPE_CLASS);
            } catch (NoSuchMethodException e) {
                PACKET_PLAYER_CHAT_CONSTRUCTOR = PACKET_PLAYER_CHAT_CLASS.getConstructor(ICHATCOMP, byte.class);
                useByte = true;
            }
            CHATMESSAGE = Class.forName("net.minecraft.server." + SERVER_VERSION + ".ChatMessage");
            CHATMESSAGE_CONSTRUCTOR = CHATMESSAGE.getConstructor(String.class, Object[].class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends the hotbar message 'message' to the player 'player'
     *
     * @param player
     * @param message
     * @throws Exception
     */
    public static void sendActionBarMessage(Player player, String message) {
        try {
            // This creates the IChatComponentBase instance
            Object icb = CHATMESSAGE_CONSTRUCTOR.newInstance(message, new Object[0]);
            // This creates the packet
            Object packet;
            if (useByte)
                packet = PACKET_PLAYER_CHAT_CONSTRUCTOR.newInstance(icb, (byte) 2);
            else
                packet = PACKET_PLAYER_CHAT_CONSTRUCTOR.newInstance(icb, CHAT_MESSAGE_TYPE_ENUM_OBJECT);
            // This casts the player to a craftplayer
            Object craftplayerInst = CRAFTPLAYERCLASS.cast(player);
            // This invokes the method above.
            Object methodhHandle = GETHANDLE.invoke(craftplayerInst);
            // This gets the player's connection
            Object playerConnection = PLAYERCONNECTION.get(methodhHandle);
            // This sends the packet.
            SENDPACKET.invoke(playerConnection, packet);
        } catch (Exception e) {
            failsafe("sendActionBarMessage");
        }
    }
    
    public static void sendTitle(Player p, String text, int fadein, int showtime, int fadeout) {
    	try {
    	    Object enumTitle = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
    	    Object titleChat = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + text + "\"}");

    	    Constructor < ? > titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
    	    Object titlePacket = titleConstructor.newInstance(enumTitle, titleChat, fadein, showtime, fadeout);

    	    sendPacket(p, titlePacket);
    	} catch (Exception e) {
    		failsafe("sendTitle");
    	}
    }
    
    public static void sendSubTitle(Player p, String text, int fadein, int showtime, int fadeout) {
    	try {
    		Object enumSubtitle = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null);
            Object subtitleChat = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + text + "\"}");

    	    Constructor < ? > titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
    	    Object subtitlePacket = titleConstructor.newInstance(enumSubtitle, subtitleChat, fadein, showtime, fadeout);

    	    sendPacket(p, subtitlePacket);
    	} catch (Exception e) {
    		failsafe("sendSubTitle");
    	}
    }
    
    private static Class <?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void failsafe(String message) {
        Bukkit.getLogger().log(Level.WARNING, "[ShootingGallery] ActionBar and Titles System disabled! Something went wrong with: " + message);
        Bukkit.getLogger().log(Level.WARNING, "[ShootingGallery] Report this to SBDeveloper");
        Bukkit.getLogger().log(Level.WARNING, "[ShootingGallery] Needed Information: " + Bukkit.getName() + ", " + Bukkit.getVersion() + ", " + Bukkit.getBukkitVersion());
        Bukkit.getLogger().log(Level.WARNING, "[ShootingGallery] [URL]https://github.com/DeveloperBoy/ShootingGallery[/URL]");
    }
}