package de.alexanderritter.varo.events;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import de.alexanderritter.varo.api.VaroMessages;

public class StandStill implements Listener {
	
	public void unregister() {
		HandlerList.unregisterAll(this);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if(e.getFrom().getX() != e.getTo().getX() || e.getFrom().getY() != e.getTo().getY() || e.getFrom().getZ() != e.getTo().getZ()) {
            Location loc = e.getFrom();
            e.getPlayer().teleport(loc.setDirection(e.getTo().getDirection()));
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		e.getPlayer().sendMessage(VaroMessages.chatDisabled);
		e.setCancelled(true);
	}
	
}