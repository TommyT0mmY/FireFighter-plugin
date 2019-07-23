package firefighter.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import firefighter.Main;
import firefighter.utility.XMaterial;
import firefighter.utility.XSound;

public class RewardsetGUI implements Listener {
    private Main mainClass;
    public RewardsetGUI(Main mainClass) {
        this.mainClass = mainClass;
    }
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
    	Inventory inv = e.getInventory();
    	if (!inv.getName().contains("§d§lRewards - ")) {
    		return;
    	}
    	Player p = (Player) e.getWhoClicked();
    	if (!p.hasPermission(mainClass.getPermission("rewardset"))) { //invalid permissions
    		p.closeInventory();
    		p.sendMessage(mainClass.messages.get("invalid_permissions"));
    		return;
    	}
    	if (e.getAction() == InventoryAction.NOTHING) { //if nothing will happen from the click, prevents NPEs
    		return;
    	}
    	
    	//detecting clicks on the footer
    	if (e.getCurrentItem().equals(getFooterPart1())) { //if clicked a void part of the footer
    		e.setCancelled(true);
    	}
    	if (e.getCurrentItem().equals(getFooterPart2())) { //if clicked the 'add a line' button
    		e.setCancelled(true);
    		if (inv.getContents().length < 45) { //only if the size of the inventory is less than 45 slots the inventory can expand    			
    			openNewInventory(p, inv, inv.getContents().length + 9);
    		}else {
    			XSound.BLOCK_ANVIL_PLACE.playSound(p, 3, 2); //error sound
    		}
    	}
    	if (e.getCurrentItem().equals(getFooterPart3())) { //if clicked the 'remove a line' button
    		e.setCancelled(true);
    		if (inv.getContents().length > 18) { //only if the size of the inventory is bigger than 18 slots the inventory can shrink    			
    			openNewInventory(p, inv, inv.getContents().length - 9);
    		}else {
    			XSound.BLOCK_ANVIL_PLACE.playSound(p, 3, 2); //error sound
    		}
    	}
    	if (e.getCurrentItem().equals(getFooterPart4())) { //if clicked the 'save changes' button
    		e.setCancelled(true);
    		saveRewards(inv);
    	}
    	
    }
    
    /*
    private int getItemCount(Inventory inv) { //currently unused but probably useful for future modifications, it detects how many slots have been used in the given inventory inv
    	int count = 0;
    	ItemStack[] contents = inv.getContents();
    	for (ItemStack currItem : contents) {
    		if (currItem == null) continue;
    		if (currItem.getType().equals(XMaterial.AIR.parseMaterial())) continue;
    		count++;
    	}
    	return count;
    }
    */
    
    @SuppressWarnings("deprecation")
    private void openNewInventory(Player InventoryOwner, Inventory inv, int newSize) { //opens a new inventory with the same contents but with a different size
        new BukkitRunnable() { //closing and opening a new inventory with the new size needs a BukkitRunnable ( https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/inventory/InventoryClickEvent.html )
            public void run() {
            	try {
                	InventoryOwner.closeInventory();
    				Inventory newInventory = Bukkit.createInventory(InventoryOwner, newSize, inv.getName());
    				ItemStack[] newContent = inv.getContents();
                	if (newContent.length > newSize) { //if the inventory is shrinking
                		ItemStack[] tmpList = new ItemStack[newSize];
                		int count = 0;
                		ItemStack airItemStack = XMaterial.AIR.parseItem();
                		for (ItemStack currItemStack : newContent) {
                			count++;
                			if (count >= newSize) break;
                			if (currItemStack == null || currItemStack.getType().equals(XMaterial.AIR.parseMaterial())) {
                				tmpList[count-1] = airItemStack; 
                				continue;
                			}
                			tmpList[count-1] = currItemStack; 
                		}
                		newInventory.setContents(tmpList);
                		newInventory = addFooter(newInventory);
                		InventoryOwner.openInventory(newInventory);
                		cancel();
                	}
                	//if the inventory is expanding
                	newInventory.setContents(newContent);
                	newInventory = addFooter(newInventory);
            		//removing the previous footer
                	int tmpSize = newSize -1;
                	ItemStack airItemStack = XMaterial.AIR.parseItem();
            		for (int slotNumber = tmpSize - 9; slotNumber > tmpSize - 18; slotNumber--) {
            			newInventory.setItem(slotNumber, airItemStack);
            		}
            		//opening the expanded inventory
    				InventoryOwner.openInventory(newInventory);
                	cancel();
                
            	}catch (Exception e) {cancel();}
            }
        }.runTaskTimer(mainClass, 1, 1);
    }
    
    private Inventory addFooter (Inventory Origin) {
    	int Size = Origin.getContents().length - 9;
    	//placing the footer in the inventory
    	Origin.setItem(Size + 0, getFooterPart1());
    	Origin.setItem(Size + 1, getFooterPart1());
    	Origin.setItem(Size + 2, getFooterPart1());
    	Origin.setItem(Size + 3, getFooterPart2());
    	Origin.setItem(Size + 4, getFooterPart1());
    	Origin.setItem(Size + 5, getFooterPart3());
    	Origin.setItem(Size + 6, getFooterPart1());
    	Origin.setItem(Size + 7, getFooterPart1());
    	Origin.setItem(Size + 8, getFooterPart4());
    	return Origin;
    }
    
    @SuppressWarnings({ "unlikely-arg-type", "deprecation" })
	private void saveRewards (Inventory inv) { //saves to config.yml the content of the inventory (excluding the footer)
    	int Size = inv.getContents().length;
    	String missionName = inv.getName().replace("§d§lRewards - ", "");
    	String missionPath = "missions." + missionName;
    	String rewardsPath = missionPath + ".rewards";
    	//clearing previous rewards
    	mainClass.getConfig().set(rewardsPath, null);
    	mainClass.getConfig().set(rewardsPath + ".size", 0);
    	//writing new rewards
    	int rewardsCount = 0;
    	for (int slotNumber = 0; slotNumber < Size - 9; slotNumber++) {
    		ItemStack currItem = inv.getContents()[slotNumber];
    		if (currItem == null || currItem.getType().equals(XMaterial.AIR.parseItem())) continue; //filtering void spaces
    		String currPath = rewardsPath + "." + rewardsCount;
    		mainClass.getConfig().set(currPath, currItem);
    		rewardsCount++;    		
    	}
    	mainClass.getConfig().set(rewardsPath + ".size", rewardsCount);
    	mainClass.saveConfig();
    }
    
    //footer parts
    private ItemStack getFooterPart1() {
    	ItemStack item1 = XMaterial.LIGHT_GRAY_STAINED_GLASS_PANE.parseItem(); //void part of the footer
    	ItemMeta im1 = item1.getItemMeta();
    	im1.setDisplayName("§r");
    	item1.setItemMeta(im1);
    	return item1;
    }
    private ItemStack getFooterPart2() {
    	ItemStack item2 = XMaterial.LIME_STAINED_GLASS_PANE.parseItem(); //'add a line' button
    	ItemMeta im2 = item2.getItemMeta();
    	im2.setDisplayName("§aAdd a line");
    	item2.setItemMeta(im2);
    	return item2;
    }
    private ItemStack getFooterPart3() {
    	ItemStack item3 = XMaterial.RED_STAINED_GLASS_PANE.parseItem(); //'remove a line' button
    	ItemMeta im3 = item3.getItemMeta();
    	im3.setDisplayName("§cRemove a line");
    	item3.setItemMeta(im3);
    	return item3;
    }
    private ItemStack getFooterPart4() {
    	ItemStack item4 = XMaterial.LIME_STAINED_GLASS.parseItem(); //'save changes' button
    	ItemMeta im4 = item4.getItemMeta();
    	im4.setDisplayName("§aSave changes");
    	item4.setItemMeta(im4);
    	return item4;
    }

}
