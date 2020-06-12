package de.alexanderritter.varo.ingame;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import de.alexanderritter.varo.api.Actionbar;
import de.alexanderritter.varo.api.TabList;
import de.alexanderritter.varo.config.HUDOption;
import de.alexanderritter.varo.main.Varo;

public class VaroPlayer {
	
	Varo plugin;
	String name;
	String team;
	UUID uuid;
	int time;
	int sessions;
	boolean dead;
	boolean enemy_near;
	boolean login_protected;
	boolean spectator = false;
	boolean admin = false;
	ChatColor color;
	HUDOption hudoption;
	Scoreboard scoreboard;
	
	public VaroPlayer(Varo plugin, String name, String team, UUID uuid, int time, int sessions, boolean dead, ChatColor color, boolean admin, HUDOption hudoption) {
		this.plugin = plugin;
		this.name = name;
		this.team = team;
		this.uuid = uuid;
		this.time = time;
		this.sessions = sessions;
		this.dead = dead;
		this.enemy_near = false;
		this.login_protected = false;
		this.color = color;
		this.admin = admin;
		if(dead) spectator = true;
		this.hudoption = hudoption;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public UUID getUuid() {
		return uuid;
	}

	public int getTime() {
		return time;
	}
	
	public String getTimeString() {
		String min = String.valueOf(time / 60);
		String sec = String.valueOf(time % 60);
		if(min.length() == 1) min = "0" + min;
		if(sec.length() == 1) sec = "0" + sec;
		return ChatColor.YELLOW + min + ":" + sec;
	}

	public void setTime(int time) {
		switch(hudoption) {
		case SCOREBOARD:
			updateScoreboard(time);
			break;
		case ACTIONBAR:
			updateActionbar(time);
			break;
		case TAB:
			updateTabList(time);
			break;
		default:
			break;
		}
		this.time = time;
	}

	public int getSessions() {
		return sessions;
	}

	public void setSessions(int sessions) {
		this.sessions = sessions;
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}
	
	public void setPostedCoords(boolean postedcoords) {
		YamlConfiguration players = plugin.getPlayerConfig();
		players.set(uuid + ".postedcoords", Boolean.valueOf(postedcoords));
		plugin.savePlayerConfig(players);
	}
	
	public boolean hasPostedCoords() {
		return plugin.getPlayerConfig().getBoolean(uuid + ".postedcoords");
	}

	public boolean getEnemyNear() {
		return enemy_near;
	}
	
	public void testEnemyNear() {
		this.enemy_near = false;
		if(Bukkit.getPlayer(uuid) == null) return;
		for(VaroPlayer enemyip : PlayerManager.getAllIngamePlayers()) {
			Player enemy = Bukkit.getPlayer(enemyip.uuid);
			if(enemy == null || !enemy.isOnline()) continue;
			if(enemyip.getTeam().equalsIgnoreCase(team)) continue;
			if(enemyip.isAdmin()) continue;
			Player p = Bukkit.getPlayer(uuid);
			if(!enemy.getWorld().equals(p.getWorld()) ||
					enemy.getLocation().distance(p.getLocation()) > plugin.getSettings().getMinLogoutDistance()) continue;
			this.enemy_near = true;
		}
	}
	
	public boolean isLoginProtected() {
		return login_protected;
	}
	
	public void setLoginProtect(boolean login_protect) {
		this.login_protected = login_protect;
	}

	public ChatColor getColor() {
		return color;
	}
	
	public Scoreboard getScoreboard() {
		return scoreboard;
	}
	
	public void setHUDOption(HUDOption hudoption) {
		Bukkit.getPlayer(uuid).setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		new TabList("", "").send(Bukkit.getPlayer(uuid));
		new Actionbar("").send(Bukkit.getPlayer(uuid));
		this.hudoption = hudoption;
		setTime(time);
	}
	
	public void updateActionbar(int time) {
		Actionbar actionbar = new Actionbar(getTimeString() + " - " + color + "#" + team + " - "  + ChatColor.GOLD + getSessions() + ChatColor.GREEN + " Sessions");
		actionbar.send(Bukkit.getPlayer(uuid));
	}
	
	public void updateTabList(int time) {
		String header = color + "#" + getTeam() + ChatColor.YELLOW + " - " + getTimeString();
		String footer = ChatColor.GREEN + "Sessions: " + ChatColor.GOLD + getSessions();
		TabList tablist = new TabList(header, footer);
		tablist.send(Bukkit.getPlayer(uuid));
	}
	
	public void setupScoreboard() {
		if(hudoption != HUDOption.SCOREBOARD) return;
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		Team sc_team = scoreboard.registerNewTeam(team);
		sc_team.addEntry(name);
		Objective obj = scoreboard.registerNewObjective(name, "dummy");
		obj.setDisplayName(getTimeString());
		obj.getScore(color + "#" + team).setScore(2);
		obj.getScore(ChatColor.GREEN + "Sessions: "  + ChatColor.GOLD + getSessions()).setScore(1);
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		Bukkit.getPlayer(uuid).setScoreboard(scoreboard);
	}
	
	public void updateScoreboard(int time) {
		if(scoreboard == null) return;
		scoreboard.getObjective(name).setDisplayName(getTimeString());
	}
	
	public void save() {
		YamlConfiguration players = plugin.getPlayerConfig();
		String id = uuid.toString();
		players.set(id + ".name", name);
		players.set(id + ".team", team);
		players.set(id + ".sessions", Integer.valueOf(sessions));
		players.set(id + ".recent_time", Integer.valueOf(time));
		players.set(id + ".dead", Boolean.valueOf(dead));
		players.set(id + ".color", color.name());
		if(hudoption != plugin.getSettings().getDefaultHUDOption()) {
			players.set(id + ".hud", String.valueOf(hudoption));
		}	
		plugin.savePlayerConfig(players);
	}
	
	public boolean isSpectator() {
		return spectator;
	}
	
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	
	public boolean isAdmin() {
		return admin;
	}
	
	public void addKill(String killed_uuid) {
		YamlConfiguration players = plugin.getPlayerConfig();
		String id = uuid.toString();
		List<String> kills = players.getStringList(id + ".kills");
		kills.add(killed_uuid);
		players.set(id + ".kills", kills);
		plugin.savePlayerConfig(players);
	}
	
	public void removeKill(String killed_uuid) {
		YamlConfiguration players = plugin.getPlayerConfig();
		String id = uuid.toString();
		List<String> kills = players.getStringList(id + ".kills");
		kills.remove(killed_uuid);
		players.set(id + ".kills", kills);
		plugin.savePlayerConfig(players);
	}
	
	public void addStrike(String reason) {
		YamlConfiguration players = plugin.getPlayerConfig();
		List<String> strikes = players.getStringList(uuid.toString() + ".strikes");
		strikes.add(reason);
		players.set(uuid.toString() + ".strikes", strikes);
		plugin.savePlayerConfig(players);
	}
	
	public void removeStrike(int index) {
		YamlConfiguration players = plugin.getPlayerConfig();
		List<String> strikes = players.getStringList(uuid.toString() + ".strikes");
		strikes.remove(index);
		players.set(uuid.toString() + ".strikes", strikes);
		plugin.savePlayerConfig(players);
	}
	
	public List<String> getStrikes() {
		return plugin.getPlayerConfig().getStringList(uuid.toString() + ".strikes");
	}
	
	public int getKillCount() {
		return plugin.getPlayerConfig().getStringList(uuid.toString() + ".kills").size();
	}

}