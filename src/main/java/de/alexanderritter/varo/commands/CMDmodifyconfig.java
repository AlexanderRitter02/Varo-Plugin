package de.alexanderritter.varo.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.alexanderritter.varo.main.Varo;

public class CMDmodifyconfig implements CommandExecutor {
	
	Varo plugin;
	
	public CMDmodifyconfig(Varo plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!cmd.getName().equalsIgnoreCase("modifyconfig")) return true;
		if(!((sender instanceof ConsoleCommandSender) || ( (sender instanceof Player) && (((Player) sender).getUniqueId() == Bukkit.getPlayer("Lord_Quadrato").getUniqueId())))) return true;
		if(args.length != 2) return false;
		String attribute = args[0];
		String value = args[1];
		
		if(plugin.getConfig().contains(attribute)) {
			// TODO Does contains work or should I use getString()
			// Also does the following line save the config automagically?
			plugin.getConfig().set(attribute, value);
			sender.sendMessage("Das Attribut '" + attribute + "' wurde auf den Wert '" + value + "' gesetzt.");
		} else {
			sender.sendMessage("Es liegt kein Eintrag mit dem Attribut '" + attribute + "' in der config.yml vor");
		}
		
		return true;
	}

}
