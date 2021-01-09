package de.alexanderritter.varo.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.alexanderritter.varo.main.Varo;

public class CMDspawn implements CommandExecutor {
	
	Varo plugin;
	
	public CMDspawn(Varo plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!command.getName().equalsIgnoreCase("varo.spawn")) return false;
		if(sender instanceof Player) {
			if(args.length != 1) return false;
			Player p = (Player) sender;
			int id;
			try {
				id = Integer.parseInt(args[0]);
			} catch(NumberFormatException e) {
				p.sendMessage(Varo.nointeger);
				return false;
			}
			if(id <= 0) {
				p.sendMessage(ChatColor.RED + "Die ID muss mindestens 1 betragen.");
				return false;
			}
			YamlConfiguration spawns = plugin.getSpawnConfig();
			int current_spawn_count = spawns.getKeys(false).size();
			if(id > current_spawn_count + 1) {
				p.sendMessage(ChatColor.RED + "Benutze die ID's in Reihenfolge! (1, 2, 3...)");
				return false;
			}
			
			if (spawns.isConfigurationSection(String.valueOf(id))) {
				p.sendMessage(Varo.prefix + ChatColor.GREEN + "Spawnpoint mit ID " + ChatColor.DARK_AQUA + String.valueOf(id) + ChatColor.GREEN + " wurde erfolgreich überschrieben.");
			} else {
				p.sendMessage(Varo.prefix + ChatColor.GREEN + "Spawnpoint mit ID " + ChatColor.DARK_AQUA + String.valueOf(id) + ChatColor.GREEN + " wurde erfolgreich erstellt.");
			}
			
			plugin.getRegistration().registerSpawn(p.getLocation().getBlock().getLocation(), id);
		} else if(sender instanceof ConsoleCommandSender) {
			sender.sendMessage(ChatColor.RED + "Spawns werden ingame registriert");
		}
		return true;
	}
}
