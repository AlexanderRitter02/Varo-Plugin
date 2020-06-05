package de.alexanderritter.varo.commands;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.alexanderritter.varo.main.Varo;

public class CMDbordersize implements CommandExecutor {
	
	Varo plugin;
	
	public CMDbordersize(Varo plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!cmd.getName().equalsIgnoreCase("bordersize")) return false;
		if(args.length != 0) return false;
		double radius = Bukkit.getWorlds().get(0).getWorldBorder().getSize() / 2;		
		
		if(!(sender instanceof Player)) {
			
			sender.sendMessage(ChatColor.GOLD + "Der Radius der Worldborder beträgt " +
			ChatColor.GREEN + Math.round(radius) + ChatColor.GOLD +  " Blöcke");
			
		} else {
			
			Player p = (Player) sender;
			WorldBorder border = p.getWorld().getWorldBorder();			
			Location loc = p.getLocation();
			Location center = border.getCenter();
			
			
			String radiusString = ChatColor.DARK_GREEN + "Radius: " + ChatColor.GREEN;
			int endsize = plugin.getConfig().getInt("borderendradius")*2;
			if(radius <= (endsize + 500)) radiusString += ChatColor.GOLD;
			if(radius <= endsize) radiusString += ChatColor.RED;
			radiusString += Math.round(radius) + " Blöcke";
			
			
			int distX_positive = Math.abs(center.getBlockX() + (int) radius - loc.getBlockX());
			int distX_negative = Math.abs(center.getBlockX() - (int) radius - loc.getBlockX());
			int distZ_positive = Math.abs(center.getBlockZ() + (int) radius - loc.getBlockZ());
			int distZ_negative = Math.abs(center.getBlockZ() - (int) radius - loc.getBlockZ());
	
			int shortestDistance = Math.min(Math.min(distX_positive, distX_negative), Math.min(distZ_positive, distZ_negative));
			
			String distString = ChatColor.DARK_GREEN  + "Entfernung: " + ChatColor.GREEN;
			if(shortestDistance <= 1000) distString += ChatColor.GOLD;
			if(shortestDistance <= 500) distString += ChatColor.RED;
			distString += shortestDistance + " Blöcke";
			
			
			double hours = shortestDistance / plugin.getSettings().getBorderShrinkPerHour();			
			String hourString = ChatColor.DARK_GREEN + "Zeit bis zu dir: " + ChatColor.GREEN;
			if(hours <= 24*7) hourString += ChatColor.GOLD;
			if(hours <= 24*2) hourString += ChatColor.RED;
			DecimalFormat df = new DecimalFormat("#.##");
			if(hours < 24) {
				hourString += df.format(hours) + " Stunden";
			} else {
				hourString += df.format(hours / 24) + " Tage";
			}
			
			
			
			sender.sendMessage(ChatColor.DARK_GREEN + "" + ChatColor.UNDERLINE + "" + ChatColor.BOLD + "Worldborder Informationen");
			sender.sendMessage(ChatColor.DARK_GREEN + "----------------------");
			sender.sendMessage(radiusString);
			sender.sendMessage(distString);
			sender.sendMessage(hourString);
		}
		
		return true;
	}
	
}
