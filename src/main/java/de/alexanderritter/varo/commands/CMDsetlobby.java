package de.alexanderritter.varo.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.alexanderritter.varo.api.VaroMessages;
import de.alexanderritter.varo.main.Varo;

public class CMDsetlobby implements CommandExecutor {
	
	Varo plugin;
	
	public CMDsetlobby(Varo plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!cmd.getName().equalsIgnoreCase("varo.setlobby")) return false;
		if(sender instanceof Player) {
			if(args.length != 0) return false;
			Player p = (Player) sender;
			Location location = p.getLocation();
			plugin.getSettings().setLobby(location.getBlock().getLocation()); // Getting location of block because it has full coordinates
			p.sendMessage(VaroMessages.lobbySet(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
		} else sender.sendMessage(VaroMessages.lobbyCanOnlyBeSetIngame);
		return true;
	}

}
