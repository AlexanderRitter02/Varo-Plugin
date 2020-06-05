package de.alexanderritter.varo.ingame;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class ChestManager {
	
	private static HashMap<Location, String> chests = new HashMap<>();
	
	public static void addChest(String team, Location chest1, Location chest2) {
		chests.put(chest1, team);
		chests.put(chest2, team);
	}
	public static void addChest(String team, Block chest1, Block chest2) {
		chests.put(chest1.getLocation(), team);
		chests.put(chest2.getLocation(), team);
	}
	
	public static void removeChest(String team) {
		chests.values().removeAll(Collections.singleton(team));
	}
	public static void removeChest(Block chest1, Block chest2) {
		chests.remove(chest1.getLocation());
		chests.remove(chest2.getLocation());
	}
	
	public static String getOwner(Location loc) {
		return chests.get(loc);
	}
	public static Collection<String> getAllOwners() {
		return chests.values();
	}
	
	public static Set<Location> getAllChests() {
		return chests.keySet();
	}
	
	public static void clear() {
		chests.clear();
	}

}