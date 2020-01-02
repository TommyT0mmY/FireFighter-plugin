package com.github.tommyt0mmy.firefighter.events;

import com.github.tommyt0mmy.firefighter.FireFighter;
import com.github.tommyt0mmy.firefighter.utility.Permissions;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class FiresetWand implements Listener {

    private FireFighter FireFighterClass = FireFighter.getInstance();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        try {
            Player p = e.getPlayer();
            Action action = e.getAction();
            if (!(action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK)) { //only clicks on blocks
                return;
            }
            if (!p.getInventory().getItemInMainHand().equals(FireFighterClass.getConfig().getItemStack("fireset.wand"))) { //only if the player has the wand in his main hand
                return;
            }
            if (!p.hasPermission(Permissions.FIRESET.getNode())) { //only if the player has the right permission
                return;
            }

            e.setCancelled(true);
            Location clickedBlock_location = e.getClickedBlock().getLocation();
            if (action == Action.LEFT_CLICK_BLOCK) { //first position
                if (FireFighterClass.fireset_first_position.containsKey(p.getUniqueId())) {
                    if (!FireFighterClass.fireset_first_position.get(p.getUniqueId()).equals(clickedBlock_location)) {
                        setFirstPosition(p, clickedBlock_location);
                    }
                } else {
                    setFirstPosition(p, clickedBlock_location);
                }
            } else { //second position
                if (FireFighterClass.fireset_second_position.containsKey(p.getUniqueId())) {
                    if (!FireFighterClass.fireset_second_position.get(p.getUniqueId()).equals(clickedBlock_location)) {
                        setSecondPosition(p, clickedBlock_location);
                    }
                } else {
                    setSecondPosition(p, clickedBlock_location);
                }
            }

        } catch (Exception E) {}
    }

    private void setFirstPosition(Player p, Location loc) {
        FireFighterClass.fireset_first_position.put(p.getUniqueId(), loc);
        String msg = FireFighterClass.messages.formattedMessage("§e", "fireset_first_position_set");
        msg = msg.replace("<x>", loc.getBlockX() + "");
        msg = msg.replace("<y>", loc.getBlockY() + "");
        msg = msg.replace("<z>", loc.getBlockZ() + "");
        p.sendMessage(msg);
    }

    private void setSecondPosition(Player p, Location loc) {
        FireFighterClass.fireset_second_position.put(p.getUniqueId(), loc);
        String msg = FireFighterClass.messages.formattedMessage("§e", "fireset_second_position_set");
        msg = msg.replace("<x>", loc.getBlockX() + "");
        msg = msg.replace("<y>", loc.getBlockY() + "");
        msg = msg.replace("<z>", loc.getBlockZ() + "");
        p.sendMessage(msg);
    }

}