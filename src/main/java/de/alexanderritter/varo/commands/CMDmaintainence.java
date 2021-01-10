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
import net.md_5.bungee.api.ChatColor;

public class CMDmaintainence implements CommandExecutor {
	
	Varo plugin;
	
	public CMDmaintainence(Varo plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!command.getName().equalsIgnoreCase("maintainence")) return false;
		if(args.length < 1 || args.length > 2) return false;
		
		UUID uuid = null;
		try {
			uuid = UUIDs.getUUID(args[0]);
		} catch (IOException e) {
			sender.sendMessage(ChatColor.RED + "Der Spieler " + args[0] + " existiert nicht oder Mojangs Auth-Server sind down");
			return true;
		}
		
		if(!plugin.getRegistration().getAllUUIDs().contains(uuid)) {
			sender.sendMessage(VaroMessages.playerNotRegistered(args[0]));
			return true;
		}
		
		YamlConfiguration players = plugin.getPlayerConfig();
		
		if(args.length == 1) {
			
			players.set(uuid.toString() + ".admin.temp", true);
			sender.sendMessage(ChatColor.GREEN + args[0] + " ist nun temporär Admin");
			
			if(Bukkit.getPlayer(uuid) != null) {
				Player p = Bukkit.getPlayer(uuid);
				p.kickPlayer(ChatColor.DARK_GREEN + "Für die nächste Session bist du temporär Admin. \nVerwende das nur, um etwas zu fixen und logge dich danach wieder aus");
			}
			
		} else if (args.length == 2 && args[1].equalsIgnoreCase("forever")) {
			
			players.set(uuid.toString() + ".admin.temp", true);
			sender.sendMessage(ChatColor.GREEN + args[0] + " ist nun " + ChatColor.DARK_RED + "für immer " + ChatColor.GREEN + "Admin");
			
			if(Bukkit.getPlayer(uuid) != null) {
				Player p = Bukkit.getPlayer(uuid);
				p.kickPlayer(ChatColor.DARK_RED + "Du bist nun Admin und überwachst das Spiel. Danke! \n" + ChatColor.RED + "Melde das sofort, falls du am Varo teilnehmen willst.");
			}
			
		} else return false;
		
		plugin.savePlayerConfig(players);;
		
		return true;
	}

}
