package de.alexanderritter.varo.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachment;

import de.alexanderritter.varo.ingame.PlayerManager;
import de.alexanderritter.varo.ingame.VaroPlayer;
import de.alexanderritter.varo.main.Varo;
import de.alexanderritter.varo.timemanagment.LoginProtection;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand.EnumClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

public class IngameEvents implements Listener {
	
	Varo plugin;
	HashMap<UUID,PermissionAttachment> permissions = new HashMap<>();
	
	public IngameEvents(Varo plugin) {
		this.plugin = plugin;
	}
	
	public void unregister() {
		HandlerList.unregisterAll(this);
	}
	
	
	@EventHandler
	public void onLogin(PlayerLoginEvent e) {
		plugin.getPerHourChecker().updateWeek();
		Player p = e.getPlayer();
		if(plugin.getRegistration().getAllUUIDs().contains(p.getUniqueId())) {
			VaroPlayer ip = plugin.getRegistration().loadPlayer(p);
			if(ip.isAdmin()) {PlayerManager.addIngamePlayer(p, ip); return;}
			if(ip.getSessions() <= 0) {e.disallow(Result.KICK_BANNED, ChatColor.RED + "Du hast keine Sessions mehr in dieser Woche!"); return;}
			if(ip.isDead()) {
				PermissionAttachment attachment = p.addAttachment(plugin);
				permissions.put(p.getUniqueId(), attachment);
				attachment.setPermission("discordsrv.silentquit", true);
				attachment.setPermission("discordsrv.silentjoin", true);
				if(!plugin.getSettings().isAllowedSpectateIfTeamAlive() && plugin.getRegistration().isTeamAlive(ip.getTeam())) e.disallow(Result.KICK_BANNED, ChatColor.RED + "Du kannst nur spectaten, wenn dein ganzes Team gestorben ist.");
				if(plugin.getRegistration().isTeamAlive(ip.getTeam()) && !plugin.getRegistration().isTeamMemberOnline(ip.getTeam())) e.disallow(Result.KICK_BANNED, ChatColor.RED + "Du kannst nur spectaten, wenn ein Teammitglied online ist.");
				return;
			}
			
			PlayerManager.addIngamePlayer(p, ip);
		} else e.disallow(Result.KICK_BANNED, ChatColor.RED + "Du bist nicht registriert.\nBitte informiere den Admin, falls du glaubst, dies sei ein Fehler!");
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		plugin.getPerHourChecker().updateWeek();
		e.setJoinMessage(null);
		if(PlayerManager.spectators.contains(p.getUniqueId())) return;
		if(PlayerManager.getIngamePlayer(p) == null) return;
		VaroPlayer ip = PlayerManager.getIngamePlayer(p);
		if(ip.isAdmin()) {
			if(plugin.getSettings().isAdmin(p.getUniqueId()).equals("admin_temp")) {
				plugin.getConfig().set("plugin.admins." + p.getUniqueId() + "_loc.world", p.getLocation().getWorld().getName());
				plugin.getConfig().set("plugin.admins." + p.getUniqueId() + "_loc.x", p.getLocation().getX());
				plugin.getConfig().set("plugin.admins." + p.getUniqueId() + "_loc.y", p.getLocation().getY());
				plugin.getConfig().set("plugin.admins." + p.getUniqueId() + "_loc.z", p.getLocation().getZ());
				plugin.saveConfig();
				p.teleport(p.getLocation().getWorld().getSpawnLocation());
			}
			p.setGameMode(GameMode.SPECTATOR);			
			String title_msg = "Du bist nun im Admin Modus";
			String subtitle_msg = "Nicht zum eigenen Vorteil verwenden";
			IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\": \"" + title_msg + "\",color:" + ChatColor.DARK_GREEN.name().toLowerCase() + "}");
			IChatBaseComponent chatSubTitle = ChatSerializer.a("{\"text\": \"" + subtitle_msg + "\",color:" + ChatColor.GREEN.name().toLowerCase() + "}");
			PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle);
			PacketPlayOutTitle subtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, chatSubTitle);
			PacketPlayOutTitle length = new PacketPlayOutTitle(5, 60, 5);
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(title);
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(subtitle);
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(length);
			return;
		}
		ip.setupScoreboard();
		String name = p.getName();
		p.setPlayerListName(ip.getColor() + name);
		
		WorldBorder border = p.getWorld().getWorldBorder();
		
		if(plugin.getWorldBorder().isOutsideOfBorder(p, border)) {
			p.sendMessage(ChatColor.RED + " [WARNING] Du bist außerhalb der Worldborder");
			switch(plugin.getSettings().getBorderMode()) {
				case KILL:
					p.setHealth(0.0);
					break;
				case EDGE:
					p.teleport(plugin.getWorldBorder().getCorrectedLocation(p.getLocation(), border));
					break;
				case SPAWN:
					p.teleport(plugin.getSettings().getVaroWorld().getSpawnLocation());
					break;
				default:
			}
		}
		
		if(plugin.getSettings().getLoginProtection() <= 0) return;
		new LoginProtection(plugin, p, ip, plugin.getSettings().getLoginProtection()).runTaskTimer(plugin, 0, 20);
		e.setJoinMessage(ChatColor.GRAY + ">> Der Spieler " + ip.getColor() + p.getName() + ChatColor.GRAY + " ist dem Spiel beigetreten!");
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		permissions.remove(p.getUniqueId());
		e.setQuitMessage(null);
		if(PlayerManager.getIngamePlayer(p) == null) return;
		VaroPlayer ip = PlayerManager.getIngamePlayer(p);
		if(ip.isAdmin()) {
			if(plugin.getSettings().isAdmin(p.getUniqueId()).equals("admin_temp")) {
				plugin.getConfig().set("plugin.admins." + p.getUniqueId(), null);
				plugin.saveConfig();
				String locString = "plugin.admins." + p.getUniqueId() + "_loc.";
				World world = Bukkit.getWorld(plugin.getConfig().getString(locString + "world"));
				double x = plugin.getConfig().getDouble(locString + "x");
				double y = plugin.getConfig().getDouble(locString + "y");
				double z = plugin.getConfig().getDouble(locString + "z");
				Location old_loc = new Location(world, x, y, z);
				p.teleport(old_loc);
				p.setGameMode(GameMode.SURVIVAL);
			}
			ip.setAdmin(false);
			return;
		}
		
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				for(VaroPlayer member : plugin.getRegistration().getTeamMembers(ip.getTeam())) {
					if(!member.isSpectator() || !(plugin.getRegistration().isTeamAlive(ip.getTeam()))) continue;
					if(Bukkit.getPlayer(member.getUuid()) == null) continue;
					Bukkit.getPlayer(member.getUuid()).kickPlayer(ChatColor.RED + "Du wurdest gekickt, weil " + ChatColor.GOLD +  ip.getName() +  ChatColor.RED + " das Spiel verlassen hat.\n\n"
							+ "Du kannst nur spectaten, wenn ein Teammitglied online ist.");
				}
			}
		}, 10*20);
		PlayerManager.removeIngamePlayer(ip);
		ip.save();
		e.setQuitMessage(ChatColor.GRAY + ">> Der Spieler " + ip.getColor() + p.getName() + ChatColor.GRAY + " hat das Spiel verlassen!");
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if(!(e.getEntity() instanceof Player)) return;
		Player p = (Player) e.getEntity();
		if(PlayerManager.getIngamePlayer(p) == null) return;
		Entity damager = e.getDamager();
		if(damager instanceof Player) {
			if(PlayerManager.getIngamePlayer((Player) damager) == null) {e.setCancelled(true); return;}
			if(plugin.getSettings().isFriendlyfire()) return;
			if(PlayerManager.getIngamePlayer(p).getTeam().equalsIgnoreCase(PlayerManager.getIngamePlayer((Player) damager).getTeam())) {
				e.setCancelled(true);
			}
		} else if(damager instanceof Projectile) {
			Projectile projectile = (Projectile) damager;
			if(!(projectile.getShooter() instanceof Player)) return;
			Player shooter = (Player) projectile.getShooter();
			if(PlayerManager.getIngamePlayer(shooter) == null) {e.setCancelled(true); return;}
			if(plugin.getSettings().isFriendlyfire()) return;
			if(PlayerManager.getIngamePlayer(p).getTeam().equalsIgnoreCase(PlayerManager.getIngamePlayer(shooter).getTeam())) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		if(PlayerManager.getIngamePlayer(p) == null) return;
		VaroPlayer ip = PlayerManager.getIngamePlayer(p);
		if(ip.isAdmin()) return;
		for(Player online : Bukkit.getOnlinePlayers()) {online.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1, 1);}
		plugin.sendDiscordMessage("```http\n " + ip.getName() + " ist aus Varo ausgeschieden.\n ```");
		p.setCanPickupItems(false);
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				ip.setDead(true);
				ip.save();
				PlayerManager.removeIngamePlayer(p);
				
			    PacketPlayInClientCommand packet = new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN);
			    ((CraftPlayer)p).getHandle().playerConnection.a(packet);
			    
				p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
				p.teleport(plugin.getSettings().getVaroWorld().getSpawnLocation());
				
				String title_msg = "Du bist gestorben";
				String subtitle_msg = "Somit bist du aus Varo ausgeschieden";
				IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\": \"" + title_msg + "\",color:" + ChatColor.RED.name().toLowerCase() + "}");
				IChatBaseComponent chatSubTitle = ChatSerializer.a("{\"text\": \"" + subtitle_msg + "\",color:" + ChatColor.RED.name().toLowerCase() + "}");
				PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle);
				PacketPlayOutTitle subtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, chatSubTitle);
				PacketPlayOutTitle length = new PacketPlayOutTitle(5, 120, 5);
				((CraftPlayer) p).getHandle().playerConnection.sendPacket(title);
				((CraftPlayer) p).getHandle().playerConnection.sendPacket(subtitle);
				((CraftPlayer) p).getHandle().playerConnection.sendPacket(length);
				
				p.setPlayerListName(ChatColor.GRAY + "[Spectator] " + p.getName());
				p.sendMessage(ChatColor.RED + "Du bist gestorben!\n" + "Somit bist du aus Varo ausgeschieden.\n");
				p.setCanPickupItems(true);
				PlayerManager.addSpectator(p);
				p.teleport(plugin.getSettings().getLobby());
				// TODO Add Message on how to use spectator
			}
		}, 40);
		if(p.getKiller() == null) return;
		Player killer = p.getKiller();
		if(PlayerManager.getIngamePlayer(killer) == null) return;
		PlayerManager.getIngamePlayer(killer).addKill(p.getUniqueId().toString());
	}

	@EventHandler
	public void onEat(PlayerItemConsumeEvent e) {
		Player p = e.getPlayer();
		if(PlayerManager.getIngamePlayer(p) == null) return;
		if(e.getItem().getType() == Material.GOLDEN_APPLE) {
			if(e.getItem().getDurability() != 1) return;
			e.setCancelled(true);
			p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1, 1);
			p.sendMessage(ChatColor.RED + "Du darfst keinen OP-Apfel essen!");
		}
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent e) {
		Material result = e.getRecipe().getResult().getType();
		if(result == Material.HOPPER_MINECART || result == Material.EYE_OF_ENDER) {
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			p.closeInventory();
			p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1, 1);
			p.sendMessage(ChatColor.RED + "Du darfst dieses Item nicht craften!");
		}
	}
	
	@EventHandler
	public void onBrewing(BrewEvent e) {
		Material m = e.getContents().getItem(3).getType();
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				boolean dropped = false;
				for(int i = 0; i < 3; i++) {
					if(e.getContents().getItem(i) != null) {
						if(e.getContents().getItem(i).getType() == Material.POTION) {
							short pm = e.getContents().getItem(i).getDurability();
							int b = Integer.parseInt((String.valueOf(pm)).substring(0, 1));
							if(b == 1 && pm != 16) {
								e.getContents().setItem(i, new ItemStack(Material.AIR));
								if(dropped == false) {
									e.getBlock().getWorld().dropItem(e.getBlock().getLocation().add(0, 1, 0), new ItemStack(m));
									for(Player send : getNearbyPlayers(e.getBlock().getLocation(), 20)) {
										send.playSound(send.getLocation(), Sound.VILLAGER_NO, 1, 1);
										send.sendMessage(ChatColor.RED + "Ein Braustand in der N�he hat versucht, einen illegalen Trank zu brauen!");
									}
									dropped = true;
								}
							} else {
								ArrayList<Short> strength = new ArrayList<>();
								strength.add((short)8201);
								strength.add((short)8233);
								strength.add((short)8265);
								ArrayList<Short> invisible = new ArrayList<>();
								invisible.add((short)8206);
								invisible.add((short)8270);
								ArrayList<Short> regeneration = new ArrayList<>();
								regeneration.add((short)8225);
								regeneration.add((short)8193);
								regeneration.add((short)8257);
								
								ArrayList<Short> allforbidden = strength;
								allforbidden.addAll(invisible);
								allforbidden.addAll(regeneration);
								
								for(Short sh : allforbidden) {
									if(pm == sh) {
										e.getContents().setItem(i, new ItemStack(Material.AIR));
										if(dropped == false) {
											e.getBlock().getWorld().dropItem(e.getBlock().getLocation().add(0, 1, 0), new ItemStack(m));
											for(Player send : getNearbyPlayers(e.getBlock().getLocation(), 20)) {
												send.playSound(send.getLocation(), Sound.VILLAGER_NO, 1, 1);
												send.sendMessage(ChatColor.RED + "Ein Braustand in der Nähe hat versucht, einen illegalen Trank zu brauen!");
											}
											dropped = true;
										}
									}
								}
							}
						}
					}
				}
			}
		}, 1);
	}

	public List<Player> getNearbyPlayers(Location loc, int distance) {
	    int distanceSquared = distance*distance;
	    List<Player> list = new ArrayList<Player>();
	    for(Player p: Bukkit.getOnlinePlayers()) {
	    	if(p.getLocation().distanceSquared(loc) < distanceSquared) {
	    		list.add(p);
	    	}
	    }
		return list;
	}
		
}