package de.alexanderritter.varo.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class CancelDamage implements Listener {
	
	public void unregister() {
		EntityDamageEvent.getHandlerList().unregister(this);
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(!(e.getEntity() instanceof Player)) return;
		if(e.getCause() == DamageCause.VOID || e.getCause() == DamageCause.SUICIDE) return;
		e.setCancelled(true);
	}

}