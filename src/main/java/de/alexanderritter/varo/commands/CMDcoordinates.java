package de.alexanderritter.varo.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.alexanderritter.varo.api.VaroMessages;
import de.alexanderritter.varo.ingame.PlayerManager;
import de.alexanderritter.varo.ingame.VaroPlayer;
import de.alexanderritter.varo.main.Varo;

public class CMDcoordinates implements CommandExecutor {
	
	Varo plugin;
	
	public CMDcoordinates(Varo plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!command.getName().equalsIgnoreCase("coordinates")) return false;
		if(!(sender instanceof Player)) return true;
		if(args.length == 0) {
			if(PlayerManager.getIngamePlayer((Player)sender) == null) return true;
			VaroPlayer ip = PlayerManager.getIngamePlayer((Player) sender);
			if(plugin.getSettings().allowPreventCoordinatePost()) {
				for(VaroPlayer member : plugin.getRegistration().getTeamMembers(ip.getTeam())) member.setPostedCoords(true);
			}
			plugin.postPlayerCoordinates(ip);
			return true;
		} else if(args.length == 1) {
			if(!sender.hasPermission("varo.coordinates.otherplayers")) {
				sender.sendMessage(ChatColor.RED + "Du hast nicht die Berechtigung, für andere Spieler Koordinaten zu posten.");
				return true;
			}
			String playerString = args[0];
			VaroPlayer ip = plugin.getRegistration().loadPlayer(playerString);
			if(ip != null) {
				plugin.postPlayerCoordinates(ip);
			} else sender.sendMessage(ChatColor.RED + "Der Spieler " + ChatColor.GOLD + playerString + ChatColor.RED + " existiert nicht oder ist nicht registriert.");
		}
		return false;
	}

}
