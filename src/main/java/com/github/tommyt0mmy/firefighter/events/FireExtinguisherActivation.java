package com.github.tommyt0mmy.firefighter.events;

import com.github.tommyt0mmy.firefighter.FireFighter;
import com.github.tommyt0mmy.firefighter.utility.Permissions;
import com.github.tommyt0mmy.firefighter.utility.XMaterial;
import com.github.tommyt0mmy.firefighter.utility.XSound;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class FireExtinguisherActivation implements Listener {

    private final FireFighter FireFighterClass = FireFighter.getInstance();
    private final Material fire = XMaterial.FIRE.parseMaterial();

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {

        Action action = e.getAction();
        //only right clicks
        if (!(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) return;

        try {
            Player p = e.getPlayer();
            ItemStack item = e.getItem();

            if (isFireExtinguisher(item)) e.setCancelled(true);
            else return;

            if (!p.hasPermission(Permissions.USE_EXTINGUISHER.getNode())) {
                p.sendMessage(FireFighterClass.messages.formattedMessage("§c", "invalid_permissions"));
                return;
            }

            //durability
            if (!p.hasPermission(Permissions.FREEZE_EXTINGUISHER.getNode())) {
                item.setDurability((short) (item.getDurability() + 1));
                if (item.getDurability() > 249) {
                    e.setCancelled(true);
                    p.getInventory().remove(item);
                    XSound.ENTITY_ITEM_BREAK.play(p, 5, 0);
                    return;
                }
            }

            //particle effects and turning off fire
            new BukkitRunnable() {
                final Location loc = p.getLocation();
                final Vector direction = loc.getDirection().normalize();
                double timer = 0;

                public void run() {
                    timer += 1;
                    boolean playExtinguishingSound = false; //to play the sound only one time every tick
                    double x = direction.getX() * timer;
                    double y = direction.getY() * timer + 1.4;
                    double z = direction.getZ() * timer;
                    loc.add(x, y, z);
                    showParticle(loc, Particle.CLOUD, (int) (timer), (int) (timer / 4));

                    // extinguishing the fire that is in the action range of the fire extinguisher at the "timer" tick
                    // ! the further the smoke from the extinguisher goes, the more its radius increases ! //
                    for (int j = 0; j < 4; j++) {
                        //it repeats 4 times,each loop for positive x, negative x, positive z, negative z
                        Location loc2 = null;
                        for (int i = 0; i < ((int) (timer / 4) + 1); i++) {
                            //  (timer/4) is the action range for each facing direction (at the "timer" tick)
                            //  so the code is repeating for each block inside the range
                            switch (j) {
                                case 0:
                                    loc2 = new Location(loc.getWorld(), loc.getX() + i, loc.getY(), loc.getZ());
                                    break;
                                case 1:
                                    loc2 = new Location(loc.getWorld(), loc.getX() - i, loc.getY(), loc.getZ());
                                    break;
                                case 2:
                                    loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ() + i);
                                    break;
                                case 3:
                                    loc2 = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ() - i);
                                    break;
                            }
                            Block currBlock2 = loc2.getBlock(); //if the block inside the range is fire
                            if (currBlock2.getType() == fire) {
                                currBlock2.setType(Material.AIR);
                                increaseContribution(p, loc2);
                                playExtinguishingSound = true;
                            }
                        }
                    }
                    //extinguishing the fire along the facing direction without offset
                    Block currBlock = loc.getBlock();
                    if (currBlock.getType() == fire) {
                        currBlock.setType(Material.AIR);
                        increaseContribution(p, loc);
                        playExtinguishingSound = true;
                    }
                    if (playExtinguishingSound) XSound.BLOCK_FIRE_EXTINGUISH.play(p, 1, 0);

                    loc.subtract(x, y, z);
                    if (timer > 9) this.cancel();
                }
            }.runTaskTimer(FireFighterClass, 0, 1);

            //sound
            new BukkitRunnable() {
                int t = 0;
                public void run() {
                    t++;
                    XSound.BLOCK_WOOL_STEP.play(p, 3, 0);
                    XSound.BLOCK_SAND_PLACE.play(p, 3, 0);
                    if (t > 3) this.cancel();
                }
            }.runTaskTimer(FireFighterClass, 0, 1);

        }catch (Exception error) {error.printStackTrace();}
    }

    private boolean isFireExtinguisher(ItemStack item) {
        if (item == null) return false;
        if (!item.hasItemMeta()) return false;
        if (item.getType() != FireFighter.getInstance().getFireExtinguisher().getType()) return false;

        ItemMeta meta = item.getItemMeta();

        assert meta != null;
        if (!meta.hasLore()) return false;

        return meta.getLore().get(0).equalsIgnoreCase(FireFighterClass.messages.getMessage("fire_extinguisher"));
    }

    private void showParticle(Location loc, Particle particle, int count, int offsetXZ) {
        loc.getWorld().spawnParticle(particle, loc, count, offsetXZ, 0, offsetXZ, 0);
    }

    private void increaseContribution(Player p, Location fireLocation) {
        if (!FireFighterClass.startedMission || FireFighterClass.missionName.equals(""))
        { //checks if a mission is started, if not the player will not contribute on a mission
            return;
        }
        //getting the mission's location (two opposite points of the rectangular selection, missionPos1 and missionPos2)
        String missionPath = "missions." + FireFighterClass.missionName;
        World missionWorld = FireFighterClass.getServer().getWorld((String) FireFighterClass.getConfig().get(missionPath + ".world"));
        int minX = Math.min(FireFighterClass.getConfig().getInt(missionPath + ".first_position.x"), FireFighterClass.getConfig().getInt(missionPath + ".second_position.x"));
        int maxX = Math.max(FireFighterClass.getConfig().getInt(missionPath + ".first_position.x"), FireFighterClass.getConfig().getInt(missionPath + ".second_position.x"));
        int minZ = Math.min(FireFighterClass.getConfig().getInt(missionPath + ".first_position.z"), FireFighterClass.getConfig().getInt(missionPath + ".second_position.z"));
        int maxZ = Math.max(FireFighterClass.getConfig().getInt(missionPath + ".first_position.z"), FireFighterClass.getConfig().getInt(missionPath + ".second_position.z"));
        int currX = fireLocation.getBlockX();
        int currZ = fireLocation.getBlockZ();

        //checking if the fire extinguished is inside the mission's area
        //if the world isn't the same
        if (!fireLocation.getWorld().equals(missionWorld)) return;

        //x position out of range
        if (currX > maxX || currX < minX) return;

        //z position out of range
        if (currZ > maxZ || currZ < minZ) return;

        //incrementing by one the player's contribution count or setting it to 1 if it's the first contribution
        if (FireFighterClass.PlayerContribution.containsKey(p.getUniqueId())) {
            int tmp = FireFighterClass.PlayerContribution.get(p.getUniqueId()) + 1;
            FireFighterClass.PlayerContribution.put(p.getUniqueId(), tmp);
        }else FireFighterClass.PlayerContribution.put(p.getUniqueId(), 1);

    }

}