package firefighter.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import firefighter.Main;

public class Help implements CommandExecutor {
    /* firefighter command */
    private Main mainClass;
    public Help(Main mainClass) {
        this.mainClass = mainClass;
    }

    private String getUsage() {
        return ((String) mainClass.getDescription().getCommands().get("firefighter").get("usage")).replaceAll("<command>", "firefighter");
    }

    @Override
    public boolean onCommand(CommandSender Sender, Command cmd, String label, String[] args) {
        if (!(Sender instanceof Player)) {
            Sender.sendMessage(mainClass.prefix + "Only players can execute this command!");
            return true;
        }
        Player p = (Player) Sender;
        if (!(p.hasPermission(mainClass.getPermission("firefighter")) || p.isOp())) {
            p.sendMessage(mainClass.messages.get("invalid_permissions"));
            return true;
        }

        if (args.length == 0) {
            sendPage("1", p);
        } else if (args.length == 1) {
            sendPage(args[0], p);
        } else if (args.length == 2) {
            sendPage(args[0] + args[1], p);
        } else {
            p.sendMessage("§4" + getUsage()); //sending 'Usage: ' message
        }
        return true;
    }

    private String getCommandDescription(String commandName) {
        if (mainClass.getDescription().getCommands().get(commandName).get("description") != null) {
            return (String) mainClass.getDescription().getCommands().get(commandName).get("description");
        } else {
            return "No description";
        }
    }

