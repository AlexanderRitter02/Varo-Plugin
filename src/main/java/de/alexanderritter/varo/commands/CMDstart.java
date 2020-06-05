package de.alexanderritter.varo.commands;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import de.alexanderritter.varo.ingame.PlayerManager;
import de.alexanderritter.varo.ingame.VaroPlayer;
import de.alexanderritter.varo.main.Varo;
import de.alexanderritter.varo.timemanagment.Countdown;

public class CMDstart implements CommandExecutor {
	
	Varo plugin;
	Countdown countdown;
	BukkitTask task;
	
	public CMDstart(Varo plugin) {
		this.plugin = plugin;
		countdown = new Countdown(plugin, 10);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!command.getName().equalsIgnoreCase("varo.start")) return false;
		if(args.length != 1) return false;
		if(args[0].equalsIgnoreCase("break")) {
			if(countdown.isRunning()) {
				countdown.pause();
			} else sender.sendMessage(ChatColor.RED + "Der Countdown läuft noch nicht");
		} else {
					
			int time;
			try {
				time = Integer.parseInt(args[0]);
				if(time <= 0 ) throw new NumberFormatException();
			} catch(NumberFormatException e) {sender.sendMessage(Varo.nointeger);return false;}
					
			if(!countdown.isRunning()) {
				if(!plugin.getSettings().isRunning()) {
					
					ArrayList<Player> players_for_start = new ArrayList<>();
					int id = 1;
					
					for(Player online : Bukkit.getOnlinePlayers()) {players_for_start.add(online);}					
					if(players_for_start.size() > plugin.getSpawnConfig().getKeys(false).size()) {
						sender.sendMessage(ChatColor.RED + "Es sind mehr Spieler vorhanden als es Spawns gibt!");
						return true;
					}
					
					
					if(plugin.getSettings().shouldTeamsSpawnTogether()) {
						
						// Spawne Teammates nebeneinander
						while(!players_for_start.isEmpty()) {
							int random_player = new Random().nextInt(players_for_start.size());
							
							plugin.reloadIngamePlayers(false);
							VaroPlayer vp = PlayerManager.getIngamePlayer(players_for_start.get(random_player));
							
							if(vp == null) {
								players_for_start.get(random_player).kickPlayer("Ähm du bist nicht registriert. Melde dich bei den Admins.");
								continue;
							}
							ArrayList<VaroPlayer> teammates = plugin.getRegistration().getTeamMembers(vp.getTeam());
							
							for(VaroPlayer teammember : teammates) {
								if(players_for_start.contains(Bukkit.getPlayer(teammember.getUuid()))) {
									Bukkit.getPlayer(teammember.getUuid()).teleport(plugin.getRegistration().getSpawn(id));
									players_for_start.remove(Bukkit.getPlayer(teammember.getUuid()));
									id++;
								}
							}	
						}
						
					} else {
						
						// Spawne alle Spieler zufällig
						while(!players_for_start.isEmpty()) {
							int random_player = new Random().nextInt(players_for_start.size());
							if(plugin.getRegistration().getSpawn(id) == null) {				
								sender.sendMessage(ChatColor.RED + "Es ist noch kein Spawn registriert / die Spawns sind nicht in der richtigen Reihenfolge."
										+ "Bitte benutze /varo.spawn");
								return true;
							}
							players_for_start.get(random_player).teleport(plugin.getRegistration().getSpawn(id));
							id++;
							players_for_start.remove(random_player);
						}
						
					}
					
					
					countdown = new Countdown(plugin, 10);
					countdown.start(time);
					sender.sendMessage(ChatColor.GREEN + "Varo startet!");							
				} else sender.sendMessage(ChatColor.RED + "Varo läuft bereits!");
			} else sender.sendMessage(ChatColor.RED + "Der Countdown läuft schon. Benutze /varo.start break, um ihn zu stoppen");
		}		
		return true;
	}
}
