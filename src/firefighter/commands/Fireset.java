package firefighter.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import firefighter.Main;

public class Fireset implements CommandExecutor {
	private Main mainClass;
	public Fireset(Main mainClass) {
		this.mainClass = mainClass;
	}
	
	private String getUsage() {
		return ((String) mainClass.getDescription().getCommands().get("fireset").get("usage")).replaceAll("<command>", "fireset");
	}
	
	@Override
	public boolean onCommand(CommandSender Sender, Command cmd, String label, String[] args) {
		if (!(Sender instanceof Player)) {
			Sender.sendMessage(mainClass.prefix + "Only players can execute this command!");
			return true;
		}
		Player p = (Player)Sender;
		if (!(p.hasPermission(mainClass.getPermission("fireset")) || p.isOp())) {
			p.sendMessage(mainClass.messages.get("invalid_permissions"));
			return true;
		}
		
		ItemStack wand = mainClass.getConfig().getItemStack("fireset.wand");
		
		if (args.length == 0) { //giving the wand
			p.getInventory().addItem(wand);
			p.sendMessage(mainClass.messages.get("fireset_wand_instructions"));
		}else if (args.length > 0) {
			switch(args[0]) {
			case "deletemission":				///DELETE MISSION///
				if (args.length != 2) {
					p.sendMessage(getUsage());
					break;
				}
				if (existsMission(args[1])) {
					mainClass.getConfig().set("missions." + args[1], null); //removes the path
					mainClass.saveConfig();
					p.sendMessage(mainClass.messages.get("fireset_delete"));
				}else {
					p.sendMessage(mainClass.messages.get("fireset_mission_not_found"));
				}
				break;
			case "editmission":				///EDIT MISSION///
				if (args.length < 3) {
					p.sendMessage(getUsage());
					break;
				}
				if (!existsMission(args[1])) {
					p.sendMessage(mainClass.messages.get("fireset_mission_not_found"));
					break;
				}
				if (args[2].equals("name")) {
					String newName = args[3];
					MemorySection mission = (MemorySection) mainClass.getConfig().get("missions." + args[1]);
					mainClass.getConfig().set("missions." + args[1], null); //removes the path
					mainClass.saveConfig();
					mainClass.getConfig().set("missions." + newName, mission);
					mainClass.saveConfig();
				}else if (args[2].equals("description")) {
					//TODO
				}else {
					p.sendMessage(getUsage());
					break;
				}
				break;
			case "addmission":				///ADD MISSION///
				if (args.length < 2) {
					p.sendMessage(getUsage());
					break;
				}
				if (mainClass.fireset_first_position.containsKey(p.getUniqueId().toString()) && mainClass.fireset_second_position.containsKey(p.getUniqueId().toString())) { //checks if the area is setted
					mainClass.getConfig().set("missions." + args[1] +  ".first_position.x", mainClass.fireset_first_position.get(p.getUniqueId().toString()).getBlockX());
					mainClass.getConfig().set("missions." + args[1] +  ".first_position.z", mainClass.fireset_first_position.get(p.getUniqueId().toString()).getBlockZ());
					mainClass.getConfig().set("missions." + args[1] +  ".second_position.x", mainClass.fireset_second_position.get(p.getUniqueId().toString()).getBlockX());
					mainClass.getConfig().set("missions." + args[1] +  ".second_position.z", mainClass.fireset_second_position.get(p.getUniqueId().toString()).getBlockZ());
					mainClass.getConfig().set("missions." + args[1] + ".altitude", Math.min((mainClass.fireset_first_position.get(p.getUniqueId().toString()).getBlockY()), (mainClass.fireset_second_position.get(p.getUniqueId().toString()).getBlockY())));
					mainClass.getConfig().set("missions." + args[1] + ".world", mainClass.fireset_first_position.get(p.getUniqueId().toString()).getWorld().getName());
					if (args.length >= 3) {
						String description = "";
						for (int i = 2; i < args.length; i++) {
							description += args[i] + " ";
						}
						mainClass.getConfig().set("missions." + args[1] + ".description", description);
					} else {
						mainClass.getConfig().set("missions." + args[1] + ".description", "§cFire at the " + args[1]);
					}
					p.sendMessage(mainClass.messages.get("fireset_added_mission"));
					mainClass.saveConfig();
				} else {
					p.sendMessage(mainClass.messages.get("fireset_invalid_selection"));
					break;
				}
				break;
			case "setwand":
				if (args.length != 1) {
					p.sendMessage(getUsage());
					break;
				}
				ItemStack newWand = p.getInventory().getItemInMainHand();
				if (newWand.getType() != Material.AIR) { //checks if the player has something in his hand
					newWand.setAmount(1);
					mainClass.getConfig().set("fireset.wand", newWand);
					mainClass.saveConfig();
					p.sendMessage(mainClass.messages.get("fireset_wand_setted"));
				}
				break;
			default:
				p.sendMessage(getUsage());
				break;
			}
		}
		return true;
	}
	
	private boolean existsMission(String name) {
		if (mainClass.getConfig().contains("missions." + name)) {
			return true;
		}
		return false;
	}
	
}
