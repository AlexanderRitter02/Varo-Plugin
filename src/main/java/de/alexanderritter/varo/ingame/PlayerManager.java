package de.alexanderritter.varo.ingame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerManager {
	
	public static ArrayList<UUID> spectators = new ArrayList<>();
	private static HashMap<UUID, VaroPlayer> ingame_players = new HashMap<>();	
	
	public static void addIngamePlayer(Player p, VaroPlayer ip) {
		ingame_players.put(p.getUniqueId(), ip);
	}
	
	public static void removeIngamePlayer(Player p) {
		ingame_players.remove(p.getUniqueId());
	}
	public static void removeIngamePlayer(VaroPlayer ip) {
		ingame_players.remove(ip.getUuid());
	}

	public static VaroPlayer getIngamePlayer(Player p) {
		return ingame_players.get(p.getUniqueId());
	}
	
	public static Set<UUID> getAllUUIDs() {
		return ingame_players.keySet();
	}
	public static Collection<VaroPlayer> getAllIngamePlayers() {
		return ingame_players.values();
	}
	public static ArrayList<String> getAllNames() { //TODO Check if necassary
		ArrayList<String> names = new ArrayList<>();
		for(VaroPlayer ip : ingame_players.values()) {names.add(ip.getName().toLowerCase());}
		return names;
	}
	
	public static void addSpectator(Player p) {
		if(!spectators.contains(p.getUniqueId())) spectators.add(p.getUniqueId());	
		p.setGameMode(GameMode.ADVENTURE);
		p.setAllowFlight(true);
		p.getInventory().clear();
		p.getInventory().setArmorContents(new ItemStack[p.getInventory().getArmorContents().length]);
		p.getInventory().setItem(0, new ItemStack(Material.COMPASS));
		p.spigot().setCollidesWithEntities(false);
		for(Player online : Bukkit.getServer().getOnlinePlayers()) {
			if(PlayerManager.spectators.contains(online.getUniqueId())) {
				p.showPlayer(online);
			} else {
				online.hidePlayer(p);
			}			
		}
	}
	
	public static void removeSpectator(Player p) {
		spectators.remove(p.getUniqueId());
		p.setGameMode(GameMode.SURVIVAL);
		p.setAllowFlight(false);
		p.getInventory().clear();
		p.getInventory().setArmorContents(new ItemStack[p.getInventory().getArmorContents().length]);
		p.spigot().setCollidesWithEntities(true);
		for(Player online : Bukkit.getServer().getOnlinePlayers()) {
			if(PlayerManager.spectators.contains(online.getUniqueId())) {
				p.hidePlayer(online);
			} else {
				online.showPlayer(p);
				
			}
		}
	}
	
}