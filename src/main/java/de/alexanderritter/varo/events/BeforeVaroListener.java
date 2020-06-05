package de.alexanderritter.varo.events;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

import de.alexanderritter.varo.ingame.PlayerManager;
import de.alexanderritter.varo.main.Varo;

public class BeforeVaroListener implements Listener {
	
	Varo plugin;
	
	public BeforeVaroListener(Varo plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if(e.getPlayer().getGameMode() == GameMode.CREATIVE || e.getPlayer().getGameMode() == GameMode.SPECTATOR) return;
		e.getPlayer().teleport(plugin.getSettings().getLobby());
	}
	
	@EventHandler
	public void onBlockBreak(PlayerInteractEvent e) {
		if(e.getPlayer().getGameMode() == GameMode.CREATIVE) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(!(e.getEntity() instanceof Player)) return;
		if(e.getCause() == DamageCause.VOID || e.getCause() == DamageCause.SUICIDE) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onStatisticChange(PlayerStatisticIncrementEvent e) {
		if(!PlayerManager.spectators.contains(e.getPlayer().getUniqueId())) return;
		e.setCancelled(true);
	}
	
}
