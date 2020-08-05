package com.github.tommyt0mmy.firefighter.events;

import com.github.tommyt0mmy.firefighter.FireFighter;
import com.github.tommyt0mmy.firefighter.utility.UpdateChecker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class onPlayerJoin implements Listener
{
    FireFighter mainClass = FireFighter.getInstance();
    UpdateChecker updateChecker = new UpdateChecker();

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e)
    {
        Player p = e.getPlayer();

        if (p.isOp())
        {
            if (updateChecker.needsUpdate())
            {
                p.sendMessage("§aAn update for §6FireFighter§a is available at");
                p.sendMessage("§6" + mainClass.getSpigotResourceUrl());
                p.sendMessage(String.format("§aInstalled version: §e%s§a Lastest version: §e%s§r", updateChecker.getCurrent_version(), updateChecker.getLastest_version()));
            }
        }
    }

}
