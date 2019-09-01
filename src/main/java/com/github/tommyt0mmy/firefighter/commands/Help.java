package com.github.tommyt0mmy.firefighter.commands;

import com.github.tommyt0mmy.firefighter.FireFighter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Help implements CommandExecutor {
    /* firefighter command */
    private FireFighter fireFighterClass;
    public Help(FireFighter fireFighterClass) {
        this.fireFighterClass = fireFighterClass;
    }

    private String getUsage() {
        return ((String) fireFighterClass.getDescription().getCommands().get("firefighter").get("usage")).replaceAll("<command>", "firefighter");
    }

    @Override
    public boolean onCommand(CommandSender Sender, Command cmd, String label, String[] args) {
        if (!(Sender instanceof Player)) {
            Sender.sendMessage(fireFighterClass.prefix + "Only players can execute this command!");
            return true;
        }
        Player p = (Player) Sender;
        if (!(p.hasPermission(fireFighterClass.getPermission("firefighter")) || p.isOp())) {
            p.sendMessage(fireFighterClass.messages.get("invalid_permissions"));
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

    private String getCommandDescription(String commandName) {
        if (fireFighterClass.getDescription().getCommands().get(commandName).get("description") != null) {
            return (String) fireFighterClass.getDescription().getCommands().get(commandName).get("description");
        } else {
            return "No description";
        }
    }

    public void sendPage(String index, Player destination) {
        String beforeCommand =  ChatColor.YELLOW + "" + ChatColor.BOLD + ">";
        String beforeParagraph =  ChatColor.RED + "" + ChatColor.BOLD + ">";
        String msg = "";

        switch (index) {
            default:
                destination.sendMessage(fireFighterClass.messages.get("page_not_found"));
                return;
            case "1": // PAGE 1 //
                msg += ( ChatColor.RED + "" + ChatColor.BOLD + "+ - - - " + ChatColor.YELLOW + "" + ChatColor.BOLD + "" + ChatColor.ITALIC + "FireFighter help" + ChatColor.RED + "" + ChatColor.BOLD + " - - - +" + ChatColor.RESET + "\n");
                msg += (beforeParagraph + ChatColor.RED + "Commands overall\n");
                msg += ( ChatColor.GRAY + "" + ChatColor.ITALIC + "/firefighter [command name] [page #] " + ChatColor.YELLOW + "" + ChatColor.ITALIC + "To get a more detailed description of the command \n");
                msg += ( ChatColor.GRAY + "" + ChatColor.ITALIC + "/firefighter permissions " + ChatColor.YELLOW + "" + ChatColor.ITALIC + "To get a list of the permissions \n");
                msg += (beforeCommand + ChatColor.GRAY + "fireset" + ChatColor.YELLOW + " " + getCommandDescription("fireset") + "\n");
                msg += (beforeCommand + ChatColor.GRAY + "firetool" + ChatColor.YELLOW + " " + getCommandDescription("firetool") + "\n");
                msg += (beforeCommand + ChatColor.GRAY + "firefighter [command|page] ..." + ChatColor.YELLOW + " " + getCommandDescription("firefighter") + "\n");
                //		(beforeCommand + ChatColor.GRAY + "..." + ChatColor.YELLOW + " ..."); //reference
                msg += ( ChatColor.RED + "" + ChatColor.BOLD + "+ - " + ChatColor.RESET + ChatColor.YELLOW + "page 1/1" + ChatColor.RED + "" + ChatColor.BOLD + "- - - - - - - - - - - - +" + ChatColor.RESET + "");
                break;
            case "fireset1":
            case "fireset": // FIRESET PAGE //
                msg += ( ChatColor.RED + "" + ChatColor.BOLD + "+ - - - " + ChatColor.YELLOW + "" + ChatColor.BOLD + "" + ChatColor.ITALIC + "FireFighter help" + ChatColor.RED + "" + ChatColor.BOLD + " - - - +" + ChatColor.RESET + "\n");
                msg += (beforeParagraph + ChatColor.RED + "'fireset' Command\n");
                msg += (beforeCommand + ChatColor.YELLOW + "Usage: " + ChatColor.GRAY + "/fireset [setwand|addmission|editmission|deletemission] ...\n");
                msg += (beforeCommand + ChatColor.YELLOW + "Permission node: " + ChatColor.GRAY + fireFighterClass.getPermission("fireset") + "\n");
                msg += (beforeCommand + ChatColor.YELLOW + "Description: " + ChatColor.GRAY + "Sets a new point that will catch at a random time on fire, firefighters ");
                msg += ( ChatColor.GRAY + "should extinguish the fire to get a reward." + ChatColor.DARK_GRAY + " " + ChatColor.ITALIC + "N.B. Only admins should have access to this command\n");
                msg += ( ChatColor.GRAY + "You can change the wand with " + ChatColor.ITALIC + "/fireset setwand" + ChatColor.GRAY + " with the permission " + fireFighterClass.getPermission("set_wand") + "\n");
                msg += ( ChatColor.RED + "" + ChatColor.BOLD + "+ - " + ChatColor.RESET + ChatColor.YELLOW + "page 1/4" + ChatColor.RED + "" + ChatColor.BOLD + "- - - - - - - - - - - - +" + ChatColor.RESET + "");
                break;
            case "fireset2": //adding a mission page
                msg += ( ChatColor.RED + "" + ChatColor.BOLD + "+ - - - " + ChatColor.YELLOW + "" + ChatColor.BOLD + "" + ChatColor.ITALIC + "FireFighter help" + ChatColor.RED + "" + ChatColor.BOLD + " - - - +" + ChatColor.RESET + "\n");
                msg += (beforeParagraph + ChatColor.RED + "'fireset' Command\n");
                msg += (beforeCommand + ChatColor.YELLOW + " - How to add a mission - \n");
                msg += ( ChatColor.YELLOW + "1)" + ChatColor.GRAY + "Select with the wand (" + ChatColor.ITALIC + "/fireset" + ChatColor.GRAY + ") the area of the mission that will be set on fire\n");
                msg += ( ChatColor.YELLOW + "2)" + ChatColor.GRAY + "Create a new mission with " + ChatColor.ITALIC + "/fireset addmission <name> [description]\n");
                msg += (beforeCommand + ChatColor.YELLOW + "name " + ChatColor.GRAY + "The name that identifies the mission\n");
                msg += (beforeCommand + ChatColor.YELLOW + "description " + ChatColor.GRAY + "The message that will be broadcasted\n");
                msg += ( ChatColor.RED + "" + ChatColor.BOLD + "+ - " + ChatColor.RESET + ChatColor.YELLOW + "page 2/4" + ChatColor.RED + "" + ChatColor.BOLD + "- - - - - - - - - - - - +" + ChatColor.RESET + "");
                break;
            case "fireset3": //editing a mission page
                msg += ( ChatColor.RED + "" + ChatColor.BOLD + "+ - - - " + ChatColor.YELLOW + "" + ChatColor.BOLD + "" + ChatColor.ITALIC + "FireFighter help" + ChatColor.RED + "" + ChatColor.BOLD + " - - - +" + ChatColor.RESET + "\n");
                msg += (beforeParagraph + ChatColor.RED + "'fireset' Command\n");
                msg += (beforeCommand + ChatColor.YELLOW + " - How to edit a mission - \n");
                msg += ( ChatColor.GRAY + "" + ChatColor.ITALIC + "/fireset editmission <name> <name|description> <new value>\n");
                msg += (beforeCommand + ChatColor.YELLOW + "name " + ChatColor.GRAY + "The name that identifies the mission\n");
                msg += (beforeCommand + ChatColor.YELLOW + "name|description " + ChatColor.GRAY + "The parameter that will be modified\n");
                msg += (beforeCommand + ChatColor.YELLOW + "new value " + ChatColor.GRAY + "The new value of the parameter\n");
                msg += ( ChatColor.RED + "" + ChatColor.BOLD + "+ - " + ChatColor.RESET + ChatColor.YELLOW + "page 3/4" + ChatColor.RED + "" + ChatColor.BOLD + "- - - - - - - - - - - - +" + ChatColor.RESET + "");
                break;
            case "fireset4": //rewards page
                msg += ( ChatColor.RED + "" + ChatColor.BOLD + "+ - - - " + ChatColor.YELLOW + "" + ChatColor.BOLD + "" + ChatColor.ITALIC + "FireFighter help" + ChatColor.RED + "" + ChatColor.BOLD + " - - - +" + ChatColor.RESET + "\n");
                msg += (beforeParagraph + ChatColor.RED + "'fireset' Command\n");
                msg += (beforeCommand + ChatColor.YELLOW + " - How to add/edit rewards of a mission - \n");
                msg += ( ChatColor.GRAY + "" + ChatColor.ITALIC + "/fireset editmission <name> rewards\n");
                msg += (beforeCommand + ChatColor.YELLOW + "name " + ChatColor.GRAY + "The name that identifies the mission\n");
                msg += (beforeCommand + ChatColor.GRAY + "The GUI that contains rewards will open, ");
                msg += ( ChatColor.GRAY + "if there are no rewards set for that specific mission the GUI will be empty\n");
                msg += ( ChatColor.RED + "" + ChatColor.BOLD + "+ - " + ChatColor.RESET + ChatColor.YELLOW + "page 4/4" + ChatColor.RED + "" + ChatColor.BOLD + "- - - - - - - - - - - - +" + ChatColor.RESET + "");
            	break;
            case "firetool1":
            case "firetool": // FIRETOOL PAGE //
                msg += ( ChatColor.RED + "" + ChatColor.BOLD + "+ - - - " + ChatColor.YELLOW + "" + ChatColor.BOLD + "" + ChatColor.ITALIC + "FireFighter help" + ChatColor.RED + "" + ChatColor.BOLD + " - - - +" + ChatColor.RESET + "\n");
                msg += (beforeParagraph + ChatColor.RED + "'firetool' Command\n");
                msg += (beforeCommand + ChatColor.YELLOW + "Usage: " + ChatColor.GRAY + "/firetool\n");
                msg += (beforeCommand + ChatColor.YELLOW + "Permission node: " + ChatColor.GRAY + fireFighterClass.getPermission("firetool_get") + "\n");
                msg += (beforeCommand + ChatColor.YELLOW + "Description: " + ChatColor.GRAY + "Gives a fire extinguisher to the player. The fire extinguisher ");
                msg += ( ChatColor.GRAY + "can be used to extinguish a fire and in the firefighter missions. Hold right click to use the fire extinguisher. ");
                msg += ( ChatColor.GRAY + "To use the fire extinguisher the player needs the permission " + fireFighterClass.getPermission("firetool_use") + "\n");
                msg += ( ChatColor.RED + "" + ChatColor.BOLD + "+ - - - - - - - - - - - - - - - - - +" + ChatColor.RESET + "");
                break;
            case "firefighter1":
            case "firefighter": // FIREFIGHTER PAGE //
                msg += ( ChatColor.RED + "" + ChatColor.BOLD + "+ - - - " + ChatColor.YELLOW + "" + ChatColor.BOLD + "" + ChatColor.ITALIC + "FireFighter help" + ChatColor.RED + "" + ChatColor.BOLD + " - - - +" + ChatColor.RESET + "\n");
                msg += (beforeParagraph + ChatColor.RED + "'firefirghter' Command\n");
                msg += (beforeCommand + ChatColor.YELLOW + "Usage: " + ChatColor.GRAY + "/firefighter [command|page] ...\n");
                msg += (beforeCommand + ChatColor.YELLOW + "Permission node: " + ChatColor.GRAY + fireFighterClass.getPermission("firefighter") + "\n");
                msg += (beforeCommand + ChatColor.YELLOW + "Description: " + ChatColor.GRAY + "Shows the help menu, pressing tab while typing " + ChatColor.ITALIC + "/firefighter " + ChatColor.RESET + "");
                msg += ( ChatColor.GRAY + "will autocomplete the command showing every existent page that you can have access to.\n");
                msg += ( ChatColor.RED + "" + ChatColor.BOLD + "+ - - - - - - - - - - - - - - - - - +" + ChatColor.RESET + "");
                break;
            case "permissions1":
            case "permissions": // PERMISSIONS PAGE //
                msg += ( ChatColor.RED + "" + ChatColor.BOLD + "+ - - - " + ChatColor.YELLOW + "" + ChatColor.BOLD + "" + ChatColor.ITALIC + "FireFighter help" + ChatColor.RED + "" + ChatColor.BOLD + " - - - +" + ChatColor.RESET + "\n");
                msg += (beforeParagraph + ChatColor.RED + "'Permissions list\n");
                msg += (beforeCommand + ChatColor.GRAY + fireFighterClass.getPermission("firefighter") + ChatColor.YELLOW + " To execute the " + ChatColor.GRAY + "" + ChatColor.ITALIC + "/firefighter" + ChatColor.YELLOW + " command\n");
                msg += (beforeCommand + ChatColor.GRAY + fireFighterClass.getPermission("firetool_get") + ChatColor.YELLOW + " To get a fire extinguisher (" + ChatColor.GRAY + "" + ChatColor.ITALIC + "/firetool" + ChatColor.YELLOW + ")\n");
                msg += (beforeCommand + ChatColor.GRAY + fireFighterClass.getPermission("firetool_use") + ChatColor.YELLOW + " To utilize a fire extinguisher\n");
                msg += (beforeCommand + ChatColor.GRAY + fireFighterClass.getPermission("firetool.freeze-durability") + ChatColor.YELLOW + " To freeze the usage of fire extinguishers\n");
                msg += (beforeCommand + ChatColor.GRAY + fireFighterClass.getPermission("fireset") + ChatColor.YELLOW + " To execute the " + ChatColor.GRAY + "" + ChatColor.ITALIC + "/fireset" + ChatColor.YELLOW + " command (" + ChatColor.ITALIC + "add/edit/delete missions" + ChatColor.GRAY + ")\n");
                msg += (beforeCommand + ChatColor.GRAY + fireFighterClass.getPermission("rewardset") + ChatColor.YELLOW + " To edit the rewards list of a mission\n");
                msg += (beforeCommand + ChatColor.GRAY + fireFighterClass.getPermission("set_wand") + ChatColor.YELLOW + " To execute the " + ChatColor.GRAY + "" + ChatColor.ITALIC + "/firetool setwand" + ChatColor.YELLOW + " command\n");
                msg += (beforeCommand + ChatColor.GRAY + fireFighterClass.getPermission("onduty") + ChatColor.YELLOW + " To receive missions\n");
                msg += ( ChatColor.RED + "" + ChatColor.BOLD + "+ - - - - - - - - - - - - - - - - - +" + ChatColor.RESET + "");
        }
        destination.sendMessage(msg);
    }
}