package firefighter.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
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

import firefighter.Main;

public class FireExtinguisherActivation implements Listener
{
	private Main mainClass;
	public FireExtinguisherActivation(Main mainClass) {
		this.mainClass = mainClass;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		try {
			Player p = e.getPlayer();
			Action action = e.getAction();
			if (!(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) { //only right clicks
				return;
			}
			//checks if the fire extinguisher is being used
			ItemStack item = e.getItem();
			if (!isFireExtinguisher(item)) {
				return;
			}else {
				e.setCancelled(true);
			}
			if (!p.hasPermission(mainClass.getPermission("firetool_use"))) {
				p.sendMessage(mainClass.messages.get("invalid_permissions"));
				return;
			}
			//durability
			item.setDurability((short) (item.getDurability() + 1));
			if (item.getDurability() > 249) {
				e.setCancelled(true);
				p.getInventory().remove(item);
				playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK);
				return;
			}
			//particle effects and turning off fire
			new BukkitRunnable() {
				Location loc = p.getLocation();
				Vector direction = loc.getDirection().normalize();
				double timer = 0;
				public void run() {
					timer += 1;
					boolean playExtinguishingSound = false; //to play the sound only one time every tick
					double x = direction.getX() * timer;
					double y = direction.getY() * timer + 1.4;
					double z = direction.getZ() * timer;
					loc.add(x, y, z);
					showParticle(loc, Particle.CLOUD,(int)(timer*3.0),(int)(timer/4));
					//extinguishing the fire that is in the action range of the fire extinguisher at the "timer" tick
					// ! the further the smoke from the extinguisher goes, the more its radius increases ! //
					for (int j = 0; j < 4; j++) { //it repeats 4 times,each loop for positive x, negative x, positive z, negative z
						Location loc2 = null;
						for (int i = 0; i < ((int)(timer/4) + 1); i++) {  //  (timer/4) is the action range for each facing direction (at the "timer" tick) so the code is repeating for each block inside the range
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
							if (currBlock2.getType() == Material.FIRE) {
								currBlock2.setType(Material.AIR);
								playExtinguishingSound = true;
							}
						}
					}
					//extinguishing the fire along the facing direction without offset
					Block currBlock = loc.getBlock();
					if (currBlock.getType() == Material.FIRE) {
						currBlock.setType(Material.AIR);
						playExtinguishingSound = true;
					}
					if (playExtinguishingSound) {
						playSound(loc, Sound.BLOCK_FIRE_EXTINGUISH, 1, 0);
					}
					
					loc.subtract(x, y, z);
					if (timer > 9) {
						this.cancel();
					}
				}
			}.runTaskTimer(mainClass, 0, 1);
			//sound
			new BukkitRunnable() {
				int t = 0;
				public void run() {
					t++;
					playSound(p.getLocation(), Sound.BLOCK_CLOTH_STEP, 3, 0);
					playSound(p.getLocation(), Sound.BLOCK_SAND_PLACE, 3, 0);
					if (t > 3) {
						this.cancel();
					}
				}
			}.runTaskTimer(mainClass, 0, 1);
			return;
		
		}catch (Exception E) {E.printStackTrace();};
	}
	
	private boolean isFireExtinguisher(ItemStack item) {
		if (item == null) {return false;}
		if (!item.hasItemMeta()) {return false;}
		ItemMeta meta = item.getItemMeta();
		if (item.getType() != Material.IRON_HOE) {return false;}
		if (!meta.hasLore()) {return false;}
		if (!(meta.getLore().get(0).equals("Fire Extinguisher"))) {return false;}
		return true;
	}
	
	private void playSound(Location loc, Sound sound) {
		World w = loc.getWorld();
		w.playSound(loc, sound, 5, 0);
	}
	
	private void playSound(Location loc, Sound sound, int volume, int pitch) {
		World w = loc.getWorld();
		w.playSound(loc, sound, volume, pitch);
	}
	
	private void showParticle(Location loc, Particle particle, int count,int offsetXZ) {
		World w = loc.getWorld();
		w.spawnParticle(particle, loc, count, offsetXZ, 0, offsetXZ, 0);
	}

}
