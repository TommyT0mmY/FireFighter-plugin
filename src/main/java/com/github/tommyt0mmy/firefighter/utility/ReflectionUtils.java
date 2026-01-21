package com.github.tommyt0mmy.firefighter.utility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class ReflectionUtils {
    
    @Nullable
    public static final String NMS_VERSION = findNMSVersionString();

    @Nullable
    public static String findNMSVersionString() {
        String found = null;
        for (Package pack : Package.getPackages()) {
            String name = pack.getName();


            if (name.startsWith("org.bukkit.craftbukkit.v")) {
                found = pack.getName().split("\\.")[3];
                try {
                    Class.forName("org.bukkit.craftbukkit." + found + ".entity.CraftPlayer");
                    break;
                }catch (ClassNotFoundException e) {found = null;}
            }
        }
        return found;
    }

    public static final int MAJOR_NUMBER;
    
    public static final int MINOR_NUMBER;
    
    public static final int PATCH_NUMBER;

    static {
        Matcher bukkitVer = Pattern
                .compile("^(?<major>\\d+)\\.(?<minor>\\d+)\\.(?<patch>\\d+)?")
                .matcher(Bukkit.getBukkitVersion());
        if (bukkitVer.find()) {
            try {

                String patch = bukkitVer.group("patch");
                MAJOR_NUMBER = Integer.parseInt(bukkitVer.group("major"));
                MINOR_NUMBER = Integer.parseInt(bukkitVer.group("minor"));
                PATCH_NUMBER = Integer.parseInt((patch == null || patch.isEmpty()) ? "0" : patch);
            } catch (Throwable ex) {
                throw new RuntimeException("Failed to parse minor number: " + bukkitVer + ' ' + getVersionInformation(), ex);
            }
        } else {
            throw new IllegalStateException("Cannot parse server version: \"" + Bukkit.getBukkitVersion() + '"');
        }
    }

    
    public static String getVersionInformation() {
        return "(NMS: " + NMS_VERSION + " | " +
                "Parsed: " + MAJOR_NUMBER + '.' + MINOR_NUMBER + '.' + PATCH_NUMBER + " | " +
                "Minecraft: " + Bukkit.getVersion() + " | " +
                "Bukkit: " + Bukkit.getBukkitVersion() + ')';
    }


    public static final String
            CRAFTBUKKIT_PACKAGE = Bukkit.getServer().getClass().getPackage().getName(),
            NMS_PACKAGE = v(17, "net.minecraft.").orElse("net.minecraft.server." + NMS_VERSION + '.');
    
    private static final MethodHandle PLAYER_CONNECTION;
    
    private static final MethodHandle GET_HANDLE;
    
    private static final MethodHandle SEND_PACKET;

    static {
        Class<?> entityPlayer = getNMSClass("server.level", "EntityPlayer");
        Class<?> craftPlayer = getCraftClass("entity.CraftPlayer");
        Class<?> playerConnection = getNMSClass("server.network", "PlayerConnection");
        Class<?> playerCommonConnection;
        if (supports(20) && supportsPatch(2)) {

            playerCommonConnection = getNMSClass("server.network", "ServerCommonPacketListenerImpl");
        } else {
            playerCommonConnection = playerConnection;
        }

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle sendPacket = null, getHandle = null, connection = null;

        try {
            connection = lookup.findGetter(entityPlayer,
                    v(20, "c").v(17, "b").orElse("playerConnection"), playerConnection);
            getHandle = lookup.findVirtual(craftPlayer, "getHandle", MethodType.methodType(entityPlayer));
            sendPacket = lookup.findVirtual(playerCommonConnection,
                    v(20, 2, "b").v(18, "a").orElse("sendPacket"),
                    MethodType.methodType(void.class, getNMSClass("network.protocol", "Packet")));
        } catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
        }

        PLAYER_CONNECTION = connection;
        SEND_PACKET = sendPacket;
        GET_HANDLE = getHandle;
    }

    private ReflectionUtils() {
    }

    
    public static <T> VersionHandler<T> v(int version, T handle) {
        return new VersionHandler<>(version, handle);
    }

    
    public static <T> VersionHandler<T> v(int version, int patch, T handle) {
        return new VersionHandler<>(version, patch, handle);
    }


    public static boolean supports(int minorNumber) {
        return MINOR_NUMBER >= minorNumber;
    }


    public static boolean supportsPatch(int patchNumber) {
        return PATCH_NUMBER >= patchNumber;
    }

    
    @Nonnull
    public static Class<?> getNMSClass(@Nullable String packageName, @Nonnull String name) {
        if (packageName != null && supports(17)) name = packageName + '.' + name;

        try {
            return Class.forName(NMS_PACKAGE + name);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    
    @Nonnull
    public static Class<?> getNMSClass(@Nonnull String name) {
        return getNMSClass(null, name);
    }

    
    @Nonnull
    public static CompletableFuture<Void> sendPacket(@Nonnull Player player, @Nonnull Object... packets) {
        return CompletableFuture.runAsync(() -> sendPacketSync(player, packets))
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }

    
    public static void sendPacketSync(@Nonnull Player player, @Nonnull Object... packets) {
        try {
            Object handle = GET_HANDLE.invoke(player);
            Object connection = PLAYER_CONNECTION.invoke(handle);


            if (connection != null) {
                for (Object packet : packets) SEND_PACKET.invoke(connection, packet);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Nonnull
    public static Class<?> getCraftClass(@Nonnull String name) {
        try {
            return Class.forName(CRAFTBUKKIT_PACKAGE + '.' + name);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }


    public static final class VersionHandler<T> {
        private int version, patch;
        private T handle;

        private VersionHandler(int version, T handle) {
            this(version, 0, handle);
        }

        private VersionHandler(int version, int patch, T handle) {
            if (supports(version) && supportsPatch(patch)) {
                this.version = version;
                this.patch = patch;
                this.handle = handle;
            }
        }

        public VersionHandler<T> v(int version, T handle) {
            return v(version, 0, handle);
        }

        public VersionHandler<T> v(int version, int patch, T handle) {
            if (version == this.version && patch == this.patch)
                throw new IllegalArgumentException("Cannot have duplicate version handles for version: " + version + '.' + patch);
            if (version > this.version && supports(version) && patch >= this.patch && supportsPatch(patch)) {
                this.version = version;
                this.patch = patch;
                this.handle = handle;
            }
            return this;
        }


        public T orElse(T handle) {
            return this.version == 0 ? handle : this.handle;
        }
    }

    public static final class CallableVersionHandler<T> {
        private int version;
        private Callable<T> handle;

        private CallableVersionHandler(int version, Callable<T> handle) {
            if (supports(version)) {
                this.version = version;
                this.handle = handle;
            }
        }

    }
}