package de.alexanderritter.varo.commands;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import de.alexanderritter.varo.ingame.ChestManager;
import de.alexanderritter.varo.ingame.PlayerManager;
import de.alexanderritter.varo.ingame.VaroPlayer;
import de.alexanderritter.varo.main.Varo;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.PacketPlayOutCloseWindow;

public class Overwrite implements CommandExecutor {
	
	Varo plugin;
	
	public Overwrite(Varo plugin) {
		this.plugin = plugin;
	}
	
	private static final HashMap<Player, Location> players = new HashMap<Player, Location>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!command.getName().equalsIgnoreCase("overwrite")) return false;
		if(!(sender instanceof Player)) return false;
		if(args.length == 0) {		
		Player p = (Player) sender;
		if(!players.keySet().contains(p)) {p.sendMessage(ChatColor.RED + "Du hast zurzeit keine Teamchest-Anfragen"); return false;}
		
		Location loc = players.get(p);
		if(loc.getBlock().getType() != Material.WALL_SIGN) {
			p.sendMessage(ChatColor.RED + "An der vorgeschlagenen Stelle gibt es kein Schild mehr");
			players.remove(p);
			return false;}
		org.bukkit.material.Sign sign = (org.bukkit.material.Sign) loc.getBlock().getState().getData();
		
		if(loc.getBlock().getRelative(sign.getAttachedFace()).getType() != Material.CHEST) {
			p.sendMessage(ChatColor.RED + "Die Teamchest, die du vorgeschlagen hast, existiert nicht mehr");
			players.remove(p);
			return false;}
		Chest chest = (Chest) loc.getBlock().getRelative(sign.getAttachedFace()).getState();
		
		InventoryHolder ih = ((InventoryHolder) chest).getInventory().getHolder();
		if(!(ih instanceof DoubleChest)) {
			p.sendMessage(ChatColor.RED + "An der vorgeschlagenen Stelle gibt es keine Doppelkiste");
			loc.getBlock().breakNaturally();
			players.remove(p);
			return false;}
		DoubleChest dc = (DoubleChest) ih;
		
		if(!PlayerManager.getAllUUIDs().contains(p.getUniqueId())) return false;
		VaroPlayer ip = PlayerManager.getIngamePlayer(p);
		
		double x = dc.getX() - dc.getLocation().getBlockX();
		double z = dc.getZ() - dc.getLocation().getBlockZ();
		Block chest1 = dc.getLocation().add(x, 0, z).getBlock();
    	Block chest2 = dc.getLocation().subtract(x, 0, z).getBlock();
    	
    	
    	plugin.getRegistration().registerChest(ip, chest1, chest2);
    	ChestManager.removeChest(ip.getTeam());
		ChestManager.addChest(ip.getTeam(), chest1, chest2);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				Sign signedit = (Sign) loc.getBlock().getState();
				signedit.setLine(1, ip.getColor() + "#" + ip.getTeam());
				signedit.update();	
				
			}
		}, 5);
			
		PacketPlayOutCloseWindow pack = new PacketPlayOutCloseWindow();		
		((CraftPlayer)p).getHandle().playerConnection.sendPacket(pack);
		players.remove(p);
		
		p.sendMessage(Varo.prefix + ChatColor.DARK_GREEN + "Deine Teamchest wurde erfolgreich überschrieben!");
		} else if(args.length == 2) {
			if(!args[0].equalsIgnoreCase("delete")) return false;
			Player p = Bukkit.getPlayerExact(args[1]);
			if(!players.containsKey(p)) return false;
			players.remove(p);
		}
		
		return false;
	}
	
	public static final void addRequest(Player p, Location loc) {
		players.put(p, loc);
	}

}
