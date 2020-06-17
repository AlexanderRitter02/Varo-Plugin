package de.alexanderritter.varo.api;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class ItemString {
	
	String name;
	Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
	
	public ItemString(ItemStack item) {
		if(item == null) {
			name = "nothing";
		} else {
			name = item.getType().toString().toLowerCase();
			enchantments = item.getEnchantments();
		}
	}
	
	public ItemString(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public String getEnchantments() {
		String enchString = "";
		if(enchantments.size() == 0) return "";
		for(Enchantment ench : enchantments.keySet()) {
			enchString += getEnchName(ench.getName()) + " " + getRomanNumeral(enchantments.get(ench)) + ", ";
		}
		enchString = " - `" + enchString.subSequence(0, enchString.length()-2).toString() + "`";
		return enchString;
	}
	
	private String getEnchName(String ench) {
		switch(ench.toUpperCase()) {
			case "ARROW_DAMAGE":
				return "Power";
			case "ARROW_FIRE":
				return "Flame";
			case "ARROW_INFINITE":
				return "Infinity";
			case "ARROW_KNOCKBACK":
				return "Punch";
			case "DAMAGE_ALL":
				return "Sharpness";
			case "DAMAGE_ARTHROPODS":
				return "Bane of Arthropods";
			case "DAMAGE_UNDEAD":
				return "Smite";
			case "DEPTH_STRIDER":
				return "Depth Strider";
			case "DIG_SPEED":
				return "Efficiency";
			case "DURABILITY":
				return "Unbreaking";
			case "FIRE_ASPECT":
				return "Fire Aspect";
			case "Knockback":
				return "Knockback";
			case "LOOT_BONUS_BLOCKS":
				return "Fortune";
			case "LOOT_BONUS_MOBS":
				return "Looting";
			case "LUCK":
				return "Luck";
			case "LURE":
				return "Lure";
			case "OXYGEN":
				return "Respiration";
			case "PROTECTION_ENVIRONMENTAL":
				return "Protection";
			case "PROTECTION_EXPLOSIONS":
				return "Blast Protection";
			case "PROTECTION_FALL":
				return "Feather Falling";
			case "PROTECTION_FIRE":
				return "Fire Protection";
			case "PROTECTION_PROJECTILE":
				return "Projectile Protection";
			case "SILK_TOUCH":
				return "Silk Touch";
			case "THORNS":
				return "Thorns";
			case "WATER_WORKER":
				return "Aqua Affinity";
			default:
				return ench;
		}
	}
	
	private String getRomanNumeral(int num) {
		String roman = "";
		for(int i = 0; i < num; i++) {
			roman += "I";
		}
		return roman
				.replace("IIIII", "V")
				.replace("IIII", "IV");
		// Vanilla enchantments can't get any higher than 5
	}
	
	public String toString() {
		return name;
	}
	
}
