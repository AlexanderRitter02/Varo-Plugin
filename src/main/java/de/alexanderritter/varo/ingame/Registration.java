package de.alexanderritter.varo.ingame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.google.common.base.Enums;

import de.alexanderritter.varo.api.UUIDs;
import de.alexanderritter.varo.config.HUDOption;
import de.alexanderritter.varo.main.Varo;

public class Registration {
	
	Varo plugin;
	
	public Registration(Varo plugin) {
		this.plugin = plugin;
	}
	
	public void registerPlayer(String name, String team, ChatColor color) throws IOException {
		YamlConfiguration players = plugin.getPlayerConfig();
		UUID uuid = UUIDs.getUUID(name);
		String id = uuid.toString();
		players.set(id + ".name", name);
		players.set(id + ".team", team);
		players.set(id + ".dead", Boolean.valueOf(false));
		players.set(id + ".sessions", Integer.valueOf(plugin.getSettings().getSessionsPerWeek()));
		players.set(id + ".recent_time", Integer.valueOf(plugin.getSettings().getSessionsLength()));
		players.set(id + ".postedcoords", Boolean.valueOf(false));
		players.set(id + ".color", color.name());
		plugin.savePlayerConfig(players);
	}
	
	
	public void deleteTeam(String team) {
		YamlConfiguration players = plugin.getPlayerConfig();
		for(String uuid : players.getKeys(false)) {
			if(players.getString(uuid + ".team").equalsIgnoreCase(team)) players.set(uuid, null);;
		}
		plugin.savePlayerConfig(players);
	}

	
	public ArrayList<String> getAllTeams() {
		ArrayList<String> teams = new ArrayList<>();
		YamlConfiguration players = plugin.getPlayerConfig();
		for(String uuid : players.getKeys(false)) {
			if(!teams.contains(players.getString(uuid + ".team").toLowerCase())) teams.add(players.getString(uuid + ".team").toLowerCase());
		}
		return teams;
	}
	
	public ArrayList<UUID> getAllUUIDs() {
		ArrayList<UUID> uuids = new ArrayList<>();
		YamlConfiguration players = plugin.getPlayerConfig();
		for(String uuid : players.getKeys(false)) {
			uuids.add(UUID.fromString(uuid));
		}
		return uuids;
	}
	
	public ArrayList<String> getAllNames() {
		ArrayList<String> names = new ArrayList<>();
		YamlConfiguration players = plugin.getPlayerConfig();
		for(UUID uuid : getAllUUIDs()) {
			names.add(players.getString(uuid.toString() + ".name").toLowerCase());
		}
		return names;
	}
	
	public VaroPlayer loadPlayer(UUID uuid) {
		YamlConfiguration players = plugin.getPlayerConfig();
		String id = uuid.toString();
		String name = players.getString(id + ".name");
		String team = players.getString(id + ".team");
		boolean dead = players.getBoolean(id + ".dead");
		int sessions = players.getInt(id + ".sessions");
		int sessions_today = players.getInt(id + ".sessions_today", 0);
		int time = players.getInt(id + ".recent_time");
		boolean admin = players.isConfigurationSection(id + ".admin");
		ChatColor color = Enums.getIfPresent(ChatColor.class, players.getString(id + ".color").toUpperCase()).or(ChatColor.WHITE);
		HUDOption hudoption = Enums.getIfPresent(HUDOption.class, players.getString(id + ".hud", plugin.getSettings().getDefaultHUDOption().toString()).toUpperCase()).orNull();
		if(hudoption == null) {
			hudoption = HUDOption.SCOREBOARD;
			plugin.getLogger().warning("HUDOption for player " + name + " could not be loaded. Using default SCOREBOARD option.\n"
					+ "Please check for malformed strings in either players.yml (" + uuid + ".hud) and config.yml (default-hud)");
		}
		VaroPlayer ip = new VaroPlayer(plugin, name, team, uuid, time, sessions, sessions_today, dead, color, admin, hudoption);
		return ip;
	}
	public VaroPlayer loadPlayer(Player p) {
		return loadPlayer(p.getUniqueId());
	}
	public VaroPlayer loadPlayer(String name) {
		YamlConfiguration players = plugin.getPlayerConfig();
		for(String id : players.getKeys(false)) {
			if(players.getString(id + ".name").equalsIgnoreCase(name)) return loadPlayer(UUID.fromString(id));
		}
		return null;
	}
	
