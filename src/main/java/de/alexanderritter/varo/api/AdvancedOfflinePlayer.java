package de.alexanderritter.varo.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftInventoryPlayer;
//import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryPlayer;
import org.bukkit.inventory.PlayerInventory;

import de.alexanderritter.varo.main.Varo;
import net.minecraft.server.v1_16_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
//import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
//import net.minecraft.server.v1_8_R3.NBTTagCompound;
//import net.minecraft.server.v1_8_R3.NBTTagList;
import net.minecraft.server.v1_16_R3.NBTTagList;

public class AdvancedOfflinePlayer {
	
	Varo plugin;
	File nbtdat;
	NBTTagCompound playernbt;
	
	public AdvancedOfflinePlayer(Varo plugin, UUID uuid) throws FileNotFoundException, IOException {
		nbtdat = new File(Bukkit.getWorldContainer()+ File.separator + plugin.getSettings().getVaroWorld().getName() + "/playerdata/" + uuid.toString() + ".dat");
		playernbt = NBTCompressedStreamTools.a(new FileInputStream(nbtdat));
	}
	
	public Location getLocationInt() {
		BigInteger addifneg = new BigInteger("18446744073709551616");
		long uuidmost = playernbt.getLong("WorldUUIDMost");
		long uuidleast = playernbt.getLong("WorldUUIDLeast");
		if(uuidmost < 0) uuidmost = addifneg.add(BigInteger.valueOf(uuidmost)).longValue();
		if(uuidleast < 0) uuidleast = addifneg.add(BigInteger.valueOf(uuidleast)).longValue();
		String worldUUID = UUIDs.convertFromTrimmed(Long.toHexString(uuidmost) + Long.toHexString(uuidleast));
		
		NBTTagList pos = (NBTTagList) playernbt.get("Pos");
		World world = Bukkit.getWorld(UUID.fromString(worldUUID));
		int x = Double.valueOf(pos.getString(0)).intValue();
		int y = Double.valueOf(pos.getString(1)).intValue();
		int z = Double.valueOf(pos.getString(2)).intValue();
		
		return new Location(world, x, y, z);
	}
	
	public PlayerInventory getInventory() {
		if(playernbt.get("Inventory") instanceof NBTTagList) {
			net.minecraft.server.v1_16_R3.PlayerInventory inv = new net.minecraft.server.v1_16_R3.PlayerInventory(null);
			inv.b((NBTTagList) playernbt.get("Inventory"));
			return new CraftInventoryPlayer(inv);
		}
		return null;
	}
	
}
