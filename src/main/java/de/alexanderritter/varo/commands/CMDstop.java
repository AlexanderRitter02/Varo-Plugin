package de.alexanderritter.varo.commands;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import de.alexanderritter.varo.events.BeforeVaroListener;
import de.alexanderritter.varo.ingame.PlayerManager;
import de.alexanderritter.varo.main.Varo;

public class CMDstop implements CommandExecutor {
	
	Varo plugin;
	
	public CMDstop(Varo plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!command.getName().equalsIgnoreCase("varo.stop")) return false;
		if(args.length != 0) return false;
		if(plugin.getSettings().isRunning()) {
			sender.sendMessage(ChatColor.RED + "Varo wird beendet!");
			plugin.getSettings().setRunning(false);
			HandlerList.unregisterAll(plugin); // alle Listener beenden
			Bukkit.getServer().getScheduler().cancelAllTasks(); // Schutzzeit und andere Scheduler beenden
			plugin.initializeGametime(); // TODO why?
			for(Player online : Bukkit.getOnlinePlayers()) {
				online.teleport(plugin.getSettings().getLobby());
				online.setPlayerListName(ChatColor.GRAY + "[Lobby] " + online.getName());
				online.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
				online.setGameMode(GameMode.ADVENTURE);
				online.sendMessage(Varo.prefix + ChatColor.GREEN + "Varo wurde von einem Admin beendet!");					
			}
			
			ArrayList<UUID> removeSpec = new ArrayList<>();
			for(UUID uuid : PlayerManager.spectators) {
				removeSpec.add(uuid);				
			}
			for(UUID uuid : removeSpec) {
				if(Bukkit.getPlayer(uuid) != null) {
					PlayerManager.removeSpectator(Bukkit.getPlayer(uuid));
				}
			}
			PlayerManager.spectators = new ArrayList<UUID>();
			
			YamlConfiguration players = plugin.getPlayerConfig();
			for(String id : players.getKeys(false)) {
				players.set(id + ".sessions", plugin.getSettings().getSessionsPerWeek());
				players.set(id + ".recent_time", plugin.getSettings().getSessionsLength());
				players.set(id + ".postedcoords", Boolean.valueOf(false));
				players.set(id + ".dead", Boolean.valueOf(false));
			}
			plugin.savePlayerConfig(players);
			
			Bukkit.getPluginManager().registerEvents(new BeforeVaroListener(plugin), plugin);
			
		} else sender.sendMessage(ChatColor.RED + "Varo ist noch nicht gestartet. Benutze /varo.start, um Varo zu starten");
		return true;
	}

}