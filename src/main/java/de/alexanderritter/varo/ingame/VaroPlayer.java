package de.alexanderritter.varo.ingame;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

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
	Scoreboard scoreboard;
	
	public VaroPlayer(Varo plugin, String name, String team, UUID uuid, int time, int sessions, boolean dead, ChatColor color, boolean admin) {
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

	public void setTime(int time) {
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
	
	public void setupScoreboard() {
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		Team sc_team = scoreboard.registerNewTeam(team);
		sc_team.addEntry(name);
		Objective obj = scoreboard.registerNewObjective(name, "dummy");
		String min = String.valueOf(time / 60);
		String sek = String.valueOf(time % 60);
		if(min.length() == 1) min = "0" + min;
		if(sek.length() == 1) sek = "0" + sek;
		obj.setDisplayName(ChatColor.YELLOW + min + ":" + sek);
		obj.getScore(color + "#" + team).setScore(2);
		obj.getScore(ChatColor.GREEN + "Sessions: "  + ChatColor.GOLD + getSessions()).setScore(1);
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		Bukkit.getPlayer(uuid).setScoreboard(scoreboard);		
	}
	
	public void updateScoreboard(int time) {
		String minuten = String.valueOf(time / 60);
		String sekunden = String.valueOf( time % 60);
		if(minuten.length() == 1) minuten = "0" + minuten;
		if(sekunden.length() == 1) sekunden = "0" + sekunden;
		if(scoreboard == null) return;
		scoreboard.getObjective(name).setDisplayName(ChatColor.YELLOW + minuten + ":" + sekunden);
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

}