package de.alexanderritter.varo.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.alexanderritter.varo.api.VaroMessages;
import de.alexanderritter.varo.ingame.PlayerManager;

public class CMDspectators implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("spectators")) {
			Bukkit.broadcastMessage(VaroMessages.spectatorsAre);
			for(UUID uuid : PlayerManager.spectators) {
				Bukkit.broadcastMessage(Bukkit.getOfflinePlayer(uuid).getName());
			}
		}
		return true;
	}

}
