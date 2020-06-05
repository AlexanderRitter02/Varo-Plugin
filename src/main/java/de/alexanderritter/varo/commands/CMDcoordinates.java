package de.alexanderritter.varo.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
		if(args.length != 0) return false;
		if(PlayerManager.getIngamePlayer((Player)sender) == null) return true;
		Player p = (Player) sender;
		VaroPlayer ip = PlayerManager.getIngamePlayer(p);
		if(plugin.getSettings().allowPreventCoordinatePost()) {
			for(VaroPlayer member : plugin.getRegistration().getTeamMembers(ip.getTeam())) member.setPostedCoords(true);
		}
		String world = p.getWorld().getName();
		Integer x = p.getLocation().getBlockX();
		Integer y = p.getLocation().getBlockY();
		Integer z = p.getLocation().getBlockZ();
		Bukkit.broadcastMessage(Varo.prefix + ChatColor.GREEN + "Team " + PlayerManager.getIngamePlayer(p).getColor()
				+ ip.getTeam() + ChatColor.GREEN + " hat seine Koordinaten gepostet: "
				+ ChatColor.GOLD + x + ", " + y + ", " + z + " ("+ world + ")");
		plugin.sendDiscordMessage("```http\n " + "Team " + ip.getTeam() + 
				" hat seine Koordinaten gepostet: " + x + ", " + y + ", " + z + " ("+ world + ") \n ```");
		return true;
	}

}