	public void registerSpawn(Location loc, int id) {
		YamlConfiguration spawns = plugin.getSpawnConfig();
		String path = String.valueOf(id);
		spawns.set(path + ".world", loc.getWorld().getName());
		spawns.set(path + ".x", Integer.valueOf(loc.getBlockX()));
		spawns.set(path + ".y", Integer.valueOf(loc.getBlockY()));
		spawns.set(path + ".z", Integer.valueOf(loc.getBlockZ()));
		plugin.saveSpawnConfig(spawns);
	}
	
	public Location getSpawn(int id) {
		YamlConfiguration spawns = plugin.getSpawnConfig();
		String path = String.valueOf(id);
		if (!spawns.isSet(path)) return null;
		World world = Bukkit.getWorld(spawns.getString(path + ".world"));
		int x = spawns.getInt(path + ".x");
		int y = spawns.getInt(path + ".y");
		int z = spawns.getInt(path + ".z");
		return new Location(world, x, y, z).add(0.5D, 0.0D, 0.5D);
	}
	
	public void registerChest(VaroPlayer ip, Block chest1, Block chest2) {
		YamlConfiguration chests = plugin.getChestConfig();
		String team = ip.getTeam();
		chests.set(team + ".1.world", chest1.getWorld().getName());
		chests.set(team + ".1.x", chest1.getX());
		chests.set(team + ".1.y", chest1.getY());
		chests.set(team + ".1.z", chest1.getZ());
		chests.set(team + ".2.world", chest2.getWorld().getName());
		chests.set(team + ".2.x", chest2.getX());
		chests.set(team + ".2.y", chest2.getY());
		chests.set(team + ".2.z", chest2.getZ());
		plugin.saveChestConfig(chests);
	}
	
	public void deleteChest(String team) {
		YamlConfiguration chests = plugin.getChestConfig();
		chests.set(team, null);
		plugin.saveChestConfig(chests);
	}
	
	public boolean isTeamAlive(String team) {
		YamlConfiguration players = plugin.getPlayerConfig();
		Boolean alive = true;
		for(String id : players.getKeys(false)) {
			if(players.getString(id + ".team").equalsIgnoreCase(team)) {
				if(players.getBoolean(id + ".dead") == true) {
					alive = false;
				} else {
					alive = true;
					break;
				}
			}
		}
		return alive;
	}
	
	public boolean isTeamMemberOnline(String team) {
		for(VaroPlayer member : getTeamMembers(team)) if(Bukkit.getPlayer(member.getUuid()) != null) return true;
		return false;
	}
	
	public ArrayList<VaroPlayer> getTeamMembers(String team) {
		ArrayList<VaroPlayer> members = new ArrayList<>();
		for(String id : plugin.getPlayerConfig().getKeys(false)) {
			UUID uuid = UUID.fromString(id);
			if(plugin.getPlayerConfig().getString(uuid + ".team").equalsIgnoreCase(team)) members.add(plugin.getRegistration().loadPlayer(uuid));
		}
		return members;
	}
	
	public ArrayList<String> getAliveTeams() {
		ArrayList<String> teams = new ArrayList<String>();
		for(UUID uuid : getAllUUIDs()) {
			VaroPlayer ip = loadPlayer(uuid);
			if(ip.isDead() || ip.isAdmin() || ip.isSpectator()) continue;
			if(!teams.contains(ip.getTeam())) teams.add(ip.getTeam());
		}
		return teams;
	}

}