    public void sendPage(String index, Player destination) {
        String beforeCommand = "§e§l►"; //UTF8 char
        String beforeParagraph = "§c§l►"; //UTF8 char
        String msg = "";

        switch (index) {
            default:
                destination.sendMessage(mainClass.messages.get("page_not_found"));
                return;
            case "1": // PAGE 1 //
                msg += ("§c§l+ - - - §e§l§oFireFighter help§c§l - - - +§r\n");
                msg += (beforeParagraph + "§cCommands overall\n");
                msg += ("§7§o/firefighter [command name] [page #] §e§oTo get a more detailed description of the command \n");
                msg += ("§7§o/firefighter permissions §e§oTo get a list of the permissions \n");
                msg += (beforeCommand + "§7fireset§e " + getCommandDescription("fireset") + "\n");
                msg += (beforeCommand + "§7firetool§e " + getCommandDescription("firetool") + "\n");
                msg += (beforeCommand + "§7firefighter [command|page] ...§e " + getCommandDescription("firefighter") + "\n");
                //		(beforeCommand + "§7...§e ..."); //reference
                msg += ("§c§l+ - §r§epage 1/1§c§l- - - - - - - - - - - - +§r");
                break;
            case "fireset1":
            case "fireset": // FIRESET PAGE //
                msg += ("§c§l+ - - - §e§l§oFireFighter help§c§l - - - +§r\n");
                msg += (beforeParagraph + "§c'fireset' Command\n");
                msg += (beforeCommand + "§eUsage: §7/fireset [setwand|addmission|editmission|deletemission] ...\n");
                msg += (beforeCommand + "§ePermission node: §7" + mainClass.getPermission("fireset") + "\n");
                msg += (beforeCommand + "§eDescription: §7Sets a new point that will catch at a random time on fire, firefighters ");
                msg += ("§7should extinguish the fire to get a reward.§8 §oN.B. Only admins should have access to this command\n");
                msg += ("§7You can change the wand with §o/fireset setwand§7 with the permission " + mainClass.getPermission("set_wand") + "\n");
                msg += ("§c§l+ - §r§epage 1/3§c§l- - - - - - - - - - - - +§r");
                break;
            case "fireset2":
                msg += ("§c§l+ - - - §e§l§oFireFighter help§c§l - - - +§r\n");
                msg += (beforeParagraph + "§c'fireset' Command\n");
                msg += (beforeCommand + "§e - How to add a mission - \n");
                msg += ("§e1)§7Select with the wand (§o/fireset§7) the area of the mission that will be set on fire\n");
                msg += ("§e2)§7Create a new mission with §o/fireset addmission <name> [description]\n");
                msg += (beforeCommand + "§ename §7The name that identifies the mission\n");
                msg += (beforeCommand + "§edescription §7The message that will be broadcasted\n");
                msg += ("§c§l+ - §r§epage 2/3§c§l- - - - - - - - - - - - +§r");
                break;
            case "fireset3":
                msg += ("§c§l+ - - - §e§l§oFireFighter help§c§l - - - +§r\n");
                msg += (beforeParagraph + "§c'fireset' Command\n");
                msg += (beforeCommand + "§e - How to edit a mission - \n");
                msg += ("§7§o/fireset editmission <name> <name|description> <new value>\n");
                msg += (beforeCommand + "§ename §7The name that identifies the mission\n");
                msg += (beforeCommand + "§ename|description §7The parameter that will be modified\n");
                msg += (beforeCommand + "§enew value §7The new value of the parameter\n");
                msg += ("§c§l+ - §r§epage 3/3§c§l- - - - - - - - - - - - +§r");
                break;
            case "firetool1":
            case "firetool": // FIRETOOL PAGE //
                msg += ("§c§l+ - - - §e§l§oFireFighter help§c§l - - - +§r\n");
                msg += (beforeParagraph + "§c'firetool' Command\n");
                msg += (beforeCommand + "§eUsage: §7/firetool\n");
                msg += (beforeCommand + "§ePermission node: §7" + mainClass.getPermission("firetool_get") + "\n");
                msg += (beforeCommand + "§eDescription: §7Gives a fire extinguisher to the player. The fire extinguisher ");
                msg += ("§7can be used to extinguish a fire and in the firefighter missions. Hold right click to use the fire extinguisher. ");
                msg += ("§7To use the fire extinguisher the player needs the permission " + mainClass.getPermission("firetool_use") + "\n");
                msg += ("§c§l+ - - - - - - - - - - - - - - - - - +§r");
                break;
            case "firefighter1":
            case "firefighter": // FIREFIGHTER PAGE //
                msg += ("§c§l+ - - - §e§l§oFireFighter help§c§l - - - +§r\n");
                msg += (beforeParagraph + "§c'firefirghter' Command\n");
                msg += (beforeCommand + "§eUsage: §7/firefighter [command|page] ...\n");
                msg += (beforeCommand + "§ePermission node: §7" + mainClass.getPermission("firefighter") + "\n");
                msg += (beforeCommand + "§eDescription: §7Shows the help menu, pressing tab while typing §o/firefighter §r");
                msg += ("§7will autocomplete the command showing every existent page that you can have access to.\n");
                msg += ("§c§l+ - - - - - - - - - - - - - - - - - +§r");
                break;
            case "permissions1":
            case "permissions": // PERMISSIONS PAGE //
                msg += ("§c§l+ - - - §e§l§oFireFighter help§c§l - - - +§r\n");
                msg += (beforeParagraph + "§c'Permissions list\n");
                msg += (beforeCommand + "§7" + mainClass.getPermission("firefighter") + "§e To execute the §7§o/firefighter§e command\n");
                msg += (beforeCommand + "§7" + mainClass.getPermission("firetool_get") + "§e To get a fire extinguisher (§7§o/firetool§e)\n");
                msg += (beforeCommand + "§7" + mainClass.getPermission("firetool_use") + "§e To utilize a fire extinguisher\n");
                msg += (beforeCommand + "§7" + mainClass.getPermission("fireset") + "§e To execute the §7§o/fireset§e command (§oadd/edit/delete missions§7)\n");
                msg += (beforeCommand + "§7" + mainClass.getPermission("set_wand") + "§e To execute the §7§o/firetool setwand§e command\n");
                msg += (beforeCommand + "§7" + mainClass.getPermission("onduty") + "§e To receive missions\n");
                msg += ("§c§l+ - - - - - - - - - - - - - - - - - +§r");
        }
        destination.sendMessage(msg);
    }
}