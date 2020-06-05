package de.alexanderritter.varo.api;

import org.bukkit.ChatColor;

public class Colors {
	
	public static ChatColor getColorfromString(String input) {		
		switch(input.toLowerCase()) {
		case "&0": case "§0": case "schwarz": case "black":
			return ChatColor.BLACK;
		case "&1": case "§1": case "dunkelblau": case "dark_blue":
			return ChatColor.DARK_BLUE;
		case "&2": case "§2": case "dunkelgrün": case "dark_green":
			return ChatColor.DARK_GREEN;
		case "&3": case "§3": case "dunkelaqua": case "dark_aqua":
			return ChatColor.DARK_AQUA;
		case "&4": case "§4": case "dunkelrot": case "dark_red":
			return ChatColor.DARK_RED;
		case "&5": case "§5": case "dunkelviolett": case "dark_purple":
			return ChatColor.DARK_PURPLE;
		case "&6": case "§6": case "gold":
			return ChatColor.GOLD;
		case "&7": case "§7": case "grau": case "grey":
			return ChatColor.GRAY;
		case "&8": case "§8": case "dunkelgrau": case "dark_grey":
			return ChatColor.DARK_GRAY;
		case "&9": case "§9": case "blau": case "blue":
			return ChatColor.BLUE;
		case "&a": case "§a": case "grün": case "gruen": case "green":
			return ChatColor.GREEN;
		case "&b": case "§b": case "aqua": case "hellblau": case "light_blue":
			return ChatColor.AQUA;
		case "&c": case "§c": case "rot": case "red":
			return ChatColor.RED;
		case "&d": case "§d": case "hellviolett": case "light_purple": case "pink":
			return ChatColor.LIGHT_PURPLE;
		case "&e": case "§e": case "gelb": case "yellow":
			return ChatColor.YELLOW;
		case "&f": case "§f": case "weiß": case "weiss": case "white":
			return ChatColor.WHITE;
		case "&k": case "§k": case "verschleiert": case "obfuscated":
			return ChatColor.MAGIC;
		case "&l": case "§l": case "fett": case "dick": case "bold":
			return ChatColor.BOLD;
		case "&m": case "§m": case "durchgestrichen": case "strikethrough":
			return ChatColor.STRIKETHROUGH;
		case "&n": case "§n": case "unterstrichen": case "underlined":
			return ChatColor.UNDERLINE;
		case "&o": case "§o": case "kursiv": case "italics": case "italic":
			return ChatColor.ITALIC;
		case "&r": case "§r": case "reset":
			return ChatColor.RESET;
		default:
			return ChatColor.RESET;
		}
	}
}
