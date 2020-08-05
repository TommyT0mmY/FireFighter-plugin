package com.github.tommyt0mmy.firefighter.commands;

import com.github.tommyt0mmy.firefighter.FireFighter;
import com.github.tommyt0mmy.firefighter.utility.Permissions;
import com.github.tommyt0mmy.firefighter.utility.TitleActionBarUtil;
import com.github.tommyt0mmy.firefighter.utility.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Firetool implements CommandExecutor
{

    private FireFighter FireFighterClass = FireFighter.getInstance();

    @Override
    public boolean onCommand(CommandSender Sender, Command cmd, String label, String[] args)
    {
        if (!(Sender instanceof Player))
        {
            Sender.sendMessage(FireFighterClass.messages.formattedMessage("", "only_players_command")); //only pl
            return true;
        }
        Player p = (Player) Sender;
        if (!(p.hasPermission(Permissions.GET_EXTINGUISHER.getNode()) || p.isOp()))
        {
            p.sendMessage(FireFighterClass.messages.formattedMessage("§c", "invalid_permissions"));
            return true;
        }

        // creating the fire extinguisher's ItemStack object //

        Inventory inventory = p.getInventory();
        inventory.addItem(FireFighterClass.getFireExtinguisher());
        TitleActionBarUtil.sendActionBarMessage(p, ChatColor.YELLOW + "" + ChatColor.UNDERLINE + FireFighterClass.messages.formattedMessage("", "hold_right_click"));

        return true;
    }

}