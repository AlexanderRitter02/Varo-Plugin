package de.alexanderritter.varo.timemanagment;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import de.alexanderritter.varo.events.LoginProtectionListener;
import de.alexanderritter.varo.ingame.VaroPlayer;
import de.alexanderritter.varo.main.Varo;

public class LoginProtection extends BukkitRunnable {
	
	Varo plugin;
	int sec;
	Player p;
	VaroPlayer ip;
	LoginProtectionListener lpl;
	
	public LoginProtection(Varo plugin, Player p, VaroPlayer ip, int sec) {
		this.sec = sec;
		this.p = p;
		this.ip = ip;
		ip.setLoginProtect(true);
		this.lpl = new LoginProtectionListener();
		Bukkit.getPluginManager().registerEvents(lpl, plugin);
	}

	@Override
	public void run() {
		if(sec <= 0) {
			p.sendMessage(Varo.prefix + ChatColor.GREEN + "Du kannst dich nun bewegen");
			ip.setLoginProtect(false);
			lpl.unregister();
			cancel();
		}
		if(sec > 0) p.sendMessage(Varo.prefix + ChatColor.GRAY + "Du kannst dich in " + sec + " Sekunden bewegen!");
		sec--;
	}

}
