package firefighter.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import firefighter.Main;
import firefighter.utility.TitleActionBarUtil;
import firefighter.utility.XMaterial;

public class Firetool implements CommandExecutor {
    private Main mainClass;
    public Firetool(Main mainClass) {
        this.mainClass = mainClass;
    }

    @Override
    public boolean onCommand(CommandSender Sender, Command cmd, String label, String[] args) {
        if (!(Sender instanceof Player)) {
            Sender.sendMessage(mainClass.prefix + "Only players can execute this command!");
            return true;
        }
        Player p = (Player) Sender;
        if (!(p.hasPermission(mainClass.getPermission("firetool_get")) || p.isOp())) {
            p.sendMessage(mainClass.messages.get("invalid_permissions"));
            return true;
        }
        ItemStack fire_extinguisher = XMaterial.IRON_HOE.parseItem();
        //getting meta
        ItemMeta meta = fire_extinguisher.getItemMeta();
        //modifying meta
        meta.setDisplayName("§c§lFire Extinguisher");
        List < String > lore = new ArrayList < String > ();
        lore.add("Fire Extinguisher");
        lore.add("§e§n" + mainClass.messages.get("hold_right_click"));
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        //setting meta
        meta.setLore(lore);
        fire_extinguisher.setItemMeta(meta);
        Inventory inventory = p.getInventory();
        inventory.addItem(fire_extinguisher);
        TitleActionBarUtil.sendActionBarMessage(p, "§e§n" + mainClass.messages.get("hold_right_click"));

        return true;
    }

}