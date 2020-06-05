package de.alexanderritter.varo.timemanagment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import de.alexanderritter.varo.api.UUIDs;
import de.alexanderritter.varo.ingame.PlayerManager;
import de.alexanderritter.varo.ingame.VaroPlayer;
import de.alexanderritter.varo.main.Varo;
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;

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
		updateHour();
		checkCoordinatePost();
		shrinkWorldborder();
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
		
		int endsize = plugin.getConfig().getInt("borderendradius")*2;
		double shrinkAmountPerHour = plugin.getSettings().getBorderShrinkPerHour();
		
		System.out.println("Worldborder diameter will be shrunken by " + (double) shrinkAmountPerHour + " blocks every " + timeinterval + " seconds (" + (double) timeinterval / 3600 + " hours).");
		
		for(World world : Bukkit.getWorlds()) {
			WorldBorder border = world.getWorldBorder();
			
			if((border.getSize() - instant*shrinkAmountPerHour) <= endsize || (border.getSize() - shrinkAmountPerHour) <= endsize) {
				border.setSize(endsize);
				return;
			}
			if(instant > 0) {
				plugin.getLogger().warning("Worldborder was shrunken by " + instant + " times after a restart: -" + instant*shrinkAmountPerHour + " blocks");
				border.setSize(border.getSize() - (instant * shrinkAmountPerHour));
				instant = 0;
			}
			plugin.getLogger().info("Worldborder is shrinking from " + border.getSize() + " to " + (border.getSize() - shrinkAmountPerHour) + " in the next hour");
			border.setSize(border.getSize() - shrinkAmountPerHour, timeinterval);
		}
	}
	
	public void checkCoordinatePost() {
		String date = new SimpleDateFormat("EEEE, HH", Locale.US).format(calendar.getTime());
		System.out.println("Checking COORDINATE POST, it is " + date);
		
		if(!date.equals(plugin.getConfig().getString("coord_post"))) return;
		for(UUID uuid : plugin.getRegistration().getAllUUIDs()) {
			VaroPlayer ip = plugin.getRegistration().loadPlayer(uuid);
			if(ip.isDead() || ip.isAdmin()) continue;
			if(ip.hasPostedCoords()) continue;
			System.out.println("Posting coordinates of " + ip.getName());
			File nbtdat = new File(Bukkit.getWorldContainer()+File.separator+plugin.getSettings().getVaroWorld().getName()+"/playerdata/" + ip.getUuid() + ".dat");
			if(!nbtdat.exists()) {plugin.getLogger().warning(("Skipping coordinate post for "+ip.getName()+": Has never joined the server"));continue;}
			try {
				NBTTagCompound playernbt = NBTCompressedStreamTools.a(new FileInputStream(nbtdat));
				NBTTagList pos = (NBTTagList) playernbt.get("Pos");
				
				BigInteger addifneg = new BigInteger("18446744073709551616");
				long uuidmost = playernbt.getLong("WorldUUIDMost");
				long uuidleast = playernbt.getLong("WorldUUIDLeast");
				if(uuidmost < 0) uuidmost = addifneg.add(BigInteger.valueOf(uuidmost)).longValue();
				if(uuidleast < 0) uuidleast = addifneg.add(BigInteger.valueOf(uuidleast)).longValue();
				String mosthex = Long.toHexString(uuidmost);
				String leasthex = Long.toHexString(uuidleast);
				String worldUUID = UUIDs.convertFromTrimmed(mosthex + leasthex);
				
				String world = Bukkit.getWorld(UUID.fromString(worldUUID)).getName();
				double x = Math.round(Double.valueOf(pos.getString(0))*100)/100.00;
				double y = Math.round(Double.valueOf(pos.getString(1))*100)/100.00;
				double z = Math.round(Double.valueOf(pos.getString(2))*100)/100.00;
				
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {

					@Override
					public void run() {
						plugin.sendDiscordMessage("```http\n " + "Team " + ip.getTeam() + 
								" hat seine Koordinaten gepostet: " + x + ", " + y + ", " + z + " ("+ world + ") \n ```");
						Bukkit.broadcastMessage("Team " + ip.getTeam() + 
								" hat seine Koordinaten gepostet: " + x + ", " + y + ", " + z + " ("+ world + ")");
					}	
					
				}, 500);
				for(VaroPlayer member : plugin.getRegistration().getTeamMembers(ip.getTeam())) member.setPostedCoords(true);		
			} catch (IOException e) {System.out.println("An error has happend during coordinate Post");e.printStackTrace();}	
		}	
	}

}
