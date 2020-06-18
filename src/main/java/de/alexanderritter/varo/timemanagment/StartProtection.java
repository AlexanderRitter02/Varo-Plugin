package de.alexanderritter.varo.timemanagment;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import de.alexanderritter.varo.api.Actionbar;
import de.alexanderritter.varo.events.CancelDamage;
import de.alexanderritter.varo.main.Varo;

public class StartProtection extends BukkitRunnable {
	
	Varo plugin;
	int time;
	CancelDamage canceldamage;
	
	public StartProtection(Varo plugin, int time) {
		this.plugin = plugin;
		this.time = time;
	}

	public void start() {
		this.runTaskTimer(plugin, 0, 20);
		canceldamage = new CancelDamage();
		Bukkit.getPluginManager().registerEvents(canceldamage, plugin);
	}

	public void stop() {
		canceldamage.unregister();
		cancel();
	}

	@Override
	public void run() {
		time--;
		Actionbar actionbar = new Actionbar(ChatColor.RED + "Schutzzeit: " + ChatColor.GOLD + time);
		for(Player p : Bukkit.getOnlinePlayers()) {
			actionbar.send(p);
		}
		switch (time) {
		case 300: case 240: case 180: case 120:
			Bukkit.broadcastMessage(Varo.prefix + ChatColor.RED + "Die Schutzzeit ist in " + ChatColor.GOLD + time/60 + ChatColor.RED + " Minuten zu Ende");
			break;
		case 60: case 30: case 10: case 5: case 3: case 2: case 1:
			if(time != 1) Bukkit.broadcastMessage(Varo.prefix + ChatColor.RED + "Die Schutzzeit ist in " + ChatColor.GOLD + time + ChatColor.RED + " Sekunden zu Ende");
			if(time == 1) Bukkit.broadcastMessage(Varo.prefix + ChatColor.RED + "Die Schutzzeit ist in " + ChatColor.GOLD + "1" + ChatColor.RED + " Sekunde zu Ende");
			break;
		case 0:
			actionbar.setText(ChatColor.RED + "Die Schutzzeit ist zu Ende. Let's go!");
			for(Player p : Bukkit.getOnlinePlayers()) {
				p.playSound(p.getLocation(), Sound.WITHER_DEATH, 1, 1);
				actionbar.send(p);
			}
			Bukkit.broadcastMessage(Varo.prefix + ChatColor.GREEN + "Die Schutzzeit ist zu Ende. Ihr könnt euch nun angreifen!");
			this.stop();
			break;
		default:
			break;
		}
	}

}
