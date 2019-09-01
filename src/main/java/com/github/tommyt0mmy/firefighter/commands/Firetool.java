package com.github.tommyt0mmy.firefighter.commands;

import java.util.ArrayList;
import java.util.List;

import com.github.tommyt0mmy.firefighter.FireFighter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.tommyt0mmy.firefighter.utility.TitleActionBarUtil;
import com.github.tommyt0mmy.firefighter.utility.XMaterial;

public class Firetool implements CommandExecutor {
    private FireFighter fireFighterClass;
    public Firetool(FireFighter fireFighterClass) {
        this.fireFighterClass = fireFighterClass;
    }

    @Override
    public boolean onCommand(CommandSender Sender, Command cmd, String label, String[] args) {
        if (!(Sender instanceof Player)) {
            Sender.sendMessage(fireFighterClass.prefix + "Only players can execute this command!");
            return true;
        }
        Player p = (Player) Sender;
        if (!(p.hasPermission(fireFighterClass.getPermission("firetool_get")) || p.isOp())) {
            p.sendMessage(fireFighterClass.messages.get("invalid_permissions"));
            return true;
        }
        ItemStack fire_extinguisher = XMaterial.IRON_HOE.parseItem();
        //getting meta
        ItemMeta meta = fire_extinguisher.getItemMeta();
        //modifying meta
        meta.setDisplayName("" + ChatColor.RED + "" + ChatColor.BOLD + "Fire Extinguisher");
        List < String > lore = new ArrayList < String > ();
        lore.add("Fire Extinguisher");
        lore.add(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + fireFighterClass.messages.get("hold_right_click"));
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        //setting meta
        meta.setLore(lore);
        fire_extinguisher.setItemMeta(meta);
        Inventory inventory = p.getInventory();
        inventory.addItem(fire_extinguisher);
        TitleActionBarUtil.sendActionBarMessage(p,ChatColor.YELLOW + "" + ChatColor.UNDERLINE + fireFighterClass.messages.get("hold_right_click"));

        return true;
    }

}