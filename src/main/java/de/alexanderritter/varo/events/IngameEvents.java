package de.alexanderritter.varo.events;

import java.awt.Color;
import java.io.IOException;
import java.time.OffsetDateTime;
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
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.potion.Potion;

import de.alexanderritter.varo.api.AdvancedOfflinePlayer;
import de.alexanderritter.varo.api.ItemString;
import de.alexanderritter.varo.api.VaroMessages;
import de.alexanderritter.varo.ingame.PlayerManager;
import de.alexanderritter.varo.ingame.VaroPlayer;
import de.alexanderritter.varo.main.Varo;
import de.alexanderritter.varo.timemanagment.LoginProtection;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
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
			
			if(ip.isDead() || ip.isAdmin()) {
				PermissionAttachment attachment = p.addAttachment(plugin);
				permissions.put(p.getUniqueId(), attachment);
				attachment.setPermission("discordsrv.silentquit", true);
				attachment.setPermission("discordsrv.silentjoin", true);
			}
			
			if(ip.isAdmin()) {
				PlayerManager.addIngamePlayer(p, ip);
				return;
			}
			
			if(ip.isDead()) {
				if(!plugin.getSettings().isAllowedSpectateIfTeamAlive() && plugin.getRegistration().isTeamAlive(ip.getTeam())) e.disallow(Result.KICK_BANNED, VaroMessages.spectateOnlyIfTeamDead);
				if(plugin.getRegistration().isTeamAlive(ip.getTeam()) && !plugin.getRegistration().isTeamMemberOnline(ip.getTeam())) e.disallow(Result.KICK_BANNED, VaroMessages.spectateOnlyIfMemberOnline);
				return;
			}
			
			if(ip.getSessions() <= 0) {
				e.disallow(Result.KICK_BANNED, VaroMessages.noSessionsThisWeek);
				return;
			}
			
			if(ip.getSessionsPlayedToday() >= plugin.getSettings().getMaxSessionsPerDay()) {
				e.disallow(Result.KICK_BANNED, VaroMessages.maxSessionsPerDay(plugin.getSettings().getMaxSessionsPerDay()));
				return;
			}
			
			PlayerManager.addIngamePlayer(p, ip);
			
		} else e.disallow(Result.KICK_BANNED, VaroMessages.kickBecauseNotRegistered);
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
			if(ip.isTempAdmin()) {
				YamlConfiguration players = plugin.getPlayerConfig();
				String id = ip.getUuid().toString();
				players.set(id + ".admin.world", p.getLocation().getWorld().getName());
				players.set(id + ".admin.x", p.getLocation().getX());
				players.set(id + ".admin.y", p.getLocation().getY());
				players.set(id + ".admin.z", p.getLocation().getZ());
				plugin.savePlayerConfig(players);
				p.teleport(p.getLocation().getWorld().getSpawnLocation());
			}
			p.setGameMode(GameMode.SPECTATOR);
			String title_msg = VaroMessages.adminModeTITLE;
			String subtitle_msg = VaroMessages.adminModeSUBTITLE;
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
			p.sendMessage(VaroMessages.outsideBorder);
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
		if(PlayerManager.getIngamePlayer(p) == null) {
			System.out.println(p.getName() + " is NULL");
			return;
		}
		VaroPlayer ip = PlayerManager.getIngamePlayer(p);
		if(ip.isAdmin()) {
			if(ip.isTempAdmin()) {
				plugin.getLogger().info((p.getName() + " joined as TEMP-Admin"));
				YamlConfiguration playerConfig = plugin.getPlayerConfig();
				
				String locString = ip.getUuid().toString() + ".admin.";
				World world = Bukkit.getWorld(playerConfig.getString(locString + "world"));
				double x = playerConfig.getDouble(locString + "x");
				double y = playerConfig.getDouble(locString + "y");
				double z = playerConfig.getDouble(locString + "z");
				Location old_loc = new Location(world, x, y, z);
				p.teleport(old_loc);
				p.setGameMode(GameMode.SURVIVAL);
				
				ip.setAdmin(false);
				playerConfig.set(ip.getUuid() + ".admin", null);
				plugin.savePlayerConfig(playerConfig);
				
			}
			return;
		}
		
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				for(VaroPlayer member : plugin.getRegistration().getTeamMembers(ip.getTeam())) {
					if(!member.isSpectator() || !(plugin.getRegistration().isTeamAlive(ip.getTeam()))) continue;
					if(Bukkit.getPlayer(member.getUuid()) == null) continue;
					Bukkit.getPlayer(member.getUuid()).kickPlayer(VaroMessages.kickedMemberLeft(ip.getName()) + "\n\n" + VaroMessages.spectateOnlyIfMemberOnline);
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
		if(plugin.getSettings().isFriendlyfire()) return;
		Player p = (Player) e.getEntity();
		if(PlayerManager.getIngamePlayer(p) == null) return;
		
		Player damager;
		Entity damagerEntity = e.getDamager();
		if(damagerEntity instanceof Player) {
			damager = (Player) damagerEntity;
		} else if(damagerEntity instanceof Projectile) {
			Projectile projectile = (Projectile) damagerEntity;
			if(!(projectile.getShooter() instanceof Player)) return;
			damager = (Player) projectile.getShooter();
		} else return;
		
		
		if(plugin.getSettings().isBoostingAllowed()) {
			if((damager == p || e.getDamage() <= 1.5) && p.getHealth() - e.getFinalDamage() > 0) return;
		}
		
		if(PlayerManager.getIngamePlayer(damager) == null || PlayerManager.getIngamePlayer(p).getTeam().equalsIgnoreCase(PlayerManager.getIngamePlayer(damager).getTeam())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		if(PlayerManager.getIngamePlayer(p) == null) return;
		VaroPlayer ip = PlayerManager.getIngamePlayer(p);
		if(ip.isAdmin()) return;
		for(Player online : Bukkit.getOnlinePlayers()) {online.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1, 1);}
		plugin.sendDiscordMessage(VaroMessages.DISCORD_playerDied(ip.getName()));
		p.setCanPickupItems(false);
		ip.setDead(true);
		ip.save();
		
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				
			    PacketPlayInClientCommand packet = new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN);
			    ((CraftPlayer)p).getHandle().playerConnection.a(packet);
			    
				p.teleport(plugin.getSettings().getVaroWorld().getSpawnLocation());
				
				String title_msg = VaroMessages.deathTITLE;
				String subtitle_msg = VaroMessages.deathSUBTITLE;
				IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\": \"" + title_msg + "\",color:" + ChatColor.RED.name().toLowerCase() + "}");
				IChatBaseComponent chatSubTitle = ChatSerializer.a("{\"text\": \"" + subtitle_msg + "\",color:" + ChatColor.RED.name().toLowerCase() + "}");
				PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle);
				PacketPlayOutTitle subtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, chatSubTitle);
				PacketPlayOutTitle length = new PacketPlayOutTitle(5, 120, 5);
				((CraftPlayer) p).getHandle().playerConnection.sendPacket(title);
				((CraftPlayer) p).getHandle().playerConnection.sendPacket(subtitle);
				((CraftPlayer) p).getHandle().playerConnection.sendPacket(length);
				
				p.setPlayerListName(ChatColor.GRAY + VaroMessages.spectatorPrefix + " " + p.getName());
				p.sendMessage(VaroMessages.youDied);
				p.setCanPickupItems(true);
				
				p.teleport(plugin.getSettings().getLobby());
				
				PlayerManager.removeIngamePlayer(p);
				PlayerManager.addSpectator(p);
				p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
				// TODO Add Message on how to use spectator
			}
		}, 40);
		
		if(p.getKiller() != null) {
			Player killerPlayer = p.getKiller();
			if(PlayerManager.getIngamePlayer(killerPlayer) == null) return;
			VaroPlayer killer = PlayerManager.getIngamePlayer(killerPlayer);
			killer.addKill(p.getUniqueId().toString());
		}
		
		if(plugin.getRegistration().getAliveTeams().size() != 1) return;
		String team = plugin.getRegistration().getAliveTeams().get(0);
		for(VaroPlayer winner : plugin.getRegistration().getTeamMembers(team)) {
			if(Bukkit.getPlayer(winner.getUuid()) != null) {
				sendDiscordWinEmbed(winner, Bukkit.getPlayer(winner.getUuid()).getInventory());
			} else {
				AdvancedOfflinePlayer op;
				try {
					op = new AdvancedOfflinePlayer(plugin, winner.getUuid());
					sendDiscordWinEmbed(winner, op.getInventory());
				} catch (IOException e1) {
					plugin.getLogger().severe("Couldn't get inventory for " + winner.getName() + ", he never logged onto the server");
				}
			}
			Bukkit.broadcastMessage(VaroMessages.wonTheGame(winner));
		}
			
	}
	
	@EventHandler
	public void onUse(PlayerInteractEvent e) {
		if(e.getAction() == Action.PHYSICAL || e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) return;
		if(!e.hasItem() || e.getItem() == null) return;
		String itemString = e.getItem().getData().toString().split("(?<=\\))")[0];
		if(!(plugin.getSettings().getDisallowedItemsToUse().contains(itemString) || plugin.getSettings().getDisallowedItemsToUse().contains(itemString.split("\\(")[0]))) return;
		e.setUseItemInHand(org.bukkit.event.Event.Result.DENY);
		Player p = e.getPlayer();
		p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1, 1);
		p.sendMessage(VaroMessages.cannotUseItem);
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent e) {
		String itemString = e.getRecipe().getResult().getData().toString().split("(?<=\\))")[0];
		if(!(plugin.getSettings().getDisallowedItemsGeneral().contains(itemString) || plugin.getSettings().getDisallowedItemsGeneral().contains(itemString.split("\\(")[0]))) return;
		e.setCancelled(true);
		Player p = (Player) e.getWhoClicked();
		p.closeInventory();
		p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1, 1);
		p.sendMessage(VaroMessages.cannotCraftItem);
	}
	
	@EventHandler
	public void onBrewing(BrewEvent e) {
		
		if(!plugin.getSettings().arePotionsAllowed()) {
			e.getBlock().breakNaturally(); // Break the brewing stand to prevent infite loop
			e.setCancelled(true);
			for(Player send : getNearbyPlayers(e.getBlock().getLocation(), 20)) {
				send.playSound(send.getLocation(), Sound.VILLAGER_NO, 1, 1);
				send.sendMessage(VaroMessages.potionsNotAllowed);
			}
			return;
		}
		
		// Getting relevant Potion data BEFORE brewing
		Material m = e.getContents().getItem(3).getType();
		ArrayList<ItemStack> oldContents = new ArrayList<>();
		for(ItemStack stack : e.getContents()) {
			if(stack == null) {
				oldContents.add(null);
			} else oldContents.add(new ItemStack(stack));
		}
		
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				boolean dropped = false;
				for(int i = 0; i < 3; i++) {
					if(e.getContents().getItem(i) != null && e.getContents().getItem(i).getType() == Material.POTION) {
						
						Potion potion = Potion.fromItemStack(e.getContents().getItem(i));
						if(potion.isSplash() && plugin.getSettings().splashPotionsAllowed()) continue;
						if(potion.getType() == null) continue;
						if((potion.isSplash() && !(plugin.getSettings().splashPotionsAllowed())) || plugin.getSettings().getDisallowedPotions().contains(potion.getType().toString())) {
							// TODO Allow only blocking specific potion levels
							e.getContents().setItem(i, new ItemStack(Material.AIR));
							e.getBlock().getWorld().dropItem(e.getBlock().getLocation().add(0, 1, 0), oldContents.get(i));
							if(dropped == false) {
								e.getBlock().getWorld().dropItem(e.getBlock().getLocation().add(0, 1, 0), new ItemStack(m));
								for(Player send : getNearbyPlayers(e.getBlock().getLocation(), 20)) {
									send.playSound(send.getLocation(), Sound.VILLAGER_NO, 1, 1);
									send.sendMessage(VaroMessages.potionIllegal);
								}
								dropped = true;
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
	
	public void sendDiscordWinEmbed(VaroPlayer ip, PlayerInventory inv) {
		TextChannel channel = DiscordUtil.getTextChannelById(plugin.getSettings().getDiscordChannelId());
		
		ItemString helmet = new ItemString(inv.getHelmet());
		ItemString chestplate = new ItemString(inv.getChestplate());
		ItemString leggings = new ItemString(inv.getLeggings());
		ItemString boots = new ItemString(inv.getBoots());
		
		
		ItemString sword = new ItemString("No sword in hotbar");
		ItemString bow = new ItemString("No bow in hotbar");
		for(int i = 0; i <= 8; i++) {
			if(inv.getItem(i) == null) continue;
			if(inv.getItem(i).getType().toString().contains("SWORD")) {
				sword = new ItemString(inv.getItem(i));
			} else if(inv.getItem(i).getType() == Material.BOW) {
				bow = new ItemString(inv.getItem(i));
			}
		}
		
		
		channel.sendMessage(
		new EmbedBuilder()
			.setTitle(VaroMessages.DISCORD_wonTheGame(ip.getName()), "https://de.namemc.com/profile/" + ip.getUuid())
		    .setDescription("**@#" + ip.getTeam() + "**\n\n" + VaroMessages.DISCORD_winPartyMessage)
		    .setColor(new Color(1408828))
		    .setTimestamp(OffsetDateTime.now())
			.setThumbnail(DiscordSRV.config().getString("Experiment_EmbedAvatarUrl")
				.replace("{uuid}", ip.getUuid().toString())
                .replace("{uuid-nodashes}", ip.getUuid().toString().replace("-", ""))
                .replace("{username}", ip.getName())
                .replace("{size}", "128"))			    
		    .addField("Rüstung", ""
		    		+ ":small_blue_diamond: " + helmet + helmet.getEnchantments() + "\n"
		    		+ ":small_blue_diamond: " + chestplate + chestplate.getEnchantments() + "\n"
		    		+ ":small_blue_diamond: " + leggings + leggings.getEnchantments() + "\n"
		    		+ ":small_blue_diamond: " + boots + boots.getEnchantments(), false)
		    .addField("Items:",
		    		":crossed_swords: " + sword + sword.getEnchantments() + "\n"
		    		+ ":bow_and_arrow: " + bow + bow.getEnchantments(), false)
		    .addField("Kills:", String.valueOf(ip.getKillCount()), true)
		    .addField("Strikes", String.valueOf(ip.getStrikes().size()), true)
		    .build()
		 ).queue();
	}
		
}