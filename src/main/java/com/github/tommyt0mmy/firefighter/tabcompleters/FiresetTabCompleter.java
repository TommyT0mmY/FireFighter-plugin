package com.github.tommyt0mmy.firefighter.tabcompleters;

import java.util.ArrayList;
import java.util.List;

import com.github.tommyt0mmy.firefighter.FireFighter;
import com.github.tommyt0mmy.firefighter.utility.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;

public class FiresetTabCompleter implements TabCompleter {

    private FireFighter FireFighterClass = FireFighter.getInstance();

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
        if ((!(sender instanceof Player)) || (!(sender.hasPermission( Permissions.FIRESET.getNode() )))) {
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
            FireFighterClass.configs.loadConfigs();
            if (args[0].equals("editmission")) {
                if (FireFighterClass.configs.getConfig().get("missions") == null) {
                    return suggestions;
                }
                for (String missionName: ((MemorySection) FireFighterClass.configs.getConfig().get("missions")).getKeys(false)) {
                    if (startsWith(args[1], missionName)) {
                        suggestions.add(missionName);
                    }
                }
            }
            if (args[0].equals("deletemission")) {
                if ( !(((MemorySection) FireFighterClass.configs.getConfig().get("missions")).getKeys(false).isEmpty()) ) {
                    for (String missionName : ((MemorySection) FireFighterClass.configs.getConfig().get("missions")).getKeys(false)) {
                        if (startsWith(args[1], missionName)) {
                            suggestions.add(missionName);
                        }
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
                if (startsWith(args[2], "rewards")) {
                    suggestions.add("rewards");
                }
            }
        }
        return suggestions;
    }


}