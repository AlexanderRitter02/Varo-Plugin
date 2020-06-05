package de.alexanderritter.varo.events;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import de.alexanderritter.varo.ingame.PlayerManager;

public class LoginProtectionListener implements Listener {
	
	public void unregister() {
		HandlerList.unregisterAll(this);
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(p.getGameMode() == GameMode.SPECTATOR || p.getGameMode() == GameMode.CREATIVE) continue;
			p.setAllowFlight(false);
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(!(e.getEntity() instanceof Player)) return;
		if(PlayerManager.getIngamePlayer((Player) e.getEntity()) == null || !PlayerManager.getIngamePlayer((Player) e.getEntity()).isLoginProtected()) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(PlayerManager.getIngamePlayer(p) == null || !PlayerManager.getIngamePlayer(p).isLoginProtected()) return;
		p.setAllowFlight(true);
		if(e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ() || e.getFrom().getY() < e.getTo().getY()) {
            Location loc = e.getFrom();
            p.teleport(loc.setDirection(e.getTo().getDirection()));
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(PlayerManager.getIngamePlayer(p) == null || !PlayerManager.getIngamePlayer(p).isLoginProtected()) return;
		if(!(e.getAction() == Action.RIGHT_CLICK_AIR && p.getItemInHand().getType() == Material.ENDER_PEARL)) return;
		e.setCancelled(true);
	}

}