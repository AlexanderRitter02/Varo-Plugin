package de.alexanderritter.varo.commands;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.alexanderritter.varo.api.UUIDs;
import de.alexanderritter.varo.api.VaroMessages;
import de.alexanderritter.varo.main.Varo;

public class CMDmaintainence implements CommandExecutor {
	
	Varo plugin;
	
	public CMDmaintainence(Varo plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!command.getName().equalsIgnoreCase("maintainence")) return false;
		if(args.length < 1 || args.length > 2) return false;
		String playerName = args[0];
		
		UUID uuid = null;
		try {
			uuid = UUIDs.getUUID(args[0]);
		} catch (IOException e) {
			sender.sendMessage(VaroMessages.playerDoesntExistOrServersDown(playerName));
			return true;
		}
		
		if(!plugin.getRegistration().getAllUUIDs().contains(uuid)) {
			sender.sendMessage(VaroMessages.playerNotRegistered(playerName));
			return true;
		}
		
		YamlConfiguration players = plugin.getPlayerConfig();
		
		if(args.length == 1) {
			
			players.set(uuid.toString() + ".admin.temp", true);
			sender.sendMessage(VaroMessages.temporaryAdmin(playerName));
			
			if(Bukkit.getPlayer(uuid) != null) {
				Player p = Bukkit.getPlayer(uuid);
				p.kickPlayer(VaroMessages.temporaryAdminKickNotice);
			}
			
		} else if (args.length == 2 && args[1].equalsIgnoreCase("forever")) {
			
			players.set(uuid.toString() + ".admin.temp", true);
			sender.sendMessage(VaroMessages.adminForever(playerName));
			
			if(Bukkit.getPlayer(uuid) != null) {
				Player p = Bukkit.getPlayer(uuid);
				p.kickPlayer(VaroMessages.adminForeverKickNotice);
			}
			
		} else return false;
		
		plugin.savePlayerConfig(players);;
		
		return true;
	}

}
