package de.alexanderritter.varo.commands;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.alexanderritter.varo.api.VaroMessages;
import de.alexanderritter.varo.ingame.VaroPlayer;
import de.alexanderritter.varo.main.Varo;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;

public class CMDstrike implements CommandExecutor {
	
	Varo plugin;

	public CMDstrike(Varo plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!command.getName().equalsIgnoreCase("varo.strike")) return false;
		if(args.length < 2) return false;
		if(!Arrays.asList("add", "remove", "list").contains(args[0])) return false;
		String playerName = args[1];
		if(plugin.getRegistration().loadPlayer(playerName) == null) {
			sender.sendMessage(VaroMessages.playerNotRegistered(playerName));
			return true;
		}
		VaroPlayer ip = plugin.getRegistration().loadPlayer(playerName);
		
		switch(args[0]) {
		case "list":
			List<String> strikes = ip.getStrikes();
			if(strikes.size() == 0) {
				sender.sendMessage(VaroMessages.strikesEmpty(playerName));
				return true;
			}
			sender.sendMessage(Varo.prefix + ChatColor.GREEN + "Der Spieler " + playerName + " hat folgende Strikes: ");
			for(int i = 0; i < strikes.size(); i++) sender.sendMessage(ChatColor.DARK_AQUA + String.valueOf(i+1) + ": " + ChatColor.YELLOW + strikes.get(i));
			break;
		case "add":
			String reason = "";
			for(int i = 2; i < args.length; i++) {
				reason += args[i] + " ";
			}
			ip.addStrike(reason);
			Bukkit.broadcastMessage(Varo.prefix + ChatColor.RED + playerName + " hat einen Strike erhaten für: " + ChatColor.YELLOW + reason);
			sendDiscordStrikeEmbed(ip, reason, sender.getName());
			break;
		case "remove":
			if(args.length != 3) {
				sender.sendMessage(ChatColor.RED + "Syntax: /varo.strike remove <player> <index>");
				return true;
			}
			int index = 0;
			try {
				index = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				sender.sendMessage(VaroMessages.nointeger);
				return true;
			}
			if(index <= 0) {
				sender.sendMessage(ChatColor.RED + "Der Index muss mindestens 1 betragen.");
				return true;
			}
			if(index > ip.getStrikes().size()) {
				sender.sendMessage(ChatColor.RED + "Der Spieler " + playerName + " besitzt keinen Strike mit Index " + index);
				return true;
			}
			sender.sendMessage(VaroMessages.strikeRemoved(playerName, ip.getStrikes().get(index - 1)));
			ip.removeStrike(index - 1);
			break;
		}
		return true;
	}
	
	public void sendDiscordStrikeEmbed(VaroPlayer ip, String reason, String issued_by) {
		TextChannel channel = DiscordUtil.getTextChannelById(plugin.getSettings().getDiscordChannelId());
		channel.sendMessage(new EmbedBuilder()
		    .setTitle(":warning: " + ip.getName() + " hat einen Strike erhalten")
		    .setDescription("```\n" + reason + "\n```")
		    .setColor(new Color(15425050))
		    .setTimestamp(OffsetDateTime.now())
		    .setThumbnail(DiscordSRV.config().getString("Experiment_EmbedAvatarUrl")
		    		.replace("{uuid}", ip.getUuid().toString())
		    		.replace("{uuid-nodashes}", ip.getUuid().toString().replace("-", ""))
		    	.replace("{username}", ip.getName())
		    	.replace("{size}", "128"))
		    .build()
		   ).queue();
	}

}
