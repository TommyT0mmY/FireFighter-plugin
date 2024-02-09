package com.github.tommyt0mmy.firefighter.utility.titles;

import com.github.tommyt0mmy.firefighter.utility.ReflectionUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Objects;

import static com.github.tommyt0mmy.firefighter.utility.ReflectionUtils.*;

/**
 * A reflection API for action bars in Minecraft.
 * Fully optimized - Supports 1.8.8+ and above.
 * Requires ReflectionUtils.
 * Messages are not colorized by default.
 * <p>
 * Action bars are text messages that appear above
 * the player's <a href="https://minecraft.gamepedia.com/Heads-up_display">hotbar</a>
 * Note that this is different from the text appeared when switching between items.
 * Those messages show the item's name and are different from action bars.
 * The only natural way of displaying action bars is when mounting.
 * <p>
 * Action bars cannot fade or stay like titles.
 * For static Action bars you'll need to send the packet every
 * 2 seconds (40 ticks) for it to stay on the screen without fading.
 * <p>
 * PacketPlayOutTitle: https://wiki.vg/Protocol#Title
 *
 * @author Crypto Morin
 * @version 4.0.0
 * @see ReflectionUtils
 */
public final class ActionBar {
    /**
     * If the server is running Spigot which has an official ActionBar API.
     * This should technically be available from 1.9, but TextComponent API
     * has some issues regarding colors from 1.9-1.11, so we're still going
     * to use NMS for anything below 1.12
     * We're not going to support Bukkit.
     */
    private static final boolean USE_SPIGOT_API = ReflectionUtils.supports(12);
    /**
     * ChatComponentText JSON message builder.
     */
    private static final MethodHandle CHAT_COMPONENT_TEXT;
    /**
     * PacketPlayOutChat
     */
    private static final MethodHandle PACKET_PLAY_OUT_CHAT;
    /**
     * GAME_INFO enum constant.
     */
    private static final Object CHAT_MESSAGE_TYPE;

    private static final char TIME_SPECIFIER_START = '^', TIME_SPECIFIER_END = '|';

    static {
        MethodHandle packet = null;
        MethodHandle chatComp = null;
        Object chatMsgType = null;

        if (!USE_SPIGOT_API) {
            // Supporting 1.12+ is not necessary, the package guards are just for readability.
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            Class<?> packetPlayOutChatClass = getNMSClass("network.protocol.game", "PacketPlayOutChat");
            Class<?> iChatBaseComponentClass = getNMSClass("network.chat", "IChatBaseComponent");
            Class<?> ChatSerializerClass = getNMSClass("network.chat", "IChatBaseComponent$ChatSerializer");

            try {
                // JSON Message Builder
                // network.chat.ChatComponentText is for raw messages, we need to support colors.
                chatComp = lookup.findStatic(ChatSerializerClass, "a", MethodType.methodType(iChatBaseComponentClass, String.class));

                // Game Info Message Type
                Class<?> chatMessageTypeClass = Class.forName(
                        NMS_PACKAGE + v(17, "network.chat").orElse("") + "ChatMessageType"
                );

                // Packet Constructor
                MethodType type = MethodType.methodType(void.class, iChatBaseComponentClass, chatMessageTypeClass);

                for (Object obj : chatMessageTypeClass.getEnumConstants()) {
                    String name = obj.toString();
                    if (name.equals("GAME_INFO") || name.equalsIgnoreCase("ACTION_BAR")) {
                        chatMsgType = obj;
                        break;
                    }
                }

                packet = lookup.findConstructor(packetPlayOutChatClass, type);
            } catch (NoSuchMethodException | IllegalAccessException | ClassNotFoundException ignored) {
                try {
                    // Game Info Message Type
                    chatMsgType = (byte) 2;

                    // Packet Constructor
                    packet = lookup.findConstructor(packetPlayOutChatClass, MethodType.methodType(void.class, iChatBaseComponentClass, byte.class));
                } catch (NoSuchMethodException | IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
        }

        CHAT_MESSAGE_TYPE = chatMsgType;
        CHAT_COMPONENT_TEXT = chatComp;
        PACKET_PLAY_OUT_CHAT = packet;
    }

    private ActionBar() {}

    /**
     * Sends an action bar to a player.
     *
     * @param player  the player to send the action bar to.
     * @param message the message to send.
     * @see #sendActionBar(Plugin, Player, String, long)
     * @since 1.0.0
     */
    @SuppressWarnings("DynamicRegexReplaceableByCompiledPattern")
    public static void sendActionBar(@Nonnull Player player, @Nullable String message) {
        Objects.requireNonNull(player, "Cannot send action bar to null player");
        Objects.requireNonNull(message, "Cannot send null actionbar message");

        if (USE_SPIGOT_API) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
            return;
        }

        try {
            // We need to escape both \ and " to avoid all possiblities of breaking JSON syntax and causing an exception.
            Object component = CHAT_COMPONENT_TEXT.invoke("{\"text\":\"" + message.replace("\\", "\\\\").replace("\"", "\\\"") + "\"}");
            Object packet = PACKET_PLAY_OUT_CHAT.invoke(component, CHAT_MESSAGE_TYPE);
            sendPacket(player, packet);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


    /**
     * Sends an action bar to a player for a specific amount of ticks.
     *
     * @param plugin   the plugin handling the message scheduler.
     * @param player   the player to send the action bar to.
     * @param message  the message to send.
     * @param duration the duration to keep the action bar in ticks.
     * @since 1.0.0
     */
    public static void sendActionBar(@Nonnull Plugin plugin, @Nonnull Player player, @Nullable String message, long duration) {
        if (duration < 1) return;
        Objects.requireNonNull(plugin, "Cannot send consistent actionbar with null plugin");
        Objects.requireNonNull(player, "Cannot send actionbar to null player");
        Objects.requireNonNull(message, "Cannot send null actionbar message");

        new BukkitRunnable() {
            long repeater = duration;

            @Override
            public void run() {
                sendActionBar(player, message);
                repeater -= 40L;
                if (repeater - 40L < -20L) cancel();
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 40L);
    }
}