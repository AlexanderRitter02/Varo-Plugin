package de.alexanderritter.varo.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.alexanderritter.varo.api.VaroMessages;
import de.alexanderritter.varo.main.Varo;

public class CMDfinalsmode implements CommandExecutor {
	
	Varo plugin;
	
	public CMDfinalsmode(Varo plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!command.getName().equalsIgnoreCase("varo.finalsmode")) return false;
		if(args.length != 0) return false;
		
		YamlConfiguration players = plugin.getPlayerConfig();
		
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.kickPlayer(VaroMessages.finalsMode);
		}
		
		for(String uuid : players.getKeys(false)) {
			players.set(uuid + ".sessions", 1);
			players.set(uuid + ".sessions_today", null);
			players.set(uuid + ".recent_time", 60*60*5); // Auf 5 Stunden
		}
		
		plugin.savePlayerConfig(players);
		return true;
	}

}
