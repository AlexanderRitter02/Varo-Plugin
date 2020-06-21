package de.alexanderritter.varo.main;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Wither;
import org.bukkit.entity.Zombie;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import de.alexanderritter.varo.api.AdvancedOfflinePlayer;
import de.alexanderritter.varo.config.ConfigUpgrade;
import de.alexanderritter.varo.config.Settings;
import de.alexanderritter.varo.events.Worldborder;
import de.alexanderritter.varo.ingame.ChestManager;
import de.alexanderritter.varo.ingame.PlayerManager;
import de.alexanderritter.varo.ingame.Registration;
import de.alexanderritter.varo.ingame.VaroPlayer;
import de.alexanderritter.varo.timemanagment.Gametime;
import de.alexanderritter.varo.timemanagment.PerHourChecker;
import de.alexanderritter.varo.timemanagment.StartProtection;
import github.scarsz.discordsrv.util.DiscordUtil;

public class Varo extends JavaPlugin {
	
	public static final String prefix = ChatColor.AQUA + "[Varo] " + ChatColor.RESET,
			   nointeger = ChatColor.RED + "Bitte gebe eine Integer an (positive ganze Zahl) an";
	
	public String path = this.getDataFolder().toString();
	Init init = new Init(this, path);
	Settings settings;
	Registration registration;
	Worldborder worldborder = new Worldborder(this);
	BukkitTask gametime;
	PerHourChecker checker;
	
	File playerConfigFile;
	File spawnConfigFile;
	File chestConfigFile;
	private YamlConfiguration playerConfig;
	private YamlConfiguration chestConfig;
	private YamlConfiguration spawnConfig;
	
	public void onEnable() {
		try {createConfigs();} catch (IOException e) {e.printStackTrace();}
		settings = init.loadSettings();
		if(!init.init()) return;
		registration = new Registration(this);
		checker = new PerHourChecker(this);
		initializeGametime();
		reloadIngamePlayers(true);
	}
	
	public void onDisable() {
		if(checker != null) checker.updateHour();
		for(Player online : Bukkit.getOnlinePlayers()) {
			try {PlayerManager.getIngamePlayer(online).save();} catch(NullPointerException e) {}
		}
	}
	
	private void createConfigs() throws IOException {
		
		getDataFolder().mkdir();
		if(new File(getDataFolder(), "config.yml").exists()) new ConfigUpgrade(this);
		getConfig().set("plugin.version", getDescription().getVersion());
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		playerConfigFile = new File(getDataFolder(), "players.yml");
		spawnConfigFile = new File(getDataFolder(), "spawns.yml");
		chestConfigFile = new File(getDataFolder(), "chests.yml");
		
		playerConfigFile.createNewFile();
		spawnConfigFile.createNewFile();
		chestConfigFile.createNewFile();
		
		savePlayerConfig(YamlConfiguration.loadConfiguration(playerConfigFile));
		saveSpawnConfig(YamlConfiguration.loadConfiguration(spawnConfigFile));
		saveChestConfig(YamlConfiguration.loadConfiguration(chestConfigFile));
		
	}
	
	public Settings getSettings() {
		return settings;
	}
	
	public void sendDiscordMessage(String message) {
		if(!(getServer().getPluginManager().isPluginEnabled("DiscordSRV"))) {
			System.out.println("Discord Plugin ist ausgeschaltet");
			return;
		}
		DiscordUtil.sendMessage(DiscordUtil.getTextChannelById(getSettings().getDiscordChannelId()), message);
	}
	
	public Worldborder getWorldBorder() {
		return worldborder;
	}
	
	public Registration getRegistration() {
		return registration;
	}
	
	public BukkitTask getGametime() {
		return gametime;
	}
	
	public PerHourChecker getPerHourChecker() {
		return checker;
	}
	
	public void initializeGametime() {
		this.gametime = new Gametime(this).runTaskTimer(this, 0, 20);
	}
	
	public YamlConfiguration getPlayerConfig() {
		return playerConfig;
	}
	public void savePlayerConfig(YamlConfiguration playerConfig) {
		try {playerConfig.save(playerConfigFile);} catch (IOException e) {e.printStackTrace();}
		this.playerConfig = playerConfig;
	}
	
	public YamlConfiguration getSpawnConfig() {
		return spawnConfig;
	}
	public void saveSpawnConfig(YamlConfiguration spawnConfig) {
		try {spawnConfig.save(spawnConfigFile);} catch (IOException e) {e.printStackTrace();}
		this.spawnConfig = spawnConfig;
	}
	
	public YamlConfiguration getChestConfig() {
		return chestConfig;
	}
	public void saveChestConfig(YamlConfiguration chestConfig) {
		try {chestConfig.save(chestConfigFile);} catch (IOException e) {e.printStackTrace();}
		this.chestConfig = chestConfig;
	}
	
	public void start() {
		reset();
		getSettings().setRunning(true);
		
		for(Player online : Bukkit.getOnlinePlayers()) preparePlayer(online);
		for(World world : Bukkit.getWorlds()) {
			prepareWorld(world);
		}
		
		if(getSettings().getStartProtection() > 0) new StartProtection(this, getSettings().getStartProtection()).start();
	}
	
