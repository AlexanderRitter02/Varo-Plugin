package de.alexanderritter.varo.config;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import de.alexanderritter.varo.main.Varo;

public class Settings {
	
	Varo plugin;
	BorderMode bordermode;
	int sessions_per_week, sessions_length, sessions_per_day, start_protection, login_protection, min_logout_distance, current_week, daytopost;
	double borderShrinkPerHour;
	boolean allowedToSpectateIfTeamAlive, friendlyfire, friendlyfire_boost, running;
	World varo;
	Location lobby;
	String discordid;
	List<String> disallowedUseOnly, disallowedGeneral;
	
	
	public Settings(Varo plugin, BorderMode bordermode, int sessions_per_week, int sessions_length, int sessions_per_day, int start_protection, int login_protection, int min_logout_distance, int current_week,
			boolean allowedToSpectateIfTeamAlive, boolean friendlyfire, boolean friendlyfire_boost, boolean running, String discordid, int daytopost, double borderShrinkPerHour, Location lobby, 
			List<String> disallowedUseOnly, List<String> disallowedGeneral) {
		this.plugin = plugin;
		this.bordermode = bordermode;
		this.sessions_per_week = sessions_per_week;
		this.sessions_length = sessions_length;
		this.sessions_per_day = sessions_per_day;
		this.start_protection = start_protection;
		this.login_protection = login_protection;
		this.min_logout_distance = min_logout_distance;
		this.current_week = current_week;
		this.allowedToSpectateIfTeamAlive = allowedToSpectateIfTeamAlive;
		this.friendlyfire = friendlyfire;
		this.friendlyfire_boost = friendlyfire_boost;
		this.running = running;
		this.discordid = discordid;
		this.daytopost = daytopost;
		this.borderShrinkPerHour = borderShrinkPerHour;
		if(lobby != null) {
			this.lobby = lobby;
		} else {
			System.out.println("BUKKIT: VARO: THere is no Lobby location");
		}
		this.disallowedUseOnly = disallowedUseOnly;
		this.disallowedGeneral = disallowedGeneral;
	}
	
	public World getVaroWorld() {
		return varo;
	}
	
	public void setVaroWorld(World varo) {
		this.varo = varo;
	}
	
	public void setLobby(Location lobby) {
		FileConfiguration config = plugin.getConfig();
		config.set("lobby.world", lobby.getWorld().getName());
		config.set("lobby.x", lobby.getBlockX());
		config.set("lobby.y", lobby.getBlockY());
		config.set("lobby.z", lobby.getBlockZ());
		plugin.saveConfig();
		this.lobby = lobby;
	}
	
	public Location getLobby() {
		return lobby;
	}
	
	public BorderMode getBorderMode() {
		return bordermode;
	}
	
	public double getBorderShrinkPerHour() {
		// Blocks the border needs to be moved per hour
		return borderShrinkPerHour;
	}

	public int getSessionsPerWeek() {
		return sessions_per_week;
	}

	public int getSessionsLength() {
		return sessions_length;
	}
	
	public int getStartProtection() {
		return start_protection;
	}
	
	public int getLoginProtection() {
		return login_protection;
	}
	
	public int getMinLogoutDistance() {
		return min_logout_distance;
	}

	public int getCurrentWeek() {
		return current_week;
	}

	public void setCurrentWeek(int current_week) {
		this.current_week = current_week; // TODO TESTs
		plugin.getConfig().set("plugin.current_week", Integer.valueOf(current_week));
		plugin.saveConfig();
	}
	
	public boolean isAllowedSpectateIfTeamAlive() {
		return allowedToSpectateIfTeamAlive;
	}

	public boolean isFriendlyfire() {
		return friendlyfire;
	}
	
	public boolean isBoostingAllowed() {
		return friendlyfire_boost;
	}
	
	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
		plugin.getConfig().set("plugin.running", Boolean.valueOf(running));
		plugin.saveConfig();
	}
	
	public String getDiscordChannelId() {
		return discordid;
	}

	public int getDayToPost() {
		return daytopost;
	}
	
	public boolean shouldTeamsSpawnTogether() {
		return plugin.getConfig().getBoolean("spawn-teams-together");
	}
	
	public boolean allowPreventCoordinatePost() {
		return plugin.getConfig().getBoolean("allow-prevent-coordinate-post");
	}
	
	public String isAdmin(UUID uuid) {
		if(plugin.getConfig().getString("plugin.admins." + uuid.toString()) != null) {
			return plugin.getConfig().getString("plugin.admins." + uuid.toString());
		}
		return "";
	}
	
	public boolean isCoordinatesPostTime() {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setFirstDayOfWeek(GregorianCalendar.MONDAY);
		String date = new SimpleDateFormat("EEEE, HH", Locale.US).format(calendar.getTime());
		System.out.println("Checking COORDINATE POST, it is " + date);
		boolean is_coord_post = false;
		for(String datestring : plugin.getConfig().getStringList("coord-post")) {
			if(date.equals(datestring)) {
				is_coord_post = true;
			}
		}
		return is_coord_post;
	}
	
	public HUDOption getDefaultHUDOption() {
		return HUDOption.valueOf(plugin.getConfig().getString("default-hud", "SCOREBOARD").toUpperCase());
	}

	public int getMaxSessionsPerDay() {
		if(sessions_per_day == 0) return Integer.MAX_VALUE;
		return sessions_per_day;
	}
	
	public List<String> getDisallowedItemsToUse() {
		List<String> disallowedUse = disallowedUseOnly;
		disallowedUse.addAll(disallowedGeneral);
		return disallowedUse;
	}
	
	public List<String> getDisallowedItemsGeneral() {
		return disallowedGeneral;
	}

}