package com.github.tommyt0mmy.firefighter.events;

import com.github.tommyt0mmy.firefighter.FireFighter;
import com.github.tommyt0mmy.firefighter.utility.Permissions;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class FiresetWand implements Listener {

    private final FireFighter fireFighterClass = FireFighter.getInstance();
    public static ItemStack wand;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        try {
            Player p = e.getPlayer();
            Action action = e.getAction();
            //only clicks on blocks
            if (!(action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK)) return;

            //only if the player has the right permission
            if (!p.hasPermission(Permissions.FIRESET.getNode())) {
                p.sendMessage(fireFighterClass.messages.formattedMessage("§c", "invalid_permissions"));
                e.setCancelled(true);
                return;
            }

            //only if the player has the wand in his main hand
            if (!p.getInventory().getItemInMainHand().isSimilar(wand)) return;

            e.setCancelled(true);
            Location clickedBlock_location = e.getClickedBlock().getLocation();
            //first position
            if (action == Action.LEFT_CLICK_BLOCK)
                if (fireFighterClass.fireset_first_position.containsKey(p.getUniqueId()))
                    if (!fireFighterClass.fireset_first_position.get(p.getUniqueId()).equals(clickedBlock_location)){
                        setFirstPosition(p, clickedBlock_location);
                        p.sendMessage("&6[1] &a&lFirst position set");
                    }
                else setFirstPosition(p, clickedBlock_location);
            else  //second position
                if (fireFighterClass.fireset_second_position.containsKey(p.getUniqueId()))
                    if (!fireFighterClass.fireset_second_position.get(p.getUniqueId()).equals(clickedBlock_location)){
                        setSecondPosition(p, clickedBlock_location);
                        p.sendMessage("&6[2] &2&lSecond position set");
                    }
                else setSecondPosition(p, clickedBlock_location);

        }catch (Exception ignored) {}
    }

    private void setFirstPosition(Player p, Location loc) {
        fireFighterClass.fireset_first_position.put(p.getUniqueId(), loc);
        String msg = fireFighterClass.messages.formattedMessage("§e", "fireset_first_position_set");
        msg = msg.replace("<x>", loc.getBlockX() + "");
        msg = msg.replace("<y>", loc.getBlockY() + "");
        msg = msg.replace("<z>", loc.getBlockZ() + "");
        p.sendMessage(msg);
    }

    private void setSecondPosition(Player p, Location loc) {
        fireFighterClass.fireset_second_position.put(p.getUniqueId(), loc);
        String msg = fireFighterClass.messages.formattedMessage("§e", "fireset_second_position_set");
        msg = msg.replace("<x>", loc.getBlockX() + "");
        msg = msg.replace("<y>", loc.getBlockY() + "");
        msg = msg.replace("<z>", loc.getBlockZ() + "");
        p.sendMessage(msg);
    }

}