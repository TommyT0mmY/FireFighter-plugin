package com.github.tommyt0mmy.firefighter.commands;

import java.util.ArrayList;
import java.util.List;

import com.github.tommyt0mmy.firefighter.FireFighter;
import com.github.tommyt0mmy.firefighter.utility.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.tommyt0mmy.firefighter.utility.XMaterial;

public class Fireset implements CommandExecutor {

    private FireFighter FireFighterClass = FireFighter.getInstance();

    private String getUsage() { //TODO Change method
        return ((String) FireFighterClass.getDescription().getCommands().get("fireset").get("usage")).replaceAll("<command>", "fireset");
    }

    @Override
    public boolean onCommand(CommandSender Sender, Command cmd, String label, String[] args) {
        if (!(Sender instanceof Player)) {
            Sender.sendMessage(FireFighterClass.messages.formattedMessage("", "only_players_command"));
            return true;
        }

        Player p = (Player) Sender;
        if (!(p.hasPermission(Permissions.FIRESET.getNode()) || p.isOp())) {
            p.sendMessage(FireFighterClass.messages.formattedMessage("§c", "invalid_permissions"));
            return true;
        }

        ItemStack wand = FireFighterClass.getConfig().getItemStack("fireset.wand");

        if (args.length == 0) { //giving the wand
            p.getInventory().addItem(wand);
            p.sendMessage(FireFighterClass.messages.formattedMessage("§e", "fireset_wand_instructions"));
        } else if (args.length > 0) {
            switch (args[0]) {
                case "deletemission": ///DELETE MISSION///
                    if (args.length != 2) {
                        p.sendMessage(getUsage());
                        break;
                    }
                    if (existsMission(args[1])) {
                        FireFighterClass.getConfig().set("missions." + args[1], null); //removes the path
                        FireFighterClass.saveConfig();
                        p.sendMessage(FireFighterClass.messages.formattedMessage("§a", "fireset_delete"));
                    } else {
                        p.sendMessage(FireFighterClass.messages.formattedMessage("§c", "fireset_mission_not_found"));
                    }
                    break;
                case "editmission": ///EDIT MISSION///
                    if (args.length < 3) {
                        p.sendMessage(getUsage()); //TODO
                        break;
                    }
                    if (!existsMission(args[1])) {
                        p.sendMessage(FireFighterClass.messages.formattedMessage("§c", "fireset_mission_not_found"));
                        break;
                    }
                    if (args[2].equals("name")) { //editing mission's name
                        if (args.length < 4) {
                            p.sendMessage(getUsage()); //TODO
                            break;
                        }
                        String newName = args[3];
                        FireFighterClass.configs.loadConfigs();
                        MemorySection mission = (MemorySection) FireFighterClass.configs.getConfigsConfiguration().get("missions." + args[1]);
                        FireFighterClass.configs.getConfigsConfiguration().set("missions." + args[1], null); //removes the old mission
                        FireFighterClass.configs.getConfigsConfiguration().set("missions." + newName, mission); //puts the new mission
                        if ( !(FireFighterClass.configs.saveToFile()) ) return false; //error on saving

                    } else if (args[2].equals("description")) { //editing mission's description
                        if (args.length < 4) {
                            p.sendMessage(getUsage()); //TODO
                            break;
                        }
                        StringBuilder newDescription = new StringBuilder();
                        for (int i = 3; i < args.length; i++) {
                            newDescription.append(args[i]).append(" ");
                        }
                        FireFighterClass.configs.getConfigsConfiguration().set("missions." + args[1] + ".description", newDescription.toString());
                        if ( !(FireFighterClass.configs.saveToFile()) ) return false; //error on saving

                    } else if (args[2].equals("rewards")) { //editing mission's rewards
                    	if (!p.hasPermission(Permissions.SET_REWARDS.getNode())) { //invalid permissions
                    		p.sendMessage(FireFighterClass.messages.formattedMessage("§c", "invalid_permissions"));
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
                    if (FireFighterClass.fireset_first_position.containsKey(p.getUniqueId()) && FireFighterClass.fireset_second_position.containsKey(p.getUniqueId())) { //checks if the area is set
                        FireFighterClass.getConfig().set("missions." + args[1] + ".first_position.x", FireFighterClass.fireset_first_position.get(p.getUniqueId()).getBlockX());
                        FireFighterClass.getConfig().set("missions." + args[1] + ".first_position.z", FireFighterClass.fireset_first_position.get(p.getUniqueId()).getBlockZ());
                        FireFighterClass.getConfig().set("missions." + args[1] + ".second_position.x", FireFighterClass.fireset_second_position.get(p.getUniqueId()).getBlockX());
                        FireFighterClass.getConfig().set("missions." + args[1] + ".second_position.z", FireFighterClass.fireset_second_position.get(p.getUniqueId()).getBlockZ());
                        FireFighterClass.getConfig().set("missions." + args[1] + ".altitude", Math.min((FireFighterClass.fireset_first_position.get(p.getUniqueId()).getBlockY()), (FireFighterClass.fireset_second_position.get(p.getUniqueId()).getBlockY())));
                        FireFighterClass.getConfig().set("missions." + args[1] + ".world", FireFighterClass.fireset_first_position.get(p.getUniqueId()).getWorld().getName());
                        if (args.length >= 3) {
                            String description = "";
                            for (int i = 2; i < args.length; i++) {
                                description += args[i] + " ";
                            }
                            FireFighterClass.getConfig().set("missions." + args[1] + ".description", description); //TODO
                        } else {
                            FireFighterClass.getConfig().set("missions." + args[1] + ".description",ChatColor.RED + "Fire at the " + args[1]); //TODO
                        }
                        p.sendMessage(FireFighterClass.messages.formattedMessage("§a", "fireset_added_mission"));
                        FireFighterClass.saveConfig();
                    } else {
                        p.sendMessage(FireFighterClass.messages.formattedMessage("§c", "fireset_invalid_selection"));
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
                        FireFighterClass.getConfig().set("fireset.wand", newWand);
                        FireFighterClass.saveConfig();
                        p.sendMessage(FireFighterClass.messages.formattedMessage("§a", "fireset_wand_set"));
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
        if (FireFighterClass.getConfig().contains("missions." + name)) {
        return true;
    }
        return false;
}
    
    private void openRewardsGUI(String missionName, Player inventoryOwner) {
    	//reading rewards informations from config.yml
    	List < ItemStack > inventoryContent = new ArrayList< ItemStack >();
    	String rewardsPath = "missions." + missionName + ".rewards";
    	int Size = 9;
    	String title = ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Rewards - " + missionName;
    	if (FireFighterClass.getConfig().get(rewardsPath) != null) { //if there are rewards set
    		int rewardsCount = FireFighterClass.getConfig().getInt(rewardsPath + ".size");
    		Size = (rewardsCount / 9 + 1) * 9;
    		for (int i = 0; i < rewardsCount; i++) {
    			ItemStack tmp = FireFighterClass.getConfig().getItemStack(rewardsPath + "." + i);
    			inventoryContent.add(tmp);
    		}
    	} else {
    		FireFighterClass.getConfig().set(rewardsPath + ".size", "0");
    	}
    	//initializing GUI
    	Inventory GUI = Bukkit.createInventory(inventoryOwner, Size + 9, title);
    	for (int i = 0; i < inventoryContent.size(); i++) {
    		ItemStack tmp = inventoryContent.get(i);
    		GUI.setItem(i, tmp);
    	}
    	ItemStack item1 = XMaterial.LIGHT_GRAY_STAINED_GLASS_PANE.parseItem(); //void part of the footer
    	ItemMeta im1 = item1.getItemMeta();
    	im1.setDisplayName("§r");
    	item1.setItemMeta(im1);
    	ItemStack item2 = XMaterial.LIME_STAINED_GLASS_PANE.parseItem(); //'add a line' button
    	ItemMeta im2 = item2.getItemMeta();
    	im2.setDisplayName (ChatColor.GREEN + "Add a line");
    	item2.setItemMeta(im2);
    	ItemStack item3 = XMaterial.RED_STAINED_GLASS_PANE.parseItem(); //'remove a line' button
    	ItemMeta im3 = item3.getItemMeta();
    	im3.setDisplayName (ChatColor.RED + "Remove a line");
    	item3.setItemMeta(im3);
    	ItemStack item4 = XMaterial.LIME_STAINED_GLASS.parseItem(); //'save changes' button
    	ItemMeta im4 = item4.getItemMeta();
    	im4.setDisplayName (ChatColor.GREEN + "Save changes");
    	item4.setItemMeta(im4);
    	//placing the footer in the inventory
    	GUI.setItem(Size, item1);
    	GUI.setItem(Size + 1, item1);
    	GUI.setItem(Size + 2, item1);
    	GUI.setItem(Size + 3, item2);
    	GUI.setItem(Size + 4, item1);
    	GUI.setItem(Size + 5, item3);
    	GUI.setItem(Size + 6, item1);
    	GUI.setItem(Size + 7, item1);
    	GUI.setItem(Size + 8, item4);
    	//opening GUI
    	inventoryOwner.openInventory(GUI);
    }

}

