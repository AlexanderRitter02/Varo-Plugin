package de.alexanderritter.varo.commands;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.alexanderritter.varo.api.Colors;
import de.alexanderritter.varo.api.UUIDs;
import de.alexanderritter.varo.api.VaroMessages;
import de.alexanderritter.varo.main.Varo;

public class CMDregister implements CommandExecutor {
	
	Varo plugin;
	
	public CMDregister(Varo plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!command.getName().equalsIgnoreCase("varo.register")) return false;
		if(args.length < 3) return false;
				
		sender.sendMessage(ChatColor.GREEN + "Registrierung läuft...");
		String team = args[0];
		ChatColor color = Colors.getColorfromString(args[1]);
				
		if(plugin.getRegistration().getAllTeams().contains(team.toLowerCase())) {
			sender.sendMessage(VaroMessages.teamAlreadyRegistered);
			return false;
		}
				
		ArrayList<String> players = new ArrayList<>();
		int c = 2;
		while(c <= args.length - 1) {
			players.add(args[c]);
			c++;
		}
				
		for(String player : players) {
			try {
				if(UUIDs.getUUID(player) == null) {
					plugin.getRegistration().deleteTeam(team);
					sender.sendMessage(ChatColor.RED + "Der Spieler " + player + " existiert nicht!");
					sender.sendMessage(VaroMessages.teamRegisteringFailed(team));
					return true;
				} else if(plugin.getRegistration().getAllUUIDs().contains(UUIDs.getUUID(player))) {
					plugin.getRegistration().deleteTeam(team);
					sender.sendMessage(VaroMessages.playerAlreadyRegisteredInAnotherTeam(player));
					sender.sendMessage(VaroMessages.teamRegisteringFailed(team));
					return true;
				}
				plugin.getRegistration().registerPlayer(player, team, color);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		sender.sendMessage(VaroMessages.teamRegisteredSuccessfully(team));
		return true;
	}
	
}
