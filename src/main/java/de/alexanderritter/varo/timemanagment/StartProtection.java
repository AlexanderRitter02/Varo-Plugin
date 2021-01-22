package de.alexanderritter.varo.timemanagment;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import de.alexanderritter.varo.api.VaroMessages;
import de.alexanderritter.varo.events.CancelDamage;
import de.alexanderritter.varo.main.Varo;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

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
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Schutzzeit: " + ChatColor.GOLD + time));
		}
		switch (time) {
		case 300: case 240: case 180: case 120:
			Bukkit.broadcastMessage(VaroMessages.protectionTimeEndsInMinutes(time/60));
			break;
		case 60: case 30: case 10: case 5: case 3: case 2: case 1:
			VaroMessages.protectionTimeEndsInSeconds(time);
			break;
		case 0:
			Bukkit.broadcastMessage(VaroMessages.protectionTimeExipred);
			for(Player p : Bukkit.getOnlinePlayers()) {
				p.playSound(p.getLocation(), Sound.ENTITY_WITHER_DEATH, 1, 1);
				p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(VaroMessages.protectionTimeExpired_Actionbar));
			}
			this.stop();
			break;
		default:
			break;
		}
	}

}
