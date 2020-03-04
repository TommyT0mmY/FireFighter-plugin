package com.github.tommyt0mmy.firefighter.commands;

import com.github.tommyt0mmy.firefighter.FireFighter;
import com.github.tommyt0mmy.firefighter.utility.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Help implements CommandExecutor {
    private FireFighter FireFighterClass = FireFighter.getInstance();

    private String getUsage() {
        return ((String) FireFighterClass.getDescription().getCommands().get("firefighter").get("usage")).replaceAll("<command>", "firefighter");
    }

    @Override
    public boolean onCommand(CommandSender Sender, Command cmd, String label, String[] args) {
        if (!(Sender instanceof Player)) {
            Sender.sendMessage(FireFighterClass.messages.formattedMessage("§c", "only_players_command"));
            return true;
        }
        Player p = (Player) Sender;
        if (!(p.hasPermission(Permissions.HELP_MENU.getNode()) || p.isOp())) {
            p.sendMessage(FireFighterClass.messages.formattedMessage("§c", "invalid_permissions"));
            return true;
        }

        if (args.length == 0) {
            sendPage("1", p);
        } else if (args.length == 1) {
            sendPage(args[0], p);
        } else if (args.length == 2) {
            sendPage(args[0] + args[1], p);
        } else {
            p.sendMessage( ChatColor.DARK_RED + getUsage()); //sending 'Usage: ' message
        }
        return true;
    }

    private void sendPage(String index, Player destination) {
        String msg = "";

        switch (index) {
            default:
                destination.sendMessage(FireFighterClass.messages.formattedMessage("§c", "page_not_found"));
                return;
            case "1": // PAGE 1 //

                msg += FireFighterClass.messages.getHelpPage("page1");

                break;
            case "fireset1":
            case "fireset": // FIRESET PAGE //

                msg += FireFighterClass.messages.getHelpPage("fireset1");

                break;
            case "fireset2": //adding a mission page

                msg += FireFighterClass.messages.getHelpPage("fireset2");

                break;
            case "fireset3": //editing a mission page

                msg += FireFighterClass.messages.getHelpPage("fireset3");

                break;
            case "fireset4": //rewards page

                msg += FireFighterClass.messages.getHelpPage("fireset4");

            	break;
            case "fireset5": //rewards page

                msg += FireFighterClass.messages.getHelpPage("fireset5");

                break;
            case "fireset6": //rewards page

                msg += FireFighterClass.messages.getHelpPage("fireset6");

                break;
            case "firetool1":
            case "firetool": // FIRETOOL PAGE //

                msg += FireFighterClass.messages.getHelpPage("firetool1");

                break;
            case "firefighter1":
            case "firefighter": // FIREFIGHTER PAGE //

                msg += FireFighterClass.messages.getHelpPage("firefighter1");

                break;
            case "permissions1":
            case "permissions": // PERMISSIONS PAGE //

                msg += FireFighterClass.messages.getHelpPage("permissions1");

                break;
        }
        destination.sendMessage(msg);
    }
}