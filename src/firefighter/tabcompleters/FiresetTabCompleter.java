package firefighter.tabcompleters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;

import firefighter.Main;

public class FiresetTabCompleter implements TabCompleter {
    private Main mainClass;
    public FiresetTabCompleter(Main mainClass) {
        this.mainClass = mainClass;
    }

    private boolean startsWith(String partialString, String completeString) { //if 'completeString' starts with 'partialString'
        if (partialString.equalsIgnoreCase(completeString)) {
            return true;
        }
        char[] partialStringCharArray = partialString.toCharArray();
        char[] completeStringCharArray = completeString.toCharArray();
        if (partialStringCharArray.length > completeStringCharArray.length) {
            return false;
        }
        for (int i = 0; i < partialStringCharArray.length; i++) {
            char currentChar = partialString.charAt(i);
            String currentCharString = (currentChar + "");
            char parallelChar = completeString.charAt(i);
            String parallelCharString = (parallelChar + "");
            if (!parallelCharString.equalsIgnoreCase(currentCharString)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List < String > onTabComplete(CommandSender sender, Command command, String paramString, String[] args) {
        if ((!(sender instanceof Player)) || (!(sender.hasPermission(mainClass.getPermission("fireset"))))) {
            return null;
        }

        List < String > suggestions = new ArrayList < > ();
        if (args.length == 1) {
            if (startsWith(args[0], "setwand")) {
                suggestions.add("setwand");
            }
            if (startsWith(args[0], "addmission")) {
                suggestions.add("addmission");
            }
            if (startsWith(args[0], "editmisison")) {
                suggestions.add("editmission");
            }
            if (startsWith(args[0], "deletemission")) {
                suggestions.add("deletemission");
            }
        }
        if (args.length == 2) {
            if (args[0].equals("editmission")) {
                for (String missionName: ((MemorySection) mainClass.getConfig().get("missions")).getKeys(false)) {
                    if (startsWith(args[1], missionName)) {
                        suggestions.add(missionName);
                    }
                }
            }
            if (args[0].equals("deletemission")) {
                for (String missionName: ((MemorySection) mainClass.getConfig().get("missions")).getKeys(false)) {
                    if (startsWith(args[1], missionName)) {
                        suggestions.add(missionName);
                    }
                }
            }
        }
        if (args.length == 3) {
            if (args[0].equals("editmission")) {
                if (startsWith(args[2], "name")) {
                    suggestions.add("name");
                }
                if (startsWith(args[2], "description")) {
                    suggestions.add("description");
                }
            }
        }
        return suggestions;
    }


}