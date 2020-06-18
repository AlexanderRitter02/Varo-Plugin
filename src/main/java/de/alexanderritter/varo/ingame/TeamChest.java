package de.alexanderritter.varo.ingame;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;

import de.alexanderritter.varo.commands.Overwrite;
import de.alexanderritter.varo.main.Varo;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutCloseWindow;

public class TeamChest implements Listener {
	
	Varo plugin;
	
	public TeamChest(Varo plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onOpen(PlayerInteractEvent e) {
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		Block block = e.getClickedBlock();
		if(block.getType() != Material.CHEST) return;
		Location chest = block.getLocation();
		if(!allowToOpen(chest, e.getPlayer())) e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlaceSign(BlockPlaceEvent e) {
		if(!(e.getBlockPlaced().getType() == Material.WALL_SIGN)) return;
		if(!(e.getBlockAgainst().getState() instanceof Chest)) return;
		Chest chest = (Chest) e.getBlockAgainst().getState();
		InventoryHolder ih = ((InventoryHolder) chest).getInventory().getHolder();
		if(!(ih instanceof DoubleChest)) return;
		DoubleChest dc = (DoubleChest) ih;
		
		Player p = e.getPlayer();
		if(!PlayerManager.getAllUUIDs().contains(p.getUniqueId())) return;
		VaroPlayer ip = PlayerManager.getIngamePlayer(p);
		
		if(ChestManager.getAllOwners().contains(ip.getTeam())) {
			p.sendMessage(Varo.prefix + ChatColor.RED + "Du hast schon eine Teamchest. Soll eine neue erstellt werden?");
			TextComponent message = new TextComponent(ChatColor.GREEN + "Ja");
			message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/overwrite"));
			TextComponent between = new TextComponent(ChatColor.GRAY + " | ");
			message.addExtra(between);
			TextComponent no = new TextComponent(ChatColor.DARK_RED + "Nein");
			no.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/overwrite delete" + ip.getName()));
			message.addExtra(no);
			
			p.spigot().sendMessage(message);
			Overwrite.addRequest(p, e.getBlockPlaced().getLocation());
			PacketPlayOutCloseWindow pack = new PacketPlayOutCloseWindow();
			((CraftPlayer)p).getHandle().playerConnection.sendPacket(pack);
			return;
		}
		
		double x = dc.getX() - dc.getLocation().getBlockX();
		double z = dc.getZ() - dc.getLocation().getBlockZ();
		Block chest1 = dc.getLocation().add(x, 0, z).getBlock();
    	Block chest2 = dc.getLocation().subtract(x, 0, z).getBlock();
    	plugin.getRegistration().registerChest(ip, chest1, chest2);
		ChestManager.addChest(ip.getTeam(), chest1, chest2);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				Sign sign = (Sign) e.getBlockPlaced().getState();
				sign.setLine(1, ip.getColor() + "#" + ip.getTeam());
				sign.update();
				
			}
		}, 5);
			
		PacketPlayOutCloseWindow pack = new PacketPlayOutCloseWindow();
		((CraftPlayer)p).getHandle().playerConnection.sendPacket(pack);
		
		p.sendMessage(Varo.prefix + ChatColor.GREEN + "Teamkiste erstellt");
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if(e.getBlock().getType() != Material.CHEST) return;
		Location chest = e.getBlock().getLocation();
		Player p = e.getPlayer();
		if(!allowToOpen(chest, p)) {
			e.setCancelled(true);
			return;
		}
		String owner = ChestManager.getOwner(chest);
		if(owner == null) return;
		ChestManager.removeChest(owner);
		plugin.getRegistration().deleteChest(owner);
		if(PlayerManager.getIngamePlayer(p).getTeam().equalsIgnoreCase(owner)) p.sendMessage(Varo.prefix + ChatColor.RED + "Du hast deine Teamchest zerstört");
	}
	
	@EventHandler
	public void onHopperPlace(BlockPlaceEvent e) {
		if(e.getBlock().getType() != Material.HOPPER) return;
		Block block = e.getBlockPlaced();
		if(!block.getRelative(BlockFace.UP).getType().equals(Material.CHEST)) return;
		Location chest = block.getRelative(BlockFace.UP).getLocation();
		if(!allowToOpen(chest, e.getPlayer())) e.setCancelled(true);
	}
	
	@EventHandler
	public void onExplode(EntityExplodeEvent e) {
		Iterator<Block> iter = e.blockList().iterator();
	    while (iter.hasNext()) {
	    	Block b = iter.next();
	        if (b.getType() == Material.CHEST) {
	        	if(isProtected(b.getLocation())) iter.remove();
	        }
	    }
	}
	
	public boolean allowToOpen(Location chest, Player p) {
		String team = ChestManager.getOwner(chest);
		if(team == null) return true;
		if(!plugin.getRegistration().isTeamAlive(team)) return true;
		if(PlayerManager.getIngamePlayer(p) == null) return false;
		VaroPlayer ip = PlayerManager.getIngamePlayer(p);
		if(ip.isAdmin()) return true;
		if(ip.getTeam().equalsIgnoreCase(team)) return true;
		p.sendMessage(ChatColor.RED + "Diese Truhe gehört Team " + ChatColor.GOLD + "#" + team);
		return false;
	}
	
	public boolean isProtected(Location chest) {
		String owner = ChestManager.getOwner(chest);
		if (owner == null) return false;
		if (this.plugin.getRegistration().isTeamAlive(owner)) return true;
		return false;
	}
	
}