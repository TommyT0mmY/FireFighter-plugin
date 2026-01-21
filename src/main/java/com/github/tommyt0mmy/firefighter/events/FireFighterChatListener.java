package com.github.tommyt0mmy.firefighter.events;

import com.github.tommyt0mmy.firefighter.FireFighter;
import com.github.tommyt0mmy.firefighter.commands.ChatToggle;
import com.github.tommyt0mmy.firefighter.utility.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class FireFighterChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        if (!e.getPlayer().hasPermission(Permissions.CHAT.getNode())) return;
        if (!e.getMessage().startsWith("!"))
            if (!ChatToggle.chats.contains(e.getPlayer().getUniqueId())) return;
        e.setCancelled(true);
        for (Player player : Bukkit.getOnlinePlayers()){
            if (!player.hasPermission(Permissions.CHAT.getNode())) continue;
            if (player.getUniqueId().equals(e.getPlayer().getUniqueId())) continue;
            player.sendMessage(FireFighter.colorize("&f[&cFireFighterChat&f] "+e.getPlayer().getName()+"&7: " + e.getMessage()).replaceFirst("!",""));
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,10,2);
        }
        e.getPlayer().sendMessage(FireFighter.colorize("&f[&cFireFighterChat&f] "+e.getPlayer().getName()+"&7: " + e.getMessage().replaceFirst("!","")));
    }

}
