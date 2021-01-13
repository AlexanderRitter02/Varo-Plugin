package de.alexanderritter.varo.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.alexanderritter.varo.api.VaroMessages;
import de.alexanderritter.varo.main.Varo;

public class CMDreload implements CommandExecutor{
	
	Varo plugin;
	
	public CMDreload(Varo plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!command.getName().equalsIgnoreCase("varo.reload")) return false;
		if(args.length != 0) return false;
		plugin.reloadPlayerConfig();
		plugin.reloadIngamePlayers(false);
		sender.sendMessage(VaroMessages.reloadedPlayerYML);	
		return true;
	}
	

}
