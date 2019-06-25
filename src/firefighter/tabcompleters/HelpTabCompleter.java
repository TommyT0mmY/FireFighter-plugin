package firefighter.tabcompleters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import firefighter.Main;

public class HelpTabCompleter implements TabCompleter {
	private Main mainClass;
	public HelpTabCompleter(Main mainClass) {
		this.mainClass = mainClass;
	}
	
	private boolean startsWith(String partialString, String completeString) {  //if 'completeString' starts with 'partialString'
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
	public List<String> onTabComplete(CommandSender sender, Command command, String paramString, String[] args) {
		
		if ((!(sender instanceof Player)) || (!(sender.hasPermission(mainClass.getPermission("firefighter"))))) {
			return null;
		}
		if (args.length > 2) { //invalid number of arguments
			return null;
		}
		
		List <String> suggestions = new ArrayList<>();
		
		if (args.length == 0) {
			suggestions.add("firefighter");
			suggestions.add("firetool");
			suggestions.add("fireset");
			suggestions.add("permissions");
		} else if (args.length == 1) {
			if (startsWith(args[0], "firefighter")) {suggestions.add("firefighter");}
			if (startsWith(args[0], "permissions")) {suggestions.add("permissions");}
			if (startsWith(args[0], "firetool")) {suggestions.add("firetool");}
			if (startsWith(args[0], "fireset")) {suggestions.add("fireset");}
		} else if (args.length == 2) {
			if (startsWith(args[0] + args[1], "fireset")) {suggestions.add("1"); suggestions.add("2"); suggestions.add("3");};
		}
		return suggestions;
	}
}
