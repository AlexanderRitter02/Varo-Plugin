package de.alexanderritter.varo.timemanagment;

import java.util.GregorianCalendar;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import de.alexanderritter.varo.ingame.PlayerManager;
import de.alexanderritter.varo.ingame.VaroPlayer;
import de.alexanderritter.varo.main.Varo;

public class PerHourChecker extends BukkitRunnable {
	
	Varo plugin;
	GregorianCalendar calendar;
	int timeinterval; // seconds
	
	public PerHourChecker(Varo plugin) {
		this.plugin = plugin;
		timeinterval = 60*60; //60*60 seconds
		calendar = new GregorianCalendar();
		calendar.setFirstDayOfWeek(GregorianCalendar.MONDAY);
		this.runTaskTimer(plugin, 0, timeinterval*20);
	}

	@Override
	public void run() {
		calendar = new GregorianCalendar();
		calendar.setFirstDayOfWeek(GregorianCalendar.MONDAY);
		updateWeek();
		checkCoordinatePost();
		shrinkWorldborder();
		updateHour();
	}
	
	public void updateWeek() {
		int week = calendar.get(GregorianCalendar.WEEK_OF_YEAR);
		if(plugin.getSettings().getCurrentWeek() == week) return;
		YamlConfiguration players = plugin.getPlayerConfig();
		for(String id : players.getKeys(false)) {
			players.set(id + ".sessions", plugin.getSettings().getSessionsPerWeek());
			players.set(id + ".recent_time", plugin.getSettings().getSessionsLength());
			players.set(id + ".postedcoords", Boolean.valueOf(false));
		}
		plugin.savePlayerConfig(players);
		plugin.getSettings().setCurrentWeek(week);
		for(Player p : Bukkit.getOnlinePlayers()) PlayerManager.getIngamePlayer(p).setSessions(plugin.getSettings().getSessionsPerWeek());
	}
	
	public void updateHour() {
		int hour = ((calendar.get(GregorianCalendar.DAY_OF_YEAR)-1)*24) + calendar.get(GregorianCalendar.HOUR_OF_DAY);
		FileConfiguration config = plugin.getConfig();
		config.set("plugin.hours", hour);
		plugin.saveConfig();
	}
	
	public void shrinkWorldborder() {
		if(!plugin.getSettings().isRunning()) return;
		int hour = ((calendar.get(GregorianCalendar.DAY_OF_YEAR)-1)*24) + calendar.get(GregorianCalendar.HOUR_OF_DAY);
		
		plugin.getLogger().info("Method 'shrinkWorldborder': hour is " + hour + ", plugin.hour in config is " + plugin.getConfig().getInt("plugin.hours"));
		
		int instant;
		if(plugin.getConfig().getInt("plugin.hours") != hour) {
			instant = hour - plugin.getConfig().getInt("plugin.hours");
		} else instant = 0;
		
		FileConfiguration config = plugin.getConfig();
		config.set("plugin.hours", hour);
		plugin.saveConfig();	
		
		int endsize = plugin.getConfig().getInt("border.end-radius")*2;
		double shrinkAmountPerHour = plugin.getSettings().getBorderShrinkPerHour();
		
		plugin.getLogger().info("Worldborder diameter will be shrunken by " + (double) shrinkAmountPerHour + " blocks every " + timeinterval + " seconds (" + (double) timeinterval / 3600 + " hours).");
		
		String bordermsg = "";
		for(World world : Bukkit.getWorlds()) {
			WorldBorder border = world.getWorldBorder();
			
			if((border.getSize() - instant*shrinkAmountPerHour) <= endsize || (border.getSize() - shrinkAmountPerHour) <= endsize) {
				border.setSize(endsize);
				return;
			}
			if(instant > 0) {
				bordermsg = "Worldborder was shrunken by " + instant + " times after a restart: -" + instant*shrinkAmountPerHour + " blocks\n";
				border.setSize(border.getSize() - (instant * shrinkAmountPerHour));
			}
			bordermsg += "Worldborder is shrinking from " + border.getSize() + " to " + (border.getSize() - shrinkAmountPerHour) + " in the next hour";
			border.setSize(border.getSize() - shrinkAmountPerHour, timeinterval);
		}
		instant = 0;
		plugin.getLogger().info(bordermsg);
	}
	
	public void checkCoordinatePost() {		
		if(!plugin.getSettings().isCoordinatesPostTime()) return;
		for(UUID uuid : plugin.getRegistration().getAllUUIDs()) {
			VaroPlayer ip = plugin.getRegistration().loadPlayer(uuid);
			if(ip.isDead() || ip.isAdmin()) continue;
			if(ip.hasPostedCoords()) continue;
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
				
				@Override
				public void run() {
					plugin.postPlayerCoordinates(ip);
				}
			
			}, 500);
			for(VaroPlayer member : plugin.getRegistration().getTeamMembers(ip.getTeam())) member.setPostedCoords(true);
		}	
	}

}
