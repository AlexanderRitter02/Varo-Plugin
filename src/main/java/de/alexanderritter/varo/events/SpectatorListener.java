package de.alexanderritter.varo.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import de.alexanderritter.varo.api.VaroMessages;
import de.alexanderritter.varo.ingame.PlayerManager;
import de.alexanderritter.varo.ingame.VaroPlayer;
import de.alexanderritter.varo.main.Varo;

public class SpectatorListener implements Listener {
	
	Varo plugin;
	
	public SpectatorListener(Varo plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.setAllowFlight(true);
		if(plugin.getRegistration().loadPlayer(p).isDead()) {
			PlayerManager.addSpectator(p);
		} else {
			if(plugin.getPlayerConfig().get(p.getUniqueId()  + ".reviving") != null) {
				YamlConfiguration yml = plugin.getPlayerConfig();
				yml.set(p.getUniqueId()  + ".reviving", null);
				plugin.savePlayerConfig(yml);
				
				p.teleport(plugin.getSettings().getVaroWorld().getSpawnLocation());
			}
			// Hide all existent spectators from the new joining player
			for(Player online : Bukkit.getServer().getOnlinePlayers()) {
				if(!PlayerManager.spectators.contains(online.getUniqueId())) continue;
				p.hidePlayer(online);
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(!PlayerManager.spectators.contains(e.getPlayer().getUniqueId())) return;
		Player p = e.getPlayer();
		if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			
			if(e.getItem() != null && e.getItem().getType() == Material.COMPASS) {
				
				int invSize = 9;
				
				VaroPlayer ip = plugin.getRegistration().loadPlayer(p);
				Inventory inv = null;
				
				if(plugin.getRegistration().isTeamAlive(ip.getTeam())) {
					// TODO only bigger inventory for online players
					while(plugin.getRegistration().getTeamMembers(ip.getTeam()).size() > invSize) invSize += 9;
					inv = Bukkit.createInventory(null, invSize, "Teleport to Player");
					for(VaroPlayer member : plugin.getRegistration().getTeamMembers(ip.getTeam())) {
						if(Bukkit.getPlayer(member.getUuid()) == null) continue;
						inv.addItem(createPlayerSkull(member.getName()));
					}
				} else {
					while(PlayerManager.getAllIngamePlayers().size() > invSize) invSize += 9;
					inv = Bukkit.createInventory(null, invSize, "Teleport to Player");
					for(VaroPlayer online : PlayerManager.getAllIngamePlayers()) {
						if(!online.isDead()) inv.addItem(createPlayerSkull(online.getName()));
					}
				}
				
				p.openInventory(inv);
				
			} else if(e.getItem() != null && e.getItem().getType() == Material.BARRIER) {
				specatorInventory(p);
			}
						
			specatorInventory(p);
		}
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if(!PlayerManager.spectators.contains(e.getWhoClicked().getUniqueId())) return;
		Player p = (Player) e.getWhoClicked();
		ItemStack clicked = e.getCurrentItem();
		
		if(e.getInventory().getName().equalsIgnoreCase("Teleport to Player")) {
			if(clicked != null && clicked.getType() == Material.SKULL_ITEM) {
				Player to = Bukkit.getPlayerExact(clicked.getItemMeta().getDisplayName());
				if(to != null) {
					p.teleport(to);
					p.sendMessage(VaroMessages.teleportedTo(to.getName()));
				} else {
					p.sendMessage(VaroMessages.teleportToFailed(clicked.getItemMeta().getDisplayName()));
					p.closeInventory();
				}
			}
		}
		e.setCancelled(true);
	}
	
	private void specatorInventory(Player p) {
		p.getInventory().setItem(0, new ItemStack(Material.COMPASS));
	}
	
	private ItemStack createPlayerSkull(String playername) {
		
		ItemStack playerhead = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        
        SkullMeta playerheadmeta = (SkullMeta) playerhead.getItemMeta();
        playerheadmeta.setOwner(playername);
        playerheadmeta.setDisplayName(playername);
        playerhead.setItemMeta(playerheadmeta);
        
        return playerhead;
	}

	@EventHandler
	public void onRightClick(PlayerInteractEntityEvent e) {
		if(!PlayerManager.spectators.contains(e.getPlayer().getUniqueId())) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onHurtEntity(EntityDamageByEntityEvent e) {
		if(!(e.getDamager() instanceof Player)) return;
		if(!PlayerManager.spectators.contains(e.getDamager().getUniqueId())) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onVehicleDamage(VehicleDamageEvent e) {
		if(!(e.getAttacker() instanceof Player)) return;
		if(!PlayerManager.spectators.contains(e.getAttacker().getUniqueId())) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent e) {
		if(!PlayerManager.spectators.contains(e.getPlayer().getUniqueId())) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e) {
		if(!PlayerManager.spectators.contains(e.getPlayer().getUniqueId())) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onStatisticChange(PlayerStatisticIncrementEvent e) {
		if(!PlayerManager.spectators.contains(e.getPlayer().getUniqueId())) return;
		e.setCancelled(true);
	}
	
	@EventHandler	
	public void onUnleashEntity(PlayerUnleashEntityEvent e) {
		if(!PlayerManager.spectators.contains(e.getPlayer().getUniqueId())) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(!(e.getEntity() instanceof Player)) return;
		if(!PlayerManager.spectators.contains(e.getEntity().getUniqueId()) || e.getCause().equals(DamageCause.SUICIDE) || e.getCause().equals(DamageCause.VOID)) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if(!PlayerManager.spectators.contains(e.getPlayer().getUniqueId())) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onHunger(FoodLevelChangeEvent e) {
		if(!PlayerManager.spectators.contains(e.getEntity().getUniqueId())) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityTargeting(EntityTargetEvent e) {
		if(!(e.getTarget() instanceof Player)) return;
		if(!PlayerManager.spectators.contains(e.getTarget().getUniqueId())) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityTargeting(EntityTargetLivingEntityEvent e) {
		if(!(e.getTarget() instanceof Player)) return;
		if(!PlayerManager.spectators.contains(e.getTarget().getUniqueId())) return;
		e.setCancelled(true);
	}
	
}
