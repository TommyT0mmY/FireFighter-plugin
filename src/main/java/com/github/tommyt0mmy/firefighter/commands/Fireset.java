package com.github.tommyt0mmy.firefighter.commands;

import java.util.ArrayList;
import java.util.List;

import com.github.tommyt0mmy.firefighter.FireFighter;
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
    private FireFighter fireFighterClass;
    public Fireset(FireFighter fireFighterClass) {
        this.fireFighterClass = fireFighterClass;
    }

    private String getUsage() {
        return ((String) fireFighterClass.getDescription().getCommands().get("fireset").get("usage")).replaceAll("<command>", "fireset");
    }

    @Override
    public boolean onCommand(CommandSender Sender, Command cmd, String label, String[] args) {
        if (!(Sender instanceof Player)) {
            Sender.sendMessage(fireFighterClass.prefix + "Only players can execute this command!");
            return true;
        }
        Player p = (Player) Sender;
        if (!(p.hasPermission(fireFighterClass.getPermission("fireset")) || p.isOp())) {
            p.sendMessage(fireFighterClass.messages.get("invalid_permissions"));
            return true;
        }

        ItemStack wand = fireFighterClass.getConfig().getItemStack("fireset.wand");

        if (args.length == 0) { //giving the wand
            p.getInventory().addItem(wand);
            p.sendMessage(fireFighterClass.messages.get("fireset_wand_instructions"));
        } else if (args.length > 0) {
            switch (args[0]) {
                case "deletemission": ///DELETE MISSION///
                    if (args.length != 2) {
                        p.sendMessage(getUsage());
                        break;
                    }
                    if (existsMission(args[1])) {
                        fireFighterClass.getConfig().set("missions." + args[1], null); //removes the path
                        fireFighterClass.saveConfig();
                        p.sendMessage(fireFighterClass.messages.get("fireset_delete"));
                    } else {
                        p.sendMessage(fireFighterClass.messages.get("fireset_mission_not_found"));
                    }
                    break;
                case "editmission": ///EDIT MISSION///
                    if (args.length < 3) {
                        p.sendMessage(getUsage());
                        break;
                    }
                    if (!existsMission(args[1])) {
                        p.sendMessage(fireFighterClass.messages.get("fireset_mission_not_found"));
                        break;
                    }
                    if (args[2].equals("name")) { //editing mission's name
                        if (args.length < 4) {
                            p.sendMessage(getUsage());
                            break;
                        }
                        String newName = args[3];
                        MemorySection mission = (MemorySection) fireFighterClass.getConfig().get("missions." + args[1]);
                        fireFighterClass.getConfig().set("missions." + args[1], null); //removes the path
                        fireFighterClass.saveConfig();
                        fireFighterClass.getConfig().set("missions." + newName, mission);
                        fireFighterClass.saveConfig();

                    } else if (args[2].equals("description")) { //editing mission's description
                        if (args.length < 4) {
                            p.sendMessage(getUsage());
                            break;
                        }
                        String newDescription = args[3];
                        for (int i = 3; i < args.length; i++) {
                            newDescription += args[i] + " ";
                        }
                        fireFighterClass.getConfig().set("missions." + args[1] + ".description", newDescription);
                        fireFighterClass.saveConfig();
                    } else if (args[2].equals("rewards")) { //editing mission's rewards
                    	if (!p.hasPermission(fireFighterClass.getPermission("rewardset"))) { //invalid permissions
                    		p.sendMessage(fireFighterClass.messages.get("invalid_permissions"));
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
                    if (fireFighterClass.fireset_first_position.containsKey(p.getUniqueId()) && fireFighterClass.fireset_second_position.containsKey(p.getUniqueId())) { //checks if the area is setted
                        fireFighterClass.getConfig().set("missions." + args[1] + ".first_position.x", fireFighterClass.fireset_first_position.get(p.getUniqueId()).getBlockX());
                        fireFighterClass.getConfig().set("missions." + args[1] + ".first_position.z", fireFighterClass.fireset_first_position.get(p.getUniqueId()).getBlockZ());
                        fireFighterClass.getConfig().set("missions." + args[1] + ".second_position.x", fireFighterClass.fireset_second_position.get(p.getUniqueId()).getBlockX());
                        fireFighterClass.getConfig().set("missions." + args[1] + ".second_position.z", fireFighterClass.fireset_second_position.get(p.getUniqueId()).getBlockZ());
                        fireFighterClass.getConfig().set("missions." + args[1] + ".altitude", Math.min((fireFighterClass.fireset_first_position.get(p.getUniqueId()).getBlockY()), (fireFighterClass.fireset_second_position.get(p.getUniqueId()).getBlockY())));
                        fireFighterClass.getConfig().set("missions." + args[1] + ".world", fireFighterClass.fireset_first_position.get(p.getUniqueId()).getWorld().getName());
                        if (args.length >= 3) {
                            String description = "";
                            for (int i = 2; i < args.length; i++) {
                                description += args[i] + " ";
                            }
                            fireFighterClass.getConfig().set("missions." + args[1] + ".description", description);
                        } else {
                            fireFighterClass.getConfig().set("missions." + args[1] + ".description",ChatColor.RED + "Fire at the " + args[1]);
                        }
                        p.sendMessage(fireFighterClass.messages.get("fireset_added_mission"));
                        fireFighterClass.saveConfig();
                    } else {
                        p.sendMessage(fireFighterClass.messages.get("fireset_invalid_selection"));
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
                        fireFighterClass.getConfig().set("fireset.wand", newWand);
                        fireFighterClass.saveConfig();
                        p.sendMessage(fireFighterClass.messages.get("fireset_wand_setted"));
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
        if (fireFighterClass.getConfig().contains("missions." + name)) {
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
    	if (fireFighterClass.getConfig().get(rewardsPath) != null) { //if there are rewards set
    		int rewardsCount = fireFighterClass.getConfig().getInt(rewardsPath + ".size");
    		Size = (rewardsCount / 9 + 1) * 9;
    		for (int i = 0; i < rewardsCount; i++) {
    			ItemStack tmp = fireFighterClass.getConfig().getItemStack(rewardsPath + "." + i);
    			inventoryContent.add(tmp);
    		}
    	} else {
    		fireFighterClass.getConfig().set(rewardsPath + ".size", "0");
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
    	GUI.setItem(Size + 0, item1);
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

