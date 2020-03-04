package com.github.tommyt0mmy.firefighter.commands;

import com.github.tommyt0mmy.firefighter.FireFighter;
import com.github.tommyt0mmy.firefighter.utility.Permissions;
import com.github.tommyt0mmy.firefighter.utility.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

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

        FireFighterClass.configs.loadConfigs();

        ItemStack wand = FireFighterClass.configs.getConfig().getItemStack("fireset.wand");

        if (args.length == 0) { //giving the wand
            p.getInventory().addItem(wand);
            p.sendMessage(FireFighterClass.messages.formattedMessage("§e", "fireset_wand_instructions"));
        } else {
            command_switch:
            switch (args[0]) {


                case "startmission":
                    //perm check
                    if (!p.hasPermission(Permissions.START_MISSION.getNode())) {
                        p.sendMessage(FireFighterClass.messages.formattedMessage("§c", "invalid_permissions"));
                    }
                    //arguments check
                    if (args.length != 2) {
                        p.sendMessage(getUsage());
                        return true;
                    }
                    if (existsMission(args[1])) {
                        if (FireFighterClass.startedMission) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', FireFighterClass.messages.formattedMessage("§c", "fireset_another_mission_started")));
                            return true;
                        }
                        FireFighterClass.missionName = args[1];
                        FireFighterClass.programmedStart = true;
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', FireFighterClass.messages.formattedMessage("§a", "fireset_started_mission")));
                    } else {
                        p.sendMessage(FireFighterClass.messages.formattedMessage("§c", "fireset_mission_not_found"));
                        return true;
                    }

                    break;



                case "missions": ///MISSIONS LIST///
                    //Page selection
                    int page = 1, count = 0;
                    if (args.length == 2) {
                        if (args[1].matches("\\d+")) {
                            page = Integer.parseInt(args[1]);
                        } else {
                            p.sendMessage(FireFighterClass.messages.formattedMessage("§c", "page_not_found"));
                            return true;
                        }
                    } else if (args.length > 2) {
                        p.sendMessage(getUsage());
                    }
                    //two missions per page
                    Set<String> missions = new TreeSet<>();
                    missions = FireFighterClass.configs.getConfig().getConfigurationSection("missions").getKeys(false);
                    if (missions.size() < (page * 2) - 1 || page * 2 <= 0) {
                        p.sendMessage(FireFighterClass.messages.formattedMessage("§c", "page_not_found"));
                        return true;
                    }
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', FireFighterClass.messages.getMessage("fireset_missions_header")));
                    for (String curr : missions) {
                        count++;
                        if (count == (page * 2) - 1 || count == page * 2) {
                            ConfigurationSection missionSection = FireFighterClass.configs.getConfig().getConfigurationSection("missions." + curr);
                            String world_name = missionSection.getString("world");
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', FireFighterClass.messages.getMessage("fireset_missions_name").replaceAll("<mission>", curr)));
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', FireFighterClass.messages.getMessage("fireset_missions_world").replaceAll("<world>", world_name)));
                            int x = (missionSection.getInt("first_position.x") + missionSection.getInt("second_position.x")) / 2;
                            int y = missionSection.getInt("altitude");
                            int z = (missionSection.getInt("first_position.z") + missionSection.getInt("second_position.z")) / 2;
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', FireFighterClass.messages.getMessage("fireset_missions_position")
                                    .replaceAll("<x>", String.valueOf(x))
                                    .replaceAll("<y>", String.valueOf(y))
                                    .replaceAll("<z>", String.valueOf(z))));
                        }
                    }
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', FireFighterClass.messages.getMessage("fireset_missions_footer")
                            .replaceAll("<current page>", String.valueOf(page))
                            .replaceAll("<total>", String.valueOf((missions.size() + 1) / 2))));
                    break;



                case "deletemission": ///DELETE MISSION///
                    if (args.length != 2) {
                        p.sendMessage(getUsage());
                        break;
                    }
                    if (existsMission(args[1])) {
                        FireFighterClass.configs.set("missions." + args[1], null); //removes the path
                        FireFighterClass.configs.saveToFile();
                        p.sendMessage(FireFighterClass.messages.formattedMessage("§a", "fireset_delete"));
                    } else {
                        p.sendMessage(FireFighterClass.messages.formattedMessage("§c", "fireset_mission_not_found"));
                    }
                    break;



                case "editmission": ///EDIT MISSION///
                    if (args.length < 3) {
                        p.sendMessage(getUsage());
                        break;
                    }
                    if (!existsMission(args[1])) {
                        p.sendMessage(FireFighterClass.messages.formattedMessage("§c", "fireset_mission_not_found"));
                        break;
                    }
                    switch (args[2]) {
                        case "name":  //editing mission's name
                            if (args.length < 4) {
                                p.sendMessage(getUsage());
                                break command_switch;
                            }
                            String newName = args[3];
                            FireFighterClass.configs.loadConfigs();
                            MemorySection mission = (MemorySection) FireFighterClass.configs.getConfig().get("missions." + args[1]);
                            FireFighterClass.configs.set("missions." + args[1], null); //removes the old mission
                            FireFighterClass.configs.set("missions." + newName, mission); //puts the new mission

                            if (!(FireFighterClass.configs.saveToFile())) return false; //error on saving

                            break;
                        case "description":  //editing mission's description
                            if (args.length < 4) {
                                p.sendMessage(getUsage());
                                break command_switch;
                            }

                            StringBuilder newDescription = new StringBuilder();
                            for (int i = 3; i < args.length; i++) {
                                newDescription.append(args[i]).append(" ");
                            }
                            FireFighterClass.configs.set("missions." + args[1] + ".description", newDescription.toString());
                            if (!(FireFighterClass.configs.saveToFile())) return false; //error on saving

                            break;
                        case "rewards":  //editing mission's rewards
                            if (!p.hasPermission(Permissions.SET_REWARDS.getNode())) { //invalid permissions
                                p.sendMessage(FireFighterClass.messages.formattedMessage("§c", "invalid_permissions"));
                                return true;
                            }
                            openRewardsGUI(args[1], p);

                            break;
                        default:
                            p.sendMessage(getUsage());
                            break command_switch;
                    }
                    break;



                case "addmission": ///ADD MISSION///
                    if (args.length < 2) {
                        p.sendMessage(getUsage());
                        break;
                    }
                    if (FireFighterClass.fireset_first_position.containsKey(p.getUniqueId()) && FireFighterClass.fireset_second_position.containsKey(p.getUniqueId())) { //checks if the area is set
                        FireFighterClass.configs.set("missions." + args[1] + ".first_position.x", FireFighterClass.fireset_first_position.get(p.getUniqueId()).getBlockX());
                        FireFighterClass.configs.set("missions." + args[1] + ".first_position.z", FireFighterClass.fireset_first_position.get(p.getUniqueId()).getBlockZ());
                        FireFighterClass.configs.set("missions." + args[1] + ".second_position.x", FireFighterClass.fireset_second_position.get(p.getUniqueId()).getBlockX());
                        FireFighterClass.configs.set("missions." + args[1] + ".second_position.z", FireFighterClass.fireset_second_position.get(p.getUniqueId()).getBlockZ());
                        FireFighterClass.configs.set("missions." + args[1] + ".altitude", Math.min((FireFighterClass.fireset_first_position.get(p.getUniqueId()).getBlockY()), (FireFighterClass.fireset_second_position.get(p.getUniqueId()).getBlockY())));
                        FireFighterClass.configs.set("missions." + args[1] + ".world", FireFighterClass.fireset_first_position.get(p.getUniqueId()).getWorld().getName());
                        if (args.length >= 3) {
                            StringBuilder description = new StringBuilder();
                            for (int i = 2; i < args.length; i++) {
                                description.append(args[i]).append(" ");
                            }
                            FireFighterClass.configs.set("missions." + args[1] + ".description", description.toString());
                        } else {
                            FireFighterClass.configs.set("missions." + args[1] + ".description", ChatColor.RED + "Fire at: " + args[1]);
                        }
                        p.sendMessage(FireFighterClass.messages.formattedMessage("§a", "fireset_added_mission"));
                        FireFighterClass.configs.saveToFile();
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
                        FireFighterClass.configs.set("fireset.wand", newWand);
                        FireFighterClass.configs.saveToFile();
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
        if (FireFighterClass.configs.getConfig().contains("missions." + name)) {
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
    	if (FireFighterClass.configs.getConfig().get(rewardsPath) != null) { //if there are rewards set
    		int rewardsCount = FireFighterClass.configs.getConfig().getInt(rewardsPath + ".size");
    		Size = (rewardsCount / 9 + 1) * 9;
    		for (int i = 0; i < rewardsCount; i++) {
    			ItemStack tmp = FireFighterClass.configs.getConfig().getItemStack(rewardsPath + "." + i);
    			inventoryContent.add(tmp);
    		}
    	} else {
    		FireFighterClass.configs.set(rewardsPath + ".size", "0");
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

