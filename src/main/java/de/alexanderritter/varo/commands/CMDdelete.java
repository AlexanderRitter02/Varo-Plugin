package de.alexanderritter.varo.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.alexanderritter.varo.api.VaroMessages;
import de.alexanderritter.varo.main.Varo;

public class CMDdelete implements CommandExecutor {
	
	Varo plugin;
	
	public CMDdelete(Varo plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!command.getName().equalsIgnoreCase("varo.delete")) return false;
		if(args.length != 1) return false;
		String team = args[0];
		if(plugin.getRegistration().getAllTeams().contains(team.toLowerCase())) {
			plugin.getRegistration().deleteTeam(team);
			sender.sendMessage(VaroMessages.teamDeleted(team));
		} else sender.sendMessage(VaroMessages.teamDoesntExist(team));
		return true;
	}
	
}
