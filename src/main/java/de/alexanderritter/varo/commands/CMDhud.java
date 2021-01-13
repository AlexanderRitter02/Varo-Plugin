package de.alexanderritter.varo.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.alexanderritter.varo.api.VaroMessages;
import de.alexanderritter.varo.config.HUDOption;
import de.alexanderritter.varo.ingame.PlayerManager;
import de.alexanderritter.varo.ingame.VaroPlayer;

public class CMDhud implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(command.getName().equalsIgnoreCase("varo.hud"))) return false;
		if(!(sender instanceof Player)) return true;
		if(args.length != 1) return false;
		HUDOption hudoption = HUDOption.SCOREBOARD;
		try {
			hudoption = HUDOption.valueOf(args[0].toUpperCase());
		} catch(IllegalArgumentException e) {
			return false;
		}
		Player p = (Player) sender;
		VaroPlayer ip = PlayerManager.getIngamePlayer(p);
		if(ip == null) {
			sender.sendMessage(VaroMessages.playerSettingsOnlyChangableAfterStart);
			return true;
		}
		ip.setHUDOption(hudoption);
		ip.setupScoreboard();
		return true;
	}

}