	public void reset() {
		YamlConfiguration players = getPlayerConfig();
		for(String id : players.getKeys(false)) {
			players.set(id + ".dead", Boolean.valueOf(false));
			players.set(id + ".sessions", getSettings().getSessionsPerWeek());
			players.set(id + ".postedcoords", Boolean.valueOf(false));
			players.set(id + ".recent_time", getSettings().getSessionsLength());
			players.set(id + ".kills", null);
			players.set(id + ".reviving", null);
			players.set(id + ".sessions_today", null);
		}
		savePlayerConfig(players);
		YamlConfiguration chests = getChestConfig();
		for(String team : chests.getKeys(false)) {
			chests.set(team, null);
		}
		saveChestConfig(chests);
		reloadIngamePlayers(false);
		ChestManager.clear();
	}
	
	public void reloadIngamePlayers(boolean serverreload) {
		for(Player online : Bukkit.getOnlinePlayers()) {
			PlayerManager.removeIngamePlayer(online);
			
			PlayerManager.addIngamePlayer(online, getRegistration().loadPlayer(online));
			if(!serverreload) continue;
			if(PlayerManager.getIngamePlayer(online).isAdmin()) continue;
			Bukkit.getScheduler().runTaskLater(this, new Runnable() {
				@Override
				public void run() {
					Bukkit.getPluginManager().callEvent(new PlayerJoinEvent(online, null));
				}
			}, 1);
		}
		for(VaroPlayer ip : PlayerManager.getAllIngamePlayers()) {
			if(Bukkit.getPlayer(ip.getUuid()) == null) continue;
			Player p = Bukkit.getPlayer(ip.getUuid());
			ip.setupScoreboard();
			String name = p.getName();
			if(name.length() > 16) {
				name = p.getName().substring(0, 16);
			}
			p.setPlayerListName(ip.getColor() + name);
		}
	}
	
	public void preparePlayer(Player p) {
		p.setGameMode(GameMode.SURVIVAL);
		p.setHealth(20);
		p.setFoodLevel(20);
		p.setExhaustion(0);
		p.setSaturation(20);
		for(PotionEffect effect : p.getActivePotionEffects()) {p.removePotionEffect(effect.getType());}
		p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 180, 100), true);
		p.setLevel(0);
		p.setTotalExperience(0);
		p.setExp(0);
		p.setFireTicks(0);
		p.getInventory().clear();
		p.getInventory().setArmorContents(new ItemStack[p.getInventory().getArmorContents().length]);
	}
	
	public void prepareWorld(World world) {
		int radius = getConfig().getInt("border.radius");
		world.setTime(0);
		world.setThundering(false);
		world.setStorm(false);
		world.setPVP(true);
		world.getWorldBorder().setCenter(getSettings().getVaroWorld().getSpawnLocation());
		world.getWorldBorder().setSize((double)radius*2);
		checker.updateHour();
		getPerHourChecker().shrinkWorldborder();
		
		for(Entity entity : world.getEntitiesByClasses(Spider.class, CaveSpider.class, Skeleton.class, 
				Zombie.class, PigZombie.class, Enderman.class, Endermite.class, Creeper.class, Witch.class, Silverfish.class, 
				Slime.class, Ghast.class, MagmaCube.class, Guardian.class, Arrow.class, Wither.class, EnderDragon.class))
		{entity.remove();}
	}
	
	public void postPlayerCoordinates(VaroPlayer ip) {
		
		if(Bukkit.getPlayer(ip.getUuid()) != null) {
			Player p = Bukkit.getPlayer(ip.getUuid());
			sendCoordinateMessage(ip, p.getLocation().getBlock().getLocation());
			return;
		}
		
		AdvancedOfflinePlayer offlinePlayer;
		try {
			offlinePlayer = new AdvancedOfflinePlayer(this, ip.getUuid());
		} catch (IOException e) {
			getLogger().warning(("Skipping coordinate post for "+ ip.getName() +": Has never joined the server"));
			e.printStackTrace();
			return;
		}
		
		sendCoordinateMessage(ip, offlinePlayer.getLocationInt());
	}
	
	private void sendCoordinateMessage(VaroPlayer ip, Location pos) {
		Bukkit.broadcastMessage(Varo.prefix + ChatColor.GREEN + "Team " + ip.getColor() + ip.getTeam() + ChatColor.GREEN + " hat seine Koordinaten gepostet: "
				+ ChatColor.GOLD + pos.getBlockX() + ", " + pos.getBlockY() + ", " + pos.getBlockZ() + " ("+ pos.getWorld().getName() + ")");
		sendDiscordMessage("```http\n " + "Team " + ip.getTeam() + " hat seine Koordinaten gepostet: " + pos.getBlockX() + ", " + pos.getBlockY() + ", " + pos.getBlockZ() + " ("+ pos.getWorld().getName() + ") \n ```");
	}
	
}