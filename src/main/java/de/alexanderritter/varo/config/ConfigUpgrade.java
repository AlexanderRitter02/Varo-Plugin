package de.alexanderritter.varo.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.alexanderritter.varo.main.Varo;

public class ConfigUpgrade {
	
	Varo plugin;
    private static String VERSION;
    FileConfiguration config;
    FileConfiguration old_config;
	
	public ConfigUpgrade(Varo plugin) {
		
		this.plugin = plugin;
		VERSION = plugin.getDescription().getVersion();
		config = plugin.getConfig();
		if(getVersion().equalsIgnoreCase(VERSION)) return;
		
		try {
			File old_config_file = new File(plugin.getDataFolder(), "config.yml-" + getVersion() + ".old");
			plugin.getConfig().save(old_config_file);
			old_config = YamlConfiguration.loadConfiguration(old_config_file);
		} catch (IOException e) {
			plugin.getLogger().severe("Failed to create old config");
			e.printStackTrace();
			return;
		}
		
		if(getVersion().equals("UNKNOWN")) {
			for(String section : config.getKeys(false)) {
				config.set(section, null);
			}
			plugin.getLogger().warning("Varo config.yml version is UNKNOWN, re-creating default configuration values.");
		} else {
			plugin.getLogger().info("Upgrading varo config.yml to version " + VERSION + " (from " + getVersion() + ")");
			update();
		}
		
		config.set("plugin.version", VERSION);
		plugin.saveConfig();
	}
	
	private void update() {
		
		// < 1.7
		config.set("coord_post_day", null); // This was already unused in 1.7
		
		// < 1.8
		moveConfigOption("borderradius", "border.radius");
		moveConfigOption("borderendradius", "border.end-radius");
		moveConfigOption("projecttime", "border.shrink-time");
		
		if(old_config.isSet("bordermode")) {
			String bordermode = BorderMode.values()[old_config.getInt("bordermode")].toString();
			config.set("border.mode", bordermode);
			config.set("bordermode", null);
		}
		
		if(old_config.isSet("coord_post")) {
			ArrayList<String> coord_posts = new ArrayList<>();
			coord_posts.add(old_config.getString("coord_post"));
			config.set("coord-post", coord_posts);
			config.set("coord_post", null);
		}
		
	}
	
	private void moveConfigOption(String old_path, String new_path) {
		if(!old_config.isSet(old_path)) return;
		config.set(new_path, old_config.get(old_path));
		config.set(old_path, null);
	}
	
	private String getVersion() {
		if(config.getString("plugin.version") != null) return plugin.getConfig().getString("plugin.version");
		if(!config.isSet("spawn-teams-together")) return "UNKNOWN";
		if(!config.isSet("allow-prevent-coordinate-post")) return "1.7";
		return "1.8";
	}
	
}
