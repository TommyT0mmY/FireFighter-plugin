package com.github.tommyt0mmy.firefighter;

import com.cryptomorin.xseries.XMaterial;
import com.github.tommyt0mmy.firefighter.utility.Permissions;
import com.github.tommyt0mmy.firefighter.utility.TitleActionBarUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MissionsHandler extends BukkitRunnable {

    private FireFighter FireFighterClass = FireFighter.getInstance();

    public MissionsHandler() {
        config = FireFighterClass.getConfig();
    }

    private Boolean firstRun = true;
    private FileConfiguration config;
    private List < Block > setOnFire = new ArrayList < > ();

    @Override
    public void run() {
        if (firstRun) {
            firstRun = false;
            return;
        }
        if (!config.contains("missions")) {
            FireFighterClass.console.info("There are no missions! Start setting up new missions by typing in-game '/firefighter fireset 2'");
            return;
        }
        if (FireFighterClass.startedMission) {
            return;
        }

        int fire_lasting_ticks = Integer.parseInt(FireFighterClass.getConfig().get("fire_lasting_seconds").toString()) * 20;
        FireFighterClass.startedMission = true;
        FireFighterClass.configs.loadConfigs();
        //selecting random mission
        Random random = new Random();
        List < String > missions = new ArrayList < > (((MemorySection) config.get("missions")).getKeys(false));
        if (missions.size() < 1) {
            FireFighterClass.console.info("There are no missions! Start setting up new missions by typing in-game '/firefighter fireset 2'");
            return;
        }
        String missionName = missions.get(random.nextInt(missions.size()));
        FireFighterClass.missionName = missionName;
        String missionPath = "missions." + missionName;
        FireFighterClass.PlayerContribution.clear();
        //broadcast message
        if (config.get(missionPath + ".world").toString() == null) { //avoids NPE
        	turnOffInstructions();
        	cancel();
        }
        World world = FireFighterClass.getServer().getWorld((config.get(missionPath + ".world").toString()));
        if (world == null) { //avoids NPE
        	return;
        }
        Broadcast(world, ChatColor.DARK_RED + "Fire alert", config.get(missionPath + ".description").toString(), ChatColor.YELLOW + "At coordinates " + getMediumCoord(missionName), Permissions.ON_DUTY.getNode()); //TODO CUSTOM MESSAGES
        Broadcast(world, ChatColor.DARK_RED + "" + ChatColor.BOLD + "Fire alert at coordinates " + ChatColor.RESET + ChatColor.YELLOW + getMediumCoord(missionName), Permissions.ON_DUTY.getNode()); //TODO CUSTOM MESSAGES
        FireFighterClass.console.info("[" + world.getName() + "] Started '" + missionName + "' mission");
        //starting fire
        int y = Integer.parseInt(config.get(missionPath + ".altitude").toString());
        int x1 = Integer.parseInt(config.get(missionPath + ".first_position.x").toString());
        int z1 = Integer.parseInt(config.get(missionPath + ".first_position.z").toString());
        int x2 = Integer.parseInt(config.get(missionPath + ".second_position.x").toString());
        int z2 = Integer.parseInt(config.get(missionPath + ".second_position.z").toString());
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++) {
                Location currLocation = new Location(world, x, y, z);
                while (!currLocation.getBlock().getType().equals(Material.AIR)) {
                    currLocation.add(0, 1, 0);
                }
                if (random.nextInt(2) == 1) { //randomizing the spawning of the fire
                    continue;
                }
                currLocation.subtract(0, 1, 0);
                if (currLocation.getBlock().getType().equals(Material.AIR)) {
                    continue;
                }
                currLocation.add(0, 1, 0);
                Block currBlock = currLocation.getBlock();
                currBlock.setType(XMaterial.FIRE.parseMaterial());
                setOnFire.add(currBlock);
            }
        }

        //keeping the fire on
        new BukkitRunnable() {
            int timer = 0;
            public void run() {
                timer++;
                if (timer >= fire_lasting_ticks / 100) {
                    cancel();
                }
                for (int i = 0; i < setOnFire.size(); i++) {
                    Block currBlock = setOnFire.get(i);
                    if (currBlock.getType().equals(XMaterial.FIRE.parseMaterial()) && !currBlock.getType().equals(Material.AIR)) {
                        continue;
                    }
                    if (random.nextInt(2) == 1) { //randomizing the respawn of the fire
                        setOnFire.remove(i);
                        continue;
                    }
                    currBlock.setType(XMaterial.FIRE.parseMaterial());
                }
            }

        }.runTaskTimer(FireFighterClass, 0, 100);

        //TURNING OFF THE MISSION

        new BukkitRunnable() {
            public void run() {
            	turnOffInstructions();
                cancel();
            }
        }.runTaskTimer(FireFighterClass, (long)(fire_lasting_ticks * (1.5)), 1);
    }

    private void Broadcast(World w, String title, String subtitle, String hotbar, String permission) {
    	if (w == null) { //avoids NPE
    		return;
    	}
        for (Player dest: w.getPlayers()) {
            if (!dest.hasPermission(permission)) {
                continue;
            }
            TitleActionBarUtil.sendTitle(dest, title, 10, 100, 20);
            TitleActionBarUtil.sendSubTitle(dest,  subtitle, 10, 100, 20);
            try {
                new BukkitRunnable() {
                    int timer = 0;
                    public void run() {
                        timer++;
                        TitleActionBarUtil.sendActionBarMessage(dest, hotbar);
                        if (timer >= 4) {
                            cancel();
                        }
                    }
                }.runTaskTimer(FireFighterClass, 0, 50);
            } catch (Exception ignored) {}
        }
    }

    private void Broadcast(World w, String message, String permission) {
    	if (w == null) { //avoids NPE
    		return;
    	}
        for (Player dest: w.getPlayers()) {
            if (dest.hasPermission(permission)) {
                dest.sendMessage(message);
            }
        }
    }

    private String getMediumCoord(String missionName) { //returns the medium position of the mission
        String res = "";
        String missionPath = "missions." + missionName;
        res += (((Integer.parseInt(config.get(missionPath + ".first_position.x").toString()) + Integer.parseInt(config.get(missionPath + ".second_position.x").toString())) / 2) + ""); //X
        res += " ";
        res += (config.get(missionPath + ".altitude").toString()); // Y
        res += " ";
        res += (((Integer.parseInt(config.get(missionPath + ".first_position.z").toString()) + Integer.parseInt(config.get(missionPath + ".second_position.z").toString())) / 2) + ""); // Z
        return res;
    }
    
    private void giveRewards() {
    	String missionPath = "missions." + FireFighterClass.missionName;
    	String rewardsPath = missionPath + ".rewards";
    	String worldName = (String) FireFighterClass.getConfig().get(missionPath + ".world");
    	if (FireFighterClass.getConfig().get(rewardsPath) == null || FireFighterClass.getConfig().getInt(rewardsPath + ".size") == 0) { //no rewards set
    		FireFighterClass.getConfig().set(rewardsPath + ".size", 0);
    		FireFighterClass.console.info("There aren't rewards set for the mission! Who will complete that mission won't receive a reward :(");
    		FireFighterClass.console.info("Begin setting rewards with '/fireset editmission <name> rewards', drag items in and out and then save!");
    	}else {
    		//picking up a random reward from the rewardsList
    		Random random = new Random();
    		int randomIndex = random.nextInt(FireFighterClass.getConfig().getInt(rewardsPath + ".size"));
    		ItemStack reward = FireFighterClass.getConfig().getItemStack(rewardsPath + "." + randomIndex);
    		//picking the best player
    		UUID bestPlayer = null;
    		for (Player p : Bukkit.getWorld(worldName).getPlayers()) {
    			UUID currentUUID = p.getUniqueId();
    			if (FireFighterClass.PlayerContribution.get(currentUUID) == null) {
    				continue;
    			}
    			if (FireFighterClass.PlayerContribution.get(currentUUID) == 0) {
    				continue;
    			}
    			if (FireFighterClass.PlayerContribution.get(bestPlayer) != null) {
	    			if (FireFighterClass.PlayerContribution.get(bestPlayer) < FireFighterClass.PlayerContribution.get(currentUUID)) {
	    				bestPlayer = currentUUID;
	    			}
    			}else if (FireFighterClass.PlayerContribution.get(currentUUID) > 0) {
    				bestPlayer = currentUUID;
    			}
    		}
    		//giving the best player the selected reward
    		if (bestPlayer != null) {
    			Bukkit.getPlayer(bestPlayer).getInventory().addItem(reward);
    			Bukkit.getPlayer(bestPlayer).sendMessage(FireFighterClass.messages.formattedMessage("§a", "received_reward"));
    		}else {
    			FireFighterClass.console.info("No one contributed to the mission!");
    		}
    	}
    }
    
    private void turnOffInstructions() {
        FireFighterClass.console.info("Mission ended");
        giveRewards();
        FireFighterClass.startedMission = false;
        FireFighterClass.missionName = "";
        setOnFire.clear();
        FireFighterClass.PlayerContribution.clear();
    }
}