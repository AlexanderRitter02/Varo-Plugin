package de.alexanderritter.varo.commands;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.alexanderritter.varo.api.VaroMessages;
import de.alexanderritter.varo.ingame.PlayerManager;
import de.alexanderritter.varo.ingame.VaroPlayer;
import de.alexanderritter.varo.main.Varo;
import net.md_5.bungee.api.ChatColor;

public class CMDpostcoordinates implements CommandExecutor {
	
	Varo plugin;
	
	public CMDpostcoordinates(Varo plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!command.getName().equalsIgnoreCase("postcoordinates")) return false;
		
		if(args.length == 0) {
			
			// Post coordinates for yourself, no permission required
			VaroPlayer ip = PlayerManager.getIngamePlayer((Player) sender);
			if(ip == null) return true;
			if(plugin.getSettings().allowPreventCoordinatePost()) {
				for(VaroPlayer member : plugin.getRegistration().getTeamMembers(ip.getTeam())) member.setPostedCoords(true);
			}
			plugin.postPlayerCoordinates(ip);
			
		} else if (args.length >= 1 && args.length <= 2) {
			
			// Permission check, if player is allowed to post coordinates for other players
			if(!sender.hasPermission("varo.postcoordinates.forothers")) {
				sender.sendMessage(VaroMessages.noPermissionToPostCoordinates);
				return true;
			}
			
			String playerName = args[0];
			boolean forced = (args.length == 2 && args[1].equalsIgnoreCase("forced"));
			
			// Post coordinates for everyone (@a)
			if(playerName.equalsIgnoreCase("@a")) {
				YamlConfiguration players = plugin.getPlayerConfig();
				for(String uuid : players.getKeys(false)) {
					if(!players.getBoolean(uuid + ".dead") && plugin.getRegistration().loadPlayer(UUID.fromString(uuid)) != null) {
						VaroPlayer ip = plugin.getRegistration().loadPlayer(UUID.fromString(uuid));
						if(!forced && ip.hasPostedCoords() && plugin.getSettings().allowPreventCoordinatePost()) continue;
						plugin.postPlayerCoordinates(ip);
					}
				}
				sender.sendMessage(ChatColor.GREEN + "Successfully posted coordinates for everyone");
				return true;
			}
			
			// Post coordinate for another player			
			VaroPlayer ip = plugin.getRegistration().loadPlayer(playerName);
			if(ip != null) {
				plugin.postPlayerCoordinates(ip);
			} else sender.sendMessage(VaroMessages.playerNotRegistered(playerName));
			
			
		} else return false;
		
		return true;
	}

}
