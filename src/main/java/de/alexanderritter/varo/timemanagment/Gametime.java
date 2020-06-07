package de.alexanderritter.varo.timemanagment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import de.alexanderritter.varo.ingame.PlayerManager;
import de.alexanderritter.varo.ingame.VaroPlayer;
import de.alexanderritter.varo.main.Varo;

public class Gametime extends BukkitRunnable {
	
	Varo plugin;
	
	public Gametime(Varo plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		List<VaroPlayer> playerstobekicked = new ArrayList<>();
		if(!plugin.getSettings().isRunning()) return;
		Iterator<VaroPlayer> iter = PlayerManager.getAllIngamePlayers().iterator();
	    while(iter.hasNext()) {
	    	VaroPlayer ip = iter.next();
	    	if(Bukkit.getPlayer(ip.getUuid()) == null || PlayerManager.spectators.contains(ip.getUuid()) || ip.isAdmin()) continue;
	    	Player p = Bukkit.getPlayer(ip.getUuid());
			ip.setTime(ip.getTime() - 1);
			switch(ip.getTime()) {
			case 600: case 300:
				p.sendMessage(Varo.prefix + ChatColor.GRAY + "Du hast noch " + ChatColor.GOLD + ip.getTime()/60 + ChatColor.GRAY + " Minuten Zeit");
				break;
			case 60:
				p.sendMessage(Varo.prefix + ChatColor.GRAY + "Du hast noch " + ChatColor.GOLD + 1 + ChatColor.GRAY + " Minute Zeit");
				break;			 
			case 30: case 15: 
				if(!ip.getEnemyNear()) {
					for(Player online : Bukkit.getOnlinePlayers()) {
						if(!online.equals(p)) {
							online.sendMessage(Varo.prefix + ChatColor.GRAY + "Der Spieler " + ip.getColor() + ip.getName() + ChatColor.GRAY + " wird in " + ip.getTime() + " Sekunden gekickt!");
						}										
					}
				}
				p.sendMessage(Varo.prefix + ChatColor.GRAY + "Noch " + ChatColor.GOLD + ip.getTime() + ChatColor.GRAY + " Sekunden");
				break;
			case 10: case 5: case 3: case 2:
				if(!ip.getEnemyNear()) {
					p.sendMessage(Varo.prefix + ChatColor.GRAY + "Noch " + ChatColor.GOLD + ip.getTime() + ChatColor.GRAY + " Sekunden");
				}				
				break;
			case 1:
				if(!ip.getEnemyNear()) {
					p.sendMessage(Varo.prefix + ChatColor.GRAY + "Noch " + ChatColor.GOLD + "eine" + ChatColor.GRAY + " Sekunde");
				}
				break;
			case 0:
				ip.testEnemyNear();
				if(!ip.getEnemyNear()) {
					playerstobekicked.add(ip);
					
				} else {
					ip.setTime(10);
					p.sendMessage(Varo.prefix + ChatColor.RED + "Deine Session wurde verlängert. Ein Feind befindet sich in deinem Umfeld.");
				}
				
			}
	    	         
	    }
	    if(!playerstobekicked.isEmpty()) {
	    	for(VaroPlayer ip : playerstobekicked) {
	    		ip.setTime(plugin.getSettings().getSessionsLength());
				ip.setSessions(ip.getSessions() - 1);
				ip.save();
				String name = ip.getName();
				int sessions = ip.getSessions();
				PlayerManager.removeIngamePlayer(ip);
				Bukkit.getPlayer(ip.getUuid()).kickPlayer(ChatColor.RED + "Deine Session ist zuende. Du hast noch " + ChatColor.GOLD + ip.getSessions() + ChatColor.RED + " Sessions.");
				plugin.sendDiscordMessage("Der Spieler " + name + " hat noch **" + sessions + "** Sessions!");
	    	}
	    }
	
	}

}
