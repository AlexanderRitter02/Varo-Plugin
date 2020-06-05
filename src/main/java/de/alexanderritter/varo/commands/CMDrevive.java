package de.alexanderritter.varo.commands;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;

import de.alexanderritter.varo.api.UUIDs;
import de.alexanderritter.varo.ingame.PlayerManager;
import de.alexanderritter.varo.ingame.VaroPlayer;
import de.alexanderritter.varo.main.Varo;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;

public class CMDrevive implements CommandExecutor {
	
	Varo plugin;
	
	public CMDrevive(Varo plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!command.getName().equalsIgnoreCase("varo.revive")) return false;
		if(args.length != 1) return false;
		
		YamlConfiguration players = plugin.getPlayerConfig();
				
		try {
			if(UUIDs.getUUID(args[0]) != null) {
				String uuid = UUIDs.getUUID(args[0]).toString();
				if(plugin.getRegistration().getAllUUIDs().contains(UUID.fromString(uuid))) {
									
					Boolean dead = players.getBoolean(uuid + ".dead");
					if(dead) {
						
						players.set(uuid + ".dead", Boolean.valueOf(false));
						
						if(Bukkit.getPlayer(UUID.fromString(uuid)) != null) {
							
							Player p = Bukkit.getPlayer(UUID.fromString(uuid));
							VaroPlayer ip = plugin.getRegistration().loadPlayer(p);
							PlayerManager.addIngamePlayer(p, ip);
							ip.setupScoreboard();
							plugin.preparePlayer(p);
							PlayerManager.removeSpectator(p);
							p.teleport(plugin.getSettings().getVaroWorld().getSpawnLocation());
							
						} else {
							// Offline Player
							players.set(uuid + ".reviving", Boolean.valueOf(true));							
						}
						
						plugin.sendDiscordMessage("```css\n Der Spieler " + args[0] + " wurde wiederbelebt. Viel Glück!.\n ```");
						sender.sendMessage(ChatColor.GREEN + "Der Spieler " + ChatColor.GOLD + args[0] + ChatColor.GREEN + " wurde erfolgreich wiederbelebt");
						
					} else sender.sendMessage(ChatColor.RED + "Der Spieler " + ChatColor.GOLD + args[0] + ChatColor.RED + " ist nicht tot. Du kannst ihn nicht wiederbeleben");
				} else sender.sendMessage(ChatColor.RED + "Der Spieler " + ChatColor.GOLD + args[0] + ChatColor.RED + " ist nicht bei Varo registriert");
			} else sender.sendMessage(ChatColor.RED + "Der Spieler " + ChatColor.GOLD + args[0] + ChatColor.RED + " existiert nicht");
		} catch (IOException e) {e.printStackTrace();}
				
		plugin.savePlayerConfig(players);
		return true;
	}
	
	public Player getOfflinePlayer(String name, UUID uuid, Location location) {
        Player target = null;
      GameProfile profile = new GameProfile(uuid, name);

      MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
      EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), profile, new PlayerInteractManager(server.getWorldServer(0)));
      entity.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
      entity.world = ((CraftWorld) location.getWorld()).getHandle();
      target = entity == null ? null : (Player) entity.getBukkitEntity();
      if (target != null) {
          target.loadData();
          return target;
      }
      return target;
  }

}
