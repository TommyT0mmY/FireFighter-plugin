package firefighter.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import firefighter.Main;
import firefighter.utility.XMaterial;

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
        Player p = (Player) Sender;
        if (!(p.hasPermission(mainClass.getPermission("fireset")) || p.isOp())) {
            p.sendMessage(mainClass.messages.get("invalid_permissions"));
            return true;
        }

        ItemStack wand = mainClass.getConfig().getItemStack("fireset.wand");

        if (args.length == 0) { //giving the wand
            p.getInventory().addItem(wand);
            p.sendMessage(mainClass.messages.get("fireset_wand_instructions"));
        } else if (args.length > 0) {
            switch (args[0]) {
                case "deletemission": ///DELETE MISSION///
                    if (args.length != 2) {
                        p.sendMessage(getUsage());
                        break;
                    }
                    if (existsMission(args[1])) {
                        mainClass.getConfig().set("missions." + args[1], null); //removes the path
                        mainClass.saveConfig();
                        p.sendMessage(mainClass.messages.get("fireset_delete"));
                    } else {
                        p.sendMessage(mainClass.messages.get("fireset_mission_not_found"));
                    }
                    break;
                case "editmission": ///EDIT MISSION///
                    if (args.length < 3) {
                        p.sendMessage(getUsage());
                        break;
                    }
                    if (!existsMission(args[1])) {
                        p.sendMessage(mainClass.messages.get("fireset_mission_not_found"));
                        break;
                    }
                    if (args[2].equals("name")) { //editing mission's name
                        String newName = args[3];
                        MemorySection mission = (MemorySection) mainClass.getConfig().get("missions." + args[1]);
                        mainClass.getConfig().set("missions." + args[1], null); //removes the path
                        mainClass.saveConfig();
                        mainClass.getConfig().set("missions." + newName, mission);
                        mainClass.saveConfig();
                    } else if (args[2].equals("description")) { //editing mission's description
                        String newDescription = args[3];
                        for (int i = 3; i < args.length; i++) {
                            newDescription += args[i] + " ";
                        }
                        mainClass.getConfig().set("missions." + args[1] + ".description", newDescription);
                        mainClass.saveConfig();
                    } else if (args[2].equals("rewards")) { //editing mission's rewards
                    	if (!p.hasPermission(mainClass.getPermission("rewardset"))) { //invalid permissions
                    		p.sendMessage(mainClass.messages.get("invalid_permissions"));
                    		return true;
                    	}
                    	openRewardsGUI(args[1], p);
                    }else {
                        p.sendMessage(getUsage());
                        break;
                    }
                    break;
                case "addmission": ///ADD MISSION///
                    if (args.length < 2) {
                        p.sendMessage(getUsage());
                        break;
                    }
                    if (mainClass.fireset_first_position.containsKey(p.getUniqueId()) && mainClass.fireset_second_position.containsKey(p.getUniqueId())) { //checks if the area is setted
                        mainClass.getConfig().set("missions." + args[1] + ".first_position.x", mainClass.fireset_first_position.get(p.getUniqueId()).getBlockX());
                        mainClass.getConfig().set("missions." + args[1] + ".first_position.z", mainClass.fireset_first_position.get(p.getUniqueId()).getBlockZ());
                        mainClass.getConfig().set("missions." + args[1] + ".second_position.x", mainClass.fireset_second_position.get(p.getUniqueId()).getBlockX());
                        mainClass.getConfig().set("missions." + args[1] + ".second_position.z", mainClass.fireset_second_position.get(p.getUniqueId()).getBlockZ());
                        mainClass.getConfig().set("missions." + args[1] + ".altitude", Math.min((mainClass.fireset_first_position.get(p.getUniqueId()).getBlockY()), (mainClass.fireset_second_position.get(p.getUniqueId()).getBlockY())));
                        mainClass.getConfig().set("missions." + args[1] + ".world", mainClass.fireset_first_position.get(p.getUniqueId()).getWorld().getName());
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
    
    private void openRewardsGUI(String missionName, Player inventoryOwner) {
    	//reading rewards informations from config.yml
    	List < ItemStack > inventoryContent = new ArrayList< ItemStack >();
    	String rewardsPath = "missions." + missionName + ".rewards";
    	int Size = 9;
    	String title = "§d§lRewards - " + missionName;
    	if (mainClass.getConfig().get(rewardsPath) != null) { //if there are rewards set
    		int rewardsCount = mainClass.getConfig().getInt(rewardsPath + ".size");
    		Size = (rewardsCount / 9 + 1) * 9;
    		for (int i = 0; i < rewardsCount; i++) {
    			ItemStack tmp = mainClass.getConfig().getItemStack(rewardsPath + "." + i);
    			inventoryContent.add(tmp);
    		}
    	} else {
    		mainClass.getConfig().set(rewardsPath + ".size", "0");
    	}
    	//initializing GUI
    	Inventory GUI = Bukkit.createInventory(inventoryOwner, Size + 9, title);
    	for (int i = 0; i < inventoryContent.size(); i++) {
    		ItemStack tmp = inventoryContent.get(i);
    		GUI.setItem(i, tmp);
    	}
    	ItemStack item1 = XMaterial.LIGHT_GRAY_STAINED_GLASS_PANE.parseItem();
    	ItemMeta im1 = item1.getItemMeta();
    	im1.setDisplayName("§r");
    	item1.setItemMeta(im1);
    	ItemStack item2 = XMaterial.LIME_STAINED_GLASS_PANE.parseItem();
    	ItemMeta im2 = item2.getItemMeta();
    	im2.setDisplayName("§aAdd a line");
    	item2.setItemMeta(im2);
    	ItemStack item3 = XMaterial.RED_STAINED_GLASS_PANE.parseItem();
    	ItemMeta im3 = item3.getItemMeta();
    	im3.setDisplayName("§cRemove a line");
    	item3.setItemMeta(im3);
    	GUI.setItem(Size + 0, item1);
    	GUI.setItem(Size + 1, item1);
    	GUI.setItem(Size + 2, item1);
    	GUI.setItem(Size + 3, item2);
    	GUI.setItem(Size + 4, item1);
    	GUI.setItem(Size + 5, item3);
    	GUI.setItem(Size + 6, item1);
    	GUI.setItem(Size + 7, item1);
    	GUI.setItem(Size + 8, item1);
    	//opening GUI
    	inventoryOwner.openInventory(GUI);
    }

}

